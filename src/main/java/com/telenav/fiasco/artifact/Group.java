package com.telenav.fiasco.artifact;

import com.telenav.tdk.core.kernel.scalars.names.Name;

/**
 * @author jonathanl (shibo)
 */
public class Group extends Name
{
    public Group(final String name)
    {
        super(name);
    }

    public Artifact artifact(final Artifact.Identifier identifier)
    {
        return new Artifact(this, identifier);
    }
}
