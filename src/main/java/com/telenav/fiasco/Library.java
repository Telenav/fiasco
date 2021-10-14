//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco;

import com.telenav.fiasco.artifact.Artifact;
import com.telenav.fiasco.dependency.Dependency;
import com.telenav.fiasco.dependency.DependencyList;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;
import com.telenav.kivakit.kernel.language.values.version.Version;

public class Library implements Dependency<Library>
{
    @SuppressWarnings("ConstantConditions")
    public static Library parse(final String descriptor)
    {
        return new Library((Library) null);
    }

    private Artifact artifact;

    private final MatcherSet<Library> exclusions = new MatcherSet<>();

    private final DependencyList<Library> dependencies = new DependencyList<>();

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
    public DependencyList<Library> dependencies()
    {
        return dependencies.copy().without(exclusions.anyMatches());
    }

    public Library excluding(final Matcher<Library> pattern)
    {
        exclusions.add(pattern);
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

