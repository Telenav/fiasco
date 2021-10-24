package com.telenav.fiasco.build.tools;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.BaseComponent;

/**
 * Base class for build tools
 *
 * @author jonathanl (shibo)
 */
public abstract class BaseTool extends BaseComponent implements Tool
{
    /** The project that this tool is helping to build */
    private Build build;

    /**
     * {@inheritDoc}
     */
    public void build(final Build build)
    {
        this.build = build;
    }

    /**
     * {@inheritDoc}
     */
    public Build build()
    {
        return build;
    }
}
