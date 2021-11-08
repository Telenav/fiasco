package com.telenav.fiasco.internal.building.phase.installation;

import com.telenav.fiasco.runtime.Phase;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * <b>Not public API</b>
 * <p>
 * {@link Mixin} for {@link InstallationPhase}
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
        return state(InstallationPhaseMixin.class, () -> new InstallationPhase(build()));
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
