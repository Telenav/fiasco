package com.telenav.fiasco.build.phase.installation;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.phase.Phase;

@SuppressWarnings("DuplicatedCode")
public class InstallationPhase extends Phase
{
    public InstallationPhase(final Build build)
    {
        super(build);
    }

    public void installPackages(Build build)
    {
        tryFinally(this::packageInstall, build::nextStep);
        tryFinally(this::packageDeploy, build::nextStep);
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
