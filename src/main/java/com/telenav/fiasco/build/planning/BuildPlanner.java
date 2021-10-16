package com.telenav.fiasco.build.planning;

import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.dependencies.graph.DependencyGraph;

/**
 * Plans a build by determining the build order and which {@link Buildable}s can be built in parallel
 *
 * @author jonathanl (shibo)
 */
public class BuildPlanner
{
    public BuildPlan plan(DependencyGraph graph)
    {
        var plan = new BuildPlan();

        return plan;
    }
}
