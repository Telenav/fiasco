package com.telenav.fiasco.internal.building.dependencies.repository.maven;

import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.internal.building.dependencies.ResolvedDependency;
import com.telenav.fiasco.internal.building.dependencies.download.Downloader;
import com.telenav.fiasco.internal.building.dependencies.download.Downloader.Download;
import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Library;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.core.collections.list.ObjectList;
import com.telenav.kivakit.core.string.AsciiArt;
import com.telenav.kivakit.core.value.count.Count;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static com.telenav.fiasco.internal.building.dependencies.download.Downloader.DownloadStatus.FAILED;
import static com.telenav.fiasco.internal.building.dependencies.pom.Pom.Packaging.BUNDLE;
import static com.telenav.fiasco.internal.building.dependencies.pom.Pom.Packaging.JAR;
import static com.telenav.fiasco.internal.building.dependencies.pom.Pom.Packaging.POM;
import static com.telenav.kivakit.core.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.resource.CopyMode.OVERWRITE;

/**
 * <b>Not public API</b>
 *
 * <p><b>Dependency Resolution</b></p>
 *
 * <p>
 * Resolves Maven artifacts and dependencies as follows:
 * </p>
 *
 * <ol>
 *     <li>The method {@link #resolve(Dependency)} ensures that the given Maven artifact is "resolved", meaning that its
 *     descriptor is resolved and its resources have been installed in the local repository</li>
 *     <li>Artifacts that are in the local repository are already resolved</li>
 *     <li>Artifacts that are not yet in the local repository are resolved by scanning a list of repositories added with
 *     {@link #addRepository(MavenRepository)}. If the artifact is found in a (usually remote) repository, it
 *     is installed from that repository into the local repository, at which point it is resolved.</li>
 *     <li>The resolution of artifacts with {@link #resolveTransitiveDependencies(Dependency)} implies the resolution of all
 *     transitive artifact dependencies.</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see DependencyResolver
 * @see Dependency
 * @see Artifact
 * @see ArtifactRepository
 * @see MavenArtifact
 * @see MavenRepository
 * @see ResolvedDependency
 */
public class MavenDependencyResolver extends BaseComponent implements DependencyResolver
{
    /** Parallel downloader to speed up artifact resource downloads */
    private final Downloader downloader;

    /** The reader of POM files */
    private final PomReader pomReader;

    /** List of repositories to search when artifacts are not in the local repository */
    private final ObjectList<MavenRepository> repositories = new ObjectList<>();

    /** The repositories for artifacts that are already resolved */
    private final Map<Dependency, ResolvedDependency> resolved = new ConcurrentHashMap<>();

    /**
     * @param threads The number of threads to use when downloading artifacts
     */
    public MavenDependencyResolver(Count threads)
    {
        downloader = listenTo(register(new Downloader(threads)));
        pomReader = listenTo(new PomReader());
    }

