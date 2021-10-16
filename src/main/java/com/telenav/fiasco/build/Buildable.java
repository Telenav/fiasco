package com.telenav.fiasco.build;

/**
 * An object that can be built by calling {@link #build(BuildListener)}
 *
 * @author jonathanl (shibo)
 */
public interface Buildable
{
    /**
     * Builds this object, calling the given listener as the build progresses.
     *
     * @param listener The listener to call
     * @return The result of building this object
     */
    BuildResult build(BuildListener listener);
}
