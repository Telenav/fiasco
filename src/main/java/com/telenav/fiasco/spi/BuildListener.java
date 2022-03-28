package com.telenav.fiasco.spi;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A build performs a set of actions on a collection of projects and other builds to produce a set of artifacts.
 * </p>
 *
 * @author jonathanl (shibo)
 */
@FunctionalInterface
public interface BuildListener
{
    /**
     * <b>Not public API</b>
     *
     * <p>
     * Called with the build result for each {@link Buildable} as it is built.
     * </p>
     *
     * @param result The build result
     */
    void onBuildCompleted(BuildResult result);
}
