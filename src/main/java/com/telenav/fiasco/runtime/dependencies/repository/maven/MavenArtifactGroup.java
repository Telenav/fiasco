package com.telenav.fiasco.runtime.dependencies.repository.maven;

import com.telenav.fiasco.runtime.dependencies.repository.ArtifactGroup;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.path.FilePath;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;

/**
 * A group of artifacts with an {@link #identifier()} (a.k.a. "groupId").
 *
 * <p><b>Creating a Group</b></p>
 *
 * <p>
 * A group is created with {@link #create(String)}, passing in the Maven group identifier. The {@link
 * #defaultVersion(String)} or {@link #defaultVersion(Version)} method can be called to specify a default version used
 * when artifacts in the group are created with {@link #artifact(String)}. This averts needing to specify the version of
 * each artifact and allows the version of all artifacts in the group to be changed in one place. For example:
 * <pre>
 * var group = MavenArtifactGroup.create("com.telenav.kivakit").version("1.1.0");
 * var application = group.artifact("kivakit-application");
 * var kernel = group.artifact("kivakit-kernel");</pre>
 * </p>
 *
 * <p><b>Repository Path</b></p>
 *
 * <p>
 * The path relative to the root of a repository to this artifact group can be retrieved with {@link #path()}. If the
 * group identifier is "com.telenav.kivakit", the path will be "com/telenav/kivakit".
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifactGroup implements ArtifactGroup
{
    /**
     * @return An artifact group with the given identifier
     */
    public static MavenArtifactGroup create(String identifier)
    {
        return new MavenArtifactGroup(identifier);
    }

    /** Group identifier */
    private final String identifier;

    /** Default version for artifacts in this group */
    private Version defaultVersion;

    protected MavenArtifactGroup(final String identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Copy constructor
     */
    protected MavenArtifactGroup(MavenArtifactGroup that)
    {
        this.identifier = that.identifier;
        this.defaultVersion = that.defaultVersion;
    }

    /**
     * @param identifier The identifier of an artifact in this group
     * @return The identified Maven artifact
     */
    public MavenArtifact artifact(String identifier)
    {
        ensureNotNull(defaultVersion, "Artifact group has no default version");
        return new MavenArtifact(new MavenArtifactDescriptor(this, identifier, defaultVersion));
    }

    public MavenArtifactGroup copy()
    {
        return new MavenArtifactGroup(this);
    }

    /**
     * Sets the default version of this group
     */
    public MavenArtifactGroup defaultVersion(final String defaultVersion)
    {
        this.defaultVersion = Version.parse(defaultVersion);
        return this;
    }

    /**
     * Sets the default version of this group
     */
    public MavenArtifactGroup defaultVersion(final Version defaultVersion)
    {
        this.defaultVersion = defaultVersion;
        return this;
    }

    /**
     * @return The Maven group identifier for this group
     */
    public String identifier()
    {
        return identifier;
    }

    /**
     * @return THh path to this group from a repository root
     */
    public FilePath path()
    {
        return FilePath.parseFilePath(identifier().replace(".", "/"));
    }

    public String toString()
    {
        return identifier();
    }
}
