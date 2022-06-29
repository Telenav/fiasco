package com.telenav.fiasco.repository.maven;

import com.telenav.fiasco.Library;
import com.telenav.fiasco.Repository;

import java.util.List;

/**
 * @author jonathanl (shibo)
 */
public class MavenRepository implements Repository
{
    public static MavenRepository mavenCentral = new MavenRepository("Maven Central", "https://repo1.maven.org/maven2/");

    public static MavenRepository local()
    {
        return new MavenRepository("Maven Central", "file://~/.m2/repository");
    }

    @SuppressWarnings("unused")
    public MavenRepository(final String name, final String url)
    {
    }

    @Override
    public void install(final Library library)
    {
    }

    @Override
    public List<Library> resolve(final Library library)
    {
        return null;
    }
}
