package com.telenav.fiasco.build.repository.maven.artifact.popular.telenav;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.maven.MavenArtifactGroup;

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

    public KivaKit defaultVersion(final String defaultVersion)
    {
        super.defaultVersion(defaultVersion);
        return this;
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