package com.telenav.fiasco.build.project.metadata;

import com.telenav.kivakit.kernel.interfaces.string.StringSource;

/**
 * Copyright notice for a {@link Project}
 *
 * @author jonathanl (shibo)
 */
public class Copyright
{
    private final String copyright;

    public Copyright(final StringSource copyright)
    {
        this.copyright = copyright.string();
    }

    @Override
    public String toString()
    {
        return copyright;
    }
}
