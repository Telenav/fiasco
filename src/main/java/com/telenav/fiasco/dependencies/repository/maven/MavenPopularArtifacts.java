package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.maven.artifacts.Apache;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.Telenav;

/**
 * Commonly used artifacts by owning organization, for autocompletion in IDEs.
 *
 * @author jonathanl (shibo)
 */
public interface MavenPopularArtifacts
{
    /**
     * @return The Apache Software Foundation
     */
    default Apache apache()
    {
        return new Apache();
    }

    /**
     * @return Telenav, inc.
     */
    default Telenav telenav()
    {
        return new Telenav();
    }
}
