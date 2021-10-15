package com.telenav.fiasco.repository.maven;

import com.telenav.fiasco.repository.maven.artifacts.apache.Apache;
import com.telenav.fiasco.repository.maven.artifacts.telenav.Telenav;

public interface MavenCommonArtifacts
{
    default Apache apache()
    {
        return new Apache();
    }

    default Telenav telenav()
    {
        return new Telenav();
    }
}
