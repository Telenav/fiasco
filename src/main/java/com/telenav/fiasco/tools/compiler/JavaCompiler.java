package com.telenav.fiasco.tools.compiler;

import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.kernel.language.values.version.Version;

public class JavaCompiler extends BaseCompiler
{
    private Version sourceVersion;

    private Version targetVersion;

    private FileList sources;

    @Override
    public void onRun()
    {

    }

    public JavaCompiler sourceVersion(final Version version)
    {
        sourceVersion = version;
        return this;
    }

    public JavaCompiler sources(final FileList sources)
    {
        this.sources = sources;
        return null;
    }

    public JavaCompiler targetVersion(final Version version)
    {
        targetVersion = version;
        return this;
    }
}
