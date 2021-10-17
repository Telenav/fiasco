package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.ArtifactGroup;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * A group of artifacts, optionally of a given version
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifactGroup implements Named, ArtifactGroup
{
    public static MavenArtifactGroup create(String name)
    {
        return new MavenArtifactGroup(name);
    }

    private final String name;

    private Version version;

    protected MavenArtifactGroup(MavenArtifactGroup that)
    {
        this.name = that.name;
        this.version = that.version;
    }

    protected MavenArtifactGroup(final String name)
    {
        this.name = name;
    }

    public MavenArtifact artifact(String identifier)
    {
        return new MavenArtifact(this, identifier).withVersion(version);
    }

    @Override
    public String name()
    {
        return name;
    }

    public FilePath path()
    {
        return FilePath.parseFilePath(name().replace(".", "/"));
    }

    public MavenArtifactGroup version(final String defaultVersion)
    {
        var copy = new MavenArtifactGroup(this);
        copy.version = Version.parse(defaultVersion);
        return copy;
    }
}
