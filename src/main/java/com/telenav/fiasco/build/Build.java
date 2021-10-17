package com.telenav.fiasco.build;

import com.telenav.fiasco.build.building.Builder;
import com.telenav.fiasco.build.building.builders.ParallelBuilder;
import com.telenav.fiasco.build.planning.BuildPlan;
import com.telenav.fiasco.build.planning.BuildPlanner;
import com.telenav.fiasco.build.project.BaseProject;
import com.telenav.fiasco.dependencies.BaseProjectDependency;
import com.telenav.fiasco.dependencies.DependencyGraph;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.messaging.messages.status.Announcement;

/**
 * Base class for user builds.
 *
 * <p>
 * The {@link #build()} method builds the set of {@link BuildableSet} added by {@link #project(String)} ({@link
 * BaseProject}s are {@link Buildable}). As the build proceeds the {@link BuildListener} specified by {@link
 * #listener(BuildListener)} is called with {@link BuildResult}s. If no build listener is specified, the default
 * listener broadcasts {@link Announcement} messages for each build that completes.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Buildable
 * @see BuildListener
 * @see BuildResult
 * @see BaseProject
 */
public abstract class Build extends BaseProjectDependency implements Buildable, Named, Initializable
{
    /** The build listener to call when the build step changes */
    private BuildListener listener;

    public Build()
    {
        listener = result -> announce("Build completed: $", result);
    }

    /**
     * Creates a {@link BuildPlan} for the buildables that were added with {@link #project(String)}, then executes the
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
