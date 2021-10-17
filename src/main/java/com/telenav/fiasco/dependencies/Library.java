//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;
import com.telenav.kivakit.kernel.language.values.version.Version;

/**
 * A library is an artifact of a particular version, with a set of dependencies and exclusions
 *
 * @author jonathanl (shibo)
 */
public class Library extends BaseDependency
{
    @SuppressWarnings("ConstantConditions")
    public static Library parse(final String descriptor)
    {
        return new Library((Library) null);
    }

    /** The library artifact */
    private Artifact artifact;

    /** Dependencies of the artifact that should be excluded */
    private final MatcherSet<Dependency> exclusions = new MatcherSet<>();

    /** The dependencies of this library's artifact */
    private final DependencyList dependencies = new DependencyList();

    /** The version of this library's artifact */
    private Version version;

    public Library(final Artifact artifact)
    {
        this.artifact = artifact;
    }

    protected Library(final Library that)
    {
        artifact = that.artifact;
        version = that.version;
    }

    public Artifact artifact()
    {
        return artifact;
    }

    @Override
    public DependencyList dependencies()
    {
        return dependencies.copy().without(exclusions.anyMatches());
    }

    public Library excluding(final Matcher<Dependency> matcher)
    {
        exclusions.add(matcher);
        return this;
    }

    public Library version(final Version version)
    {
        artifact = artifact.withVersion(version);
        return this;
    }

    public Library version(final String version)
    {
        return version(Version.parse(version));
    }
}

