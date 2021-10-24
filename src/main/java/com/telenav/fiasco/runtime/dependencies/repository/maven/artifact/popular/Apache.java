package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular;

import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache.ApacheHttpComponents;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache.ApacheCommons;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache.ApacheWicket;

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
