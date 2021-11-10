package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.dependencies.DependencyGraph;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.data.validation.Validatable;
import com.telenav.kivakit.kernel.interfaces.collection.Addable;
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
public interface Dependency extends Validatable, Component, Addable<Dependency>
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
     * <b>Not public API</b>
     *
     * @return The dependency graph for this dependency
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    default DependencyGraph dependencyGraph()
    {
        return DependencyGraph.of(this);
    }

    /**
     * @return The artifact descriptor for this dependency in [group-name]:[artifact-name](:[version])? form.
     */
    ArtifactDescriptor descriptor();

    /**
     * @return This dependency with the given exclusions as filters
     */
    default Dependency excluding(Dependency... exclusions)
    {
        var list = Arrays.asList(exclusions);
        return excluding(list::contains);
    }

    /**
     * This dependency with the given exclusion as a filter
     *
     * @param exclusion The exclusion
     */
    Dependency excluding(Matcher<Dependency> exclusion);

    /**
     * @return True if this dependency has no dependencies
     */
    default boolean isLeaf()
    {
        return dependencies().isEmpty();
    }
}
