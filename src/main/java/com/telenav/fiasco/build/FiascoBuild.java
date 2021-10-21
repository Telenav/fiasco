package com.telenav.fiasco.build;

import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.build.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.build.phase.testing.TestingPhaseMixin;
import com.telenav.fiasco.dependencies.ProjectDependency;
import com.telenav.fiasco.dependencies.repository.ArtifactResolver;
import com.telenav.fiasco.dependencies.repository.maven.MavenPopularArtifacts;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;

public interface FiascoBuild extends
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
    default FiascoBuild build()
    {
        return this;
    }

    /**
     * Sets this build's root folder
     */
    FiascoBuild root(final Folder root);

    /**
     * @return The root folder for this project
     */
    Folder root();
}
