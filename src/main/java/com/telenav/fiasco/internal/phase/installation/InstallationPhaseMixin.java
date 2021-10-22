package com.telenav.fiasco.internal.phase.installation;

import com.telenav.fiasco.internal.phase.Phase;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
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
