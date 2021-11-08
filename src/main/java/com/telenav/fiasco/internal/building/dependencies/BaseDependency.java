package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Library;
import com.telenav.fiasco.runtime.dependencies.DependencySet;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactDescriptor;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;

import java.util.Arrays;
import java.util.Objects;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Base class for dependencies.
 * </p>
 *
 * <p>
 * Dependencies can be added with {@link #require(Dependency...)} or {@link #require(String)}, and retrieved with {@link
 * #dependencies()}. The dependencies returned by {@link #dependencies()} are filtered by the set of matchers added with
 * successive calls to the functional method {@link #excluding(Matcher)}.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Dependency
 * @see BaseComponent
 */
public abstract class BaseDependency extends BaseComponent implements Dependency
{
    /** The list of dependencies of this dependency */
    private final DependencySet dependencies;

    /** Matchers that exclude dependencies from the list of dependencies */
    private final MatcherSet<Dependency> exclusions;

    /**
     * Copy constructor
     */
    public BaseDependency(BaseDependency that)
    {
        dependencies = that.dependencies.copy();
        exclusions = that.exclusions.copy();
    }

    /**
     * Default constructor
     */
    public BaseDependency()
    {
        dependencies = new DependencySet();
        exclusions = new MatcherSet<>();
    }

    @Override
    public boolean add(Dependency value)
    {
        dependencies.add(value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DependencySet dependencies()
    {
        return dependencies.without(exclusions);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof BaseDependency)
        {
            var that = (BaseDependency) object;
            return descriptor().equals(that.descriptor());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dependency excluding(Matcher<Dependency> exclusion)
    {
        var copy = copy();
        exclusions.add(exclusion);
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(descriptor());
    }

    /**
     * @return An artifact descriptor for the given string
     */
    public ArtifactDescriptor parseArtifactDescriptor(String text)
    {
        return MavenArtifactDescriptor.parse(this, text);
    }

    /**
     * Adds the given dependency by descriptor
     *
     * @param descriptor The Maven artifact descriptor of the dependency in [project-identifier]:[artifact-identifier]:[version]
     * format
     */
    public void require(String descriptor)
    {
        var artifact = MavenArtifact.parse(this, descriptor);
        require(Library.create(artifact));
    }

    /**
     * Adds the given dependency(ies)
     */
    public void require(Dependency... dependencies)
    {
        this.dependencies.addAll(Arrays.asList(dependencies));
    }
}
