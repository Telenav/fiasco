package com.telenav.fiasco.build;

import com.telenav.fiasco.build.building.Builder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * A group of {@link Buildable}s to build
 */
public class Buildables extends ObjectList<Buildable>
{
    public static Buildables create()
    {
        return new Buildables();
    }

    protected Buildables()
    {
    }

    public void build(final Builder builder, final BuildListener listener)
    {
        builder.build(this, listener);
    }
}
