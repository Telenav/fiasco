package com.telenav.fiasco.module;

import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.DependencyList;
import com.telenav.kivakit.component.BaseComponent;

/**
 * @author jonathanl (shibo)
 */
public class Module extends BaseComponent implements Dependency
{
    @Override
    public DependencyList dependencies()
    {
        return null;
    }
}
