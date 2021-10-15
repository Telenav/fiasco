package com.telenav.fiasco.build;

/**
 * A build performs a set of actions on a collection of projects and other builds to produce a set of artifacts.
 *
 * @author jonathanl (shibo)
 */
public interface BuildListener
{
    default void onStep(Build.Step step)
    {
    }
}
