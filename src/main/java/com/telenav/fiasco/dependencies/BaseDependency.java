package com.telenav.fiasco.dependencies;

import com.telenav.kivakit.component.BaseComponent;

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
}
