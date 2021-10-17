package com.telenav.fiasco.build.tools.file;

import com.telenav.kivakit.filesystem.FileGlobPattern;

/**
 * Interface to file tools
 */
public interface FileTool
{
    /**
     * @return A file globbing pattern for the given pattern string
     */
    FileGlobPattern glob(final String pattern);
}
