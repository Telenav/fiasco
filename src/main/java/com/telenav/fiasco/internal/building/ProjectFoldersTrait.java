package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.filesystem.Folder;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Locations of resources within a project
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Folder
 * @see Build
 */
public interface ProjectFoldersTrait
{
    /**
     * @return Location of Java source code tree
     */
    default Folder javaSourceFolder()
    {
        return mainSourceFolder().folder("java");
    }

    /**
     * Location of main source code, as opposed to test code
     */
    default Folder mainSourceFolder()
    {
        return sourceFolder().folder("main");
    }

    /**
     * @return The project root folder
     */
    Folder projectRootFolder();

    /**
     * Sets the root of the project
     *
     * @param root The project root folder
     */
    Build projectRootFolder(Folder root);

    /**
     * @return The source folder
     */
    default Folder sourceFolder()
    {
        return projectRootFolder().folder("src");
    }

    /**
     * @return The output folder for build artifacts
     */
    default Folder targetFolder()
    {
        return projectRootFolder().folder("target");
    }

    /**
     * @return The folder containing test sources
     */
    default Folder testSourceFolder()
    {
        return sourceFolder().folder("test");
    }
}
