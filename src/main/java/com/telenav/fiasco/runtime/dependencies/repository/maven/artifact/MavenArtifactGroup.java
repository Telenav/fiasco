package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact;

import com.telenav.fiasco.runtime.dependencies.repository.ArtifactGroup;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.kernel.logging.Logger;
import com.telenav.kivakit.kernel.logging.LoggerFactory;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.Objects;

/**
 * A group of artifacts with an {@link #identifier()} (a.k.a. "groupId").
 *
 * <p><b>Creating a Group</b></p>
 *
 * <p>
 * A group is created with {@link #parse(Listener, String)}, passing in the Maven group identifier. The {@link
 * #defaultVersion(Version)} method can be called to specify a default version used when artifacts in the group are
 * created with {@link #artifact(String)}. This averts needing to specify the version of each artifact and allows the
 * version of all artifacts in the group to be changed in one place. For example:
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
    private static final Logger LOGGER = LoggerFactory.newLogger();

    /**
     * @return An artifact group with the given identifier
     */
    public static MavenArtifactGroup parse(Listener listener, String identifier)
    {
        return new MavenArtifactGroup(identifier);
    }

    /** Group identifier */
    private final String identifier;

    /** Default version for artifacts in this group */
    private Version defaultVersion;

    protected MavenArtifactGroup(String identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Copy constructor
     */
    protected MavenArtifactGroup(MavenArtifactGroup that)
    {
        identifier = that.identifier;
        defaultVersion = that.defaultVersion;
    }

    /**
     * @param identifier The identifier of an artifact in this group
     * @return The identified Maven artifact
     */
    public MavenArtifact artifact(String identifier)
    {
        return new MavenArtifact(new MavenArtifactDescriptor(this, identifier, defaultVersion));
    }

    public MavenArtifactGroup copy()
    {
        return new MavenArtifactGroup(this);
    }

    /**
     * Sets the default version of this group
     */
    public MavenArtifactGroup defaultVersion(Version defaultVersion)
    {
        this.defaultVersion = defaultVersion;
        return this;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MavenArtifactGroup)
        {
            MavenArtifactGroup that = (MavenArtifactGroup) object;
            return identifier().equals(that.identifier());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(identifier());
    }

    /**
     * @return The Maven group identifier for this group
     */
    @Override
    public String identifier()
    {
        return identifier;
    }

    /**
     * @return THh path to this group from a repository root
     */
    @Override
    public FilePath path()
    {
        return FilePath.parseFilePath(LOGGER, identifier().replace(".", "/"));
    }

    @Override
    public String toString()
    {
        return identifier();
    }
}
