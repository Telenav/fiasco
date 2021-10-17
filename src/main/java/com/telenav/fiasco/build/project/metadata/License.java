package com.telenav.fiasco.build.project.metadata;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.resource.Resource;

/**
 * Legal license for a {@link Project}, with a {@link #title} and a {@link #body()}
 *
 * @author jonathanl (shibo)
 */
public class License
{
    public static License create()
    {
        return new License();
    }

    private String title;

    private String body;

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
        this.body = body.string();
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
