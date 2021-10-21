package com.telenav.fiasco.build;

import com.telenav.fiasco.build.metadata.Contributor;
import com.telenav.fiasco.build.metadata.Copyright;
import com.telenav.fiasco.build.metadata.License;
import com.telenav.fiasco.build.metadata.Organization;
import com.telenav.fiasco.build.metadata.SourceCode;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Data about a project, including:
 *
 * <ul>
 *     <li>The project {@link #name()}</li>
 *     <li>The organization that is the {@link #owner()} of the project</li>
 *     <li>The {@link #contributors()} who have worked on the project</li>
 *     <li>The project {@link #copyright()}</li>
 *     <li>The {@link #licenses()} under which the project may be used</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class BuildMetadata
{
    public static BuildMetadata create()
    {
        return new BuildMetadata();
    }

    /** The list of contributors to this project */
    private ObjectList<Contributor> contributors = ObjectList.create();

    /** The project copyright notice */
    private Copyright copyright;

    /** The organization maintaining the project */
    private Organization owner;

    /** The set of licenses under which the project is available for use */
    private ObjectList<License> licenses = ObjectList.create();

    /** Project source code */
    private SourceCode code;

    protected BuildMetadata()
    {
    }

    protected BuildMetadata(final BuildMetadata that)
    {
        copyright = that.copyright;
        contributors = that.contributors.copy();
        licenses = that.licenses.copy();
        owner = that.owner;
    }

    public ObjectList<Contributor> contributors()
    {
        return contributors;
    }

    public Copyright copyright()
    {
        return copyright;
    }

    public ObjectList<License> licenses()
    {
        return licenses;
    }

    public Organization owner()
    {
        return owner;
    }

    public BuildMetadata withContributor(final Contributor contributor)
    {
        final var copy = new BuildMetadata(this);
        copy.contributors.add(contributor);
        return copy;
    }

    public BuildMetadata withCopyright(final Copyright copyright)
    {
        final var copy = new BuildMetadata(this);
        copy.copyright = copyright;
        return copy;
    }

    public BuildMetadata withLicense(final License license)
    {
        final var copy = new BuildMetadata(this);
        copy.licenses.add(license);
        return copy;
    }

    public BuildMetadata withOriginator(final Organization organization)
    {
        final var copy = new BuildMetadata(this);
        copy.owner = organization;
        return copy;
    }

    public BuildMetadata withOwner(final Organization owner)
    {
        final var copy = new BuildMetadata(this);
        copy.owner = owner;
        return copy;
    }

    public BuildMetadata withSourceCode(final SourceCode code)
    {
        final var copy = new BuildMetadata(this);
        copy.code = code;
        return copy;
    }
}
