package com.telenav.fiasco.runtime.dependencies.repository;

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
     * @return The relative path to this group relative to any repository root
     */
    FilePath path();
}
