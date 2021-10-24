package com.telenav.fiasco.build.repository.maven;

import com.telenav.fiasco.build.dependencies.Dependency;
import com.telenav.fiasco.build.dependencies.Library;
import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.ArtifactRepository;
import com.telenav.fiasco.build.repository.ArtifactResolver;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

import java.util.HashMap;
import java.util.Map;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;

/**
 * Resolves Maven artifacts. Artifacts that are in the local repository are already resolved. Artifacts that are not yet
 * in the local repository are located by scanning a list of {@link MavenRepository}s. If the artifact is found in a
 * Maven repository, it is copied from that repository into the local repository. Resolution of artifacts with {@link
 * #resolveAll(Dependency)} implies the resolution of all transitive artifact dependencies.
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifactResolver extends BaseComponent implements ArtifactResolver
{
    /** List of repositories to search for this project, with the local repository first */
    private final ObjectList<ArtifactRepository> remoteRepositories = ObjectList.create();

    /** The repositories for artifacts that are already resolved */
    private final Map<Artifact, ArtifactRepository> resolved = new HashMap<>();

    /**
     * Adds the given repository to the list of repositories that this librarian searches
     */
    public MavenArtifactResolver addRemoteRepository(ArtifactRepository repository)
    {
        ensure(repository.isRemote());
        remoteRepositories.add(repository);
        information("Repository => $", repository);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactRepository resolve(final Artifact artifact)
    {
        // If the artifact has already been resolved,
        var resolved = this.resolved.get(artifact);
        if (resolved != null)
        {
            // return the repository for it.
            return resolved;
        }

        // If the artifact is in the local repository
        final var local = MavenRepository.local(this);
        if (local.contains(artifact))
        {
            // resolve it there.
            information("Resolved $ => $", artifact, local);
            this.resolved.put(artifact, local);
            return local;
        }

        // for each remote repository,
        for (var repository : remoteRepositories)
        {
            // if it contains the artifact,
            if (repository.contains(artifact))
            {
                // then copy it into the local repository,
                local.install(repository, artifact);

                // resolve all of the artifact's dependencies,
                resolveAll(artifact);

                // and return the repository where we found it.
                information("Installed $ => $", artifact, repository);
                this.resolved.put(artifact, repository);
                return repository;
            }
        }

        throw problem("Cannot find artifact: $\nTried these repositories:\n\n$\n\n", artifact, remoteRepositories.bulleted(2)).asException();
    }

    /**
     * Resolves all artifacts that are transitive dependencies of the given dependency
     *
     * @param dependency The dependency to resolve
     */
    public ObjectList<Artifact> resolveAll(Dependency dependency)
    {
        var artifacts = new ObjectList<Artifact>();
        resolveAll(dependency, artifacts);
        return artifacts;
    }

    /**
     * Resolves all transitive dependencies of the given dependency
     *
     * @param dependency The dependency to resolve
     */
    private void resolveAll(Dependency dependency, ObjectList<Artifact> artifacts)
    {
        // If the dependency is an artifact,
        if (dependency instanceof Artifact)
        {
            // resolve it,
            var artifact = (Artifact) dependency;
            artifacts.add(artifact);
            resolve(artifact);
        }

        // and if the dependency is a library,
        if (dependency instanceof Library)
        {
            // resolve it,
            var library = (Library) dependency;
            artifacts.add(library.artifact());
            resolve(library.artifact());
        }

        // then, resolve all the child dependencies.
        dependency.dependencies().forEach(at -> resolveAll(at, artifacts));
    }
}
