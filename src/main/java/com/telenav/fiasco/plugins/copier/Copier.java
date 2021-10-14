//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.plugins.copier;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;
import com.telenav.tdk.core.filesystem.*;
import com.telenav.tdk.core.kernel.interfaces.object.Matcher;
import com.telenav.tdk.core.kernel.language.matching.All;
import com.telenav.tdk.core.kernel.operation.progress.Progress;
import com.telenav.tdk.core.kernel.scalars.counts.Maximum;

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
    private final Progress progress = Progress.of(this, "files");

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
        progress.withMaximum(Maximum.of(files.size()));
        progress.start("Copying " + files.size() + " files");
        for (final var source : files)
        {
            // find the path relative to the root,
            final var relative = source.relativePath(from);

            // construct a file with the same path relative to the 'to' folder,
            final var destination = to.file(relative);

            // create any parent folders that might be required
            destination.parent().mkdirs();

            // and copy the source file to the destination location
            source.safeCopyTo(destination, progress);
            progress.next();
        }
        progress.end(files.size() + " files copied");
    }
}
