package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.ProjectDependency;
import com.telenav.fiasco.internal.building.ProjectFoldersTrait;
import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.internal.building.phase.compilation.CompilationPhase;
import com.telenav.fiasco.internal.building.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.internal.building.phase.installation.InstallationPhase;
import com.telenav.fiasco.internal.building.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.internal.building.phase.packaging.PackagingPhase;
import com.telenav.fiasco.internal.building.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.internal.building.phase.testing.TestingPhase;
import com.telenav.fiasco.internal.building.phase.testing.TestingPhaseMixin;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenPopularArtifacts;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;

/**
 * <p>
 * Organizes and executes the phases of a Fiasco build.
 * </p>
 * <p>
 * A build is an {@link Initializable} and {@link Buildable} {@link ProjectDependency} that consists of several phases
 * that have been split up into independent mixins to reduce code complexity:
 *
 * <ul>
 *      <li>{@link CompilationPhaseMixin}</li>
 *      <li>{@link TestingPhaseMixin}</li>
 *      <li>{@link PackagingPhaseMixin}</li>
 *      <li>{@link InstallationPhaseMixin}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 * @see Initializable
 * @see Buildable
 * @see ProjectDependency
 * @see CompilationPhaseMixin
 * @see CompilationPhase
 * @see TestingPhaseMixin
 * @see TestingPhase
 * @see PackagingPhaseMixin
 * @see PackagingPhase
 * @see InstallationPhaseMixin
 * @see InstallationPhase
 * @see DependencyResolver
 * @see ProjectFoldersTrait
 * @see MavenPopularArtifacts
 */
public interface Build extends
        Initializable,          // Allows the build to be initialized
        Buildable,              // Allows the build to be executed
        ProjectDependency,      // Makes the build a project dependency
        CompilationPhaseMixin,  // Compiles sources into target folder
        TestingPhaseMixin,      // Runs unit and integration tests
        PackagingPhaseMixin,    // Creates packages from built sources
        InstallationPhaseMixin, // Installs and deploys packages
        ProjectFoldersTrait,    // Defines the build layout
        DependencyResolver,     // Resolves artifacts
        MavenPopularArtifacts   // Gives easy access to the top maven artifacts
{
}
