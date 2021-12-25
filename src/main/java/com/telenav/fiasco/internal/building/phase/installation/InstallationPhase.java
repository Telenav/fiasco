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
 *     <li>{@link BuildStep#INSTALLATION_INSTALL}</li>
 *     <li>{@link BuildStep#INSTALLATION_DEPLOY}</li>
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

    public void installationDeploy()
    {
        onInstallationDeploy();
    }

    public void installationInstall()
    {
        onInstallationInstall();
    }

    public void installationPhase()
    {
        tryFinallyThrow(this::installationInstall, this::nextStep);
        tryFinallyThrow(this::installationDeploy, this::finished);
    }

    public void onInstallationDeploy()
    {
    }

    public void onInstallationInstall()
    {
    }

    /**
     * The installation phase is finished and so is the entire build
     */
    void finished()
    {
    }
}
