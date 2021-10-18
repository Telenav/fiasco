package com.telenav.fiasco.build;

import com.telenav.fiasco.dependencies.Dependency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

/**
 * An object that can be built by calling {@link #build()}
 *
 * @author jonathanl (shibo)
 */
public interface Buildable extends Dependency, Callable<BuildResult>
{
    /**
     * Builds this object
     *
     * @return The result of building this object
     */
    BuildResult build();

    /**
     * Allows this buildable object to be submitted to a {@link CompletionService}
     *
     * @return The result of building this object
     */
    @Override
    default BuildResult call()
    {
        return build();
    }
}
