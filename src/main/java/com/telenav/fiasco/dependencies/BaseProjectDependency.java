package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.project.Project;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.filesystem.Folder;

/**
 * Base class for dependencies that can have {@link Project}s as dependencies. Not all dependencies can have dependent
 * projects. For example, {@link Artifact}s cannot have {@link Project} dependencies (because they are already built).
 *
 * @author jonathanl (shibo)
 */
public abstract class BaseProjectDependency extends BaseDependency implements ProjectDependency
{
    /**
     * Builds the classes in the <i>fiasco</i> folder under the given root, then loads classes until one is loaded that
     * implements the {@link Project} interface. This class is instantiated and the resulting object is added to the set
     * of {@link #dependencies()}.
     *
     * @param folder The project root folder
     */
    public void project(final Folder folder)
    {
        // TODO shibo
        dependencies().add(null);
    }
}
