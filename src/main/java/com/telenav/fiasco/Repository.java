package com.telenav.fiasco;

import com.telenav.kivakit.core.collections.list.ObjectList;
import com.telenav.kivakit.core.messaging.Repeater;

/**
 * Interface to a repository that stores libraries
 *
 * @author jonathanl (shibo)
 */
public interface Repository extends Repeater
{
    /**
     * Resolves the dependencies for a library
     *
     * @return The library dependencies
     */
    ObjectList<Library> dependencies(Library library);

    /**
     * Installs the given library into this repository
     *
     * @param library The library to install
     */
    void install(Library library);

    /**
     * Removes the given library from this repository
     *
     * @param library The library to remove
     * @return True if the library was removed
     */
    boolean remove(Library library);

    /**
     * Removes all libraries from this repository
     *
     * @return True if the repository is empty
     */
    boolean removeAll();
}
