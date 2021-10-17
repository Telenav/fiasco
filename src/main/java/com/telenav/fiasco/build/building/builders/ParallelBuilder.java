package com.telenav.fiasco.build.building.builders;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildResult;
import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.BuildableSet;
import com.telenav.fiasco.build.building.BaseBuilder;
import com.telenav.kivakit.kernel.language.values.count.Count;
import com.telenav.kivakit.kernel.language.vm.JavaVirtualMachine;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

/**
 * Builds sets of {@link Buildable}s in parallel.
 *
 * @author jonathanl (shibo)
 */
public class ParallelBuilder extends BaseBuilder
{
    /** The number of threads to build with */
    private final Count threads;

    /**
     * Builds with one thread for each processor
     */
    public ParallelBuilder()
    {
        this(JavaVirtualMachine.local().processors().asCount());
    }

    /**
     * Build with the given number of threads
     */
    public ParallelBuilder(Count threads)
    {
        this.threads = threads;
    }

    /**
     * Builds the given {@link Buildable}s, calling the {@link BuildListener} as the build progresses
     */
    @Override
    public void build(BuildableSet buildables, BuildListener listener)
    {
        // Create executor,
        var executor = Executors.newFixedThreadPool(threads.asInt());

        // wrap it in a completion service,
        var completion = new ExecutorCompletionService<BuildResult>(executor);

        // submit each buildable,
        buildables.forEach(completion::submit);

        // then while there are more builds to complete,
        buildables.count().loop(() ->
        {
            try
            {
                // get the result of the build and pass it to the listener.
                listener.onBuildResult(completion.take().get());
            }
            catch (Exception e)
            {
                problem(e, "Build failed");
            }
        });
    }
}
