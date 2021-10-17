package com.telenav.fiasco.build.tools.file;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.FileList;

public class FileRemover extends BaseFileTool
{
    public FileRemover remove(FileList files)
    {
        files.forEach(File::delete);
        return this;
    }
}
