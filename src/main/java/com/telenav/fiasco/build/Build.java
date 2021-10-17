package com.telenav.fiasco.build;

import com.telenav.fiasco.build.building.Builder;
import com.telenav.fiasco.build.building.builders.ParallelBuilder;
import com.telenav.fiasco.build.planning.BuildPlan;
import com.telenav.fiasco.build.planning.BuildPlanner;
import com.telenav.fiasco.build.project.Project;
import com.telenav.fiasco.dependencies.DependencyGraph;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.messaging.messages.status.Announcement;

/**
 * Base class for user builds.
 *
 * <p>
 * The {@link #build()} method builds the set of {@link Buildables} added by {@link #add(Buildable...)} ({@link
 * Project}s are {@link Buildable}). As the build proceeds the {@link BuildListener} specified by {@link
 * #listener(BuildListener)} is called with {@link BuildResult}s. If no build listener is specified, the default
 * listener broadcasts {@link Announcement} messages for each build that completes.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Buildable
 * @see BuildListener
 * @see BuildResult
 * @see Project
 */
public abstract class Build extends BaseComponent implements Buildable, Named, Initializable
{
    /** Group of {@link Buildable}s to build */
    private final Buildables buildables = Buildables.create();

    /** The build listener to call when the build step changes */
    private BuildListener listener;

    public Build()
    {
        listener = result -> announce("Build completed: $", result);
    }

    public Build add(Buildable... buildables)
    {
        this.buildables.addAll(buildables);

        for (var at : buildables)
        {
            at.build(this);
        }

        return this;
    }

    /**
     * Creates a {@link BuildPlan} for the buildables that were added with {@link #add(Buildable...)}, then executes the
     * plan using the {@link Builder} returned by {@link #builder()}. As the build proceeds the {@link BuildListener} is
     * called when {@link Buildable}s finish building.
     */
    public BuildResult build()
    {
        var result = new BuildResult(name());
        result.start();
        try
        {
            var planner = new BuildPlanner();
            var graph = DependencyGraph.of(null);
            var plan = planner.plan(graph);
            plan.build(builder(), listener);
        }
        finally
        {
            result.end();
        }
        return result;
    }

    @Override
    public void build(final Build build)
    {
    }

    /**
     * @param listener The build listener to call at each new step
     */
    public void listener(BuildListener listener)
    {
        this.listener = listener;
    }

    /**
     * @return The {@link Builder} to use for this build
     */
    protected Builder builder()
    {
        return new ParallelBuilder();
    }
}
