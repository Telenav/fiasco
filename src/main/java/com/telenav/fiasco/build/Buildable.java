package com.telenav.fiasco.build;

import com.telenav.kivakit.filesystem.Folder;

import java.util.concurrent.Callable;

/**
 * An object that can be built by calling {@link #build()}
 *
 * @author jonathanl (shibo)
 */
public interface Buildable extends Callable<BuildResult>
{
    /**
     * Specifies the build for this buildable
     */
    void build(Build build);

    /**
     * Builds this object
     *
     * @return The result of building this object
     */
    BuildResult build();

    @Override
    default BuildResult call()
    {
        return build();
    }

    default Folder workspace()
    {
        return Folder.parse("${WORKSPACE}");
    }
}
