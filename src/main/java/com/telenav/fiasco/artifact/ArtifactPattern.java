package com.telenav.fiasco.artifact;

import com.telenav.tdk.core.kernel.interfaces.object.Matcher;

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
