package com.telenav.fiasco.build.tools.file;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.FileList;

/**
 * <p>
 * Removes files. For example:
 * </p>
 *
 * <p>
 * <pre>
 *     remover.remove(glob("**&#x2f;*.class"))
 * </pre>
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class FileRemover extends BaseFileTool
{
    public FileRemover remove(FileList files)
    {
        files.forEach(File::delete);
        return this;
    }
}
