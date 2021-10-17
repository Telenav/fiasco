package com.telenav.fiasco.build.building.builders;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.Buildables;
import com.telenav.fiasco.build.building.BaseBuilder;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unsupported;

public class RemoteBuilder extends BaseBuilder
{
    @Override
    public void build(Buildables buildables, BuildListener listener)
    {
        unsupported();
    }
}
