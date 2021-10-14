package com.telenav.fiasco.project;

import com.telenav.kivakit.kernel.language.values.name.Name;

import java.net.MalformedURLException;
import java.net.URL;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

public class Organization extends Name
{
    private URL url;

    public Organization(final String name)
    {
        super(name);
    }

    public URL url()
    {
        return url;
    }

    public Organization withUrl(final URL url)
    {
        this.url = url;
        return this;
    }

    public Organization withUrl(final String url)
    {
        try
        {
            return withUrl(new URL(url));
        }
        catch (final MalformedURLException e)
        {
            return fail(e, "Invalid URL: $", url);
        }
    }
}
