package com.telenav.fiasco;

import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.language.objects.Lazy;
import com.telenav.kivakit.kernel.project.Project;
import com.telenav.kivakit.resource.ResourceProject;

/**
 * KivaKit {@link Project} for Fiasco
 *
 * @author jonathanl (shibo)
 */
public class FiascoProject extends Project
{
    private static final Lazy<FiascoProject> project = Lazy.of(FiascoProject::new);

    public static FiascoProject get()
    {
        return project.get();
    }

    @Override
    public ObjectSet<Project> dependencies()
    {
        return ObjectSet.objectSet(ResourceProject.get());
    }
}
