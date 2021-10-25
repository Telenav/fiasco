package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * An {@link Artifact} repository where artifacts can be installed with {@link #install(ArtifactRepository, Artifact)}.
 * The path to an artifact can be found with {@link #folderPath(Artifact)}.
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
     * @return Path to the folder containing the given artifact in this repository
     */
    FilePath folderPath(Artifact artifact);

    /**
     * Installs the given artifact and all its dependencies from the given repository into this one
     *
     * @param source The source repository to copy from
     * @param artifact The artifact to install
     * @return True if the artifact from the source repository was installed in this repository
     */
    boolean install(ArtifactRepository source, Artifact artifact);

    /**
     * @return True if this repository is remote
     */
    boolean isRemote();
}
