package com.telenav.fiasco.project.metadata;

import com.telenav.fiasco.project.Project;

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

    public License withBody(String body)
    {
        this.body = body;
        return this;
    }

    public License withTitle(String title)
    {
        this.title = title;
        return this;
    }
}
