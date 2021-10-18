package com.telenav.fiasco.build.building.builders;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildableGroup;
import com.telenav.fiasco.build.building.BaseBuilder;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unimplemented;

/**
 * Builds the given set of buildables on a remote compute resource
 *
 * @author jonathanl (shibo)
 */
public class RemoteBuilder extends BaseBuilder
{
    @Override
    public void build(BuildableGroup buildables, BuildListener listener)
    {
        unimplemented();
    }
}
