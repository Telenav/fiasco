package com.telenav.fiasco.internal.building.phase;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.kivakit.component.BaseComponent;

/**
 * <b>Not public API</b>
 * <p>
 * Base class for build {@link Phase}s
 *
 * @author jonathanl (shibo)
 */
public class BasePhase extends BaseComponent implements Phase
{
    private final Build build;

    public BasePhase(Build build)
    {
        this.build = build;
    }

    public Build build()
    {
        return build;
    }
}
