package com.telenav.fiasco.runtime.metadata;

import com.telenav.kivakit.core.language.values.name.Name;
import com.telenav.kivakit.core.project.Project;

import java.net.MalformedURLException;
import java.net.URL;

import static com.telenav.kivakit.core.data.validation.ensure.Ensure.fail;

/**
 * An {@link Organization} to which contributors belong and which may originate {@link Project}s
 *
 * @author jonathanl (shibo)
 */
public class Organization extends Name
{
    /** Organization website */
    private URL website;

    public Organization(String name)
    {
        super(name);
    }

    public URL url()
    {
        return website;
    }

    public Organization withWebsite(URL url)
    {
        website = url;
        return this;
    }

    public Organization withWebsite(String url)
    {
        try
        {
            return withWebsite(new URL(url));
        }
        catch (MalformedURLException e)
        {
            return fail(e, "Invalid URL: $", url);
        }
    }
}
