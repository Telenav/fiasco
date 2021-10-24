package com.telenav.fiasco.internal.building.phase.installation;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.phase.BasePhase;

/**
 * <b>Not public API</b>
 * <p>
 * Executes the steps in the installation phase of a build:
 *
 * <ol>
 *     <li>PACKAGE_INSTALL</li>
 *     <li>PACKAGE_DEPLOY</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see Phase
 * @see BuildStep
 */
@SuppressWarnings("DuplicatedCode")
public class InstallationPhase extends BasePhase
{
    public InstallationPhase(Build build)
    {
        super(build);
    }

    public void installPackages()
    {
        tryFinally(this::packageInstall, this::nextStep);
        tryFinally(this::packageDeploy, () ->
        {
        });
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