    /**
     * Adds the given repository to the list of repositories that this librarian searches
     */
    public MavenDependencyResolver addRepository(MavenRepository repository)
    {
        repositories.add(repository);
        information("Repository => $", repository);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResolvedDependency resolve(Dependency dependency)
    {
        return resolve((MavenArtifact) dependency, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectList<ResolvedDependency> resolveTransitiveDependencies(Dependency dependency)
    {
        var resolved = new ObjectList<ResolvedDependency>();
        resolveTransitive(dependency, resolved, 0);
        return resolved;
    }

    /**
     * Copies the given artifact from the source repository to the destination repository by copying all its resources
     *
     * @param source The source repository
     * @param destination The destination repository
     * @param artifact The artifact to copy
     * @param packaging The POM packaging, which implies the resources to copy
     * @return True if all of the artifact's resources were installed, false otherwise
     */
    private boolean copyArtifactResources(ArtifactRepository source,
                                          ArtifactRepository destination,
                                          MavenArtifact artifact,
                                          Pom.Packaging packaging)
    {
        try
        {
            // Get the artifact path in the source repository,
            var artifactFolder = source.pathTo(artifact);

            // and then for each resource in the set of artifact resources to be downloaded,
            var futureDownloads = new ObjectList<Future<Download>>();
            for (var resource : artifact.resources(this, artifactFolder, packaging))
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
                if (download.status() == FAILED)
                {
                    return false;
                }
            }

            narrate("Downloaded $ [$]", artifact, source);
            return true;
        }
        catch (Exception e)
        {
            problem(e, "Unable to install $ $ => $", artifact, source, this);
            return false;
        }
    }

    /**
     * @return True if the given artifact has been resolved
     */
    private boolean isResolved(Artifact artifact)
    {
        return resolved.containsKey(artifact);
    }

    /**
     * Ensures that the given repository's artifact is installed in the local repository
     */
    private boolean materialize(ArtifactRepository repository, MavenArtifact artifact)
    {
        // If the artifact is not already installed,
        var local = MavenRepository.local(this);
        if (!local.contains(artifact))
        {
            // copy the pom resources for the artifact to the local repository
            copyArtifactResources(repository, local, artifact, POM);

            // then read the pom,
            var reader = listenTo(new PomReader());
            var pom = reader.read(local, artifact);

            // and if it has jar packaging,
            if (pom.packaging() == JAR || pom.packaging() == BUNDLE)
            {
                // download the jar resources
                copyArtifactResources(repository, local, artifact, JAR);
            }
        }

        // The artifact is already installed
        return true;
    }

    /**
     * Resolves the given artifact into the local repository, searching the added repositories as needed.
     *
     * @param artifact The artifact to resolve
     * @param level The indentation of any output message, to allow the artifact hierarchy to be visualized
     * @return The repository in which the artifact was found
     */
    private ResolvedDependency resolve(MavenArtifact artifact, int level)
    {
        var indentation = AsciiArt.repeat(level, ' ');

        // If the artifact has not already been resolved,
        var resolved = this.resolved.get(artifact);
        if (resolved == null)
        {
            // and it is in the local repository,
            var local = MavenRepository.local(this);
            if (local.contains(artifact))
            {
                // resolve it,
                resolved = resolve(local, artifact);
            }
            else
            {
                // otherwise, go through the added repositories,
                for (var repository : repositories)
                {
                    // and if we are able to install the artifact locally,
                    if (materialize(repository, artifact))
                    {
                        // resolve it.
                        resolved = resolve(repository, artifact);
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
     * If the given artifact in the given repository has not yet been fully resolved (including reading its POM file),
     * resolves it. First, materializes the artifact into the local repository. Then reads the POM for the artifact from
     * the local repository, and finally records the artifact as a {@link ResolvedDependency} with the POM information
     * and the repository where the artifact was found.
     *
     * @param repository The repository where the artifact was found
     * @param artifact The artifact
     * @return The resolved artifact
     */
    private ResolvedDependency resolve(MavenRepository repository, MavenArtifact artifact)
    {
        // If the artifact hasn't already been resolved,
        if (!isResolved(artifact))
        {
            // install the artifact if necessary,
            materialize(repository, artifact);

            // read the artifact's POM,
            var pom = ensureNotNull(pomReader.read(repository, artifact));

            // and cache the resolved artifact.
            information("Resolved $ [$]", artifact, repository);
            resolved.put(artifact, new ResolvedDependency(repository, artifact, pom));
        }

        // Return the resolved artifact.
        return resolved.get(artifact);
    }

    /**
     * Resolves all transitive dependencies of the given dependency
     *
     * @param dependency The dependency to resolve
     * @param artifacts The artifacts that have been resolved so far
     * @param level The level of recursion
     */
    private void resolveTransitive(Dependency dependency, ObjectList<ResolvedDependency> artifacts, int level)
    {
        // If the dependency is an artifact,
        if (dependency instanceof Artifact)
        {
            // resolve it as one,
            artifacts.add(resolve((MavenArtifact) dependency, level));
        }

        // and if the dependency is a library,
        if (dependency instanceof Library)
        {
            // resolve the library's artifact,
            var library = (Library) dependency;
            artifacts.add(resolve((MavenArtifact) library.artifact(), level));
        }

        // then, resolve all the child dependencies.
        dependency.dependencies().forEach(at -> resolveTransitive(at, artifacts, level + 1));
    }
}
