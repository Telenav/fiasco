package com.telenav.fiasco.example.fiasco;

import com.telenav.fiasco.Library;
import com.telenav.fiasco.LibraryCollection;

/**
 * @author jonathanl (shibo)
 */
@SuppressWarnings({ "unused", "SpellCheckingInspection" })
public interface TelenavLibraries extends LibraryCollection
{
    default Library commons_logging()
    {
        return library("org.apache.commons:logging:1.0");
    }

    default Library kryo()
    {
        return library("com.esotericsoftware:kryo:4.3.1");
    }

    default Library minlog()
    {
        return library("com.esotericsoftware:minlog:1.0");
    }

    default Library wicket_core()
    {
        return library("org.apache:wicket-core:9.3.0");
    }
}
