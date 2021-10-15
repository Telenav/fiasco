package com.telenav.fiasco.repository.maven.artifacts.apache;

import com.telenav.fiasco.repository.Artifact;
import com.telenav.fiasco.repository.maven.MavenGroup;

public class ApacheHttpComponents extends MavenGroup
{
    public ApacheHttpComponents()
    {
        super("org.apache.httpcomponents");
    }

    public Artifact httpClient()
    {
        return artifact("httpclient");
    }

    public Artifact httpCore()
    {
        return artifact("httpcore");
    }

    public ApacheHttpComponents version(final String defaultVersion)
    {
        super.version(defaultVersion);
        return this;
    }
}
