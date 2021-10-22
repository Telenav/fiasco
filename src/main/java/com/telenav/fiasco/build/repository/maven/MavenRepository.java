package com.telenav.fiasco.build.repository.maven;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.ArtifactRepository;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.progress.ProgressReporter;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.CopyMode;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * A maven repository containing {@link MavenArtifact}s, organized in {@link MavenArtifactGroup}s. A {@link
 * MavenRepository} can be created with {@link #create(String)}, passing in the name of the repository. The root of the
 * repository can then be added with {@link #withRoot(FilePath)}. The {@link #local(Listener)} method returns the local
 * Maven repository. The {@link #mavenCentral(Listener)} method returns the Maven Central repository.
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
    public static MavenRepository create(String name)
    {
        return new MavenRepository(name);
    }

    /**
     * @return The local maven repository at ~/.m2/repository
     */
    public static MavenRepository local(Listener listener)
    {
        return create("local")
                .withRoot(FilePath.parseFilePath("${user.home}/.m2/repository"));
    }

    /**
     * @return The Maven Central repository
     */
    public static MavenRepository mavenCentral(Listener listener)
    {
        return create("Maven Central")
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
        return Resource.resolve(path(artifact)).exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void install(ArtifactRepository source, final Artifact artifact)
    {
        // Get the artifact path in this repository,
        var artifactPath = path(artifact);

        // and then for each resource in the set of resources to be copied,
        for (var resource : resourcesToCopy(artifactPath, artifact))
        {
            //  if the resource exists,
            if (resource.exists())
            {
                // copy the artifact into this repository,
                var destination = Folder.of(path(artifact));
                resource.safeCopyTo(destination, CopyMode.OVERWRITE, ProgressReporter.NULL);
            }
            else
            {
                // otherwise, complain.
                problem("Cannot find artifact resource: $", resource);
            }
        }
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
        return root.withChild(artifact.path());
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

    private ObjectList<Resource> resourcesToCopy(final FilePath path, final Artifact artifact)
    {
        return ObjectList.objectList(
                path.withChild(artifact.identifier() + ".jar"),
                path.withChild(artifact.identifier() + ".jar.md5"),
                path.withChild(artifact.identifier() + ".jar.sha1"),
                path.withChild(artifact.identifier() + ".pom"),
                path.withChild(artifact.identifier() + ".pom.md5"),
                path.withChild(artifact.identifier() + ".pom.sha1")
        ).mapped(Resource::resolve);
    }
}
