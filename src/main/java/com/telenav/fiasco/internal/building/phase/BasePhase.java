package com.telenav.fiasco.internal.building.phase;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.component.BaseComponent;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Base class for build {@link Phase}s
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class BasePhase extends BaseComponent implements Phase
{
    /** The build which owns this phase */
    private final Build build;

    /**
     * @param build The build to which this phase belongs
     */
    public BasePhase(Build build)
    {
        this.build = build;
    }

    /**
     * @return The build that owns this {@link Phase}
     */
    @Override
    public Build parentBuild()
    {
        return build;
    }
}
