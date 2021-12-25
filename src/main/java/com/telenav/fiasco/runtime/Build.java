package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.ProjectDependency;
import com.telenav.fiasco.internal.building.ProjectTrait;
import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.internal.building.phase.compilation.BuildPhase;
import com.telenav.fiasco.internal.building.phase.compilation.BuildPhaseMixin;
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
 *
 * <p>
 * A build is an {@link Initializable} and {@link Buildable} {@link ProjectDependency} that consists of several phases
 * that have been split up into independent mixins to reduce code complexity:
 *
 * <ul>
 *      <li>{@link BuildPhaseMixin}</li>
 *      <li>{@link TestingPhaseMixin}</li>
 *      <li>{@link PackagingPhaseMixin}</li>
 *      <li>{@link InstallationPhaseMixin}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 * @see Initializable
 * @see Buildable
 * @see ProjectDependency
 * @see BuildPhaseMixin
 * @see BuildPhase
 * @see TestingPhaseMixin
 * @see TestingPhase
 * @see PackagingPhaseMixin
 * @see PackagingPhase
 * @see InstallationPhaseMixin
 * @see InstallationPhase
 * @see DependencyResolver
 * @see ProjectTrait
 * @see MavenPopularArtifacts
 */
public interface Build extends
        Initializable,          // Allows the build to be initialized
        Buildable,              // Allows the build to be executed
        ProjectTrait,           // Defines the build layout
        ProjectDependency,      // Makes the build a project dependency
        BuildPhaseMixin,        // Compiles sources into target folder
        TestingPhaseMixin,      // Runs unit and integration tests
        PackagingPhaseMixin,    // Creates packages from built sources
        InstallationPhaseMixin, // Installs and deploys packages
        DependencyResolver,     // Resolves artifacts
        MavenPopularArtifacts   // Gives easy access to the top maven artifacts
{
}
