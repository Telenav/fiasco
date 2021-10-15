package com.telenav.fiasco.dependencies.libraries;

import com.telenav.fiasco.dependencies.Library;

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
