package com.telenav.fiasco.repository;

import com.telenav.fiasco.Library;
import com.telenav.fiasco.Repository;
import com.telenav.kivakit.core.collections.list.ObjectList;
import com.telenav.kivakit.filesystem.Folder;

/**
 * A read-only repository that caches artifacts read from the network.
 *
 * <p>
 * The artifacts in this cache repository are downloaded once and only once. Because it only contains valid artifacts
 * from the repositories that it is configured to cache, it allows rapid population of the {@link LocalRepository}
 * without accessing the network.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class RemoteCacheRepository extends BaseRepository
{
    /** The folder where this cache stores artifacts */
    private final Folder folder;

    /** The repositories that this cache is caching */
    private final Repository[] repositories;

    /**
     * @param folder The folder for storing cached artifacts
     * @param repositories The repositories to cache, in search order
     */
    public RemoteCacheRepository(Folder folder, Repository... repositories)
    {
        this.folder = folder;
        this.repositories = repositories;
    }

    @Override
    public ObjectList<Library> dependencies(Library library)
    {
        return null;
    }

    @Override
    public void install(Library library)
    {
        // copy library into this cache
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
