package com.telenav.fiasco.internal.building.dependencies.download;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.resource.CopyMode;
import com.telenav.kivakit.resource.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.telenav.fiasco.internal.building.dependencies.download.ParallelCopier.CopyJob.Status.COPIED;
import static com.telenav.fiasco.internal.building.dependencies.download.ParallelCopier.CopyJob.Status.FAILED;
import static com.telenav.fiasco.internal.building.dependencies.download.ParallelCopier.CopyJob.Status.WAITING;

/**
 * Copies resources into folders in parallel. Useful for copying from network resources.
 *
 * @author jonathanl (shibo)
 */
public class ParallelCopier extends BaseComponent
{
    public static class CopyJob implements Runnable
    {
        public enum Status
        {
            WAITING,
            COPIED,
            FAILED
        }

        private final Resource from;

        private final Folder to;

        private final CopyMode mode;

        private Status status = WAITING;

        public CopyJob(final Resource from, final Folder to, final CopyMode mode)
        {
            this.from = from;
            this.to = to;
            this.mode = mode;
        }

        public void run()
        {
            try
            {
                from.safeCopyTo(to, mode);
                status = COPIED;
            }
            catch (Exception e)
            {
                status = FAILED;
            }
        }

        public Status status()
        {
            return status;
        }

        public String toString()
        {
            return Message.format("$: $ => $", status, from, to);
        }
    }

    private final Executor executor = Executors.newFixedThreadPool(16);

    private final ExecutorCompletionService<CopyJob> completionService = new ExecutorCompletionService<>(executor);

    private final Map<Resource, CopyJob> jobs = new HashMap<>();

    /**
     * @param from The resource to copy
     * @param to The folder to copy it to
     * @param mode The copying mode
     * @return The future to wait on or null if the resource is already being copied
     */
    public Future<CopyJob> add(Resource from, Folder to, CopyMode mode)
    {
        var existingJob = jobs.get(from);
        if (existingJob == null)
        {
            final var job = new CopyJob(from, to, mode);
            return completionService.submit(job, job);
        }
        return null;
    }

    /**
     * @return The next copy job that completes
     */
    public CopyJob waitForNextCompleted()
    {
        try
        {
            return completionService.take().get();
        }
        catch (Exception ignored)
        {
            return null;
        }
    }
}
