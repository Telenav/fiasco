package com.telenav.fiasco.plugins;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.Tool;
import com.telenav.kivakit.core.messaging.repeaters.BaseRepeater;
import com.telenav.kivakit.filesystem.Folder;

/**
 * @author jonathanl (shibo)
 */
public abstract class Plugin extends BaseRepeater implements Tool
{
    private final Module module;

    /**
     * @param module The module where this tool should be executed, if any
     */
    public Plugin(final Module module)
    {
        this.module = module;
    }

    @Override
    public final void run()
    {
        onRunning();
        onRun();
        onRan();
    }

    protected Module module()
    {
        return module;
    }

    protected void onRan()
    {
    }

    protected abstract void onRun();

    protected void onRunning()
    {
    }

    protected Folder resolveFolder(final Folder folder)
    {
        if (folder.path().isRelative())
        {
            return module().folder().folder(folder);
        }
        else
        {
            return folder;
        }
    }
}
