package com.telenav.fiasco.runtime.tools.jar;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.resource.compression.archive.ZipArchive;

public class JarArchiver extends BaseJarTool
{
    private final File jar;

    public JarArchiver(File jar)
    {
        this.jar = jar;
    }

    public JarArchiver add(FileList files)
    {
        var progress = progress("Archiving", files);
        try (var archive = ZipArchive.open(this, jar, ZipArchive.Mode.WRITE))
        {
            archive.add(files, progress);
        }
        progress.end("Archived $ files", files.count());
        return this;
    }
}
