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

package com.telenav.fiasco.runtime.tools.resource;

import com.telenav.kivakit.core.progress.ProgressReporter;
import com.telenav.kivakit.resource.CopyMode;
import com.telenav.kivakit.resource.ResourceFolder;
import com.telenav.kivakit.resource.ResourceList;

/**
 * Copies selected files from one folder to another.
 *
 * @author shibo
 */
@SuppressWarnings("unused")
public class Copier extends BaseResourceTool
{
    /** Copying mode */
    private CopyMode copyMode = CopyMode.OVERWRITE;

    /**
     * Copies the given files relative to the "from" folder to the "to" folder
     */
    public Copier copy(ResourceFolder<?> from, ResourceFolder<?> to, ResourceList resources)
    {
        // For each source file that matches,
        var progress = progress("Copying", resources);
        for (var resource : resources)
        {
            // find the resource relative to the root,
            var relative = resource.relativeTo(from);

            // construct a resource with the same path relative to the 'to' folder,
            var destination = to.resource(relative.path()).asWritable();

            // create any parent folders that might be required
            destination.parent().mkdirs();

            // and copy the source file to the destination location
            resource.safeCopyTo(destination, copyMode, ProgressReporter.none());
            progress.next();
        }
        progress.end(resources.count() + " files copied");
        return this;
    }

    public Copier copyMode(CopyMode mode)
    {
        copyMode = mode;
        return this;
    }
}
