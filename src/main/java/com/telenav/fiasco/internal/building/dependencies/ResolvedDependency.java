package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;

/**
 * <b>Not public API</b>
 *
 * <p>
 * A resolved dependency.
 * </p>
 *
 * <p>
 * If the dependency is an artifact, contains the artifact repository where the dependency was found and its {@link Pom}
 * if it is a Maven artifact.
 *
 * @author jonathanl (shibo)
 * @see ArtifactRepository
 * @see Artifact
 * @see Pom
 */
public class ResolvedDependency
{
    /** The repository where the artifact was found */
    private final ArtifactRepository repository;

    /** The artifact that was resolved */
    private final Artifact artifact;

    /** The POM information for the artifact */
    private final Pom pom;

    public ResolvedDependency(ArtifactRepository repository,
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