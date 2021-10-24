package com.telenav.fiasco.internal.building.builders;

import com.telenav.fiasco.internal.building.BuildListener;
import com.telenav.fiasco.internal.building.planning.BuildableGroup;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unimplemented;

/**
 * <b>Not public API</b>
 * <p>
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
