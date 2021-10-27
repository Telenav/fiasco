package com.telenav.fiasco.runtime.dependencies.repository.maven;

import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Library;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.runtime.dependencies.repository.ResolvedArtifact;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.strings.AsciiArt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final ObjectList<MavenRepository> remoteRepositories = ObjectList.create();

    /** The repositories for artifacts that are already resolved */
    private final Map<Artifact, ResolvedArtifact> resolved = new ConcurrentHashMap<>();

    /**
     * Adds the given repository to the list of repositories that this librarian searches
     */
    public MavenArtifactResolver addRemoteRepository(MavenRepository repository)
    {
        ensure(repository.isRemote());
        remoteRepositories.add(repository);
        information("Repository => $", repository);
        return this;
    }

    /**
     * Resolves all artifacts that are transitive dependencies of the given dependency
     *
     * @param artifact The artifact to resolve
     */
    @Override
    public ResolvedArtifact resolve(Artifact artifact)
    {
        return resolve(artifact, 0);
    }

    /**
     * Resolves all artifacts that are transitive dependencies of the given dependency
     *
     * @param dependency The dependency to resolve
     */
    @Override
    public ObjectList<ResolvedArtifact> resolveAll(Dependency dependency)
    {
        var artifacts = new ObjectList<ResolvedArtifact>();
        resolveAll(dependency, artifacts, 0);
        return artifacts;
    }

    /**
     * Resolves the given artifact into the local repository, searching the remoteRepositories as needed.
     *
     * @param artifact The artifact to resolve
     * @param indent The indentation of any output message, to allow the artifact hierarchy to be visualized
     * @return The repository in which the artifact was found
     */
    private ResolvedArtifact resolve(final Artifact artifact, int indent)
    {
        final var indentation = AsciiArt.repeat(indent, ' ');

        // If the artifact has not already been resolved,
        var resolved = this.resolved.get(artifact);
        if (resolved == null)
        {
            // and it is in the local repository,
            final var local = MavenRepository.local(this);
            if (local.contains(artifact))
            {
                // record it as locally resolved,
                resolved = resolve(local, artifact);
                information(indentation + "$ [$]", artifact, local);
            }
            else
            {
                // otherwise, for each remote repository,
                for (var repository : remoteRepositories)
                {
                    // if we can install the artifact from there,
                    if (local.install(repository, artifact))
                    {
                        // we record it as installed and resolved.
                        resolved = resolve(repository, artifact);
                        information(indentation + "$ [$]", artifact, repository);
                        break;
                    }
                }
            }

            // If the artifact couldn't be resolved,
            if (resolved == null)
            {
                // throw an exception.
                throw problem("Cannot find artifact: $\nTried these repositories:\n\n$\n\n", artifact,
                        remoteRepositories.bulleted(2)).asException();
            }
        }

        return resolved;
    }

    /**
     * Resolves artifact in the given repository and adds the {@link ResolvedArtifact} to the resolved map
     *
     * @return The resolved artifact
     */
    private ResolvedArtifact resolve(MavenRepository repository, Artifact artifact)
    {
        var resolved = new ResolvedArtifact(repository, artifact, repository.pom(artifact));
        this.resolved.put(artifact, resolved);
        return resolved;
    }

    /**
     * Resolves all transitive dependencies of the given dependency
     *
     * @param dependency The dependency to resolve
     */
    private void resolveAll(Dependency dependency, ObjectList<ResolvedArtifact> artifacts, int indent)
    {
        // If the dependency is an artifact,
        if (dependency instanceof Artifact)
        {
            // resolve it as one,
            artifacts.add(resolve((Artifact) dependency, indent));
        }

        // and if the dependency is a library,
        if (dependency instanceof Library)
        {
            // resolve the library's artifact,
            artifacts.add(resolve(((Library) dependency).artifact(), indent));
        }

        // then, resolve all the child dependencies.
        dependency.dependencies().forEach(at -> resolveAll(at, artifacts, indent + 4));
    }
}
