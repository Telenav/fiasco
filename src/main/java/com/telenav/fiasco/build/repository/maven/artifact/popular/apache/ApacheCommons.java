package com.telenav.fiasco.build.repository.maven.artifact.popular.apache;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.maven.MavenArtifactGroup;

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

    public ApacheCommons defaultVersion(final String defaultVersion)
    {
        super.defaultVersion(defaultVersion);
        return this;
    }

    public Artifact lang3()
    {
        return artifact("commons-lang3");
    }

    public Artifact math()
    {
        return artifact("commons-math");
    }
}
