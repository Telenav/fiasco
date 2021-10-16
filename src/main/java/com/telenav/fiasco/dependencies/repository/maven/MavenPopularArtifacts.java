package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.Apache;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav.Telenav;

public interface MavenPopularArtifacts
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
