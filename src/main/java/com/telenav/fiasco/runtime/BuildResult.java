package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.strings.Strip;
import com.telenav.kivakit.kernel.language.threading.status.WakeState;
import com.telenav.kivakit.kernel.language.time.Duration;
import com.telenav.kivakit.kernel.language.time.Time;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.kernel.messaging.listeners.MessageList;
import com.telenav.kivakit.kernel.messaging.messages.status.Problem;
import com.telenav.kivakit.kernel.messaging.messages.status.Warning;

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
    /** The time at which the build started */
    private Time start;

    /** The time at which the build ended */
    private Time end;

    /** Messages captured during the build */
    private final MessageList messages = new MessageList();

    /** The name of the build */
    private final String buildName;

    /** The reason that the build ended */
    private WakeState endedBecause;

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

    public StringList statistics()
    {
        return messages.statisticsByType(Problem.class, Warning.class);
    }

    public StringList summary()
    {
        return StringList.stringList(Message.format("Build \"$\" completed in $", buildName(), elapsed()));
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
