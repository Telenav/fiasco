package com.telenav.fiasco.build.phase.packaging;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface PackagingPhaseMixin extends Component, Mixin
{
    default void buildPackages()
    {
        packagingPhase().buildPackages();
    }

    default void onPackageCompile()
    {
        packagingPhase().onPackageCompile();
    }

    default void onPackageInitialize()
    {
        packagingPhase().onPackageInitialize();
    }

    default void onPackagePostprocess()
    {
        packagingPhase().onPackagePostprocess();
    }

    default void onPackagePreprocess()
    {
        packagingPhase().onPackagePreprocess();
    }

    default void onPackageVerify()
    {
        packagingPhase().onPackageVerify();
    }

    default PackagingPhase packagingPhase()
    {
        return state(PackagingPhaseMixin.class, () -> new PackagingPhase(project()));
    }

    default Project project()
    {
        return packagingPhase().project();
    }
}
