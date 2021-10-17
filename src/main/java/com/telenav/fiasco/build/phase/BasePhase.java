package com.telenav.fiasco.build.phase;

import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.BaseComponent;

public class BasePhase extends BaseComponent implements Phase
{
    private final Project project;

    public BasePhase(Project project)
    {
        this.project = project;
    }

    public boolean atStep(BuildStep step)
    {
        return project.atStep(step);
    }

    @Override
    public void nextStep()
    {
        project.nextStep();
    }

    @Override
    public Project project()
    {
        return project;
    }
}
