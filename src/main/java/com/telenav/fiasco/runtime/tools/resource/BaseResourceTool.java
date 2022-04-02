package com.telenav.fiasco.runtime.tools.resource;

import com.telenav.fiasco.runtime.tools.BaseTool;
import com.telenav.kivakit.core.progress.ProgressReporter;
import com.telenav.kivakit.core.progress.reporters.BroadcastingProgressReporter;
import com.telenav.kivakit.core.value.count.Countable;
import com.telenav.kivakit.filesystem.FileGlobPattern;

/**
 * Base class for file tools
 *
 * @author jonathanl (shibo)
 */
public abstract class BaseResourceTool extends BaseTool
{
    /** progress in performing an action on a set of files */
    private ProgressReporter progress;

    /**
     * @return A {@link FileGlobPattern} for the given glob pattern
     */
    public FileGlobPattern glob(String pattern)
    {
        return FileGlobPattern.parse(this, pattern);
    }

    /**
     * @return A configured {@link ProgressReporter} object
     */
    protected ProgressReporter progress(String action, Countable count)
    {
        if (progress == null)
        {
            progress = BroadcastingProgressReporter.create(this, "files");
            progress.steps(count.count());
            progress.start(action + " " + count.count() + " files");
        }
        return progress;
    }
}
