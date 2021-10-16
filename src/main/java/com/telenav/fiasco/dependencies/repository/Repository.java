package com.telenav.fiasco.dependencies.repository;

import com.telenav.fiasco.dependencies.Library;

import java.util.List;

public interface Repository
{
    void install(Library library);

    List<Library> resolve(final Library library);
}
