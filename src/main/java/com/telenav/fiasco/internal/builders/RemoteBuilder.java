package com.telenav.fiasco.internal.builders;

import com.telenav.fiasco.internal.BuildListener;
import com.telenav.fiasco.internal.BuildableGroup;
import com.telenav.fiasco.internal.BaseBuilder;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unimplemented;

/**
 * Builds the given set of buildables on a remote compute resource
 *
 * @author jonathanl (shibo)
 */
public class RemoteBuilder extends BaseBuilder
{
    @Override
    public void build(BuildListener listener, BuildableGroup buildables)
    {
        unimplemented();
    }
}
