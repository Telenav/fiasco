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
}
