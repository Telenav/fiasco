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

package com.telenav.fiasco.build.tools;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.kivakit.component.Component;

/**
 * Interface for executable tools.
 *
 * @author jonathanl (shibo)
 */
public interface Tool extends Component
{
    /**
     * Sets the project for this tool
     *
     * @param build The build
     */
    void build(final FiascoBuild build);

    /**
     * @return The project that this tool is running in
     */
    FiascoBuild build();
}
