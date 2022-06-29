package com.telenav.fiasco.example.fiasco;

import com.telenav.fiasco.Library;

/**
 * @author jonathanl (shibo)
 */
public interface Libraries extends TelenavLibraries
{
    @Override
    default Library wicket_core()
    {
        return TelenavLibraries.super.wicket_core().version("9.4.0");
    }
}
