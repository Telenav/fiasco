package com.telenav.fiasco.repository.maven.artifacts.apache;

import com.telenav.fiasco.repository.Artifact;
import com.telenav.fiasco.repository.maven.MavenGroup;

public class ApacheWicket extends MavenGroup
{
    public ApacheWicket()
    {
        super("org.apache.wicket");
    }

    public Artifact core()
    {
        return artifact("wicket-core");
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

    public ApacheWicket version(final String defaultVersion)
    {
        super.version(defaultVersion);
        return this;
    }
}
