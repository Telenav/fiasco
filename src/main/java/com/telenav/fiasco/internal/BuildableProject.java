package com.telenav.fiasco.internal;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.internal.dependencies.Dependency;
import com.telenav.fiasco.internal.dependencies.Library;
import com.telenav.kivakit.filesystem.Folder;

/**
 * A {@link FiascoBuild} project dependency that must be built (as opposed to an {@link Library} dependency which is
 * already built.
 *
 * @author jonathanl (shibo)
 */
public interface BuildableProject extends Dependency
{
    /**
     * Builds and adds project in the given folder
     *
     * @param folder The project build folder
     */
    void project(Folder folder);

    /**
     * Builds and adds the build classes for the given path
     *
     * @param path A path relative to the {@link #workspace()}
     */
    default void project(String path)
    {
        project(workspace().folder(path));
    }

    /**
     * @return The workspace
     */
    Folder workspace();
}
