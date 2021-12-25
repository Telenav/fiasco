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
    default void onPackageBuild()
    {
        packagingPhaseMixin().onPackageBuild();
    }

    default void onPackageInitialize()
    {
        packagingPhaseMixin().onPackageInitialize();
    }

    default void onPackagePostprocess()
    {
        packagingPhaseMixin().onPackagePostprocess();
    }

    default void onPackagePreprocess()
    {
        packagingPhaseMixin().onPackagePreprocess();
    }

    default void onPackageVerify()
    {
        packagingPhaseMixin().onPackageVerify();
    }

    default void packageInitialize()
    {
        packagingPhaseMixin().packageInitialize();
    }

    default void packagePostprocess()
    {
        packagingPhaseMixin().packagePostprocess();
    }

    default void packagePreprocess()
    {
        packagingPhaseMixin().packagePreprocess();
    }

    default void packageVerify()
    {
        packagingPhaseMixin().packageVerify();
    }

    default void packagingBuild()
    {
        packagingPhaseMixin().packageBuild();
    }

    default void packagingPhase()
    {
        packagingPhaseMixin().packagingPhase();
    }

    default PackagingPhase packagingPhaseMixin()
    {
        return mixin(PackagingPhaseMixin.class, () -> new PackagingPhase(parentBuild()));
    }
}
