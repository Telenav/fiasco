package com.telenav.fiasco.dependencies;

import com.telenav.kivakit.kernel.interfaces.naming.Named;

/**
 * A dependency has a list of {@link #dependencies()}, which must be resolved for it to function. A graph of
 * dependencies in depth-first order, where the leaves first and the root is last, can be created with {@link #graph()}.
 * This structure of a dependency graph is similar to typical project structure where the root module has a set of child
 * modules that must be built and those projects have their own child projects, etc.
 *
 * @author shibo
 */
public interface Dependency extends Named
{
    /**
     * @return The objects that this depends on
     */
    DependencyList dependencies();

    /**
     * @return A dependency graph with this dependency at the root
     */
    default DependencyGraph graph()
    {
        return DependencyGraph.of(this);
    }
}
