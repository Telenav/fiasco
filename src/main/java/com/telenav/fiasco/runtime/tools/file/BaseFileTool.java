package com.telenav.fiasco.runtime.tools.file;

import com.telenav.fiasco.runtime.tools.BaseTool;
import com.telenav.kivakit.filesystem.FileGlobPattern;
import com.telenav.kivakit.kernel.interfaces.numeric.Countable;
import com.telenav.kivakit.kernel.language.progress.reporters.Progress;

/**
 * Base class for file tools
 *
 * @author jonathanl (shibo)
 */
public abstract class BaseFileTool extends BaseTool
{
    /** progress in performing an action on a set of files */
    private Progress progress;

    /**
     * @return A {@link FileGlobPattern} for the given glob pattern
     */
    public FileGlobPattern glob(String pattern)
    {
        return FileGlobPattern.parse(pattern);
    }

    /**
     * @return A configured {@link Progress} object
     */
    protected Progress progress(String action, Countable count)
    {
        if (progress == null)
        {
            progress = Progress.create(this, "files");
            progress.steps(count.count());
            progress.start(action + " " + count.count() + " files");
        }
        return progress;
    }
}
