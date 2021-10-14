//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.plugins.copier;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.progress.reporters.Progress;
import com.telenav.kivakit.kernel.language.values.count.Maximum;
import com.telenav.kivakit.kernel.messaging.filters.operators.All;
import com.telenav.kivakit.resource.CopyMode;

/**
 * Copies selected files from one folder to another.
 *
 * @author shibo
 */
public class Copier extends Plugin
{
    /** The folder to copy to */
    private Folder to;

    /** The folder to copy from */
    private Folder from;

    /** The files to copy */
    private Matcher<File> matcher = new All<>();

    /** Progress in copying files */
    private final Progress progress = Progress.create(this, "files");

    private CopyMode mode;

    public Copier(final Module module)
    {
        super(module);
    }

    public Copier from(final Folder from)
    {
        this.from = resolveFolder(from);
        return this;
    }

    public Folder from()
    {
        return from;
    }

    public Matcher<File> matching()
    {
        return matcher;
    }

    public Copier matching(final Matcher<File> matcher)
    {
        this.matcher = matcher;
        return this;
    }

    public void mode(final CopyMode mode)
    {
        this.mode = mode;
    }

    public CopyMode mode()
    {
        return mode;
    }

    public Copier to(final Folder to)
    {
        this.to = resolveFolder(to);
        return this;
    }

    public Folder to()
    {
        return to;
    }

    @Override
    protected void onRun()
    {
        // For each source file in the from folder that matches,
        final var files = from.nestedFiles(matcher);
        progress.steps(Maximum.maximum(files.size()));
        progress.start("Copying " + files.size() + " files");
        for (final var source : files)
        {
            // find the path relative to the root,
            final var relative = source.relativeTo(from);

            // construct a file with the same path relative to the 'to' folder,
            final var destination = to.file(relative);

            // create any parent folders that might be required
            destination.parent().mkdirs();

            // and copy the source file to the destination location
            source.safeCopyTo(destination, CopyMode.OVERWRITE, progress);
            progress.next();
        }
        progress.end(files.size() + " files copied");
    }
}
