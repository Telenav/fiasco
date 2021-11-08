////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// © 2011-2021 Telenav, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.dependencies.BaseDependency;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.values.version.Version;

import java.util.Objects;

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

    protected Library(Library that)
    {
        super(that);
        artifact = that.artifact;
    }

    private Library(Artifact artifact)
    {
        this.artifact = artifact;
    }

    /**
     * @return The artifact for this library
     */
    public Artifact artifact()
    {
        return artifact;
    }

    @Override
    public Dependency copy()
    {
        return new Library(artifact);
    }

    @Override
    public ArtifactDescriptor descriptor()
    {
        return artifact.descriptor();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Library)
        {
            var that = (Library) object;
            return artifact().equals(that.artifact());
        }
        return false;
    }

    /**
     * @return This library without the matching dependencies
     */
    @Override
    public Library excluding(Matcher<Dependency> matcher)
    {
        artifact = (Artifact) artifact.excluding(matcher);
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(artifact());
    }

    @Override
    public Validator validator(ValidationType type)
    {
        return new BaseValidator()
        {
            @Override
            protected void onValidate()
            {
                problemIf(artifact == null, "No library artifact");
            }
        };
    }

    /**
     * @return This library with the given artifact version
     */
    public Library withVersion(Version version)
    {
        var copy = new Library(this);
        copy.artifact = artifact.withVersion(version);
        return copy;
    }

    /**
     * @return This library with the given artifact version
     */
    public Library withVersion(String version)
    {
        return withVersion(Version.parse(this, version));
    }
}
