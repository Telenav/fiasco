package com.telenav.fiasco.dependencies.repository;

import com.telenav.fiasco.dependencies.Library;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * An {@link Artifact} repository
 */
public interface Repository
{
    /**
     * Puts the given artifact into the repository
     *
     * @param artifact The artifact to install
     */
    void install(Artifact artifact);

    /**
     * Installs the artifact dependencies of the given library
     *
     * @param library The library
     */
    default void install(Library library)
    {
        for (var artifact : resolve(library))
        {
            install(artifact);
        }
    }

    /**
     * Resolves the give artifact from a remote repository
     *
     * @param artifact The artifact to resolve
     */
    Artifact resolve(Artifact artifact);

    /**
     * Resolves the artifact dependencies of the given library by copying them from a remote source into the local
     * repository, if necessary
     *
     * @return The dependent artifacts
     */
    default ObjectList<Artifact> resolve(final Library library)
    {
        // For each dependency of the library,
        var artifacts = new ObjectList<Artifact>();
        for (var dependency : library.dependencies())
        {
            // resolve the dependency as an artifact (which it must be).
            artifacts.add(resolve((Artifact) dependency));
        }
        return artifacts;
    }
}
