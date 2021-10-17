//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.build.tools;

import com.telenav.fiasco.build.project.Project;
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
     * @param project The project
     */
    void project(final Project project);

    /**
     * @return The project that this tool is running in
     */
    Project project();
}
