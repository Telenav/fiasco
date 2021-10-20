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

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Locates and installs dependencies from a list of repositories to {@link #add(ArtifactRepository)}. When {@link
 * #resolve(Artifact)} is called, if the artifact is in the local repository, the librarian returns the local
 * repository. If the artifact is not in the local repository yet, the librarian looks in each repository until it finds
 * it, returning the repository that contains the artifact as the result. If the librarian cannot find the artifact in
 * any repository, an exception is thrown.
 *
 * @author shibo
 */
public class Librarian extends BaseRepositoryTool implements ArtifactResolver
{
    /** List of repositories to search for this project, with the local repository first */
    private final ObjectList<ArtifactRepository> repositories = ObjectList.create();

    /**
     * Adds the given repository to the list of repositories that this librarian searches
     */
    public void add(ArtifactRepository repository)
    {
        repositories.add(repository);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactRepository resolve(final Artifact artifact)
    {
        // If the local repository does not contain the artifact,
        if (!MavenRepository.local().contains(artifact))
        {
            // For each repository starting with the local repository,
            for (var at : repositories)
            {
                // if the repository contains the artifact,
                if (at.contains(artifact))
                {
                    // then copy it into the local repository,
                    MavenRepository.local().install(at, artifact);

                    // and return the repository where we found it.
                    return at;
                }
            }

            throw problem("Cannot resolve: $", artifact).asException();
        }
        else
        {
            return MavenRepository.local();
        }
    }
}
