package com.telenav.fiasco.runtime.dependencies.repository.maven;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactGroup;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * A maven repository containing {@link MavenArtifact}s, organized in {@link MavenArtifactGroup}s. A {@link
 * MavenRepository} can be created with {@link #create(Listener, String)}, passing in the name of the repository. The
 * root of the repository can then be added with {@link #withRoot(FilePath)}. The {@link #local(Listener)} method
 * returns the local Maven repository. The {@link #mavenCentral(Listener)} method returns the Maven Central repository.
 *
 * <p>
 * The {@link #contains(Artifact)} method returns true if this repository contains the given artifact. The {@link
 * #pathTo(Artifact)} method returns the full path to the given artifact within this repository.
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
        return Resource.resolve(this, pathTo(artifact)
                .withChild(artifact.identifier() + "-" + artifact.version() + ".jar")).exists();
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
    public FilePath pathTo(Artifact artifact)
    {
        return root.withoutTrailingSlash().withChild(artifact.path());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource resource(Artifact artifact, Extension extension)
    {
        return artifact.resource(pathTo(artifact), extension);
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
