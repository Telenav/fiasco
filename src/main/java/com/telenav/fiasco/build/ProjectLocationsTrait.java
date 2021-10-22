package com.telenav.fiasco.build;

import com.telenav.kivakit.filesystem.Folder;

/**
 * Locations of resources within a project
 *
 * @author jonathanl (shibo)
 */
public interface ProjectLocationsTrait
{
    /**
     * @return Location of Java source code tree
     */
    default Folder javaSources()
    {
        return mainSources().folder("java");
    }

    /**
     * Location of main source code, as opposed to test code
     */
    default Folder mainSources()
    {
        return sources().folder("main");
    }

    /**
     * @return The output folder for build artifacts
     */
    default Folder output()
    {
        return projectRoot().folder("target");
    }

    /**
     * @return The project root folder
     */
    Folder projectRoot();

    /**
     * @param root The project root folder
     */
    FiascoBuild projectRoot(final Folder root);

    /**
     * @return The source folder
     */
    default Folder sources()
    {
        return projectRoot().folder("src");
    }

    /**
     * @return The folder containing test sources
     */
    default Folder testSources()
    {
        return sources().folder("test");
    }
}
