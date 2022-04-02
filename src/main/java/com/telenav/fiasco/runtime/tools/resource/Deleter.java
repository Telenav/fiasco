package com.telenav.fiasco.runtime.tools.resource;

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
@SuppressWarnings("unused")
public class Deleter extends BaseResourceTool
{
    public Deleter delete(FileList files)
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
