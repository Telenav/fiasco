package com.telenav.fiasco;

import java.util.List;

public interface Repository
{
    void install(Library library);

    List<Library> resolve(final Library library);
}
