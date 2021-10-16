package com.telenav.fiasco.build;

import com.telenav.kivakit.kernel.language.time.Time;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.kernel.messaging.listeners.MessageList;

/**
 * The result of a building a {@link Buildable}. Captures any messages it hears and saves them. The messages can be
 * retrieved with {@link #messages()}
 *
 * @author jonathanl (shibo)
 */
public class BuildResult implements Listener
{
    private final Time start;

    private final Time end;

    private final MessageList messages = new MessageList();

    public BuildResult(final Time start, final Time end)
    {
        this.start = start;
        this.end = end;
    }

    public Time end()
    {
        return end;
    }

    public MessageList messages()
    {
        return messages;
    }

    @Override
    public void onMessage(final Message message)
    {
        messages.add(message);
    }

    public Time start()
    {
        return start;
    }
}
