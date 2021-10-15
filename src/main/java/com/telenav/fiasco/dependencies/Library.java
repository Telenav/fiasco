//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.repository.maven.MavenArtifact;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;
import com.telenav.kivakit.kernel.language.values.version.Version;

public class Library implements Dependency
{
    @SuppressWarnings("ConstantConditions")
    public static Library parse(final String descriptor)
    {
        return new Library((Library) null);
    }

    private MavenArtifact artifact;

    private final MatcherSet<Dependency> exclusions = new MatcherSet<>();

    private final DependencyList dependencies = new DependencyList();

    private Version version;

    public Library(final MavenArtifact artifact)
    {
        this.artifact = artifact;
    }

    protected Library(final Library that)
    {
        artifact = that.artifact;
        version = that.version;
    }

    public MavenArtifact artifact()
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

