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
 *     <li>{@link BuildStep#PACKAGE_INSTALL}</li>
 *     <li>{@link BuildStep#PACKAGE_DEPLOY}</li>
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

    public void artifactsDeploy()
    {
        tryFinallyThrow(this::artifactsInstall, this::nextStep);
        tryFinallyThrow(this::onArtifactsDeploy, this::finished);
    }

    public final void artifactsInstall()
    {
        onArtifactsInstall();
    }

    public void onArtifactsDeploy()
    {
    }

    public void onArtifactsInstall()
    {
    }

    /**
     * The installation phase is finished and so is the entire build
     */
    void finished()
    {
    }
}
