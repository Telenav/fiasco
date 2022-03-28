package com.telenav.fiasco.spi;

import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.core.time.Duration;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A builder builds sets of {@link Buildable}s, calling a {@link BuildListener} with each {@link BuildResult} as the
 * build proceeds. Note that builds may be executed in parallel, and may complete out of order.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see BuildableGroup
 * @see BuildListener
 * @see BuildResult
 */
public interface Builder extends Component
{
    /**
     * <b>Not public API</b>
     *
     * <p>
     * Builds the given {@link BuildableGroup}, calling the {@link BuildListener} with each {@link BuildResult} as the
     * build proceeds. Fails if the build takes longer than the given timeout.
     * </p>
     */
    void build(BuildListener listener, BuildableGroup buildables, Duration timeout);
}
