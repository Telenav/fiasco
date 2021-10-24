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

package com.telenav.fiasco.build.tools.repository;

import com.telenav.fiasco.build.Dependency;
import com.telenav.fiasco.build.dependencies.repository.Artifact;
import com.telenav.fiasco.build.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.build.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.build.dependencies.repository.maven.MavenArtifactResolver;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.messaging.Listener;

/**
 * Locates and installs dependencies from a list of repositories created from calls to {@link
 * #addRemoteRepository(ArtifactRepository)}. When {@link #resolve(Artifact)} is called, if the artifact is in the local
 * repository, the librarian returns the local repository. If the artifact is not in the local repository yet, the
 * librarian looks in each repository until it finds it, returning the repository that contains the artifact as the
 * result. If the librarian cannot find the artifact in any repository, an exception is thrown.
 *
 * @author shibo
 */
public class Librarian extends BaseComponent implements ArtifactResolver
{
    private final MavenArtifactResolver resolver = listenTo(new MavenArtifactResolver());

    public Librarian(Listener listener)
    {
        addListener(listener);
    }

    /**
     * Adds the given repository to the list of repositories that this librarian searches
     */
    public Librarian addRemoteRepository(ArtifactRepository repository)
    {
        resolver.addRemoteRepository(repository);
        return this;
    }

    @Override
    public ArtifactRepository resolve(final Artifact artifact)
    {
        return resolver.resolve(artifact);
    }

    public ObjectList<Artifact> resolveAll(final Dependency dependency)
    {
        return resolver.resolveAll(dependency);
    }
}
