package com.telenav.fiasco.internal.building.dependencies.download;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.resource.CopyMode;
import com.telenav.kivakit.resource.Resource;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.telenav.fiasco.internal.building.dependencies.download.Downloader.DownloadStatus.DOWNLOADED;
import static com.telenav.fiasco.internal.building.dependencies.download.Downloader.DownloadStatus.DOWNLOADING;
import static com.telenav.fiasco.internal.building.dependencies.download.Downloader.DownloadStatus.FAILED;
import static com.telenav.fiasco.internal.building.dependencies.download.Downloader.DownloadStatus.WAITING;
import static com.telenav.kivakit.kernel.language.objects.Objects.equalPairs;

/**
 * Downloads resources into folders concurrently.
 *
 * <p>
 * If the method {@link #download(Download)} returns a Future&lt;Download&gt; the download is in progress, and calling
 * {@link Future#get()} on this value will block the calling thread until the download completes. If {@link
 * #download(Download)} returns null, the resource has already been downloaded and waiting is not necessary. To wait for
 * the next download (of all submitted downloads) that completes, use the {@link #waitForNextCompletedDownload()}
 * method.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class Downloader extends BaseComponent
{
    /** The current status of a download */
    public enum DownloadStatus
    {
        /** Downloading has not yet begun */
        WAITING,

        /** The resource is downloading */
        DOWNLOADING,

        /** The resource has finished downloading */
        DOWNLOADED,

        /** The resource could not be downloaded */
        FAILED
    }

    /**
     * Represents a resource download to a destination folder
     */
    public static class Download implements Callable<Download>
    {
        /** The resource to download */
        private final Resource source;

        /** The folder to download to */
        private final Folder destination;

        /** What to do if the file already exists */
        private final CopyMode mode;

        /** The status of this download */
        private DownloadStatus status = WAITING;

        public Download(final Resource source, final Folder destination, final CopyMode mode)
        {
            this.source = source;
            this.destination = destination;
            this.mode = mode;
        }

        /**
         * Perform the download
         */
        public Download call()
        {
            try
            {
                // then do the download
                status = DOWNLOADING;
                source.safeCopyTo(destination, mode);
                status = DOWNLOADED;
            }
            catch (Exception e)
            {
                status = FAILED;
            }
            return this;
        }

        public Folder destination()
        {
            return destination;
        }

        @Override
        public boolean equals(final Object object)
        {
            if (object instanceof Download)
            {
                Download that = (Download) object;
                return equalPairs(source, that.source, destination, that.destination);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(source, destination);
        }

        public Resource source()
        {
            return source;
        }

        public DownloadStatus status()
        {
            return status;
        }

        public String toString()
        {
            return Message.format("$: $ => $", status, source, destination);
        }
    }

    /** The executor for downloading in parallel */
    private final Executor executor = Executors.newFixedThreadPool(16);

    /** The completion service that manages which downloads finish */
    private final ExecutorCompletionService<Download> downloader = new ExecutorCompletionService<>(executor);

    /** The downloads that have already been completed */
    private final ObjectSet<Download> completed = new ObjectSet<>();

    /** The downloads that are in progress */
    private final Map<Download, Future<Download>> downloading = new ConcurrentHashMap<>();

    /**
     * Adds a download job to this downloader that will copy the <i>from</i> resource to the <i>to</i> folder using the
     * given {@link CopyMode}. A thread can add several download jobs in this way and then wait for them in the order
     * that they complete with {@link #waitForNextCompletedDownload()}.
     *
     * @param download The download to perform
     * @return The future to wait on or null if the resource has already been downloaded
     */
    public synchronized Future<Download> download(Download download)
    {
        // If the download has already completed,
        if (completed.contains(download))
        {
            // there's no future.
            return null;
        }

        // If there is a download in progress,
        var existing = downloading.get(download);
        if (existing != null)
        {
            // return the future for that download so the caller can wait for it.
            return existing;
        }

        // Submit the download and add it to the downloads in progress
        var submitted = downloader.submit(download);
        downloading.put(download, submitted);
        return submitted;
    }

    /**
     * @return The next download that completes
     */
    public synchronized Download waitForNextCompletedDownload()
    {
        try
        {
            var completed = downloader.take().get();
            this.completed.add(completed);
            return completed;
        }
        catch (Exception ignored)
        {
            return null;
        }
    }
}