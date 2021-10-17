package com.telenav.fiasco.build.tools;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.BaseComponent;

/**
 * Base class for build tools
 *
 * @author jonathanl (shibo)
 */
public abstract class BaseTool extends BaseComponent implements Tool
{
    /** The project that this tool is helping to build */
    private Project project;

    /**
     * {@inheritDoc}
     */
    public void project(final Project project)
    {
        this.project = project;
    }

    /**
     * {@inheritDoc}
     */
    public Project project()
    {
        return project;
    }
}
