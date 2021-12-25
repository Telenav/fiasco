package com.telenav.fiasco.internal.building.phase.installation;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * <b>Not public API</b>
 *
 * <p>
 * {@link Mixin} for {@link InstallationPhase}
 * </p>
 *
 * @author jonathanl (shibo)
 */
public interface InstallationPhaseMixin extends Phase, Mixin
{
    default void installPackages()
    {
        installationPhase().installPackages();
    }

    default InstallationPhase installationPhase()
    {
        return mixin(InstallationPhaseMixin.class, () -> new InstallationPhase(parentBuild()));
    }

    default void onPackageDeploy()
    {
        installationPhase().onPackageDeploy();
    }

    default void onPackageInstall()
    {
        installationPhase().onPackageInstall();
    }
}
