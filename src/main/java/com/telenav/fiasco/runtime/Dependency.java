package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.dependencies.DependencyGraph;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.core.collections.list.StringList;
import com.telenav.kivakit.core.path.StringPath;
import com.telenav.kivakit.interfaces.collection.Addable;
import com.telenav.kivakit.interfaces.comparison.Matcher;
import com.telenav.kivakit.interfaces.naming.Named;
import com.telenav.kivakit.resource.PropertyMap;
import com.telenav.kivakit.validation.Validatable;

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
public interface Dependency extends
        Validatable,
        Addable<Dependency>,
        Named,
        Component
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

    /**
     * @return True if this dependency's descriptor includes a version (that is not an unresolved property)
     */
    default boolean isResolved()
    {
        return descriptor().isResolved();
    }

    /**
     * @return The parent of this dependency
     */
    Dependency parent();

    /**
     * @return The dependency path to this artifact (the parent chain)
     */
    default StringPath path()
    {
        var path = new StringList();
        for (var at = this; at != null; at = at.parent())
        {
            path.append(at.descriptor().name());
        }
        return StringPath.stringPath(path.reversed());
    }

    /**
     * Resolve the version of this dependency using the given properties if it contains any references of the form
     * "${property-name}"
     */
    default void resolvePropertyReferences(PropertyMap properties)
    {
        descriptor().resolvePropertyReferences(properties);
    }

    /**
     * Resolves the version of this dependency to the given version
     *
     * @param version The version that should be assigned to this dependency
     */
    void resolveVersionTo(String version);
}
