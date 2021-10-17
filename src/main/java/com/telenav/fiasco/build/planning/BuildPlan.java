package com.telenav.fiasco.build.planning;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildableSet;
import com.telenav.fiasco.build.building.Builder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * A build plan is a list of {@link BuildableSet}s that can be built with a {@link Builder}
 */
public class BuildPlan
{
    private final ObjectList<BuildableSet> groups = ObjectList.create();

    public void add(BuildableSet group)
    {
        groups.add(group);
    }

    public void build(Builder builder, BuildListener listener)
    {
        groups.forEach(group -> builder.build(group, listener));
    }
}
