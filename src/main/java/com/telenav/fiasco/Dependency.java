package com.telenav.fiasco;

import com.telenav.fiasco.dependency.DependencyGraph;
import com.telenav.fiasco.dependency.DependencyList;
import com.telenav.kivakit.interfaces.naming.Named;

/**
 * A dependency has a list of {@link #dependencies()}, which must be resolved for it to function. A graph of
 * dependencies in depth-first order, where the leaves first and the root is last, can be created with {@link #graph()}.
 * This structure of a dependency graph is similar to typical project structure where the root module has a set of child
 * modules that must be built and those projects have their own child projects, etc.
 *
 * @author shibo
 */
@SuppressWarnings("unused")
public interface Dependency<T extends Dependency<T>> extends Named
{
    /**
     * @return The objects that this depends on
     */
    DependencyList<T> dependencies();

    /**
     * @return A dependency graph with this dependency at the root
     */
    @SuppressWarnings("unchecked")
    default DependencyGraph<T> graph()
    {
        return DependencyGraph.of((T) this);
    }
}
