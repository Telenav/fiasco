package com.telenav.fiasco.build.building;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildResult;
import com.telenav.fiasco.build.Buildables;
import com.telenav.kivakit.component.Component;

/**
 * A builder builds {@link Buildables}, calling a {@link BuildListener} with each {@link BuildResult} as the build
 * proceeds. Note that builds may be executed in parallel, and may complete out of order.
 *
 * @author jonathanl (shibo)
 */
public interface Builder extends Component
{
    /**
     * Builds the given {@link Buildables}, calling the {@link BuildListener} with each {@link BuildResult} as the build
     * proceeds.
     */
    void build(Buildables buildables, BuildListener listener);
}
