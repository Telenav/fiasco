package com.telenav.fiasco.artifact;

import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

/**
 * @author jonathanl (shibo)
 */
public class ArtifactPattern implements Matcher<Artifact>
{
    @Override
    public boolean matches(final Artifact artifact)
    {
        return false;
    }
}
