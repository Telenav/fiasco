package com.telenav.fiasco.build.planning;

import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.BuildableGroup;
import com.telenav.fiasco.build.FiascoBuild;

/**
 * Plans a build by determining the build order and which {@link Buildable}s can be built in parallel
 *
 * @author jonathanl (shibo)
 */
public class BuildPlanner
{
    /**
     * Creates a build plan from the {@link FiascoBuild} dependency graph.
     *
     * <p><b>The Algorithm</b></p>
     *
     * <ol>
     *     <li>The <i>unvisited</i> leaves of the project dependency graph have no dependencies (that have not already
     *     been visited), so they are added to the build plan in a {@link BuildableGroup} (to be built in parallel)</li>
     *     <li>These leaves are then marked as visited</li>
     *     <li>If there are more <i>unvisited</i> leaves left to process, go to Step 1</li>
     * </ol>
     */
    public BuildPlan plan(FiascoBuild build)
    {
        var plan = new BuildPlan();
        ProjectDependencyGraph.of(build).visitLeafGroups(plan::add);
        return plan;
    }
}
