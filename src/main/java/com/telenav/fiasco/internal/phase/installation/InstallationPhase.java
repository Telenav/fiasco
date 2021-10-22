package com.telenav.fiasco.internal.phase.installation;

import com.telenav.fiasco.internal.BuildStep;
import com.telenav.fiasco.internal.phase.BasePhase;
import com.telenav.fiasco.internal.phase.Phase;
import com.telenav.fiasco.build.FiascoBuild;

/**
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
    public InstallationPhase(FiascoBuild build)
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
