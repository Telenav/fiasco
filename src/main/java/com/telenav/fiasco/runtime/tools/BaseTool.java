package com.telenav.fiasco.runtime.tools;

import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.Tool;
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
    @Override
    public void parentBuild(Build build)
    {
        this.build = build;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Build parentBuild()
    {
        return build;
    }
}
