package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.dependencies.Library;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

/**
 * @author jonathanl (shibo)
 */
public class LibraryPattern implements Matcher<Library>
{
    @Override
    public boolean matches(final Library library)
    {
        return false;
    }
}
