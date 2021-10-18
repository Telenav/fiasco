//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.values.version.Version;

/**
 * A library is an artifact of a particular version, with a set of dependencies from the artifact. Dependencies can be
 * retrieved with {@link #dependencies()} and excluded with {@link #excluding(Matcher)} or {@link
 * #excluding(Dependency...)}.
 *
 * @author jonathanl (shibo)
 */
public class Library extends BaseDependency
{
    public static Library create(Artifact artifact)
    {
        return new Library(artifact);
    }

    /** The library artifact */
    private Artifact artifact;

    protected Library(final Artifact artifact)
    {
        this.artifact = artifact;
    }

    protected Library(final Library that)
    {
        artifact = that.artifact;
    }

    /**
     * @return The artifact for this library
     */
    public Artifact artifact()
    {
        return artifact;
    }

    /**
     * @return This library without the matching dependencies
     */
    public Library excluding(final Matcher<Dependency> matcher)
    {
        this.artifact = (Artifact) artifact.excluding(matcher);
        return this;
    }

    /**
     * @return This library with the given artifact version
     */
    public Library withVersion(final Version version)
    {
        var copy = new Library(this);
        copy.artifact = artifact.withVersion(version);
        return copy;
    }

    /**
     * @return This library with the given artifact version
     */
    public Library withVersion(final String version)
    {
        return withVersion(Version.parse(version));
    }
}

