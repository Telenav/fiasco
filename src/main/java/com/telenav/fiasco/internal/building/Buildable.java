package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.build.Dependency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

/**
 * <b>Not public API</b>
 *
 * <p>
 * An object that can be built by calling {@link #executeBuild()}. Implementing the {@link Callable} interface allows
 * the buildable to be used in the Java executor framework.
 * </p>
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
