package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.filesystem.Folder;

/**
 * An example build class
 */
public class ExampleBuild extends Build
{
    @Override
    public void onInitialize()
    {
        project("example");
    }

    @Override
    public Folder workspace()
    {
        return Folder.parse("${EXAMPLE_WORKSPACE}");
    }
}
