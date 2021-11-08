package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.fiasco.runtime.BuildResult;
import com.telenav.kivakit.component.Component;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A builder builds sets of {@link Buildable}s, calling a {@link BuildListener} with each {@link BuildResult} as the
 * build proceeds. Note that builds may be executed in parallel, and may complete out of order.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public interface Builder extends Component
{
    /**
     * Builds the given {@link BuildableGroup}, calling the {@link BuildListener} with each {@link BuildResult} as the
     * build proceeds.
     */
    void build(BuildListener listener, BuildableGroup buildables);
}
