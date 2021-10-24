package com.telenav.fiasco.internal.building.planning;

import com.telenav.fiasco.internal.building.BuildListener;
import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.Builder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A build plan is a list of {@link BuildableGroup}s that can be built with a {@link Builder}
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class BuildPlan
{
    /** The groups of {@link Buildable}s that must be built under this build plan */
    private final ObjectList<BuildableGroup> groups = ObjectList.create();

    /**
     * Adds the group of Buildables to this plan
     */
    public void add(BuildableGroup group)
    {
        groups.add(group);
    }

    /**
     * Executes this build plan using the given builder and build listener
     */
    public void build(Builder builder, BuildListener listener)
    {
        groups.forEach(group -> builder.build(listener, group));
    }
}
