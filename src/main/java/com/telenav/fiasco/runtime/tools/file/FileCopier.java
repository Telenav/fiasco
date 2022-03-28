////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// © 2011-2021 Telenav, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.runtime.tools.file;

import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.core.language.progress.ProgressReporter;
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
        for (var source : files)
        {
            // find the path relative to the root,
            var relative = source.relativeTo(from);

            // construct a file with the same path relative to the 'to' folder,
            var destination = to.file(relative);

            // create any parent folders that might be required
            destination.parent().mkdirs();

            // and copy the source file to the destination location
            source.safeCopyTo(destination, copyMode, ProgressReporter.NULL);
            progress.next();
        }
        progress.end(files.count() + " files copied");
        return this;
    }

    public FileCopier copyMode(CopyMode mode)
    {
        copyMode = mode;
        return this;
    }
}
