package com.telenav.fiasco.build.tools.repository;

import com.telenav.fiasco.dependencies.Library;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * @author jonathanl (shibo)
 */
public interface LibraryResolver
{
    ObjectList<Artifact> resolve(Library library, MatcherSet<Library> exclusions);
}
