package com.telenav.fiasco.internal.building.builders;

import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.fiasco.spi.BuildListener;
import com.telenav.kivakit.core.language.time.Duration;

import static com.telenav.kivakit.core.data.validation.ensure.Ensure.unimplemented;

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
