package com.telenav.fiasco.repository;

import com.telenav.fiasco.Library;
import com.telenav.kivakit.interfaces.comparison.Matcher;

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
