package com.telenav.fiasco.build.repository;

import com.telenav.fiasco.build.dependencies.Dependency;
import com.telenav.fiasco.build.repository.maven.MavenArtifact;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * A dependent artifact with an identifier, group and version.
 *
 * @author jonathanl (shibo)
 * @see Dependency
 * @see MavenArtifact
 */
public interface Artifact extends Dependency
{
    /**
     * @return The artifact descriptor [group-name]:[artifact-name]:[version]
     */
    default String descriptor()
    {
        return group().identifier() + ":" + identifier() + ":" + version();
    }

    /**
     * @return The group to which this artifact belongs
     */
    ArtifactGroup group();

    /**
     * @return The artifact identifier
     */
    String identifier();

    /**
     * @return The fully-qualified name of the artifact
     */
    String name();

    /**
     * @return This artifact's path relative to repository root
     */
    FilePath path();

    /**
     * @return This artifact's resource with the given extensions at the given repository path
     */
    Resource resource(FilePath path, Extension extension);

    /**
     * @return The expected artifact resources at the given path
     */
    ObjectList<Resource> resources(final FilePath path);

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
