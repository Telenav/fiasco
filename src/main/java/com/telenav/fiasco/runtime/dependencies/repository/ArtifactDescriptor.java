package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactDescriptor;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.path.FilePath;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

/**
 * Model for a Maven artifact descriptor of the form: [group-identifier]:[artifact-identifier](:[version])?
 *
 * <p><b>Properties</b></p>
 *
 * <ul>
 *     <li>{@link #group} - The Maven group with groupId value</li>
 *     <li>{@link #identifier()} - The artifact identifier</li>
 *     <li>{@link #version()} - The artifact version, or null for all versions</li>
 *     <li>{@link #name()} - The fully qualified artifact descriptor, like "com.telenav.kivakit:kivakit-kernel:9.5.0"</li>
 *     <li>{@link #path()} - Relative path to the artifact folder, like "com/telenav/kivakit/kivakit-kernel/9.5.0"</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public interface ArtifactDescriptor extends Named
{
    enum MatchType
    {
        EXACT,
        EXCLUDING_VERSION
    }

    /**
     * @return The artifact group (groupId)
     */
    ArtifactGroup group();

    /**
     * @return The artifact identifier (artifactId)
     */
    String identifier();

    /**
     * @return True if this artifact descriptor includes a group, an identifier and a resolved version. A version is not
     * resolved until it is non-null and does not contain any property references of the form "${property-name}.
     */
    boolean isResolved();

    /**
     * @return True if this descriptor logically matches that descriptor
     */
    boolean matches(ArtifactDescriptor that, MatchType type);

    /**
     * @return The fully qualified artifact descriptor, like "com.telenav.kivakit:kivakit-kernel:9.5.0"
     */
    @Override
    String name();

    /**
     * @return Path to the artifact folder, like "com/telenav/kivakit/kivakit-kernel/9.5.0"
     */
    FilePath path();

    /**
     * Resolve the version for this artifact descriptor using the given properties if it contains any references of the
     * form "${property-name}"
     */
    MavenArtifactDescriptor resolvePropertyReferences(PropertyMap properties);

    /**
     * @return The artifact version, or null if the descriptor matches all artifact versions
     */
    String version();

    /**
     * @return The artifact version text as a {@link Version}, or null if the text is not a valid {@link Version}
     */
    default Version version(Listener listener)
    {
        return Version.parse(listener, version());
    }
}
