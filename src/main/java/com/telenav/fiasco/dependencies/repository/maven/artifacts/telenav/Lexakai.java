package com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.maven.MavenGroup;

public class Lexakai extends MavenGroup
{
    public Lexakai()
    {
        super("com.telenav.lexakai");
    }

    public Artifact annotations()
    {
        return artifact("lexakai-annotations");
    }

    public Lexakai version(final String defaultVersion)
    {
        super.version(defaultVersion);
        return this;
    }
}
