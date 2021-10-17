package com.telenav.fiasco.mockup;

import com.telenav.fiasco.build.project.BaseProject;
import com.telenav.fiasco.build.project.metadata.Contributor;
import com.telenav.fiasco.build.project.metadata.License;
import com.telenav.fiasco.build.project.metadata.Organization;
import com.telenav.fiasco.build.project.metadata.ProjectMetadata;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.network.core.EmailAddress;

import static com.telenav.fiasco.build.project.metadata.Role.ARCHITECT;
import static com.telenav.fiasco.build.project.metadata.Role.ARTIST;
import static com.telenav.fiasco.build.project.metadata.Role.COMMITTER;
import static com.telenav.fiasco.build.project.metadata.Role.DEVELOPER;
import static com.telenav.fiasco.build.project.metadata.Role.LEAD;
import static com.telenav.fiasco.build.project.metadata.Role.ORIGINATOR;
import static com.telenav.fiasco.build.project.metadata.Role.WRITER;

public class ExampleProject extends BaseProject
{
    @Override
    public void onInitialize()
    {
        metadata(createMetadata());

        var wicket = apache().wicket().version("9.5.0");
        var commons = apache().commons().version("3.7");
        var kivakit = telenav().kivakit().version("1.1.0");

        require(wicket.core(),
                wicket.util(),
                commons.lang3(),
                kivakit.application(),
                kivakit.networkHttp());

        project("subproject");
    }

    private ProjectMetadata createMetadata()
    {
        final var telenav = new Organization("Telenav")
                .withWebsite("https://www.telenav.com");

        final var jonathan = new Contributor("Jonathan Locke")
                .withEmail(EmailAddress.parse("jonathanl@telenav.com"))
                .withRoles(ARCHITECT, ARTIST, COMMITTER, DEVELOPER, LEAD, ORIGINATOR, WRITER);

        final var license = License.create().withBody(packageResource("license.txt"));

        final var copyright = StringSource.of("Copyright (C) 2021");

        return ProjectMetadata.create()
                .withOriginator(telenav)
                .withOwner(telenav)
                .withCopyright(copyright)
                .withContributor(jonathan)
                .withLicense(license);
    }
}
