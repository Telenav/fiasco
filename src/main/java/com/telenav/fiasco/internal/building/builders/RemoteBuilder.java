package com.telenav.fiasco.internal.building.builders;

import com.telenav.fiasco.internal.building.BuildListener;
import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.kivakit.kernel.language.time.Duration;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unimplemented;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Builds the given {@link BuildableGroup} on one or more remote compute resources
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class RemoteBuilder extends BaseBuilder
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void build(BuildListener listener, BuildableGroup buildables, Duration timeout)
    {
        unimplemented();
    }
}
