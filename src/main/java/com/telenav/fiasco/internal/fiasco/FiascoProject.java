package com.telenav.fiasco.internal.fiasco;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.messaging.Message;

public class FiascoProject
{
    private Folder root;

    public FiascoProject(Folder root)
    {
        this.root = root;
    }

    public File buildFile()
    {
        return buildFolder().file("FiascoBuild.java");
    }

    public Folder buildFolder()
    {
        return root.folder("src/main/java/fiasco").mkdirs();
    }

    public String name()
    {
        return root.name().name();
    }

    public Folder rootFolder()
    {
        return root;
    }

    @Override
    public String toString()
    {
        return Message.format("\"$\" ($)", name(), rootFolder());
    }
}
