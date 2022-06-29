package com.telenav.fiasco.plugins.cleaner;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.plugins.Plugin;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.interfaces.comparison.Matcher;

public class Cleaner extends Plugin
{
    private final Matcher<File> matcher;

    public Cleaner(final Module module, final Matcher<File> matcher)
    {
        super(module);
        this.matcher = matcher;
    }

    @Override
    protected void onRun()
    {
        module().outputFolder()
                .nestedFiles(matcher)
                .forEach(File::delete);
    }
}
