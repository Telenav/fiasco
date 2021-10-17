package com.telenav.fiasco.dependencies.repository;

import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.Library;
import com.telenav.fiasco.dependencies.repository.maven.MavenArtifact;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * A dependent artifact with a name, group and version
 *
 * @author jonathanl (shibo)
 * @see Dependency
 * @see MavenArtifact
 */
public interface Artifact extends Named, Dependency
{
    /**
     * @return This artifact as a library
     */
    default Library asLibrary()
    {
        return new Library(this);
    }

    /**
     * @return The artifact descriptor [group-name]:[artifact-name]:[version]
     */
    default String descriptor()
    {
        return group().name() + ":" + name() + ":" + version();
    }

    /**
     * @return The group to which this artifact belongs
     */
    ArtifactGroup group();

    /**
     * @return This artifact's path relative to repository root
     */
    FilePath path();

    /**
     * @return The version of this artifact
     */
    Version version();

    /**
     * @return The given version of this artifact
     */
    Artifact withVersion(Version version);

    /**
     * @return The given version of this artifact
     */
    default Artifact withVersion(String version)
    {
        return withVersion(Version.parse(version));
    }
}
