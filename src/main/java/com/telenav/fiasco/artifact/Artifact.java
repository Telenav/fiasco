package com.telenav.fiasco.artifact;

import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.values.identifier.StringIdentifier;
import com.telenav.kivakit.kernel.language.values.version.Version;

import java.util.regex.Pattern;

/**
 * @author jonathanl (shibo)
 */
public class Artifact implements Named
{
    private static final Pattern pattern = Pattern.compile("(?<group>[A-Za-z.]+)"
            + ":"
            + "(?<identifier>[A-Za-z-.]+)"
            + "(:"
            + "(?<version>[\\d.]+(-(snapshot|alpha|beta|rc|final)))"
            + ")?");

    public static Artifact parse(final String descriptor)
    {
        final var matcher = pattern.matcher(descriptor);
        if (matcher.matches())
        {
            final var group = new Group(matcher.group("group"));
            final var identifier = new Identifier(matcher.group("identifier"));
            final var version = Version.parse(matcher.group("version"));
            return new Artifact(group, identifier, version);
        }
        return null;
    }

    public static class Identifier extends StringIdentifier
    {
        public Identifier(final String identifier)
        {
            super(identifier);
        }
    }

    private Group group;

    private Identifier identifier;

    private Version version;

    public Artifact(final Group group, final Identifier identifier)
    {
        this.group = group;
        this.identifier = identifier;
        version = null;
    }

    public Artifact(final Group group, final Identifier identifier, final Version version)
    {
        this.group = group;
        this.identifier = identifier;
        this.version = version;
    }

    public Artifact(final Artifact that)
    {
        group = that.group;
        identifier = that.identifier;
        version = that.version;
    }

    public Group group()
    {
        return group;
    }

    public Identifier identifier()
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

    public Version version()
    {
        return version;
    }

    public Artifact withGroup(final Group group)
    {
        final var copy = new Artifact(this);
        copy.group = group;
        return copy;
    }

    public Artifact withIdentifier(final Identifier group)
    {
        final var copy = new Artifact(this);
        copy.identifier = identifier;
        return copy;
    }

    public Artifact withVersion(final Version version)
    {
        final var copy = new Artifact(this);
        copy.version = version;
        return copy;
    }
}
