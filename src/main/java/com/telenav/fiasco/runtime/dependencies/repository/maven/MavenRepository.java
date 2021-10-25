package com.telenav.fiasco.runtime.dependencies.repository.maven;

import com.telenav.fiasco.internal.building.dependencies.download.ParallelCopier;
import com.telenav.fiasco.internal.building.dependencies.download.ParallelCopier.CopyJob;
import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.data.formats.xml.stax.StaxReader;
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
    private static final ParallelCopier copier = new ParallelCopier();

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
                .withRoot(FilePath.parseFilePath("${user.home}/.m2/repository"));
    }

    /**
     * @return The Maven Central repository
     */
    public static MavenRepository mavenCentral(Listener listener)
    {
        return create(listener, "Maven Central")
                .withRoot(FilePath.parseFilePath("https://repo1.maven.org/maven2/"));
    }

    /** The repository name */
    private final String name;

    /** Path to the repository root */
    private FilePath root;

    protected MavenRepository(MavenRepository that)
    {
        this.name = that.name;
        this.root = that.root;

        listenTo(copier);
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
    public boolean contains(final Artifact artifact)
    {
        return Resource.resolve(this, folderPath(artifact)
                .withChild(artifact.identifier() + "-" + artifact.version() + ".jar")).exists();
    }

    /**
     * @return The full path to the given artifact in this repository
     */
    @Override
    public FilePath folderPath(final Artifact artifact)
    {
        return root.withoutTrailingSlash().withChild(artifact.path());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean install(ArtifactRepository source, final Artifact artifact)
    {
        try
        {
            // Get the artifact path in the source repository,
            var artifactFolder = source.folderPath(artifact);

            // and then for each resource in the set of resources to be copied,
            var pending = new ObjectList<Future<CopyJob>>();
            for (var resource : artifact.resources(artifactFolder))
            {
                // copy the artifact into this repository in the background.
                pending.addIfNotNull(copier.add(resource, Folder.of(folderPath(artifact)).mkdirs(), OVERWRITE));
            }

            // Wait for each copy job to complete and show the results
            for (int i = 0; i < pending.size(); i++)
            {
                var job = copier.waitForNextCompleted();

                switch (job.status())
                {
                    case COPIED:
                        // information(job.toString());
                        break;

                    case FAILED:
                        problem(job.toString());
                        return false;

                    case WAITING:
                    default:
                        fail("Internal error: copy waiting");
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
     * @param artifact The artifact for which to read the POM information from this repository
     * @return The POM information
     */
    public PomReader.Pom pom(final Artifact artifact)
    {
        var pomResource = listenTo(resource(artifact, POM));

        // open it with a STAX reader,
        try (var reader = StaxReader.open(pomResource))
        {
            // and return the parsed POM information.
            return listenTo(new PomReader(reader)).read();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Resource resource(final Artifact artifact, Extension extension)
    {
        return artifact.resource(folderPath(artifact), extension);
    }

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
