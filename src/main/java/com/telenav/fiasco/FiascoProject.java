package com.telenav.fiasco;

import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.project.Project;
import com.telenav.kivakit.resource.ResourceProject;

/**
 * KivaKit {@link Project} definition for Fiasco
 *
 * @author jonathanl (shibo)
 */
public class FiascoProject extends Project
{
    @Override
    public ObjectSet<Project> dependencies()
    {
        return ObjectSet.objectSet(ResourceProject.get());
    }
}
