package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.DependencyList;
import com.telenav.fiasco.dependencies.Library;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.language.values.version.Version;

import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
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
            final var group = new MavenGroup(matcher.group("group"));
            final var identifier = matcher.group("identifier");
            final var version = Version.parse(matcher.group("version"));
            return new MavenArtifact(group, identifier, version);
        }
        return fail("Unable to parse Maven artifact descriptor: $", descriptor);
    }

    private MavenGroup group;

    private String identifier;

    private Version version;

    public MavenArtifact(final MavenGroup group, final String identifier)
    {
        this.group = group;
        this.identifier = identifier;
        version = null;
    }

    public MavenArtifact(final MavenGroup group, final String identifier, final Version version)
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

    public Library asLibrary()
    {
        return null;
    }

    @Override
    public DependencyList dependencies()
    {
        return null;
    }

    public MavenGroup group()
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
    public String toString()
    {
        return name();
    }

    public Library version(final String version)
    {
        return withVersion(Version.parse(version)).asLibrary();
    }

    public MavenArtifact withGroup(final MavenGroup group)
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
