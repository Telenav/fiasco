package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.telenav;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactGroup;
import com.telenav.kivakit.kernel.language.values.version.Version;

public class Lexakai extends MavenArtifactGroup
{
    public Lexakai()
    {
        super("com.telenav.lexakai");
    }

    public Artifact annotations()
    {
        return artifact("lexakai-annotations");
    }

    @Override
    public Lexakai defaultVersion(Version defaultVersion)
    {
        return (Lexakai) super.defaultVersion(defaultVersion);
    }
}
