package com.telenav.fiasco.build;

import com.telenav.fiasco.build.builder.Builder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * A group of {@link Buildable}s to build
 */
public class Buildables implements Iterable<Buildable>
{
    private final ObjectList<Buildable> buildables;

    public Buildables(final ObjectList<Buildable> buildables)
    {
        this.buildables = buildables;
    }

    public void build(final Builder builder, final BuildListener listener)
    {
        builder.build(this, listener);
    }

    @NotNull
    @Override
    public Iterator<Buildable> iterator()
    {
        return buildables.iterator();
    }
}
