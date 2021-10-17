package com.telenav.fiasco.build.building;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildResult;
import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.BuildableSet;
import com.telenav.kivakit.component.Component;

/**
 * A builder builds sets of {@link Buildable}s, calling a {@link BuildListener} with each {@link BuildResult} as the
 * build proceeds. Note that builds may be executed in parallel, and may complete out of order.
 *
 * @author jonathanl (shibo)
 */
public interface Builder extends Component
{
    /**
     * Builds the given {@link BuildableSet}, calling the {@link BuildListener} with each {@link BuildResult} as the
     * build proceeds.
     */
    void build(BuildableSet buildables, BuildListener listener);
}
