package com.telenav.fiasco.internal.building.planning;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.dependencies.DependencyGraph;
import com.telenav.fiasco.runtime.Build;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Plans a build by determining the build order and which {@link Buildable}s can be built in parallel
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class BuildPlanner
{
    /**
     * <b>Not public API</b>
     *
     * <p>
     * Creates a build plan from the {@link Build} dependency graph.
     * </p>
     *
     * <p><b>The Algorithm</b></p>
     *
     * <ol>
     *     <li>The <i>unvisited</i> leaves of the project dependency graph have no dependencies (that have not already
     *     been visited), so they are added to the build plan in a {@link BuildableGroup} (to be built in parallel)</li>
     *     <li>These leaves are then marked as visited</li>
     *     <li>If there are more <i>unvisited</i> leaves left to process, go to Step 1</li>
     * </ol>
     *
     * @see Build
     * @see Buildable
     * @see BuildableGroup
     * @see BuildPlan
     */
    public BuildPlan plan(Build build)
    {
        return DependencyGraph.of(build).planBuild();
    }
}
