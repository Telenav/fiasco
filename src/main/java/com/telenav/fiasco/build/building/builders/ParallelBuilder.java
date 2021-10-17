package com.telenav.fiasco.build.building.builders;

import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildResult;
import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.Buildables;
import com.telenav.fiasco.build.building.BaseBuilder;
import com.telenav.kivakit.kernel.language.values.count.Count;
import com.telenav.kivakit.kernel.language.vm.JavaVirtualMachine;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

/**
 * Builds sets of {@link Buildable}s in parallel.
 */
public class ParallelBuilder extends BaseBuilder
{
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
    public void build(Buildables buildables, BuildListener listener)
    {
        var executor = Executors.newFixedThreadPool(threads.asInt());
        var completion = new ExecutorCompletionService<BuildResult>(executor);
        buildables.forEach(completion::submit);
        buildables.count().loop(() ->
        {
            try
            {
                listener.onBuildResult(completion.take().get());
            }
            catch (Exception e)
            {
                problem(e, "Build failed");
            }
        });
    }
}
