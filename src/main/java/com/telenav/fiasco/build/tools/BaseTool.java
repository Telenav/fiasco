package com.telenav.fiasco.build.tools;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.BaseComponent;

public abstract class BaseTool extends BaseComponent implements Tool
{
    private Project project;

    public void project(final Project project)
    {
        this.project = project;
    }

    /**
     * @return The project that this tool is running in
     */
    public Project project()
    {
        return project;
    }
}
