//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco.plugins.librarian;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.*;
import com.telenav.fiasco.plugins.Plugin;
import com.telenav.fiasco.repository.LibraryResolver;
import com.telenav.fiasco.repository.maven.MavenRepository;
import com.telenav.tdk.core.kernel.interfaces.object.MatcherSet;

import java.util.*;

import static com.telenav.tdk.core.kernel.validation.Validate.unsupported;

/**
 * Copies selected files from one folder to another.
 *
 * @author shibo
 */
public class Librarian extends Plugin implements LibraryResolver
{
    private final List<Repository> repositories = new ArrayList<>();

    private Repository deploymentRepository;

    public Librarian(final Module module)
    {
        super(module);
    }

    public Librarian deploy(final Library library)
    {
        deploymentRepository.install(library);
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
    public List<Library> resolve(final Library library, final MatcherSet<Library> exclusions)
    {
        for (final var repository : repositories)
        {
            final var libraries = repository.resolve(library);
            if (libraries != null)
            {
                return libraries;
            }
        }
        return Collections.emptyList();
    }

    public Librarian withDeploymentRepository(final Repository deploymentRepository)
    {
        this.deploymentRepository = deploymentRepository;
        return this;
    }

    @Override
    protected void onRun()
    {
        unsupported();
    }
}
