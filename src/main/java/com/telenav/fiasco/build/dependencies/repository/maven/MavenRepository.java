package com.telenav.fiasco.build.dependencies.repository.maven;

import com.telenav.fiasco.build.dependencies.repository.Artifact;
import com.telenav.fiasco.build.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.data.formats.xml.stax.StaxReader;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.FilePath;

import static com.telenav.kivakit.kernel.language.progress.ProgressReporter.NULL;
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
 * #path(Artifact)} method returns the full path to the given artifact within this repository. The {@link
 * #install(ArtifactRepository, Artifact)} method copies the given artifact from the given repository into this
 * repository if it is not already there.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class MavenRepository extends BaseComponent implements ArtifactRepository
{
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
        return create(listener, "Local Repository")
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
        return Resource.resolve(this, path(artifact)
                .withChild(artifact.identifier() + ".jar")).exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean install(ArtifactRepository source, final Artifact artifact)
    {
        try
        {
            // Get the artifact path in this repository,
            var artifactFolder = source.path(artifact);

            // and then for each resource in the set of resources to be copied,
            for (var resource : artifact.resources(artifactFolder))
            {
                // copy the artifact into this repository,
                resource.safeCopyTo(Folder.of(path(artifact)).mkdirs(), OVERWRITE, NULL);
            }

            // then get the POM resource for the artifact,
            var pomResource = artifact.resource(artifactFolder, POM);

            // open it with a STAX reader,
            try (var reader = StaxReader.open(pomResource))
            {
                // and return the parsed POM information.
                new PomReader(reader).read().dependencies().forEach(at -> install(source, at));
            }
        }
        catch (Exception e)
        {
            problem(e, "Unable to install $ $ => $", artifact, source, this);
            return false;
        }

        return true;
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
     * @return The full path to the given artifact in this repository
     */
    @Override
    public FilePath path(final Artifact artifact)
    {
        return root.withoutTrailingSlash().withChild(artifact.path());
    }

    public String toString()
    {
        return name() + " (" + root + ")";
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
