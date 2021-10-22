package com.telenav.fiasco.build.repository.maven.artifact.popular.apache;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.maven.MavenArtifactGroup;

public class ApacheHttpComponents extends MavenArtifactGroup
{
    public ApacheHttpComponents()
    {
        super("org.apache.httpcomponents");
    }

    public ApacheHttpComponents defaultVersion(final String defaultVersion)
    {
        super.defaultVersion(defaultVersion);
        return this;
    }

    public Artifact httpClient()
    {
        return artifact("httpclient");
    }

    public Artifact httpCore()
    {
        return artifact("httpcore");
    }
}
