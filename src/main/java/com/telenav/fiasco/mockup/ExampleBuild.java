package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.filesystem.Folder;

public class ExampleBuild extends Build
{
    @Override
    public void onInitialize()
    {
        add(new ExampleProject().in("example"));
    }

    @Override
    public Folder workspace()
    {
        return Folder.parse("${WORKSPACE}");
    }
}
