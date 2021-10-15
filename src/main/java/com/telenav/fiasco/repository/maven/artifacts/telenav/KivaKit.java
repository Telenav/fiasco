package com.telenav.fiasco.repository.maven.artifacts.telenav;

import com.telenav.fiasco.repository.Artifact;
import com.telenav.fiasco.repository.maven.MavenGroup;

public class KivaKit extends MavenGroup
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

    public KivaKit version(final String defaultVersion)
    {
        super.version(defaultVersion);
        return this;
    }
}
