package com.telenav.fiasco.runtime;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.DependencySet;
import com.telenav.kivakit.kernel.data.validation.Validatable;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

import java.util.Arrays;

/**
 * A dependency has a list of {@link #dependencies()}, each of which can be either {@link Library} or a {@link Build}.
 * The {@link #excluding(Matcher)} method adds an exclusion filter to the dependencies. When {@link #dependencies()} is
 * called, all exclusion filters are applied to filter the resulting {@link DependencySet}.
 *
 * @author shibo
 * @see DependencySet
 * @see Build
 * @see Library
 * @see Artifact
 */
public interface Dependency extends Validatable
{
    /**
     * @return A deep copy of this dependency
     */
    Dependency copy();

    /**
     * @return The immediate dependencies of this dependency
     */
    DependencySet dependencies();

    /**
     * @return This dependency with the given exclusions as filters
     */
    default Dependency excluding(Dependency... exclusions)
    {
        var list = Arrays.asList();
        return excluding(list::contains);
    }

    /**
     * This dependency with the given exclusion as a filter
     *
     * @param exclusion The exclusion
     */
    Dependency excluding(Matcher<Dependency> exclusion);
}
