package com.telenav.fiasco.internal.building.dependencies.repository.maven;

import com.telenav.fiasco.internal.building.dependencies.download.Downloader;
import com.telenav.fiasco.internal.building.dependencies.download.Downloader.Download;
import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;
import com.telenav.fiasco.internal.building.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.internal.building.dependencies.repository.ResolvedArtifact;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Library;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.strings.AsciiArt;
import com.telenav.kivakit.kernel.language.values.count.Count;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;
import static com.telenav.kivakit.resource.CopyMode.OVERWRITE;

/**
 * <b>Not public API</b>
 *
 * <p><b>Artifact Resolution</b></p>
 *
 * <p>
 * Resolves Maven artifacts as follows:
 * </p>
 *
 * <ol>
 *     <li>The method {@link #resolve(Artifact)} ensures that the given artifact is "resolved", meaning that its
 *     resources have been installed in the local repository</li>
 *     <li>Artifacts that are in the local repository are already resolved</li>
 *     <li>Artifacts that are not yet in the local repository are resolved by scanning a list of repositories added with
 *     {@link #addRepository(MavenRepository)}. If the artifact is found in a (usually remote) repository, it
 *     is installed from that repository into the local repository, at which point it is resolved.</li>
 *     <li>The resolution of artifacts with {@link #resolveTransitive(Dependency)} implies the resolution of all
 *     transitive artifact dependencies.</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifactResolver extends BaseComponent implements ArtifactResolver
{
    /** Parallel downloader to speed up downloads */
    private final Downloader downloader;

    /** List of repositories to search for this project, with the local repository first */
    private final ObjectList<MavenRepository> repositories = new ObjectList<>();

    /** The repositories for artifacts that are already resolved */
    private final Map<Artifact, ResolvedArtifact> resolved = new ConcurrentHashMap<>();

    public MavenArtifactResolver(Count threads)
    {
        downloader = listenTo(Downloader.get(threads));
    }

    /**
     * Adds the given repository to the list of repositories that this librarian searches
     */
    public MavenArtifactResolver addRepository(MavenRepository repository)
    {
        repositories.add(repository);
        information("Repository => $", repository);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResolvedArtifact resolve(Artifact artifact)
    {
        return resolve(artifact, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectList<ResolvedArtifact> resolveTransitive(Dependency dependency)
    {
        var artifacts = new ObjectList<ResolvedArtifact>();
        resolveTransitive(dependency, artifacts, 0);
        return artifacts;
    }

    /**
     * Installs the given artifact by copying all its resources from the source repository to the destination
     * repository
     *
     * @param source The source repository
     * @param destination The destination repository
     * @param artifact The artifact to install
     * @return True if all of the artifact's resources were installed
     */
    private boolean install(ArtifactRepository source, ArtifactRepository destination, Artifact artifact)
    {
        try
        {
            // Get the artifact path in the source repository,
            var artifactFolder = source.pathTo(artifact);

            // and then for each resource in the set of artifact resources to be downloaded,
            var futureDownloads = new ObjectList<Future<Download>>();
            for (var resource : artifact.resources(artifactFolder))
            {
                // submit a job to copy the resource into this repository in the background.
                var download = new Download(resource, destination.folder(artifact).mkdirs(), OVERWRITE);
                futureDownloads.addIfNotNull(downloader.download(download));
            }

            // Then, for each submitted resource that is downloading in the background,
            for (var futureDownload : futureDownloads)
            {
                // wait for the download to complete,
                var download = futureDownload.get();

                // and check the status.
                switch (download.status())
                {
                    case DOWNLOADED:
                        break;

                    case FAILED:
                        problem(download.toString());
                        return false;

                    case WAITING:
                    case DOWNLOADING:
                    default:
                        fail("Internal error: download completed in state: $", download.status());
                        break;
                }
            }

            return true;
        }
        catch (Exception e)
        {
            problem(e, "Unable to install $ $ => $", artifact, source, this);
            return false;
        }
    }

    /**
     * Resolves the given artifact into the local repository, searching the added repositories as needed.
     *
     * @param artifact The artifact to resolve
     * @param indent The indentation of any output message, to allow the artifact hierarchy to be visualized
     * @return The repository in which the artifact was found
     */
    private ResolvedArtifact resolve(Artifact artifact, int indent)
    {
        var indentation = AsciiArt.repeat(indent, ' ');

        // If the artifact has not already been resolved,
        var resolved = this.resolved.get(artifact);
        if (resolved == null)
        {
            // and it is in the local repository,
            var local = MavenRepository.local(this);
            if (local.contains(artifact))
            {
                // resolve it from there,
                resolved = resolve(local, artifact);
                information(indentation + "$ [$]", artifact, local);
            }
            else
            {
                // otherwise, go through the added repositories,
                for (var repository : repositories)
                {
                    // and if we are able to install the artifact locally,
                    if (install(repository, local, artifact))
                    {
                        // resolve it locally.
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
                        repositories.bulleted(2)).asException();
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
        // If the artifact hasn't already been resolved,
        if (!resolved.containsKey(artifact))
        {
            // read the artifact's POM,
            var pom = ensureNotNull(PomReader.read(repository, artifact));

            //  and cache it,
            resolved.put(artifact, new ResolvedArtifact(repository, artifact, pom));
        }

        // then return the resolved artifact.
        return resolved.get(artifact);
    }

    /**
     * Resolves all transitive dependencies of the given dependency
     *
     * @param dependency The dependency to resolve
     */
    private void resolveTransitive(Dependency dependency, ObjectList<ResolvedArtifact> artifacts, int indent)
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
        dependency.dependencies().forEach(at -> resolveTransitive(at, artifacts, indent + 4));
    }
}
