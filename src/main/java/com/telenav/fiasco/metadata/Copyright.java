package com.telenav.fiasco.metadata;

public class Copyright
{
    private final String copyright;

    public Copyright(final String copyright)
    {
        this.copyright = copyright;
    }

    @Override
    public String toString()
    {
        return copyright;
    }
}
