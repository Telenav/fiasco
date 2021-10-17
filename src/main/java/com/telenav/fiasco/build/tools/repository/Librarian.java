//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.build.tools.repository;

import com.telenav.fiasco.dependencies.Library;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.Repository;
import com.telenav.fiasco.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.kernel.interfaces.comparison.MatcherSet;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copies selected files from one folder to another.
 *
 * @author shibo
 */
public class Librarian extends BaseRepositoryTool implements LibraryResolver
{
    private final List<Repository> repositories = new ArrayList<>();

    private Repository deploymentRepository;

    public Librarian deploy(final Library library)
    {
        deploymentRepository.install(library.artifact());
        return this;
    }

    public Librarian install(final Library library)
    {
        MavenRepository.local().install(library);
        return this;
    }

    public Librarian lookIn(final Repository repository)
    {
        repositories.add(repository);
        return this;
    }

    public List<Repository> repositories()
    {
        return Collections.unmodifiableList(repositories);
    }

    @Override
    public ObjectList<Artifact> resolve(final Library library, final MatcherSet<Library> exclusions)
    {
        for (final var repository : repositories)
        {
            final var libraries = repository.resolve(library);
            if (libraries != null)
            {
                return libraries;
            }
        }
        return ObjectList.emptyList();
    }

    public Librarian withDeploymentRepository(final Repository deploymentRepository)
    {
        this.deploymentRepository = deploymentRepository;
        return this;
    }
}
