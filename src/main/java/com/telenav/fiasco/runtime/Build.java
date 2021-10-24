package com.telenav.fiasco.runtime;

import com.telenav.fiasco.runtime.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenPopularArtifacts;
import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.DependentProject;
import com.telenav.fiasco.internal.building.ProjectFoldersTrait;
import com.telenav.fiasco.internal.building.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.internal.building.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.internal.building.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.internal.building.phase.testing.TestingPhaseMixin;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;

/**
 * A build is {@link Initializable} and {@link Buildable}, and consists of several phases that have been split up into
 * independent mixins to reduce code complexity:
 *
 * <ul>
 *      <li>{@link CompilationPhaseMixin}</li>
 *      <li>{@link TestingPhaseMixin}</li>
 *      <li>{@link PackagingPhaseMixin}</li>
 *      <li>{@link InstallationPhaseMixin}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public interface Build extends
        Initializable,          // Allows the build to be initialized
        Buildable,              // Allows the build to be executed
        CompilationPhaseMixin,  // Compiles sources into target folder
        TestingPhaseMixin,      // Runs unit and integration tests
        PackagingPhaseMixin,    // Creates packages from built sources
        InstallationPhaseMixin, // Installs and deploys packages
        ProjectFoldersTrait,    // Defines the build layout
        DependentProject,       // Makes the build a project dependency
        ArtifactResolver,       // Resolves artifacts
        MavenPopularArtifacts   // Gives easy access to the top maven artifacts
{
    @Override
    default Build build()
    {
        return this;
    }
}
