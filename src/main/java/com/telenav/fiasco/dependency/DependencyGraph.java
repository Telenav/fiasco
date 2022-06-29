package com.telenav.fiasco.dependency;

import com.telenav.fiasco.Dependency;

import java.util.Collections;

import static com.telenav.kivakit.core.ensure.Ensure.fail;

/**
 * Graph of dependencies created by traversing dependencies from a root. If the dependency graph is cyclic, terminal
 * failure will be reported via validation.
 *
 * @author shibo
 */
@SuppressWarnings("unused")
public class DependencyGraph<T extends Dependency<T>>
{
    /**
     * @return The dependency graph formed by traversing dependencies starting at the given root
     */
    public static <T extends Dependency<T>> DependencyGraph<T> of(final T root)
    {
        return new DependencyGraph<>(root);
    }

    /** The root of this dependency graph */
    private final Dependency<T> root;

    /** The dependencies of this graph in depth-first-order */
    private final DependencyList<T> depthFirst;

    private DependencyGraph(final T root)
    {
        this.root = root;
        depthFirst = depthFirst(root);
    }

    /**
     * @return The dependencies in this graph in depth-first order
     */
    public DependencyList<T> depthFirst()
    {
        return depthFirst;
    }

    /**
     * @return The root node of this dependency graph
     */
    public Dependency<T> root()
    {
        return root;
    }

    /**
     * @return List of dependencies in depth-first order
     */
    private DependencyList<T> depthFirst(final T root)
    {
        final DependencyList<T> explored = new DependencyList<>();

        // Go through each child of the root
        for (final T child : root.dependencies())
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
