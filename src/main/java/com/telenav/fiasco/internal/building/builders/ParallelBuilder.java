package com.telenav.fiasco.internal.building.builders;

import com.telenav.fiasco.internal.building.planning.BuildPlan;
import com.telenav.fiasco.internal.building.planning.BuildPlanner;
import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.fiasco.spi.BuildListener;
import com.telenav.fiasco.spi.BuildResult;
import com.telenav.fiasco.spi.Buildable;
import com.telenav.kivakit.core.time.Duration;
import com.telenav.kivakit.core.value.count.Count;

import java.util.IdentityHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.telenav.kivakit.core.thread.WakeState.COMPLETED;
import static com.telenav.kivakit.core.thread.WakeState.INTERRUPTED;
import static com.telenav.kivakit.core.thread.WakeState.TERMINATED;
import static com.telenav.kivakit.core.thread.WakeState.TIMED_OUT;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Builds {@link BuildableGroup}s in parallel using the number of threads passed to the constructor.
 * </p>
 *
 * <p>
 * A parallel build is planned with {@link BuildPlanner}, which creates a {@link BuildPlan} consisting of groups of
 * {@link Buildable}s that can be built in parallel. The {@link #build(BuildListener, BuildableGroup, Duration)} method
 * builds a single group of buildables in parallel. Each {@link Buildable} in the given group is submitted to an {@link
 * ExecutorCompletionService} running on a fixed thread pool. The completion service executes builds in parallel by
 * calling {@link Buildable#call()}, which implements {@link Callable}. As each build completes, the {@link
 * BuildListener#onBuildCompleted(BuildResult)} method is called with the build results. When all parallel builds have
 * completed, the method returns. In the event that a build task hangs, the method will return after the timeout
 * duration passed to {@link #build(BuildListener, BuildableGroup, Duration)}.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see BuildPlanner
 * @see BuildPlan
 * @see BuildableGroup
 * @see Executors
 * @see ExecutorCompletionService
 * @see Callable
 */
public class ParallelBuilder extends BaseBuilder
{
    /** The number of threads to build with */
    private final Count threads;

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Build with the given number of threads
     * </p>
     */
    public ParallelBuilder(Count threads)
    {
        this.threads = threads;
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Builds the given {@link Buildable}s, calling the {@link BuildListener} as the build progresses
     * </p>
     */
    @Override
    public void build(BuildListener listener, BuildableGroup buildables, Duration timeout)
    {
        // Create executor,
        var executor = Executors.newFixedThreadPool(threads.asInt());

        // wrap it in a completion service,
        var completion = new ExecutorCompletionService<BuildResult>(executor);

        // then submit each buildable, mapping the future back the buildable so we can associate the
        // buildable with the future if it fails below.
        var futures = new IdentityHashMap<Future<BuildResult>, Buildable>();
        buildables.forEach(at -> futures.put(completion.submit(at), at));

        // While there are more builds to complete,
        buildables.count().loop(() ->
        {
            BuildResult result;
            Future<BuildResult> future = null;
            try
            {
                // get the next build result to wait for,
                future = completion.take();

                // block until it's done building,
                result = future.get(timeout.milliseconds(), TimeUnit.MILLISECONDS);

                // and mark that it completed.
                result.endedBecause(COMPLETED);
            }
            catch (Exception e)
            {
                // We timed out (or possibly were interrupted), so get the buildable associated with
                // the future we were waiting on (if any),
                var buildable = futures.get(future);

                // construct a result with the name of the build,
                result = new BuildResult(buildable == null ? "Unknown" : buildable.name());

                // stop the build timer,
                result.end();

                // record the exception that ended the build,
                result.terminationCause(e);

                // and mark the reason that the awaited build ended.
                if (e instanceof InterruptedException)
                {
                    result.endedBecause(INTERRUPTED);
                }
                else if (e instanceof TimeoutException)
                {
                    result.endedBecause(TIMED_OUT);
                }
                else
                {
                    result.endedBecause(TERMINATED);
                }
            }

            // Call the build listener with the result of this completed build.
            listener.onBuildCompleted(result);
        });
    }
}
