package com.telenav.fiasco.build.project.metadata;

import com.telenav.kivakit.kernel.language.values.name.Name;

import java.net.MalformedURLException;
import java.net.URL;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * An {@link Organization} to which contributors belong and which may originate {@link Project}s
 *
 * @author jonathanl (shibo)
 */
public class Organization extends Name
{
    /** Organization website */
    private URL website;

    public Organization(final String name)
    {
        super(name);
    }

    public URL url()
    {
        return website;
    }

    public Organization withWebsite(final URL url)
    {
        this.website = url;
        return this;
    }

    public Organization withWebsite(final String url)
    {
        try
        {
            return withWebsite(new URL(url));
        }
        catch (final MalformedURLException e)
        {
            return fail(e, "Invalid URL: $", url);
        }
    }
}
