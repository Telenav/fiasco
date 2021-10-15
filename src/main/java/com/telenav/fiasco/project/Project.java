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

import com.telenav.fiasco.project.metadata.Contributor;
import com.telenav.fiasco.project.metadata.Organization;
import com.telenav.fiasco.project.metadata.ProjectMetadata;

public abstract class Project
{
    private ProjectMetadata metadata = new ProjectMetadata();

    public ProjectMetadata metadata()
    {
        return metadata;
    }

    protected void contributor(final Contributor contributor)
    {
        metadata = metadata.withContributor(contributor);
    }

    @SuppressWarnings("SameParameterValue")
    protected void copyright(final String copyright)
    {
        metadata = metadata.withCopyright(copyright);
    }

    protected void organization(final Organization organization)
    {
        metadata = metadata.withOrganization(organization);
    }
}
