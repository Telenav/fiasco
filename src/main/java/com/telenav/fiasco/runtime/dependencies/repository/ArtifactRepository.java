package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.core.interfaces.naming.Named;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * An {@link Artifact} repository where artifacts are stored. The folder containing a given artifact can be retrieved
 * with {@link #folder(Artifact)} and a given artifact resource with {@link #resource(Artifact, Extension)}.
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
     * @return The folder containing the given artifact in this repository
     */
    default Folder folder(Artifact artifact)
    {
        return Folder.of(pathTo(artifact));
    }

    /**
     * @return True if this repository is remote
     */
    boolean isRemote();

    /**
     * @return Path to the folder containing the given artifact in this repository
     */
    FilePath pathTo(Artifact artifact);

    /**
     * @return The resource for the given artifact and extension in this repository
     */
    Resource resource(Artifact artifact, Extension extension);
}
