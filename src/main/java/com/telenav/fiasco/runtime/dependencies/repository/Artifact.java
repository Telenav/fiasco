package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifact;
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
     * @return The group to which this artifact belongs
     */
    default ArtifactGroup group()
    {
        return descriptor().group();
    }

    /**
     * @return The artifact identifier
     */
    String identifier();

    /**
     * @return True if this artifact is matched by the given artifact. The two artifacts can be equal, or the given
     * artifact can be missing a version and match only the group and identifier.
     */
    default boolean matches(Artifact that)
    {
        return descriptor().matches(that.descriptor());
    }

    /**
     * @return The fully-qualified name of the artifact
     */
    @Override
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
    ObjectList<Resource> resources(FilePath path);

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
        return withVersion(Version.parse(this, version));
    }
}
