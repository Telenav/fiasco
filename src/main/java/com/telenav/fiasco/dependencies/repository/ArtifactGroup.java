package com.telenav.fiasco.dependencies.repository;

import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * An artifact group, for example "com.telenav.kivakit"
 *
 * @author jonathanl (shibo)
 */
public interface ArtifactGroup extends Named
{
    /**
     * @return Path to this group in a repository
     */
    FilePath path();
}
