package com.telenav.fiasco.build.phase;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.BaseComponent;

/**
 * Base class for build {@link Phase}s
 *
 * @author jonathanl (shibo)
 */
public class BasePhase extends BaseComponent implements Phase
{
    private final Project project;

    public BasePhase(Project project)
    {
        this.project = project;
    }

    public Project project()
    {
        return project;
    }
}
