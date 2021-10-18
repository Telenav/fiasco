package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.ArtifactGroup;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.path.FilePath;

/**
 * A group of artifacts with an {@link #identifier()}. A group is created with {@link #create(String)}, passing in the
 * Maven group identifier. The {@link #version(String)} method can be called with a version. This version is used as the
 * default version when artifacts in the group are created with {@link #artifact(String)}. This averts needing to
 * specify the version of each artifact and allows the version of all artifacts in the group to be changed in one place.
 * For example:
 * <pre>
 * var group = MavenArtifactGroup.create("com.telenav.kivakit").version("1.1.0");
 * var application = group.artifact("kivakit-application");
 * var kernel = group.artifact("kivakit-kernel");
 * </pre>
 * The path relative to the root of a repository to this artifact group can be retrieved with {@link #path()}. If the
 * group identifier is "com.telenav.kivakit", the path will be "com/telenav/kivakit".
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
    private Version version;

    protected MavenArtifactGroup(final String identifier)
    {
        this.identifier = identifier;
    }

    protected MavenArtifactGroup(MavenArtifactGroup that)
    {
        this.identifier = that.identifier;
        this.version = that.version;
    }

    /**
     * @param identifier The identifier of an artifact in this group
     * @return The identified Maven artifact
     */
    public MavenArtifact artifact(String identifier)
    {
        return new MavenArtifact(this, identifier).withVersion(version);
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

    /**
     * @return This group with the given default version. The {@link #artifact(String)} method will use this default.
     */
    public MavenArtifactGroup version(final String defaultVersion)
    {
        return version(Version.parse(defaultVersion));
    }

    /**
     * @return This group with the given default version. The {@link #artifact(String)} method will use this default.
     */
    public MavenArtifactGroup version(final Version defaultVersion)
    {
        var copy = new MavenArtifactGroup(this);
        copy.version = defaultVersion;
        return copy;
    }
}
