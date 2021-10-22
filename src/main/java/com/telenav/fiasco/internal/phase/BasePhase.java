package com.telenav.fiasco.internal.phase;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.kivakit.component.BaseComponent;

/**
 * Base class for build {@link Phase}s
 *
 * @author jonathanl (shibo)
 */
public class BasePhase extends BaseComponent implements Phase
{
    private final FiascoBuild build;

    public BasePhase(FiascoBuild build)
    {
        this.build = build;
    }

    public FiascoBuild build()
    {
        return build;
    }
}
