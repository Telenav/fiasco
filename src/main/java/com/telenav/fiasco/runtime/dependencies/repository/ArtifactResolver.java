package com.telenav.fiasco.runtime.dependencies.repository;

import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Library;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * Resolves one or more {@link Artifact}s for a {@link Dependency}.
 *
 * <p><b>Artifact Resolution</b></p>
 * <p>
 * An artifact is considered resolved when it is located in the local repository. The {@link #resolve(Dependency)}
 * method searches a series of remote repositories, resolving dependent artifacts by installing them from one of the
 * remote repositories. If an artifact cannot be found, an exception will be thrown.
 * </p>
 *
 * <p><b>Transitive Dependencies</b></p>
 * <p>
 * The {@link #resolve(Dependency)} method resolves artifacts by examining all transitive dependencies starting from the
 * given root dependency. The root {@link Dependency} may be:
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
     * Resolves all transitive artifacts for the given dependency ({@link Artifact}, {@link Library} or {@link Build})
     *
     * @param dependency The dependency to resolve
     * @return The transitively resolved artifacts
     * @throws RuntimeException A runtime exception is thrown if all artifacts cannot be resolved
     */
    ObjectList<ResolvedArtifact> resolve(final Dependency dependency);
}
