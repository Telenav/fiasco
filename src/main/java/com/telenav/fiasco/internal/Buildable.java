package com.telenav.fiasco.internal;

import com.telenav.fiasco.internal.dependencies.Dependency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

/**
 * An object that can be built by calling {@link #executeBuild()}
 *
 * @author jonathanl (shibo)
 */
public interface Buildable extends Dependency, Callable<BuildResult>
{
    /**
     * Allows this buildable object to be submitted to a {@link CompletionService}
     *
     * @return The result of building this object
     */
    @Override
    default BuildResult call()
    {
        return executeBuild();
    }

    /**
     * Builds this object
     *
     * @return The result of building this object
     */
    BuildResult executeBuild();
}
