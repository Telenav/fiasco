package com.telenav.fiasco.build.phase.installation;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface InstallationPhaseMixin extends Component, Mixin
{
    default void installPackages(Build build)
    {
        installationPhase().installPackages(build);
    }

    default InstallationPhase installationPhase()
    {
        return state(InstallationPhaseMixin.class, () -> new InstallationPhase((Build) this));
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
