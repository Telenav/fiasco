package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.project.Project;

public class ExampleProject extends Project
{
    @Override
    public void onInitialize()
    {
        var wicket = apache().wicket().version("9.5.0");
        var commons = apache().commons().version("3.7");
        var kivakit = telenav().kivakit().version("1.1.0");

        require(wicket.core(),
                wicket.util(),
                commons.lang3(),
                kivakit.application(),
                kivakit.networkHttp());
    }
}
