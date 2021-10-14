package com.telenav.fiasco.plugins.builder;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;

/**
 * @author jonathanl (shibo)
 */
public class BuildStamper extends Plugin
{
    public BuildStamper(final Module module)
    {
        super(module);
    }

    @Override
    protected void onRun()
    {
        try (final var out = module().classesFolder().file("build.txt").printWriter())
        {
            out.println(module().project().version());
        }
    }
}
