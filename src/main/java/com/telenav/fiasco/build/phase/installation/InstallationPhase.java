package com.telenav.fiasco.build.phase.installation;

import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.project.Project;

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
