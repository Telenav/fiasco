package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

/**
 * A dependency has a list of {@link #dependencies()}, each of which can be either {@link Library} or a {@link
 * FiascoBuild}.
 *
 * @author shibo
 * @see DependencyList
 * @see FiascoBuild
 * @see Library
 * @see Artifact
 */
public interface Dependency
{
    /**
     * @return The immediate dependencies of this dependency
     */
    DependencyList dependencies();

    /**
     * @return This dependency without the matching dependencies
     */
    Dependency excluding(Matcher<Dependency> matcher);

    /**
     * @return This dependency without the given dependencies
     */
    default Dependency excluding(Dependency... dependencies)
    {
        return excluding(dependency ->
        {
            for (var at : dependencies)
            {
                if (at.equals(dependency))
                {
                    return true;
                }
            }
            return false;
        });
    }
}
