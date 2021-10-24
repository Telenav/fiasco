package com.telenav.fiasco.build.dependencies.repository;

import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.path.FilePath;

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
public interface ArtifactDescriptor
{
    /**
     * @return The artifact group (groupId)
     */
    ArtifactGroup group();

    /**
     * @return The artifact identifier (artifactId)
     */
    String identifier();

    /**
     * @return The fully qualified artifact descriptor, like "com.telenav.kivakit:kivakit-kernel:9.5.0"
     */
    String name();

    /**
     * @return Path to the artifact folder, like "com/telenav/kivakit/kivakit-kernel/9.5.0"
     */
    FilePath path();

    /**
     * @return The artifact version, or null if the descriptor matches all artifact versions
     */
    Version version();
}
