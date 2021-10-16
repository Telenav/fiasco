package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.Build;

public class ExampleBuild extends Build
{
    @Override
    public void onInitialize()
    {
        buildables(new ExampleModule(folderForProperty("EXAMPLE_HOME")));
    }
}
