package com.telenav.fiasco.internal.building.planning;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.dependencies.DependencyGraph;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A set of {@link Buildable}s that can be built at the same time.
 * <p>
 *
 * <p>
 * Buildable groups are created with the {@link DependencyGraph#planBuild()} method, according to the algorithm
 * described in that class.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Buildable
 * @see DependencyGraph
 */
public class BuildableGroup extends ObjectList<Buildable>
{
}
