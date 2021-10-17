package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.ArtifactRepository;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.progress.ProgressReporter;
import com.telenav.kivakit.resource.CopyMode;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.List;

/**
 * A maven repository containing {@link MavenArtifact}s, organized in {@link MavenArtifactGroup}s.
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
    public static MavenRepository local()
    {
        return create("local")
                .withRoot(FilePath.parseFilePath("${user.home}/.m2/repository"));
    }

    /**
     * @return The Maven Central repository
     */
    public static MavenRepository mavenCentral()
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

    @Override
    public boolean contains(final Artifact artifact)
    {
        return Resource.resolve(path(artifact)).exists();
    }

    @Override
    public void install(ArtifactRepository source, final Artifact artifact)
    {
        // get the .jar, .pom and maven-metadata.xml paths,
        var path = path(artifact);
        for (var file : files(path, artifact))
        {
            var resource = Resource.resolve(file);
            if (resource.exists())
            {
                var destination = Folder.of(path(artifact));
                resource.safeCopyTo(destination, CopyMode.OVERWRITE, ProgressReporter.NULL);
            }
            else
            {
                problem("Cannot find artifact resource: $", resource);
            }
        }
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public FilePath path(final Artifact artifact)
    {
        return root.withChild(artifact.path());
    }

    public MavenRepository withRoot(FilePath root)
    {
        var copy = new MavenRepository(this);
        copy.root = root;
        return copy;
    }

    private List<FilePath> files(final FilePath path, final Artifact artifact)
    {
        return List.of(
                path.withChild(artifact.name() + ".jar"),
                path.withChild(artifact.name() + ".jar.md5"),
                path.withChild(artifact.name() + ".jar.sha1"),
                path.withChild(artifact.name() + ".pom"),
                path.withChild(artifact.name() + ".pom.md5"),
                path.withChild(artifact.name() + ".pom.sha1")
        );
    }
}
