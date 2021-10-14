package com.telenav.fiasco.plugins.builder;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;

import java.util.*;

/**
 * A build performs a set of actions on a collection of projects and other builds to produce a set of artifacts.
 *
 * @author jonathanl (shibo)
 */
public class Builder extends Plugin
{
    private final List<BuildListener> listeners = new ArrayList<>();

    public Builder(final Module module)
    {
        super(module);
    }

    public void archive()
    {
        listeners.forEach(BuildListener::onArchiving);
        module().archiver().run();
        listeners.forEach(BuildListener::onArchived);
    }

    public void compile()
    {
        listeners.forEach(BuildListener::onCompiling);
        module().compiler().run();
        listeners.forEach(BuildListener::onCompiled);
    }

    public void deploy()
    {
        listeners.forEach(BuildListener::onDeploying);
        module().librarian().deploy(null).run();
        listeners.forEach(BuildListener::onDeployed);
    }

    public void install()
    {
        listeners.forEach(BuildListener::onInstalling);
        module().librarian().install(null);
        listeners.forEach(BuildListener::onInstalled);
    }

    public void listenTo(final BuildListener listener)
    {
        listeners.add(listener);
    }

    public void onCompiled(final Runnable code)
    {
        listenTo(new BuildListener()
        {
            @Override
            public void onCompiled()
            {
                code.run();
            }
        });
    }

    public void onCompiling(final Runnable code)
    {
        listenTo(new BuildListener()
        {
            @Override
            public void onCompiling()
            {
                code.run();
            }
        });
    }

    @Override
    public void onRun()
    {
        listeners.forEach(BuildListener::onBuilding);
        onBuild();
        listeners.forEach(BuildListener::onBuilt);
    }

    public void test()
    {
        listeners.forEach(BuildListener::onTesting);
        module().tester().run();
        listeners.forEach(BuildListener::onTested);
    }

    protected void onBuild()
    {
        compile();
        test();
        archive();
        install();
        deploy();
    }
}
