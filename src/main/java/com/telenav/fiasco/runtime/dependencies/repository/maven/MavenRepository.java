package com.telenav.fiasco.runtime.dependencies.repository.maven;

import com.telenav.fiasco.internal.building.dependencies.download.Downloader;
import com.telenav.fiasco.internal.building.dependencies.download.Downloader.Download;
import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactGroup;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.concurrent.Future;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;
import static com.telenav.kivakit.resource.CopyMode.OVERWRITE;
import static com.telenav.kivakit.resource.path.Extension.POM;

/**
 * A maven repository containing {@link MavenArtifact}s, organized in {@link MavenArtifactGroup}s. A {@link
 * MavenRepository} can be created with {@link #create(Listener, String)}, passing in the name of the repository. The
 * root of the repository can then be added with {@link #withRoot(FilePath)}. The {@link #local(Listener)} method
 * returns the local Maven repository. The {@link #mavenCentral(Listener)} method returns the Maven Central repository.
 *
 * <p>
 * The {@link #contains(Artifact)} method returns true if this repository contains the given artifact. The {@link
 * #folderPath(Artifact)} method returns the full path to the given artifact within this repository. The {@link
 * #install(ArtifactRepository, Artifact)} method copies the given artifact from the given repository into this
 * repository if it is not already there.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class MavenRepository extends BaseComponent implements ArtifactRepository
{
    /** Parallel copier to speed up downloads */
    private static final Downloader downloader = new Downloader();

    /**
     * @return A {@link MavenRepository} instance with the given name (but no root path)
     */
    public static MavenRepository create(Listener listener, String name)
    {
        return listener.listenTo(new MavenRepository(name));
    }

    /**
     * @return The local maven repository at ~/.m2/repository
     */
    public static MavenRepository local(Listener listener)
    {
        return create(listener, "Local")
                .withRoot(FilePath.parseFilePath(listener, "${user.home}/.m2/repository"));
    }

    /**
     * @return The Maven Central repository
     */
    public static MavenRepository mavenCentral(Listener listener)
    {
        return create(listener, "Maven Central")
                .withRoot(FilePath.parseFilePath(listener, "https://repo1.maven.org/maven2/"));
    }

    /** The repository name */
    private final String name;

    /** Path to the repository root */
    private FilePath root;

    protected MavenRepository(MavenRepository that)
    {
        name = that.name;
        root = that.root;

        listenTo(downloader);
        copyListeners(that);
    }

    protected MavenRepository(String name)
    {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Artifact artifact)
    {
        return Resource.resolve(this, folderPath(artifact)
                .withChild(artifact.identifier() + "-" + artifact.version() + ".jar")).exists();
    }

    /**
     * @return The full path to the given artifact in this repository
     */
    @Override
    public FilePath folderPath(Artifact artifact)
    {
        return root.withoutTrailingSlash().withChild(artifact.path());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean install(ArtifactRepository source, Artifact artifact)
    {
        try
        {
            // Get the artifact path in the source repository,
            var artifactFolder = source.folderPath(artifact);

            // and then for each resource in the set of artifact resources to be downloaded,
            var futureDownloads = new ObjectList<Future<Download>>();
            for (var resource : artifact.resources(artifactFolder))
            {
                // submit a job to copy the resource into this repository in the background.
                var download = new Download(resource, Folder.of(folderPath(artifact)).mkdirs(), OVERWRITE);
                futureDownloads.addIfNotNull(downloader.download(download));
            }

            // Then, for each submitted resource that is downloading in the background,
            for (var futureDownload : futureDownloads)
            {
                // wait for the download to complete,
                var download = futureDownload.get();

                // and check the status.
                switch (download.status())
                {
                    case DOWNLOADED:
                        // information(job.toString());
                        break;

                    case FAILED:
                        problem(download.toString());
                        return false;

                    case WAITING:
                    case DOWNLOADING:
                    default:
                        fail("Internal error: download completed in state: $", download.status());
                        break;
                }
            }

            return true;
        }
        catch (Exception e)
        {
            problem(e, "Unable to install $ $ => $", artifact, source, this);
            return false;
        }
    }

    @Override
    public boolean isRemote()
    {
        return !name.equals("local");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name()
    {
        return name;
    }

    /**
     * <b>Not public API</b>
     *
     * @param artifact The artifact for which to read the POM information from this repository
     * @return The POM information
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    public Pom pom(Artifact artifact)
    {
        return PomReader.read(this, listenTo(resource(artifact, POM)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource resource(Artifact artifact, Extension extension)
    {
        return artifact.resource(folderPath(artifact), extension);
    }

    @Override
    public String toString()
    {
        return name();
    }

    /**
     * @return This repository with the given root path
     */
    public MavenRepository withRoot(FilePath root)
    {
        var copy = new MavenRepository(this);
        copy.root = root;
        return copy;
    }
}
