package com.telenav.fiasco.build.phase;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.BuildStep;
import com.telenav.kivakit.component.BaseComponent;

public class Phase extends BaseComponent
{
    private final Build build;

    public Phase(Build build)
    {
        this.build = build;
    }

    public boolean atStep(BuildStep step)
    {
        return build.atStep(step);
    }
}
