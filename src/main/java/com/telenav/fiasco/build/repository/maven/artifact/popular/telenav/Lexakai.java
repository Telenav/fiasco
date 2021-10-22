package com.telenav.fiasco.build.repository.maven.artifact.popular.telenav;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.maven.MavenArtifactGroup;

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
        super.defaultVersion(defaultVersion);
        return this;
    }
}