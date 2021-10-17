package com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.maven.MavenArtifactGroup;

public class Lexakai extends MavenArtifactGroup
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
