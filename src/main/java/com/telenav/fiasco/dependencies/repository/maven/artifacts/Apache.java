package com.telenav.fiasco.dependencies.repository.maven.artifacts;

import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.ApacheCommons;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.ApacheHttpComponents;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.ApacheWicket;

/**
 * The Apache group
 *
 * @author jonathanl (shibo)
 */
public class Apache
{
    public static Apache get()
    {
        return new Apache();
    }

    public ApacheCommons commons()
    {
        return new ApacheCommons();
    }

    public ApacheHttpComponents httpComponents()
    {
        return new ApacheHttpComponents();
    }

    public ApacheWicket wicket()
    {
        return new ApacheWicket();
    }
}