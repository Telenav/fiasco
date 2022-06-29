package com.telenav.fiasco.repository.artifact;

import com.telenav.kivakit.interfaces.comparison.Matcher;

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
