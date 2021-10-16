package com.telenav.fiasco.dependencies;

import com.telenav.kivakit.kernel.data.validation.ensure.Ensure;

import java.util.Collections;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * Graph of dependencies created by traversing dependencies from a root. If the dependency graph is cyclic, terminal
 * failure will be reported via {@link Ensure#fail()}.
 *
 * @author shibo
 */
public class DependencyGraph
{
    /**
     * @return The dependency graph formed by traversing dependencies starting at the given root
     */
    public static DependencyGraph of(final Dependency root)
    {
        return new DependencyGraph(root);
    }

    /** The root of this dependency graph */
    private final Dependency root;

    /** The dependencies of this graph in depth-first-order */
    private final DependencyList depthFirst;

    private DependencyGraph(final Dependency root)
    {
        this.root = root;
        depthFirst = depthFirst(root);
    }

    /**
     * @return The dependencies in this graph in depth-first order
     */
    public DependencyList depthFirst()
    {
        return depthFirst;
    }

    /**
     * @return The root node of this dependency graph
     */
    public Dependency root()
    {
        return root;
    }

    /**
     * @return List of dependencies in depth-first order
     */
    private DependencyList depthFirst(final Dependency root)
    {
        final DependencyList explored = new DependencyList();

        // Go through each child of the root
        for (final var child : root.dependencies())
        {
            // and explore it (in a depth-first traversal)
            final var descendants = depthFirst(child);

            // and if none of the explored values has already been explored
            if (Collections.disjoint(explored, descendants))
            {
                // then add the explored descendants to the list of dependencies
                explored.addAll(descendants);
            }
            else
            {
                // otherwise, if the explored list intersects the descendants, there is a cyclic dependency graph.
                fail("The dependency graph '$' is cyclic.", root);
            }
        }

        // Finally, add the root to the list
        explored.add(root);

        return explored;
    }
}
