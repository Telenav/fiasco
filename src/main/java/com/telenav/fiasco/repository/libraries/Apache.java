package com.telenav.fiasco.repository.libraries;

import com.telenav.fiasco.Library;

/**
 * @author jonathanl (shibo)
 */
public interface Apache
{
    interface Wicket
    {
        Library core = Library.parse("org.apache.wicket:wicket-core");
    }

    interface Commons
    {
        Library logging = Library.parse("commons-logging:commons-logging");
    }
}
