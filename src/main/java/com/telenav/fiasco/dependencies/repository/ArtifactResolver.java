package com.telenav.fiasco.dependencies.repository;

import com.telenav.fiasco.dependencies.Library;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Resolves a {@link Library} into a set of installed artifacts, possibly copying them from a remote repository.
 *
 * @author jonathanl (shibo)
 */
public interface ArtifactResolver
{
    /**
     * Resolves the give artifact from a remote repository into the local one
     *
     * @param artifact The artifact to resolve
     * @return The repository where the artifact was found
     */
    ArtifactRepository resolve(Artifact artifact);

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
            var artifact = (Artifact) dependency;
            resolve(artifact);
            artifacts.add(artifact);
        }
        return artifacts;
    }
}
