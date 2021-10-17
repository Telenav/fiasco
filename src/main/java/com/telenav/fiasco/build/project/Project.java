package com.telenav.fiasco.build.project;

import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.build.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.build.phase.testing.TestingPhaseMixin;
import com.telenav.fiasco.dependencies.ProjectDependency;
import com.telenav.fiasco.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.dependencies.repository.maven.MavenPopularArtifacts;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;

public interface Project extends
        Buildable,
        MavenPopularArtifacts,
        Initializable,
        CompilationPhaseMixin,
        TestingPhaseMixin,
        PackagingPhaseMixin,
        InstallationPhaseMixin,
        ProjectDependency,
        ArtifactResolver
{
    @Override
    default Project project()
    {
        return TestingPhaseMixin.super.project();
    }

    /**
     * @return The root folder for this project
     */
    Folder root();
}
