//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.build.tools.file;

import com.telenav.cactus.build.metadata.BuildMetadataUpdater;

/**
 * Updates the build.properties file for a project
 *
 * @author shibo
 */
public class BuildStamper extends BaseFileTool
{
    public void save()
    {
        BuildMetadataUpdater.main(new String[] { project()
                .root()
                .absolute()
                .path().toString() });
    }
}
