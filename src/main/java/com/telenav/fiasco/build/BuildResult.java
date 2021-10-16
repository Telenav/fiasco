package com.telenav.fiasco.build;

import com.telenav.kivakit.kernel.language.time.Time;
import com.telenav.kivakit.kernel.messaging.listeners.MessageList;

public class BuildResult extends MessageList
{
    private final Time start;

    private final Time end;

    public BuildResult(final Time start, final Time end)
    {
        this.start = start;
        this.end = end;
    }

    public Time end()
    {
        return end;
    }

    public Time start()
    {
        return start;
    }
}
