package com.telenav.fiasco.dependencies.repository.maven.artifacts.apache;

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
