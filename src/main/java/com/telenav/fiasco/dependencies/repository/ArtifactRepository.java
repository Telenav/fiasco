package com.telenav.fiasco.dependencies.repository;

import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * An {@link Artifact} repository where artifacts can be installed with {@link #install(ArtifactRepository, Artifact)}.
 * The path to an artifact can be found with {@link #path(Artifact)}.
 *
 * @author jonathanl (shibo)
 */
public interface ArtifactRepository extends Named, Component
{
    /**
     * @return True if this repository contains the given artifact
     */
    boolean contains(Artifact artifact);

    /**
     * Installs the given artifact from the given repository into this one
     *
     * @param source The source repository to copy from
     * @param artifact The artifact to install
     */
    void install(ArtifactRepository source, Artifact artifact);

    /**
     * @return Path to the folder containing the given artifact in this repository
     */
    FilePath path(Artifact artifact);
}
