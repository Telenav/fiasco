package com.telenav.fiasco.build.phase.packaging;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface PackagingPhaseMixin extends Component, Mixin
{
    default void buildPackages(Build build)
    {
        packagingPhase().buildPackages(build);
    }

    default void onPackageCompile()
    {
        packagingPhase().onPackageCompile();
    }

    default void onPackageDeploy()
    {
        packagingPhase().onPackageDeploy();
    }

    default void onPackageInitialize()
    {
        packagingPhase().onPackageInitialize();
    }

    default void onPackageInstall()
    {
        packagingPhase().onPackageInstall();
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
        return state(PackagingPhaseMixin.class, () -> new PackagingPhase((Build) this));
    }
}
