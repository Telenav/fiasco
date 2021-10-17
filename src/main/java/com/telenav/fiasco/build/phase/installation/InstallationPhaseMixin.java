package com.telenav.fiasco.build.phase.installation;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface InstallationPhaseMixin extends Component, Mixin, Phase
{
    default void installPackages()
    {
        installationPhase().installPackages();
    }

    default InstallationPhase installationPhase()
    {
        return state(InstallationPhaseMixin.class, () -> new InstallationPhase(project()));
    }

    default void onPackageDeploy()
    {
        installationPhase().onPackageDeploy();
    }

    default void onPackageInstall()
    {
        installationPhase().onPackageInstall();
    }

    default Project project()
    {
        return installationPhase().project();
    }
}
