package com.telenav.fiasco.build.repository.maven;

import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.Validatable;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * Model for a Maven artifact descriptor of the form: [group-identifier]:[artifact-identifier](:[version])?
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifactDescriptor implements Validatable, Named
{
    /** Regular expression for parsing descriptors like "com.telenav.kivakit:kivakit-kernel:9.5.0" */
    private static final Pattern DESCRIPTOR_PATTERN = Pattern.compile("(?<group>[A-Za-z.]+)"
            + ":"
            + "(?<identifier>[A-Za-z-.]+)"
            + "(:"
            + "(?<version>[\\d.]+(-(snapshot|alpha|beta|rc|final)))"
            + ")?");

    /**
     * Parses a descriptor of the form [group-identifier]:[artifact-identifier](:[version])?
     *
     * @return A {@link MavenArtifactDescriptor} for the given descriptor, or an exception is thrown
     */
    public static MavenArtifactDescriptor parse(Listener listener, final String text)
    {
        final var matcher = DESCRIPTOR_PATTERN.matcher(text);
        if (matcher.matches())
        {
            final var group = new MavenArtifactGroup(matcher.group("group"));
            final var identifier = matcher.group("identifier");
            final var version = Version.parse(matcher.group("version"));

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
    private final MavenArtifactGroup group;

    /** The artifact identifier */
    private final String identifier;

    /** The artifact version */
    private Version version;

    public MavenArtifactDescriptor(final MavenArtifactGroup group, final String identifier, Version version)
    {
        this.group = group;
        this.identifier = identifier;
        this.version = version;
    }

    public MavenArtifactDescriptor(MavenArtifactDescriptor that)
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
    public boolean equals(final Object object)
    {
        if (object instanceof MavenArtifactDescriptor)
        {
            var that = (MavenArtifactDescriptor) object;
            return this.toString().equals(that.toString());
        }
        return false;
    }

    /**
     * @return The group for this descriptor
     */
    public MavenArtifactGroup group()
    {
        return group;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(toString());
    }

    /**
     * @return The identifier for this descriptor
     */
    public String identifier()
    {
        return identifier;
    }

    /**
     * @return The fully qualified name of this artifact in the format: [group-identifier]:[artifact-identifier]:[version]
     */
    public String name()
    {
        return group() + ":" + identifier() + (version() == null ? "" : ":" + version());
    }

    /**
     * @return The full path for this descriptor relative to an arbitrary repository root
     */
    public FilePath path()
    {
        return group()
                .path()
                .withChild(identifier())
                .withChild(version.toString());
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
    public Validator validator(final ValidationType type)
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
     * @return The version of this descriptor
     */
    public Version version()
    {
        return version;
    }

    /**
     * @return The given version of this artifact
     */
    public MavenArtifactDescriptor withVersion(final Version version)
    {
        final var copy = new MavenArtifactDescriptor(this);
        copy.version = version;
        return copy;
    }
}
