package com.telenav.fiasco.build.repository.maven;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.internal.dependencies.BaseDependency;
import com.telenav.fiasco.internal.dependencies.Dependency;
import com.telenav.fiasco.internal.dependencies.DependencyList;
import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * A Maven artifact stored in a {@link MavenRepository} and belonging to a {@link MavenArtifactGroup} and having other
 * artifacts as {@link #dependencies()}. An artifact can be created from a descriptor with {@link #parse(Listener,
 * String)}, where the descriptor is of the form [group-identifier]:[artifact-identifier]:[version].
 *
 * <p><b>Properties</b></p>
 * <ul>
 *     <li>{@link #group()} - The group to which this artifact belongs</li>
 *     <li>{@link #identifier()} - The artifact identifier</li>
 *     <li>{@link #fullyQualifiedName()} - The fully qualified name of the artifact: [group-name]:[artifact-name]:[version]</li>
 *     <li>{@link #path()} - The complete path to the artifact from the root of a repository</li>
 *     <li>{@link #version()} - The artifact version</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifact extends BaseDependency implements Artifact, Dependency
{
    private static final Pattern pattern = Pattern.compile("(?<group>[A-Za-z.]+)"
            + ":"
            + "(?<identifier>[A-Za-z-.]+)"
            + "(:"
            + "(?<version>[\\d.]+(-(snapshot|alpha|beta|rc|final)))"
            + ")?");

    /**
     * @return An artifact in the given group with the given identifier
     */
    public static MavenArtifact artifact(MavenArtifactGroup group, String identifier)
    {
        return new MavenArtifact(group, identifier);
    }

    /**
     * Parses a descriptor of the form [group-identifier]:[artifact-identifier]:[version]
     *
     * @return A {@link MavenArtifact} for the given descriptor
     */
    public static MavenArtifact parse(Listener listener, final String descriptor)
    {
        final var matcher = pattern.matcher(descriptor);
        if (matcher.matches())
        {
            final var group = new MavenArtifactGroup(matcher.group("group"));
            final var identifier = matcher.group("identifier");
            final var version = Version.parse(matcher.group("version"));

            var artifact = artifact(group, identifier).withVersion(version);
            if (artifact.isValid(listener))
            {
                return artifact;
            }
            else
            {
                fail("Artifact descriptor is invalid: $", descriptor);
            }
        }
        return fail("Unable to parse Maven artifact descriptor: $", descriptor);
    }

    /** The group of this artifact */
    private final MavenArtifactGroup group;

    /** The artifact identifier */
    private final String identifier;

    /** The artifact version */
    private Version version;

    /** The dependencies that this artifact depends on */
    private DependencyList dependencies = new DependencyList();

    public MavenArtifact(final MavenArtifact that)
    {
        group = that.group;
        identifier = that.identifier;
        version = that.version;
        dependencies = dependencies().copy();
    }

    protected MavenArtifact(final MavenArtifactGroup group, final String identifier)
    {
        this.group = group;
        this.identifier = identifier;
        version = null;
    }

    /**
     * Adds the given artifact as a dependency of this artifact
     */
    public void add(MavenArtifact artifact)
    {
        dependencies.add(artifact);
    }

    /**
     * @return The artifacts that this artifact depends on
     */
    @Override
    public DependencyList dependencies()
    {
        return dependencies;
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object instanceof MavenArtifact)
        {
            MavenArtifact that = (MavenArtifact) object;
            return this.fullyQualifiedName().equals(that.fullyQualifiedName());
        }
        return false;
    }

    /**
     * @return This artifact without the matching dependencies
     */
    @Override
    public BaseDependency excluding(final Matcher<Dependency> matcher)
    {
        final var copy = new MavenArtifact(this);
        copy.dependencies.without(matcher);
        return copy;
    }

    /**
     * @return The fully qualified name of this artifact in the format: [group-identifier]:[artifact-identifier]:[version]
     */
    public String fullyQualifiedName()
    {
        return group() + ":" + identifier() + (version == null ? "" : ":" + version());
    }

    /**
     * @return The group of artifacts to which this artifact belongs
     */
    public MavenArtifactGroup group()
    {
        return group;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fullyQualifiedName());
    }

    /**
     * @return The Maven artifact identifier
     */
    public String identifier()
    {
        return identifier;
    }

    /**
     * @return The full path to this artifact relative to a repository root
     */
    @Override
    public FilePath path()
    {
        final var child = FilePath.parseFilePath(fullyQualifiedName().replace(".", "/"));
        return group()
                .path()
                .withChild(child)
                .withChild(version.toString());
    }

    @Override
    public String toString()
    {
        return fullyQualifiedName();
    }

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
                problemIf(version == null, "No artifact version");
            }
        };
    }

    /**
     * @return The version of this artifact
     */
    @Override
    public Version version()
    {
        return version;
    }

    /**
     * @return The given version of this artifact
     */
    public MavenArtifact withVersion(final Version version)
    {
        final var copy = new MavenArtifact(this);
        copy.version = version;
        return copy;
    }
}
