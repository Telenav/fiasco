package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.Apache;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav.Telenav;

/**
 * Commonly used artifacts by owning organization, for autocompletion in IDEs.
 *
 * @author jonathanl (shibo)
 */
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
