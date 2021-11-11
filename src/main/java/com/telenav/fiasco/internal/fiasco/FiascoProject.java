package com.telenav.fiasco.internal.fiasco;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.messaging.Message;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Represents a project being built by Fiasco.
 * </p>
 *
 * <p><b>Properties</b></p>
 *
 * <ul>
 *     <li>{@link #name()} - The name of this build, derived from the name of the project's root folder</li>
 *     <li>{@link #rootFolder()} - The root folder of the project</li>
 *     <li>{@link #buildSourceFile()} - The src/main/java/fiasco/Fiasco.java build file for this project</li>
 *     <li>{@link #target()} - The target folder for this project in the Fiasco cache. The path to the target folder
 *         is ~/.fiasco/[version]/target/[project-name]</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 * @see Folder
 * @see File
 * @see FiascoCache
 */
public class FiascoProject extends BaseComponent
{
    /** The root folder of this project */
    private final Folder root;

    /**
     * <b>Not public API</b>
     *
     * @param root The root folder of this project
     */
    public FiascoProject(Folder root)
    {
        this.root = root;
    }

    /**
     * <b>Not public API</b>
     *
     * @return The <i>FiascoBuild.java</i> file for this project
     */
    public File buildSourceFile()
    {
        return root.file("src/main/java/fiasco/FiascoBuild.java");
    }

    /**
     * <b>Not public API</b>
     *
     * @return The name of this project, derived from the name of the project's root folder
     */
    public String name()
    {
        return root.name().name();
    }

    /**
     * <b>Not public API</b>
     *
     * @return The root folder of this project
     */
    public Folder rootFolder()
    {
        return root;
    }

    /**
     * <b>Not public API</b>
     *
     * @return The target folder for this project in the Fiasco cache
     */
    public Folder target()
    {
        return require(FiascoCache.class)
                .targetFolder()
                .folder(name());
    }

    @Override
    public String toString()
    {
        return Message.format("\"$\" ($)", name(), rootFolder());
    }
}
