package com.telenav.fiasco.build.metadata;

import java.net.MalformedURLException;
import java.net.URL;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * Information about project source code
 *
 * @author jonathanl (shibo)
 */
public class SourceCode
{
    public static SourceCode create()
    {
        return new SourceCode();
    }

    public URL repository;

    protected SourceCode()
    {
    }

    protected SourceCode(SourceCode that)
    {
        this.repository = that.repository;
    }

    public SourceCode withRepository(final URL repository)
    {
        var copy = new SourceCode(this);
        copy.repository = repository;
        return copy;
    }

    public SourceCode withRepository(final String repository)
    {
        try
        {
            return withRepository(new URL(repository));
        }
        catch (MalformedURLException e)
        {
            return fail(e, "Bad URL: $", repository);
        }
    }
}
