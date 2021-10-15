package com.telenav.fiasco.build.phase;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;

public class BasePhase extends BaseComponent
{
    private final Build build;

    public BasePhase(Build build)
    {
        this.build = build;
    }

    public boolean atStep(Build.Step step)
    {
        return build.atStep(step);
    }

    public Folder javaSources()
    {
        return mainSources().folder("java");
    }

    public Folder mainSources()
    {
        return sources().folder("main");
    }

    public Folder output()
    {
        return root().folder("target");
    }

    public Folder root()
    {
        return build.root();
    }

    public Folder sources()
    {
        return root().folder("source");
    }

    public Folder testSources()
    {
        return sources().folder("test");
    }
}
