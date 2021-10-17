//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.build.tools.file;

import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.progress.ProgressReporter;
import com.telenav.kivakit.resource.CopyMode;

/**
 * Copies selected files from one folder to another.
 *
 * @author shibo
 */
public class FileCopier extends BaseFileTool
{
    /** Copying mode */
    private CopyMode copyMode = CopyMode.OVERWRITE;

    /**
     * Copies the given files relative to the "from" folder to the "to" folder
     */
    public FileCopier copy(Folder from, Folder to, FileList files)
    {
        // For each source file that matches,
        var progress = progress("Copying", files);
        for (final var source : files)
        {
            // find the path relative to the root,
            final var relative = source.relativeTo(from);

            // construct a file with the same path relative to the 'to' folder,
            final var destination = to.file(relative);

            // create any parent folders that might be required
            destination.parent().mkdirs();

            // and copy the source file to the destination location
            source.safeCopyTo(destination, copyMode, ProgressReporter.NULL);
            progress.next();
        }
        progress.end(files.count() + " files copied");
        return this;
    }

    public FileCopier copyMode(final CopyMode mode)
    {
        this.copyMode = mode;
        return this;
    }
}
