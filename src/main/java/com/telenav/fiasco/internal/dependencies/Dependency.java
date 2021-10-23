package com.telenav.fiasco.internal.dependencies;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.kivakit.kernel.data.validation.Validatable;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

/**
 * A dependency has a list of {@link #dependencies()}, each of which can be either {@link Library} or a {@link
 * FiascoBuild}.
 *
 * @author shibo
 * @see DependencySet
 * @see FiascoBuild
 * @see Library
 * @see Artifact
 */
public interface Dependency extends Validatable
{
    /**
     * @return The immediate dependencies of this dependency
     */
    DependencySet dependencies();

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
