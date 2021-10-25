package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;

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
    private final PomReader.Pom pom;

    public ResolvedArtifact(ArtifactRepository repository,
                            Artifact artifact,
                            PomReader.Pom pom)
    {
        this.repository = repository;
        this.artifact = artifact;
        this.pom = pom;
    }

    public PomReader.Pom pom()
    {
        return pom;
    }

    public ArtifactRepository repository()
    {
        return repository;
    }
}
