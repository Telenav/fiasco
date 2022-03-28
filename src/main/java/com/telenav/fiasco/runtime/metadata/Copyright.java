package com.telenav.fiasco.runtime.metadata;

import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.interfaces.string.StringSource;
import com.telenav.kivakit.resource.Resource;

/**
 * Copyright notice for a {@link Build}
 *
 * @author jonathanl (shibo)
 */
public class Copyright
{
    /** The copyright text */
    private final String copyright;

    /**
     * Construct with the given copyright text. Note that {@link Resource} implements {@link StringSource}, so a
     * copyright can be constructed directly from a {@link File} object.
     */
    public Copyright(StringSource copyright)
    {
        this.copyright = copyright.asString();
    }

    @Override
    public String toString()
    {
        return copyright;
    }
}
