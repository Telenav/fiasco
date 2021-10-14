package com.telenav.fiasco.example.fiasco;

import com.telenav.fiasco.repository.maven.MavenRepository;

/**
 * @author jonathanl (shibo)
 */
public interface Repositories
{
    MavenRepository nexus = new MavenRepository("Nexus", "http://hqb-nexus-01.telenav.com:8081/nexus/content/groups/public/");
}
