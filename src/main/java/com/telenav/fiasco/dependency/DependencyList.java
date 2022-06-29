package com.telenav.fiasco.dependency;

import com.telenav.fiasco.Dependency;
import com.telenav.kivakit.core.collections.list.ObjectList;
import com.telenav.kivakit.core.collections.set.ConcurrentHashSet;
import com.telenav.kivakit.core.messaging.Listener;
import com.telenav.kivakit.core.thread.Monitor;
import com.telenav.kivakit.core.thread.Threads;
import com.telenav.kivakit.core.value.count.Count;
import com.telenav.kivakit.interfaces.code.Callback;
import com.telenav.kivakit.interfaces.comparison.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * An ordered list of {@link Dependency} objects. The objects in the list can be processed with
 * {@link #process(Listener, Count, Callback)}, which calls the given callback with the number of threads requested and
 * reports issues to the given listener.
 *
 * @author jonathanl (shibo)
 */
@SuppressWarnings("unused") public class DependencyList<T extends Dependency<T>> extends ObjectList<T>
{
    @SafeVarargs
    public static <T extends Dependency<T>> DependencyList<T> of(final T... dependencies)
    {
        return new DependencyList<>(List.of(dependencies));
    }

    public DependencyList()
    {
    }

    public DependencyList(final List<T> dependencies)
    {
        addAll(dependencies);
    }

    @Override
    public DependencyList<T> copy()
    {
        return new DependencyList<>(this);
    }

    public void process(final Listener listener, final Callback<T> callback)
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
    @SuppressWarnings("unchecked")
    public void process(final Listener listener, final Count threads, final Callback<T> callback)
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
            final var completed = new ConcurrentHashSet<Dependency<T>>();
            final var monitor = new Monitor();
            threads.loop(() -> executor.submit(() ->
            {
                // While the queue has dependencies to process,
                while (!queue.isEmpty())
                {
                    Dependency<T> dependency = null;
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
                        callback.callback((T) dependency);
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
    public LinkedBlockingDeque<Dependency<T>> queue()
    {
        final var queue = new LinkedBlockingDeque<Dependency<T>>(size());
        queue.addAll(this);
        return queue;
    }

    public DependencyList<T> without(final Collection<T> exclusions)
    {
        final var copy = new DependencyList<>(this);
        copy.removeAll(exclusions);
        return copy;
    }

    public DependencyList<T> without(final Matcher<T> pattern)
    {
        final var copy = new DependencyList<>(this);
        copy.removeIf(pattern::matches);
        return copy;
    }
}
