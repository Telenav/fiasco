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
    default void artifactsDeploy()
    {
        mixin().artifactsDeploy();
    }

    default void artifactsInstall()
    {
        mixin().artifactsInstall();
    }

    default InstallationPhase mixin()
    {
        return mixin(InstallationPhaseMixin.class, () -> new InstallationPhase(parentBuild()));
    }

    default void onArtifactsDeploy()
    {
        mixin().onArtifactsDeploy();
    }

    default void onArtifactsInstall()
    {
        mixin().onArtifactsInstall();
    }
}
