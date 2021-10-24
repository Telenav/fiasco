package com.telenav.fiasco.internal.phase;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.BaseComponent;

/**
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
