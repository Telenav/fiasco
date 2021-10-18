package com.telenav.fiasco.build.project.metadata;

import com.telenav.kivakit.kernel.interfaces.naming.Named;
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
public class ProjectMetadata implements Named
{
    public static ProjectMetadata create()
    {
        return new ProjectMetadata();
    }

    /** The list of contributors to this project */
    private ObjectList<Contributor> contributors;

    /** The project copyright notice */
    private Copyright copyright;

    /** The organization maintaining the project */
    private Organization owner;

    /** The set of licenses under which the project is available for use */
    private ObjectList<License> licenses;

    /** Project source code */
    private SourceCode code;

    protected ProjectMetadata()
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

    public ProjectMetadata withCopyright(final Copyright copyright)
    {
        final var copy = new ProjectMetadata(this);
        copy.copyright = copyright;
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

    public ProjectMetadata withSourceCode(final SourceCode code)
    {
        final var copy = new ProjectMetadata(this);
        copy.code = code;
        return copy;
    }
}
