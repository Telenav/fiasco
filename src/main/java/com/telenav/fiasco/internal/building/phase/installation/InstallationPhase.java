package com.telenav.fiasco.internal.building.phase.installation;

import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.phase.BasePhase;
import com.telenav.fiasco.runtime.Build;

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
public class InstallationPhase extends BasePhase
{
    public InstallationPhase(Build build)
    {
        super(build);
    }

    public void installPackages()
    {
        tryFinallyThrow(this::packageInstall, this::nextStep);
        tryFinallyThrow(this::packageDeploy, this::finished);
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

    /**
     * The installation phase is finished and so is the entire build
     */
    void finished()
    {
    }
}
