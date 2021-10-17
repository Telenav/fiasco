//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.build.tools.repository;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Installs dependencies from a list of repositories to {@link #add(ArtifactRepository)}.
 *
 * @author shibo
 */
public class Librarian extends BaseRepositoryTool implements ArtifactResolver
{
    /** List of repositories to search for this project, with the local repository first */
    private final ObjectList<ArtifactRepository> repositories = ObjectList.create();

    public void add(ArtifactRepository repository)
    {
        repositories.add(repository);
    }

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
