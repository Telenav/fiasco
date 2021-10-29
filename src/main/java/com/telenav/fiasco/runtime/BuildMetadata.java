package com.telenav.fiasco.runtime;

import com.telenav.fiasco.runtime.metadata.Contributor;
import com.telenav.fiasco.runtime.metadata.Copyright;
import com.telenav.fiasco.runtime.metadata.License;
import com.telenav.fiasco.runtime.metadata.Organization;
import com.telenav.fiasco.runtime.metadata.SourceCode;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Data about a project, including:
 *
 * <ul>
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

    protected BuildMetadata(BuildMetadata that)
    {
        copyright = that.copyright;
        contributors = that.contributors.copy();
        licenses = that.licenses.copy();
        owner = that.owner;
        code = that.code;
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

    public BuildMetadata withContributor(Contributor contributor)
    {
        var copy = new BuildMetadata(this);
        copy.contributors.add(contributor);
        return copy;
    }

    public BuildMetadata withCopyright(Copyright copyright)
    {
        var copy = new BuildMetadata(this);
        copy.copyright = copyright;
        return copy;
    }

    public BuildMetadata withLicense(License license)
    {
        var copy = new BuildMetadata(this);
        copy.licenses.add(license);
        return copy;
    }

    public BuildMetadata withOriginator(Organization organization)
    {
        var copy = new BuildMetadata(this);
        copy.owner = organization;
        return copy;
    }

    public BuildMetadata withOwner(Organization owner)
    {
        var copy = new BuildMetadata(this);
        copy.owner = owner;
        return copy;
    }

    public BuildMetadata withSourceCode(SourceCode code)
    {
        var copy = new BuildMetadata(this);
        copy.code = code;
        return copy;
    }
}
