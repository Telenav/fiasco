package com.telenav.fiasco.build;

import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * A set of {@link Buildable}s
 *
 * @author jonathanl (shibo)
 */
public class BuildableGroup extends ObjectList<Buildable>
{
    public static BuildableGroup create()
    {
        return new BuildableGroup();
    }

    protected BuildableGroup()
    {
    }
}
