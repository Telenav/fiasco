package com.telenav.fiasco.build;

import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * A set of {@link Buildable}s
 *
 * @author jonathanl (shibo)
 */
public class BuildableSet extends ObjectList<Buildable>
{
    public static BuildableSet create()
    {
        return new BuildableSet();
    }

    protected BuildableSet()
    {
    }
}
