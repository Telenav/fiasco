package com.telenav.fiasco.build;

/**
 * A build performs a set of actions on a collection of projects and other builds to produce a set of artifacts.
 *
 * @author jonathanl (shibo)
 */
@FunctionalInterface
public interface BuildListener
{
    /**
     * Called with the build result for each {@link Buildable} as it is built.
     *
     * @param result The build result
     */
    void onBuildResult(BuildResult result);

    /**
     * Called at each step in the build lifecycle
     *
     * @param step The build step
     */
    default void onBuildStep(BuildStep step)
    {
    }
}
