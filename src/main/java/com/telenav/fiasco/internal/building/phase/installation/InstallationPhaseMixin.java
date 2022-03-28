package com.telenav.fiasco.internal.building.phase.installation;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.kivakit.core.language.mixin.Mixin;

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
    default void installationDeploy()
    {
        installationPhaseMixin().installationDeploy();
    }

    default void installationInstall()
    {
        installationPhaseMixin().installationInstall();
    }

    default void installationPhase()
    {
        tryFinallyThrow(this::installationInstall, this::nextStep);
        tryFinallyThrow(this::installationDeploy, this::nextStep);
    }

    default InstallationPhase installationPhaseMixin()
    {
        return mixin(InstallationPhaseMixin.class, () -> new InstallationPhase(parentBuild()));
    }

    default void onInstallationDeploy()
    {
        installationPhaseMixin().onInstallationDeploy();
    }

    default void onInstallationInstall()
    {
        installationPhaseMixin().onInstallationInstall();
    }
}
