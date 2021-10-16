//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.project;

import com.telenav.fiasco.dependencies.BuildableModule;
import com.telenav.fiasco.project.metadata.ProjectMetadata;

/**
 * A project containing {@link BuildableModule}s
 *
 * @author jonathanl (shibo)
 */
public abstract class Project
{
    private final ProjectMetadata metadata = new ProjectMetadata();

    public ProjectMetadata metadata()
    {
        return metadata;
    }
}
