package com.telenav.fiasco.example.fiasco;

import com.telenav.fiasco.Project;
import com.telenav.fiasco.example.fiasco.modules.Client;
import com.telenav.fiasco.example.fiasco.modules.Server;
import com.telenav.fiasco.metadata.Contributor;
import com.telenav.fiasco.metadata.Organization;
import com.telenav.kivakit.filesystem.Folder;

import static com.telenav.fiasco.repository.RemoteMavenRepository.mavenCentral;

/**
 * @author jonathanl (shibo)
 */
@SuppressWarnings({ "InnerClassMayBeStatic", "unused" })
public class Fiasco extends Project implements Libraries, Repositories
{
    public Fiasco(final Folder root)
    {
        super(root);

        artifact("com.telenav.fiasco:fiasco-example:1.0");
        organization(new Organization("Telenav")
                .withUrl("https://www.telenav.com"));
        copyright("Copyright 2020, Telenav Inc. All Rights Reserved.");
        contributor(new Contributor("shibo")
                .withRole("Project Lead")
                .withEmail("shibo@telenav.com"));

        librarian()
                .lookIn(mavenCentral)
                .lookIn(nexus);

        requires(new Client(this));
        requires(new Server(this));
    }
}
