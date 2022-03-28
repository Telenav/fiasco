package com.telenav.fiasco.runtime.metadata;

import java.net.MalformedURLException;
import java.net.URL;

import static com.telenav.kivakit.core.data.validation.ensure.Ensure.fail;

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

    protected SourceCode()
    {
    }

    protected SourceCode(SourceCode that)
    {
        repository = that.repository;
    }

    public SourceCode withRepository(URL repository)
    {
        var copy = new SourceCode(this);
        copy.repository = repository;
        return copy;
    }

    public SourceCode withRepository(String repository)
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

    public URL repository;
}
