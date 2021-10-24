package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.Dependency;
import com.telenav.fiasco.build.Library;
import com.telenav.kivakit.filesystem.Folder;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A {@link Build} project dependency that must be built (as opposed to an {@link Library} dependency which is already
 * built.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public interface DependentProject extends Dependency
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
