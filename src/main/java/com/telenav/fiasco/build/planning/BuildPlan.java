package com.telenav.fiasco.build.planning;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.BuildableGroup;
import com.telenav.fiasco.build.building.Builder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * A build plan is a list of {@link BuildableGroup}s that can be built with a {@link Builder}
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
        groups.forEach(group -> builder.build(group, listener));
    }
}
