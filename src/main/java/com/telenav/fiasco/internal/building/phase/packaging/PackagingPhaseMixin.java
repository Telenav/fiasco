package com.telenav.fiasco.internal.building.phase.packaging;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * <b>Not public API</b>
 *
 * <p>
 * {@link Mixin} for {@link PackagingPhase}
 * </p>
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
        packagingPhase().onPackageBuild();
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
        return mixin(PackagingPhaseMixin.class, () -> new PackagingPhase(parentBuild()));
    }
}
