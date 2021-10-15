package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.dependencies.Library;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;

import java.util.List;

/**
 * @author jonathanl (shibo)
 */
public interface LibraryResolver
{
    List<Library> resolve(Library library, MatcherSet<Library> exclusions);
}
