package com.telenav.fiasco.build.repository.maven.artifact.popular.apache;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.maven.MavenArtifactGroup;

public class ApacheWicket extends MavenArtifactGroup
{
    public ApacheWicket()
    {
        super("org.apache.wicket");
    }

    public Artifact core()
    {
        return artifact("wicket-core");
    }

    public ApacheWicket defaultVersion(final String defaultVersion)
    {
        super.defaultVersion(defaultVersion);
        return this;
    }

    public Artifact extensions()
    {
        return artifact("wicket-extensions");
    }

    public Artifact request()
    {
        return artifact("wicket-request");
    }

    public Artifact util()
    {
        return artifact("wicket-util");
    }
}
