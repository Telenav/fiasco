package com.telenav.fiasco.dependencies.repository.maven.artifacts.apache;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.maven.MavenArtifactGroup;

public class ApacheCommons extends MavenArtifactGroup
{
    public ApacheCommons()
    {
        super("org.apache.commons");
    }

    public Artifact compress()
    {
        return artifact("commons-compress");
    }

    public Artifact lang3()
    {
        return artifact("commons-lang3");
    }

    public Artifact math()
    {
        return artifact("commons-math");
    }

    public ApacheCommons version(final String defaultVersion)
    {
        super.version(defaultVersion);
        return this;
    }
}
