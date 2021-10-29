package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactGroup;
import com.telenav.kivakit.kernel.language.values.version.Version;

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

    @Override
    public ApacheWicket defaultVersion(Version defaultVersion)
    {
        return (ApacheWicket) super.defaultVersion(defaultVersion);
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
