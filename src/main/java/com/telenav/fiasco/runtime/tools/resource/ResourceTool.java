package com.telenav.fiasco.runtime.tools.resource;

import com.telenav.kivakit.filesystem.FileGlobPattern;

/**
 * Interface to file tools
 */
public interface ResourceTool
{
    /**
     * @return A file globbing pattern for the given pattern string
     */
    FileGlobPattern glob(String pattern);
}
