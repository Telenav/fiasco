package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.Build;

public class ExampleBuild extends Build
{
    @Override
    public void onInitialize()
    {
        final var home = folderForProperty("WORKSPACE");

        add(new ExampleModule(home.folder("example")));
    }
}
