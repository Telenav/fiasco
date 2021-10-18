package com.telenav.fiasco.build.tools.file;

import com.telenav.kivakit.filesystem.FileList;

/**
 * <p>
 * Removes files and any empty parent folders. For example:
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
        files.forEach(file ->
        {
            // Delete the file
            file.delete();

            // and if the parent is empty,
            if (file.parent().isEmpty())
            {
                // delete it too.
                file.parent().delete();
            }
        });

        return this;
    }
}
