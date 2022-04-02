package com.telenav.fiasco;

import com.telenav.fiasco.internal.fiasco.Project;
import com.telenav.fiasco.runtime.DependencySet;
import com.telenav.kivakit.filesystem.Folder;

/**
 * KivaKit {@link Project} definition for Fiasco
 *
 * @author jonathanl (shibo)
 */
public class FiascoProject extends Project
{
    /**
     * <b>Not public API</b>
     *
     * @param root The root folder of this project
     */
    public FiascoProject(final Folder root)
    {
        super(root);
    }

    @Override
    public DependencySet dependencies()
    {
        return DependencySet.of();
    }
}
