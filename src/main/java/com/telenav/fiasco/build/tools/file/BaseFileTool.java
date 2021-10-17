package com.telenav.fiasco.build.tools.file;

import com.telenav.fiasco.build.tools.BaseTool;
import com.telenav.kivakit.filesystem.FileGlobPattern;
import com.telenav.kivakit.kernel.interfaces.numeric.Countable;
import com.telenav.kivakit.kernel.language.progress.reporters.Progress;

public abstract class BaseFileTool extends BaseTool
{
    private Progress progress;

    protected FileGlobPattern globPattern(final String glob)
    {
        return FileGlobPattern.parse(glob);
    }

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

