package com.telenav.fiasco.example.fiasco;

import com.telenav.fiasco.repository.RemoteMavenRepository;

/**
 * @author jonathanl (shibo)
 */
public interface Repositories
{
    RemoteMavenRepository nexus = new RemoteMavenRepository("Nexus", "https://hqb-nexus-01.telenav.com:8081/nexus/content/groups/public/");
}
