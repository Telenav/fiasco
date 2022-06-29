package com.telenav.fiasco.repository;

import com.telenav.fiasco.Library;
import com.telenav.kivakit.interfaces.comparison.MatcherSet;

import java.util.List;

/**
 * @author jonathanl (shibo)
 */
@SuppressWarnings("unused")
public interface LibraryResolver
{
    List<Library> resolve(Library library, MatcherSet<Library> exclusions);
}
