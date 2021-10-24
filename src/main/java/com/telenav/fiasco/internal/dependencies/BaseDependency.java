package com.telenav.fiasco.internal.dependencies;

import com.telenav.fiasco.build.dependencies.Dependency;
import com.telenav.fiasco.build.dependencies.DependencySet;
import com.telenav.fiasco.build.dependencies.Library;
import com.telenav.fiasco.build.repository.maven.MavenArtifact;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;

import java.util.Arrays;

/**
 * Base class for dependencies.
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
        this.dependencies = that.dependencies.copy();
        this.exclusions = that.exclusions.copy();
    }

    /**
     * Default constructor
     */
    public BaseDependency()
    {
        this.dependencies = new DependencySet();
        this.exclusions = new MatcherSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DependencySet dependencies()
    {
        return dependencies.without(exclusions);
    }

    /**
     * {@inheritDoc}
     */
    public Dependency excluding(Matcher<Dependency> exclusion)
    {
        var copy = copy();
        exclusions.add(exclusion);
        return this;
    }

    /**
     * Adds the given dependency by descriptor
     *
     * @param descriptor The Maven artifact descriptor of the dependency in [project-identifier]:[artifact-identifier]:[version]
     * format
     */
    public void require(String descriptor)
    {
        final var artifact = MavenArtifact.parse(this, descriptor);
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
