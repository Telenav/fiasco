package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.telenav;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactGroup;
import com.telenav.kivakit.kernel.language.values.version.Version;

public class KivaKit extends MavenArtifactGroup
{
    public KivaKit()
    {
        super("com.telenav.kivakit");
    }

    public Artifact application()
    {
        return artifact("kivakit-application");
    }

    public Artifact collections()
    {
        return artifact("kivakit-collections");
    }

    public Artifact commandLine()
    {
        return artifact("kivakit-commandline");
    }

    public Artifact component()
    {
        return artifact("kivakit-component");
    }

    public Artifact configuration()
    {
        return artifact("kivakit-configuration");
    }

    @Override
    public KivaKit defaultVersion(final Version defaultVersion)
    {
        return (KivaKit) super.defaultVersion(defaultVersion);
    }

    public Artifact kernel()
    {
        return artifact("kivakit-kernel");
    }

    public Artifact kivakit()
    {
        return artifact("kivakit");
    }

    public Artifact networkCore()
    {
        return artifact("kivakit-network-core");
    }

    public Artifact networkEmail()
    {
        return artifact("kivakit-network-http");
    }

    public Artifact networkFtp()
    {
        return artifact("kivakit-network-http");
    }

    public Artifact networkHttp()
    {
        return artifact("kivakit-network-http");
    }

    public Artifact networkSocket()
    {
        return artifact("kivakit-network-socket");
    }

    public Artifact resource()
    {
        return artifact("kivakit-resource");
    }
}
