package com.telenav.fiasco.plugins.builder;

/**
 * A build performs a set of actions on a collection of projects and other builds to produce a set of artifacts.
 *
 * @author jonathanl (shibo)
 */
public interface BuildListener
{
    default void onArchived()
    {
    }

    default void onArchiving()
    {
    }

    default void onBuilding()
    {
    }

    default void onBuilt()
    {
    }

    default void onCompiled()
    {
    }

    default void onCompiling()
    {
    }

    default void onDeployed()
    {
    }

    default void onDeploying()
    {
    }

    default void onInstalled()
    {

    }

    default void onInstalling()
    {
    }

    default void onTested()
    {
    }

    default void onTesting()
    {
    }
}
