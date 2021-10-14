package com.telenav.fiasco.plugins;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.*;
import com.telenav.tdk.core.filesystem.Folder;
import com.telenav.tdk.core.kernel.messaging.Message;
import com.telenav.tdk.core.kernel.messaging.repeaters.BaseRepeater;

/**
 * @author jonathanl (shibo)
 */
public abstract class Plugin extends BaseRepeater<Message> implements Tool
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
