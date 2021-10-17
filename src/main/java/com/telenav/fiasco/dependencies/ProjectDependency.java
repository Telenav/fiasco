package com.telenav.fiasco.dependencies;

import com.telenav.kivakit.filesystem.Folder;

public interface ProjectDependency extends Dependency
{
    /**
     * Builds and adds project in the given folder
     *
     * @param folder The project build folder
     * @return The project
     */
    void project(Folder folder);

    /**
     * Builds and adds the build classes for the given path
     *
     * @param path A path relative to the {@link #workspace()}
     * @return The project
     */
    default void project(String path)
    {
        project(workspace().folder(path));
    }

    /**
     * @return The workspace
     */
    default Folder workspace()
    {
        return Folder.parse("${WORKSPACE}");
    }
}
