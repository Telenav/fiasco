package com.telenav.fiasco.repository;

import com.telenav.fiasco.Library;
import com.telenav.tdk.core.kernel.interfaces.object.MatcherSet;

import java.util.List;

/**
 * @author jonathanl (shibo)
 */
public interface LibraryResolver
{
    List<Library> resolve(Library library, MatcherSet<Library> exclusions);
}
