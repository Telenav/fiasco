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

package com.telenav.fiasco.runtime.tools.repository;

import com.telenav.fiasco.internal.building.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.internal.building.dependencies.repository.ResolvedArtifact;
import com.telenav.fiasco.internal.building.dependencies.repository.maven.MavenArtifactResolver;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.messaging.Listener;

/**
 * Locates and installs dependencies from a list of repositories designated by calls to {@link
 * #addRemoteRepository(MavenRepository)}. When {@link #resolveTransitive(Dependency)} is called, the transitive
 * dependencies of the given dependency are resolved using an {@link ArtifactResolver}. Dependent artifacts are realized
 * into the local repository if they are not already installed there. If the librarian cannot find any of the dependent
 * artifacts in any repository, an exception is thrown.
 *
 * @author shibo
 * @see ArtifactRepository
 * @see ArtifactResolver
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
    public Librarian addRemoteRepository(MavenRepository repository)
    {
        resolver.addRemoteRepository(repository);
        return this;
    }

    @Override
    public ResolvedArtifact resolve(Artifact artifact)
    {
        return resolver.resolve(artifact);
    }

    @Override
    public ObjectList<ResolvedArtifact> resolveTransitive(Dependency dependency)
    {
        return resolver.resolveTransitive(dependency);
    }
}
