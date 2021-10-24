package com.telenav.fiasco.build.dependencies.repository;

import com.telenav.kivakit.resource.path.FilePath;

/**
 * An artifact group, for example "com.telenav.kivakit"
 *
 * @author jonathanl (shibo)
 */
public interface ArtifactGroup
{
    /**
     * @return The identifier of this artifact group
     */
    String identifier();

    /**
     * @return Path to this group relative to a repository root
     */
    FilePath path();
}
