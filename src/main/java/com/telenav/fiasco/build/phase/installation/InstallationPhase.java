package com.telenav.fiasco.build.phase.installation;

import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.project.Project;

@SuppressWarnings("DuplicatedCode")
public class InstallationPhase extends BasePhase
{
    public InstallationPhase(Project project)
    {
        super(project);
    }

    public void installPackages()
    {
        tryFinally(this::packageInstall, this::nextStep);
        tryFinally(this::packageDeploy, this::nextStep);
    }

    public void onPackageDeploy()
    {

    }

    public void onPackageInstall()
    {

    }

    public final void packageDeploy()
    {
        onPackageDeploy();
    }

    public final void packageInstall()
    {
        onPackageInstall();
    }
}
