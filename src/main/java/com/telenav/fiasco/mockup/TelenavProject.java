package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.project.BaseProject;
import com.telenav.fiasco.build.project.metadata.Contributor;
import com.telenav.fiasco.build.project.metadata.Copyright;
import com.telenav.fiasco.build.project.metadata.License;
import com.telenav.fiasco.build.project.metadata.Organization;
import com.telenav.fiasco.build.project.metadata.ProjectMetadata;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.network.core.EmailAddress;

/**
 * Base class for Telenav projects
 *
 * @author jonathanl (shibo)
 */
public class TelenavProject extends BaseProject implements TelenavArtifacts
{
    protected Copyright copyright(int firstYear)
    {
        return new Copyright(StringSource.of("Copyright (C) " + firstYear));
    }

    protected Contributor jonathan()
    {
        return new Contributor("Jonathan Locke")
                .withEmail(EmailAddress.parse("jonathanl@telenav.com"));
    }

    protected License license()
    {
        return License.create().withBody(packageResource("LICENSE"));
    }

    protected ProjectMetadata projectMetadata()
    {
        var telenav = new Organization("Telenav")
                .withWebsite("https://www.telenav.com");

        return ProjectMetadata.create()
                .withOriginator(telenav)
                .withOwner(telenav)
                .withLicense(license());
    }
}
