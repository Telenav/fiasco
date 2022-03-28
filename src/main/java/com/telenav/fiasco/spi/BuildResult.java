package com.telenav.fiasco.spi;

import com.telenav.kivakit.core.collections.list.StringList;
import com.telenav.kivakit.core.messaging.Listener;
import com.telenav.kivakit.core.messaging.Message;
import com.telenav.kivakit.core.messaging.listeners.MessageList;
import com.telenav.kivakit.core.messaging.messages.status.Problem;
import com.telenav.kivakit.core.messaging.messages.status.Warning;
import com.telenav.kivakit.core.string.Formatter;
import com.telenav.kivakit.core.string.Strip;
import com.telenav.kivakit.core.thread.WakeState;
import com.telenav.kivakit.core.time.Duration;
import com.telenav.kivakit.core.time.Time;

/**
 * <b>Not public API</b>
 *
 * <p>
 * The result of a building a {@link Buildable}. Captures any messages it hears and saves them. The messages can be
 * retrieved with {@link #messages()}.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class BuildResult implements Listener
{
    /** The name of the build */
    private final String buildName;

    /** The time at which the build ended */
    private Time end;

    /** The reason that the build ended */
    private WakeState endedBecause;

    /** Messages captured during the build */
    private final MessageList messages = new MessageList();

    /** The time at which the build started */
    private Time start;

    /** The cause for termination if endedBecause is not COMPLETED */
    private Exception terminationCause;

    public BuildResult(String buildName)
    {
        this.buildName = buildName;
    }

    public String buildName()
    {
        return buildName;
    }

    public Duration elapsed()
    {
        return end.minus(start);
    }

    public void end()
    {
        end = Time.now();
    }

    /**
     * @return The reason why this build ended, either {@link WakeState#COMPLETED}, {@link WakeState#INTERRUPTED} or
     * {@link WakeState#TIMED_OUT}.
     */
    public WakeState endedBecause()
    {
        return endedBecause;
    }

    public void endedBecause(WakeState wakeState)
    {
        endedBecause = wakeState;
    }

    public MessageList messages()
    {
        return messages;
    }

    @Override
    public void onMessage(Message message)
    {
        messages.add(message);
    }

    public void start()
    {
        start = Time.now();
    }

    @SuppressWarnings("unchecked")
    public StringList statistics()
    {
        return messages.statistics(Problem.class, Warning.class);
    }

    public StringList summary()
    {
        return StringList.stringList(Formatter.format("Build \"$\" completed in $", buildName(), elapsed()));
    }

    public Exception terminationCause()
    {
        return terminationCause;
    }

    public void terminationCause(Exception terminationCause)
    {
        this.terminationCause = terminationCause;
    }

    @Override
    public String toString()
    {
        return summary()
                .appendAll(statistics())
                .titledBox("$ Build Completed", Strip.trailing(buildName, "Build"));
    }
}
