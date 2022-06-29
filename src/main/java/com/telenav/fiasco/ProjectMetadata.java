package com.telenav.fiasco;

import com.telenav.fiasco.metadata.Contributor;
import com.telenav.fiasco.metadata.Copyright;
import com.telenav.fiasco.metadata.Organization;
import com.telenav.kivakit.interfaces.naming.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * Data about a project, including its name, the organization that develops it and a copyright.
 *
 * @author jonathanl (shibo)
 */
@SuppressWarnings("unused")
public class ProjectMetadata implements Named
{
    private List<Contributor> contributors;

    private Copyright copyright;

    private Organization organization;

    public ProjectMetadata()
    {
    }

    protected ProjectMetadata(final ProjectMetadata that)
    {
        copyright = that.copyright;
        contributors = new ArrayList<>(that.contributors);
        organization = that.organization;
    }

    public List<Contributor> contributors()
    {
        return contributors;
    }

    public Copyright copyright()
    {
        return copyright;
    }

    public Organization organization()
    {
        return organization;
    }

    public ProjectMetadata withContributor(final Contributor contributor)
    {
        final var copy = new ProjectMetadata(this);
        copy.contributors.add(contributor);
        return copy;
    }

    public ProjectMetadata withCopyright(final String copyright)
    {
        final var copy = new ProjectMetadata(this);
        copy.copyright = new Copyright(copyright);
        return copy;
    }

    public ProjectMetadata withOrganization(final Organization organization)
    {
        final var copy = new ProjectMetadata(this);
        copy.organization = organization;
        return copy;
    }
}
