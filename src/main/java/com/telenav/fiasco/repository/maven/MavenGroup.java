package com.telenav.fiasco.repository.maven;

import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.version.Version;

/**
 * @author jonathanl (shibo)
 */
public class MavenGroup implements Named
{
    public static MavenGroup create(String name)
    {
        return new MavenGroup(name);
    }

    private final String name;

    private Version version;

    public MavenGroup(MavenGroup that)
    {
        this.name = that.name;
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
        version = Version.parse(defaultVersion);
        return this;
    }
}
