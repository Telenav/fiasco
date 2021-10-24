package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactGroup;

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

    public ApacheCommons withDefaultVersion(final String defaultVersion)
    {
        return (ApacheCommons) super.withDefaultVersion(defaultVersion);
    }
}
