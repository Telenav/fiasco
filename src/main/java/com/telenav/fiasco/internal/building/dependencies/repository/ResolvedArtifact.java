package com.telenav.fiasco.internal.building.dependencies.repository;

import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;

/**
 * A resolved artifact, including the artifact's repository, resources and POM.
 *
 * @author jonathanl (shibo)
 */
public class ResolvedArtifact
{
    /** The repository where the artifact was found */
    private final ArtifactRepository repository;

    /** The artifact that was resolved */
    private final Artifact artifact;

    /** The POM information for the artifact */
    private final Pom pom;

    public ResolvedArtifact(ArtifactRepository repository,
                            Artifact artifact,
                            Pom pom)
    {
        this.repository = repository;
        this.artifact = artifact;
        this.pom = pom;
    }

    /**
     * @return The artifact
     */
    public Artifact artifact()
    {
        return artifact;
    }

    /**
     * @return The POM for the artifact
     */
    public Pom pom()
    {
        return pom;
    }

    /**
     * @return The repository where the artifact was found
     */
    public ArtifactRepository repository()
    {
        return repository;
    }

    /**
     * The resource with the given extension for this artifact
     *
     * @param extension The extension
     * @return The resource
     */
    public Resource resource(Extension extension)
    {
        return repository().resource(artifact, extension);
    }
}
