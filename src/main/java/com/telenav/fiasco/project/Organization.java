package com.telenav.fiasco.project;

import com.telenav.tdk.core.kernel.scalars.names.Name;

import java.net.*;

import static com.telenav.tdk.core.kernel.validation.Validate.fail;

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
