package com.telenav.fiasco.build.dependencies.repository;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.Dependency;
import com.telenav.fiasco.build.Library;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Resolves one or more {@link Artifact}s for a {@link Dependency}.
 *
 * <p><b>Artifact Resolution</b></p>
 * <p>
 * An artifact is considered resolved when it is located in the local repository. The {@link #resolve(Artifact)} method
 * searches a series of remote repositories, resolving the artifact by installing it from one of the remote
 * repositories. If the artifact cannot be found, an exception will be thrown.
 * </p>
 *
 * <p><b>Transitive Dependencies</b></p>
 * <p>
 * The {@link #resolveAll(Dependency)} method resolves artifacts by examining all transitive dependencies starting from
 * the given root dependency. The root {@link Dependency} may be:
 * <ul>
 *     <li>Artifact - An artifact with dependent artifacts</li>
 *     <li>Library - A library with dependent artifacts</li>
 *     <li>Build - A build with dependent projects, libraries and artifacts</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public interface ArtifactResolver
{
    /**
     * Resolves a single artifact from a remote repository into the local one
     *
     * @param artifact The artifact to resolve
     * @return The repository where the artifact was found
     * @throws RuntimeException A runtime exception is thrown if all artifacts cannot be resolved
     */
    ArtifactRepository resolve(Artifact artifact);

    /**
     * Resolves all transitive artifacts for the given dependency ({@link Artifact}, {@link Library} or {@link Build})
     *
     * @param dependency The dependency to resolve
     * @return All transitively resolved artifacts
     * @throws RuntimeException A runtime exception is thrown if all artifacts cannot be resolved
     */
    ObjectList<Artifact> resolveAll(final Dependency dependency);
}
