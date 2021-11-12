package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact;

import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.Validatable;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * Model for a Maven artifact descriptor of the form: [group-identifier]:[artifact-identifier](:[version])?
 *
 * <p><b>Creation</b></p>
 *
 * <p>
 * Artifact descriptors can be created by calling the {@link #MavenArtifactDescriptor(MavenArtifactGroup, String,
 * String)} constructor, or by parsing a descriptor string with {@link #parse(Listener, String)}.
 * </p>
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
 * <p><b>Functional methods</b></p>
 *
 * <p>
 * The method {@link #withVersion(String)} returns a copy of this descriptor with a different version
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifactDescriptor implements ArtifactDescriptor, Validatable, Named
{
    /** Regular expression for parsing descriptors like "com.telenav.kivakit:kivakit-kernel:9.5.0" */
    private static final Pattern DESCRIPTOR_PATTERN = Pattern.compile("(?<group>[A-Za-z.]+)"
            + ":"
            + "(?<identifier>[A-Za-z-.]+)"
            + "(:"
            + "(?<version>[\\d.]+(-(snapshot|alpha|beta|rc|final))?)"
            + ")?");

    /**
     * Parses a descriptor of the form [group-identifier]:[artifact-identifier](:[version])?
     *
     * @return A {@link MavenArtifactDescriptor} for the given descriptor, or an exception is thrown
     */
    public static MavenArtifactDescriptor parse(Listener listener, String text)
    {
        var matcher = DESCRIPTOR_PATTERN.matcher(text);
        if (matcher.matches())
        {
            var group = new MavenArtifactGroup(matcher.group("group"));
            var identifier = matcher.group("identifier");
            var version = matcher.group("version");

            var descriptor = new MavenArtifactDescriptor(group, identifier, version);
            if (descriptor.isValid(listener))
            {
                return descriptor;
            }
            else
            {
                fail("Maven artifact descriptor is invalid: $", descriptor);
            }
        }
        return fail("Unable to parse Maven artifact descriptor: $", text);
    }

    /** The group of this artifact */
    private MavenArtifactGroup group;

    /** The artifact identifier */
    private String identifier;

    /** The artifact version */
    private String version;

    public MavenArtifactDescriptor(MavenArtifactGroup group, String identifier, String version)
    {
        this.group = ensureNotNull(group);
        this.identifier = ensureNotNull(identifier);
        this.version = version;
    }

    /**
     * Copy constructor
     */
    protected MavenArtifactDescriptor(MavenArtifactDescriptor that)
    {
        this(that.group, that.identifier, that.version);
    }

    /**
     * @return A deep copy of this descriptor
     */
    public MavenArtifactDescriptor copy()
    {
        return new MavenArtifactDescriptor(this);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MavenArtifactDescriptor)
        {
            var that = (MavenArtifactDescriptor) object;
            return name().equals(that.name());
        }
        return false;
    }

    /**
     * @return The group for this descriptor
     */
    @Override
    public MavenArtifactGroup group()
    {
        return group;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name());
    }

    /**
     * @return The identifier for this descriptor
     */
    @Override
    public String identifier()
    {
        return identifier;
    }

    /**
     * @return True if this descriptor is complete (has a version)
     */
    public boolean isResolved()
    {
        return version != null;
    }

    @Override
    public boolean matches(ArtifactDescriptor that)
    {
        return group.equals(that.group())
                && identifier.equals(that.identifier())
                && (version == null || that.version() == null || version.equals(that.version()));
    }

    /**
     * @return The fully qualified name of this artifact in the format: [group-identifier]:[artifact-identifier]:[version]
     */
    @Override
    public String name()
    {
        return group() + ":" + identifier() + (version() == null ? "" : ":" + version());
    }

    /**
     * @return The full path for this descriptor relative to an arbitrary repository root. For example,
     * "com/telenav/kivakit/kivakit-kernel/9.5.0"
     */
    @Override
    public FilePath path()
    {
        return group()
                .path()
                .withChild(identifier())
                .withChild(version);
    }

    @Override
    public String toString()
    {
        return name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator validator(ValidationType type)
    {
        return new BaseValidator()
        {
            @Override
            protected void onValidate()
            {
                problemIf(group == null, "No artifact group");
                problemIf(identifier == null, "No artifact identifier");
            }
        };
    }

    /**
     * @return The version text of this descriptor
     */
    @Override
    public String version()
    {
        return version;
    }

    /**
     * @return The given version of this artifact
     */
    public MavenArtifactDescriptor withGroup(MavenArtifactGroup group)
    {
        var copy = copy();
        copy.group = group;
        return copy;
    }

    /**
     * @return The given version of this artifact
     */
    public MavenArtifactDescriptor withIdentifier(String identifier)
    {
        var copy = copy();
        copy.identifier = identifier;
        return copy;
    }

    /**
     * @return The given version of this artifact
     */
    public MavenArtifactDescriptor withVersion(String version)
    {
        var copy = copy();
        copy.version = version;
        return copy;
    }

    /**
     * @return This descriptor without any version
     */
    public MavenArtifactDescriptor withoutVersion()
    {
        return withVersion(null);
    }
}
