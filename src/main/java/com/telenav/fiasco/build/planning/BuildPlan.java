package com.telenav.fiasco.build.planning;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.Buildables;
import com.telenav.fiasco.build.building.Builder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * A build plan is a list of {@link Buildables}s that can be built with a {@link Builder}
 */
public class BuildPlan
{
    private ObjectList<Buildables> groups;

    public void add(Buildables group)
    {
        groups.add(group);
    }

    public void build(Builder builder, BuildListener listener)
    {
        groups.forEach(group -> group.build(builder, listener));
    }
}
