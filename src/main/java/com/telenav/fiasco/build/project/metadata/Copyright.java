package com.telenav.fiasco.build.project.metadata;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.resource.Resource;

/**
 * Copyright notice for a {@link Project}
 *
 * @author jonathanl (shibo)
 */
public class Copyright
{
    private final String copyright;

    /**
     * Construct with the given copyright text. Note that {@link Resource} implements {@link StringSource}, so a
     * copyright can be constructed directly from a {@link File} object.
     */
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
