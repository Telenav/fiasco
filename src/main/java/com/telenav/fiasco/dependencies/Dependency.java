package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.project.Project;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

/**
 * A dependency has a list of {@link #dependencies()}, each of which can be either {@link Library} or a {@link Project}.
 * Although {@link Build}s implement {@link Dependency} (so that {@link Project}s can be added to them), they cannot
 * presently be dependent on each other (although independent builds can be started at the same time from the command
 * line).
 *
 * @author shibo
 * @see DependencyList
 * @see Build
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
    Dependency excluding(final Matcher<Dependency> matcher);

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
