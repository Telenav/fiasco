package com.telenav.fiasco.mockup;

import static com.telenav.fiasco.build.project.metadata.Role.ARCHITECT;
import static com.telenav.fiasco.build.project.metadata.Role.ARTIST;
import static com.telenav.fiasco.build.project.metadata.Role.COMMITTER;
import static com.telenav.fiasco.build.project.metadata.Role.DEVELOPER;
import static com.telenav.fiasco.build.project.metadata.Role.LEAD;
import static com.telenav.fiasco.build.project.metadata.Role.ORIGINATOR;
import static com.telenav.fiasco.build.project.metadata.Role.WRITER;

public class ExampleProject extends TelenavProject
{
    @Override
    public void onInitialize()
    {
        metadata(super.projectMetadata()
                .withCopyright(copyright(2021))
                .withContributor(jonathan()
                        .withRoles(ARCHITECT, ARTIST, COMMITTER, DEVELOPER, LEAD, ORIGINATOR, WRITER)));

        require(apacheWicket().core(),
                apacheWicket().util(),
                apacheCommons().lang3(),
                kivakit().application(),
                kivakit().networkHttp());

        project("subproject");
    }
}
