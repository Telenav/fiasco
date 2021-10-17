package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.DependencyList;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * A Maven artifact stored in a {@link MavenRepository} and belonging to a {@link MavenArtifactGroup}
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifact implements Artifact, Dependency
{
    private static final Pattern pattern = Pattern.compile("(?<group>[A-Za-z.]+)"
            + ":"
            + "(?<identifier>[A-Za-z-.]+)"
            + "(:"
            + "(?<version>[\\d.]+(-(snapshot|alpha|beta|rc|final)))"
            + ")?");

    public static MavenArtifact parse(final String descriptor)
    {
        final var matcher = pattern.matcher(descriptor);
        if (matcher.matches())
        {
            final var group = new MavenArtifactGroup(matcher.group("group"));
            final var identifier = matcher.group("identifier");
            final var version = Version.parse(matcher.group("version"));
            return new MavenArtifact(group, identifier, version);
        }
        return fail("Unable to parse Maven artifact descriptor: $", descriptor);
    }

    /** The group of this artifact */
    private MavenArtifactGroup group;

    /** The artifact identifier */
    private String identifier;

    /** The artifact version */
    private Version version;

    public MavenArtifact(final MavenArtifactGroup group, final String identifier)
    {
        this.group = group;
        this.identifier = identifier;
        version = null;
    }

    public MavenArtifact(final MavenArtifactGroup group, final String identifier, final Version version)
    {
        this.group = group;
        this.identifier = identifier;
        this.version = version;
    }

    public MavenArtifact(final MavenArtifact that)
    {
        group = that.group;
        identifier = that.identifier;
        version = that.version;
    }

    @Override
    public DependencyList dependencies()
    {
        return null;
    }

    public MavenArtifactGroup group()
    {
        return group;
    }

    public String identifier()
    {
        return identifier;
    }

    @Override
    public String name()
    {
        return group + ":" + identifier + (version == null ? "" : ":" + version);
    }

    @Override
    public FilePath path()
    {
        final var child = FilePath.parseFilePath(name().replace(".", "/"));
        return group()
                .path()
                .withChild(child)
                .withChild(version.toString());
    }

    @Override
    public String toString()
    {
        return name();
    }

    @Override
    public Version version()
    {
        return version;
    }

    public MavenArtifact withGroup(final MavenArtifactGroup group)
    {
        final var copy = new MavenArtifact(this);
        copy.group = group;
        return copy;
    }

    public MavenArtifact withIdentifier(final String group)
    {
        final var copy = new MavenArtifact(this);
        copy.identifier = identifier;
        return copy;
    }

    public MavenArtifact withVersion(final Version version)
    {
        final var copy = new MavenArtifact(this);
        copy.version = version;
        return copy;
    }
}
