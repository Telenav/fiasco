package com.telenav.fiasco.internal.building.planning;

import com.telenav.fiasco.internal.building.builders.ParallelBuilder;
import com.telenav.fiasco.spi.BuildListener;
import com.telenav.fiasco.spi.Buildable;
import com.telenav.fiasco.spi.Builder;
import com.telenav.kivakit.core.collections.list.ObjectList;
import com.telenav.kivakit.core.time.Duration;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A build plan is a list of {@link BuildableGroup}s that can be built with a {@link Builder}.
 * </p>
 *
 * <p>
 * A build plan is executed by calling {@link #build(Builder, BuildListener, Duration)}. The {@link Builder} builds each
 * {@link BuildableGroup} in order. A {@link ParallelBuilder} builds each group in parallel due to the fact that a
 * {@link BuildableGroup} contains only {@link Buildable}s which have no dependencies that have not already been built.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Buildable
 * @see BuildableGroup
 * @see Builder
 * @see BuildListener
 * @see ParallelBuilder
 */
public class BuildPlan extends ObjectList<BuildableGroup>
{
    /**
     * Executes this build plan using the given builder and build listener
     */
    public void build(Builder builder, BuildListener listener, Duration timeout)
    {
        forEach(group -> builder.build(listener, group, timeout));
    }
}
