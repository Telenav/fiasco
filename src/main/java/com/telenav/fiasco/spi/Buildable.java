package com.telenav.fiasco.spi;

import com.telenav.fiasco.runtime.Dependency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

/**
 * <b>Not public API</b>
 *
 * <p>
 * An object that can be built by calling {@link #build()}.
 * </p>
 *
 * <p>
 * By implementing the {@link Callable} interface, buildables can consumed by the Java executor framework's {@link
 * CompletionService}.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Dependency
 * @see BuildResult
 * @see CompletionService
 */
public interface Buildable extends Dependency, Callable<BuildResult>
{
    /**
     * <b>Not public API</b>
     *
     * <p>
     * Builds this buildable object
     * </p>
     *
     * @return The result of building this object
     */
    BuildResult build();

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Allows this buildable object to be submitted to a {@link CompletionService}
     * </p>
     *
     * @return The result of building this object
     */
    @Override
    default BuildResult call()
    {
        return build();
    }
}
