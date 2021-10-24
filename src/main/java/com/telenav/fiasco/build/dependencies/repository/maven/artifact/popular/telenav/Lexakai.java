package com.telenav.fiasco.build.dependencies.repository.maven.artifact.popular.telenav;

import com.telenav.fiasco.build.dependencies.repository.Artifact;
import com.telenav.fiasco.build.dependencies.repository.maven.MavenArtifactGroup;

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

    public Lexakai withDefaultVersion(final String defaultVersion)
    {
        return (Lexakai) super.withDefaultVersion(defaultVersion);
    }
}
