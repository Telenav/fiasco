package com.telenav.fiasco.plugins.compiler;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;
import com.telenav.kivakit.core.version.Version;
import com.telenav.kivakit.filesystem.FileList;

@SuppressWarnings({ "FieldCanBeLocal", "unused" })
public class Compiler extends Plugin
{
    private Version sourceVersion;

    private Version targetVersion;

    private FileList sources;

    public Compiler(final Module module)
    {
        super(module);
    }

    public Compiler sourceVersion(final Version version)
    {
        sourceVersion = version;
        return this;
    }

    public Compiler sources(final FileList sources)
    {
        this.sources = sources;
        return null;
    }

    public Compiler targetVersion(final Version version)
    {
        targetVersion = version;
        return this;
    }

    @Override
    protected void onRun()
    {

    }
}
