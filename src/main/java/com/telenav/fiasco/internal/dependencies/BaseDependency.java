package com.telenav.fiasco.internal.dependencies;

import com.telenav.fiasco.build.repository.maven.MavenArtifact;
import com.telenav.kivakit.component.BaseComponent;

import java.util.Arrays;

/**
 * Base class for dependencies
 *
 * @author jonathanl (shibo)
 * @see Dependency
 * @see BaseComponent
 */
public abstract class BaseDependency extends BaseComponent implements Dependency
{
    /** The list of dependencies of this dependency */
    private final DependencyList dependencies = new DependencyList();

    /**
     * {@inheritDoc}
     */
    @Override
    public DependencyList dependencies()
    {
        return dependencies;
    }

    /**
     * Adds the given dependency by descriptor
     *
     * @param descriptor The Maven artifact descriptor of the dependency in [project-identifier]:[artifact-identifier]:[version]
     * format
     */
    protected void require(String descriptor)
    {
        require(MavenArtifact.parse(this, descriptor).asLibrary());
    }

    /**
     * Adds the given dependency(ies)
     */
    protected void require(Dependency... dependencies)
    {
        this.dependencies.addAll(Arrays.asList(dependencies));
    }
}
