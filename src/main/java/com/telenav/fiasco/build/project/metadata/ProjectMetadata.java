package com.telenav.fiasco.build.project.metadata;

import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Data about a project, including:
 *
 * <ul>
 *     <li>The project {@link #name()}</li>
 *     <li>The organization that is the {@link #owner()} of the project</li>
 *     <li>The {@link #contributors()} who have worked on the project</li>
 *     <li>The {@link #licenses()} under which the project may be used</li>
 *     <li>The project {@link #copyright()}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class ProjectMetadata implements Named
{
    private ObjectList<Contributor> contributors;

    private Copyright copyright;

    private Organization owner;

    private ObjectList<License> licenses;

    public ProjectMetadata()
    {
    }

    protected ProjectMetadata(final ProjectMetadata that)
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

    public ProjectMetadata withContributor(final Contributor contributor)
    {
        final var copy = new ProjectMetadata(this);
        copy.contributors.add(contributor);
        return copy;
    }

    public ProjectMetadata withCopyright(final StringSource copyright)
    {
        final var copy = new ProjectMetadata(this);
        copy.copyright = new Copyright(copyright);
        return copy;
    }

    public ProjectMetadata withLicense(final License license)
    {
        final var copy = new ProjectMetadata(this);
        copy.licenses.add(license);
        return copy;
    }

    public ProjectMetadata withOriginator(final Organization organization)
    {
        final var copy = new ProjectMetadata(this);
        copy.owner = organization;
        return copy;
    }

    public ProjectMetadata withOwner(final Organization owner)
    {
        final var copy = new ProjectMetadata(this);
        copy.owner = owner;
        return copy;
    }
}
