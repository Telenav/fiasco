package com.telenav.fiasco.build.phase.packaging;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * {@link Mixin} for {@link PackagingPhase}
 *
 * @author jonathanl (shibo)
 */
public interface PackagingPhaseMixin extends Phase, Mixin
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
        return state(PackagingPhaseMixin.class, () -> new PackagingPhase(build()));
    }
}
