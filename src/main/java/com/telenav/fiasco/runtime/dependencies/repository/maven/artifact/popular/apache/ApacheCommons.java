package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactGroup;
import com.telenav.kivakit.core.language.values.version.Version;

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

    @Override
    public ApacheCommons defaultVersion(String defaultVersion)
    {
        return (ApacheCommons) super.defaultVersion(defaultVersion);
    }

    @Override
    public ApacheCommons defaultVersion(Version defaultVersion)
    {
        return (ApacheCommons) super.defaultVersion(defaultVersion);
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
