package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.ArtifactGroup;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.version.Version;

/**
 * A group of artifacts, optionally of a given version
 *
 * @author jonathanl (shibo)
 */
public class MavenGroup implements Named, ArtifactGroup
{
    public static MavenGroup create(String name)
    {
        return new MavenGroup(name);
    }

    private final String name;

    private Version version;

    protected MavenGroup(MavenGroup that)
    {
        this.name = that.name;
        this.version = that.version;
    }

    protected MavenGroup(final String name)
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

    public MavenGroup version(final String defaultVersion)
    {
        var copy = new MavenGroup(this);
        copy.version = Version.parse(defaultVersion);
        return copy;
    }
}
