package com.telenav.fiasco.repository.maven;

import com.telenav.kivakit.kernel.language.values.name.Name;

/**
 * @author jonathanl (shibo)
 */
public class MavenGroup extends Name
{
    public MavenGroup(final String name)
    {
        super(name);
    }

    public MavenArtifact artifact(final MavenArtifact.Identifier identifier)
    {
        return new MavenArtifact(this, identifier);
    }
}
