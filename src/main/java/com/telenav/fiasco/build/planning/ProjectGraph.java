package com.telenav.fiasco.build.planning;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.BuildableGroup;
import com.telenav.fiasco.build.project.Project;
import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.kivakit.kernel.data.validation.ensure.Ensure;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * Processes the graph of projects from the root {@link Build}.
 *
 * @author shibo
 */
public class ProjectGraph
{
    /**
     * @return The dependency graph formed by traversing dependencies starting at the given root
     */
    public static ProjectGraph of(final Build build)
    {
        return new ProjectGraph(build);
    }

    /**
     * Called with each {@link BuildableGroup} of leaves in the graph
     */
    public interface LeavesVisitor
    {
        void at(BuildableGroup leaves);
    }

    /** The root of this project graph */
    private final Build build;

    private ProjectGraph(final Build build)
    {
        this.build = build;
    }

    /**
     * Visits the group of <i>unvisited</i> projects that form the leaves of the build. Then marks those projects as
     * visited and repeats the process until only the build node is left.
     *
     * @param visitor The visitor to call with leaf groups
     */
    public void visitLeafGroups(LeavesVisitor visitor)
    {
        var visitedLeaves = new HashSet<Dependency>();

        while (true)
        {
            var group = BuildableGroup.create();

            depthFirst(build, new HashSet<>(), at ->
            {
                if (isLeaf(at, visitedLeaves) && at != build)
                {
                    group.add(at);
                }
            });

            if (group.isEmpty())
            {
                break;
            }

            visitor.at(group);
        }
    }

    /**
     * Calls the visitor with the dependencies of the given node in depth-first order. If a circular dependency is
     * detected, {@link Ensure#fail()} is called.
     *
     * @param at The node to traverse from
     * @param visited The set of nodes already visited
     * @param visitor THe visitor to call with nodes
     */
    private void depthFirst(final Buildable at, Set<Buildable> visited, Consumer<Buildable> visitor)
    {
        // If we already visited this node,
        if (visited.contains(at))
        {
            // we have a circular dependency.
            fail("Circular dependency detected at: $", at);
        }

        // Go through each child dependency,
        for (final var child : at.dependencies())
        {
            // and if it's a project,
            if (child instanceof Project)
            {
                // explore it (in a depth-first traversal)
                depthFirst((Project) child, visited, visitor);
            }
        }

        // Then visit the node.
        visitor.accept(at);
        visited.add(at);
    }

    /**
     * @return True if the given node doesn't have an unvisited child
     */
    private boolean isLeaf(Buildable node, Set<Dependency> visited)
    {
        boolean hasChild = false;

        for (var at : node.dependencies())
        {
            if (!visited.contains(at))
            {
                hasChild = true;
            }
        }

        return !hasChild;
    }
}
