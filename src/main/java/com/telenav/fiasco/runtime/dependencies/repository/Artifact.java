package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.kernel.messaging.Listener;
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
public interface Artifact extends Named, Dependency
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
     * @return This artifact's path relative to repository root
     */
    FilePath path();

    /**
     * @return This artifact's resource with the given extensions at the given repository root path
     */
    Resource resource(FilePath repositoryRoot, Extension extension);

    /**
     * @return The expected artifact resources at the given path
     */
    ObjectList<Resource> resources(FilePath path);

    /**
     * @return The version of this artifact
     */
    String version();

    /**
     * @return The artifact version text as a {@link Version}, or null if the text is not a valid {@link Version}
     */
    default Version version(Listener listener)
    {
        return Version.parse(listener, version());
    }

    /**
     * @return This artifact with the given group
     */
    Artifact withGroup(ArtifactGroup group);

    /**
     * @return This artifact with the given identifier
     */
    Artifact withIdentifier(String identifier);

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

    /**
     * @return This artifact with no version
     */
    Artifact withoutVersion();
}
