package com.telenav.fiasco.repository;

import com.telenav.fiasco.Library;
import com.telenav.kivakit.core.collections.list.ObjectList;

/**
 * @author jonathanl (shibo)
 */
public class RemoteMavenRepository extends BaseRepository
{
    public static RemoteMavenRepository mavenCentral = new RemoteMavenRepository("Maven Central", "https://repo1.maven.org/maven2/");

    public static RemoteMavenRepository local()
    {
        return new RemoteMavenRepository("Maven Central", "file://~/.m2/repository");
    }

    @SuppressWarnings("unused")
    public RemoteMavenRepository(final String name, final String url)
    {
    }

    @Override
    public ObjectList<Library> dependencies(final Library library)
    {
        return null;
    }

    @Override
    public void install(final Library library)
    {
    }

    @Override
    public boolean remove(Library library)
    {
        return false;
    }

    @Override
    public boolean removeAll()
    {
        return false;
    }
}
