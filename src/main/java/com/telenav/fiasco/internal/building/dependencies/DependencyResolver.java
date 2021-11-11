package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.fiasco.internal.building.dependencies.repository.ResolvedArtifact;
import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Library;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Resolves one or more {@link Artifact}s for a {@link Dependency}.
 * </p>
 *
 * <p><b>Artifact Resolution</b></p>
 *
 * <p>
 * An artifact is considered resolved when it is located in the local repository. The {@link #resolve(Artifact)} method
 * searches for the given artifact and copies it into the local repository when it is found. If an artifact cannot be
 * found, an exception will be thrown.
 * </p>
 *
 * <p><b>Transitive Dependencies</b></p>
 *
 * <p>
 * The {@link #resolveTransitiveDependencies(Dependency)} method resolves all transitive dependencies starting from the
 * given root dependency. The root {@link Dependency} may be:
 * <ul>
 *     <li>Artifact - An artifact with dependent artifacts</li>
 *     <li>Library - A library with dependent artifacts</li>
 *     <li>Build - A build with dependent projects, libraries and artifacts</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public interface DependencyResolver
{
    /**
     * Retrieves a POM for the given artifact
     *
     * @param artifact The artifact for which to resolve a POM
     * @return The POM
     */
    default Pom pom(Artifact artifact)
    {
        return resolve(artifact).pom();
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Resolves a single artifact by installing it in the local repository
     * </p>
     *
     * @param artifact The artifact
     * @return The resolved artifact
     */
    ResolvedArtifact resolve(Artifact artifact);

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Resolves all transitive artifacts for the given dependency ({@link Artifact}, {@link Library} or {@link Build})
     * </p>
     *
     * @param dependency The dependency to resolve
     * @return The transitively resolved artifacts
     * @throws RuntimeException A runtime exception is thrown if all artifacts cannot be resolved
     */
    ObjectList<ResolvedArtifact> resolveTransitiveDependencies(Dependency dependency);
}
