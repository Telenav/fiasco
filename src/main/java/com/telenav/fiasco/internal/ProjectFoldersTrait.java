package com.telenav.fiasco.internal;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.kivakit.filesystem.Folder;

/**
 * Locations of resources within a project
 *
 * @author jonathanl (shibo)
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
     * @param root The project root folder
     */
    FiascoBuild projectRootFolder(final Folder root);

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
