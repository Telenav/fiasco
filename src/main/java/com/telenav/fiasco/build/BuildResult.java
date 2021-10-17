package com.telenav.fiasco.build;

import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.time.Duration;
import com.telenav.kivakit.kernel.language.time.Time;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.kernel.messaging.listeners.MessageList;
import com.telenav.kivakit.kernel.messaging.messages.status.Problem;
import com.telenav.kivakit.kernel.messaging.messages.status.Warning;

/**
 * The result of a building a {@link Buildable}. Captures any messages it hears and saves them. The messages can be
 * retrieved with {@link #messages()}.
 *
 * @author jonathanl (shibo)
 */
public class BuildResult implements Listener
{
    /** The time at which the build started */
    private Time start;

    /** The time at which the build ended */
    private Time end;

    /** Messages captured during the build */
    private final MessageList messages = new MessageList();

    /** The name of the build */
    private final String buildName;

    public BuildResult(String buildName)
    {
        this.buildName = buildName;
    }

    public Duration elapsed()
    {
        return end.minus(start);
    }

    public void end()
    {
        this.end = Time.now();
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

    public void start()
    {
        start = Time.now();
    }

    public StringList statistics()
    {
        return messages.statisticsByType(Problem.class, Warning.class);
    }

    public StringList summary()
    {
        return StringList.stringList("Elapsed: " + elapsed());
    }

    @Override
    public String toString()
    {
        return summary()
                .appendAll(statistics())
                .appendAll(messages().asStringList())
                .titledBox("$ Build Completed", buildName);
    }
}
