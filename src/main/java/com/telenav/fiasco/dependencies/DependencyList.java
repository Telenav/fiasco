package com.telenav.fiasco.dependencies;

import com.telenav.kivakit.collections.set.ConcurrentHashSet;
import com.telenav.kivakit.kernel.interfaces.code.Callback;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.threading.Threads;
import com.telenav.kivakit.kernel.language.threading.locks.Monitor;
import com.telenav.kivakit.kernel.language.values.count.Count;
import com.telenav.kivakit.kernel.messaging.Listener;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * An ordered list of {@link Dependency} objects. The objects in the list can be processed with {@link
 * #process(Listener, Count, Callback)}, which calls the given callback with the number of threads requested and reports
 * issues to the given listener.
 *
 * @author jonathanl (shibo)
 */
public class DependencyList extends ObjectList<Dependency>
{
    public static DependencyList of(final Dependency... dependencies)
    {
        return new DependencyList(List.of(dependencies));
    }

    public DependencyList()
    {
    }

    public DependencyList(final List<Dependency> dependencies)
    {
        addAll(dependencies);
    }

    @Override
    public DependencyList copy()
    {
        return new DependencyList(this);
    }

    public void process(final Listener listener, final Callback<Dependency> callback)
    {
        process(listener, Count._1, callback);
    }

    /**
     * Processes the dependencies in this list, possibly in parallel, calling the callback with each dependency to
     * process only after its dependencies have been processed.
     *
     * @param listener The listener to call with any messages from processing
     * @param threads The number of threads to use
     * @param callback The callback to process each dependency
     */
    public void process(final Listener listener, final Count threads, final Callback<Dependency> callback)
    {
        // If there is only one thread requested,
        if (threads.equals(Count._1))
        {
            // call the callback for each dependency in order
            forEach(callback::callback);
        }
        else
        {
            // otherwise, create an executor with the requested number of threads
            final var executor = Executors.newFixedThreadPool(threads.asInt());

            // and a queue with the dependencies in it
            final var queue = queue();

            // and submit jobs for each thread
            final var completed = new ConcurrentHashSet<Dependency>();
            final var monitor = new Monitor();
            threads.loop(() -> executor.submit(() ->
            {
                // While the queue has dependencies to process,
                while (!queue.isEmpty())
                {
                    Dependency dependency = null;
                    try
                    {
                        // take the next dependency from the queue
                        dependency = queue.take();

                        // and wait until all of its dependencies have been processed
                        while (!completed.containsAll(dependency.dependencies()))
                        {
                            monitor.await();
                        }

                        // before processing it
                        callback.callback(dependency);
                        completed.add(dependency);

                        // and waking any threads waiting on this dependency.
                        monitor.done();
                    }
                    catch (final InterruptedException ignored)
                    {
                    }
                    catch (final Exception e)
                    {
                        listener.problem(e, "Error processing '$'", dependency);
                    }
                }
            }));

            // then wait for the executor threads to finish processing.
            Threads.shutdownAndAwait(executor);
        }
    }

    /**
     * @return A blocking queue of dependencies in depth-first order
     */
    public LinkedBlockingDeque<Dependency> queue()
    {
        final var queue = new LinkedBlockingDeque<Dependency>(size());
        queue.addAll(this);
        return queue;
    }

    public DependencyList without(final Collection<Dependency> exclusions)
    {
        final var copy = new DependencyList(this);
        copy.removeAll(exclusions);
        return copy;
    }

    public DependencyList without(final Matcher<Dependency> pattern)
    {
        final var copy = new DependencyList(this);
        copy.removeIf(pattern::matches);
        return copy;
    }
}
