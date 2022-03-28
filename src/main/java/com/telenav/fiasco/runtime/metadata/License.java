package com.telenav.fiasco.runtime.metadata;

import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.interfaces.string.StringSource;
import com.telenav.kivakit.resource.Resource;

/**
 * Legal license for a {@link Build}, with a {@link #title} and a {@link #body()}
 *
 * @author jonathanl (shibo)
 */
public class License
{
    public static License create()
    {
        return new License();
    }

    /** The text of the license */
    private String body;

    /** The license title */
    private String title;

    protected License()
    {
    }

    public String body()
    {
        return body;
    }

    public String title()
    {
        return title;
    }

    @Override
    public String toString()
    {
        return title();
    }

    /**
     * @return This license with the given body text. Note that {@link Resource} implements {@link StringSource}.
     */
    public License withBody(StringSource body)
    {
        this.body = body.asString();
        return this;
    }

    /**
     * @return This license with the given title
     */
    public License withTitle(String title)
    {
        this.title = title;
        return this;
    }
}
