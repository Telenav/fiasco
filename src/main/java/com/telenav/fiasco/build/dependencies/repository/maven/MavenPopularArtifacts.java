package com.telenav.fiasco.build.dependencies.repository.maven;

import com.telenav.fiasco.build.dependencies.repository.maven.artifact.popular.Apache;
import com.telenav.fiasco.build.dependencies.repository.maven.artifact.popular.Telenav;

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
