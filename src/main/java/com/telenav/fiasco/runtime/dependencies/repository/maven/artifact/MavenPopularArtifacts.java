package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact;

import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.Apache;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.Telenav;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.core.language.values.version.Version;

/**
 * Commonly used artifacts by owning organization, for autocompletion in IDEs.
 *
 * @author jonathanl (shibo)
 */
public interface MavenPopularArtifacts extends Component
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

    default Version version(String version)
    {
        return Version.parse(this, version);
    }
}
