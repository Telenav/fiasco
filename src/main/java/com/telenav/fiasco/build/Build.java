package com.telenav.fiasco.build;

import com.telenav.fiasco.build.repository.ArtifactResolver;
import com.telenav.fiasco.build.repository.maven.MavenPopularArtifacts;
import com.telenav.fiasco.internal.Buildable;
import com.telenav.fiasco.internal.BuildableProject;
import com.telenav.fiasco.internal.ProjectFoldersTrait;
import com.telenav.fiasco.internal.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.internal.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.internal.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.internal.phase.testing.TestingPhaseMixin;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;

public interface Build extends
        Buildable,
        MavenPopularArtifacts,
        Initializable,
        CompilationPhaseMixin,
        TestingPhaseMixin,
        PackagingPhaseMixin,
        InstallationPhaseMixin,
        ProjectFoldersTrait,
        BuildableProject,
        ArtifactResolver
{
    @Override
    default Build build()
    {
        return this;
    }
}
