package com.telenav.fiasco.build.tools;

import com.telenav.fiasco.build.project.BaseProject;
import com.telenav.kivakit.component.BaseComponent;

public abstract class BaseTool extends BaseComponent implements Tool
{
    private BaseProject project;

    public void project(final BaseProject project)
    {
        this.project = project;
    }

    /**
     * @return The project that this tool is running in
     */
    public BaseProject project()
    {
        return project;
    }
}
