package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;

/**
 * A resolved artifact
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

    public Artifact artifact()
    {
        return artifact;
    }

    public Pom pom()
    {
        return pom;
    }

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
