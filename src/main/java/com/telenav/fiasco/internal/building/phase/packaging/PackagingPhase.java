package com.telenav.fiasco.internal.building.phase.packaging;

import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.phase.BasePhase;
import com.telenav.fiasco.runtime.Build;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Executes the steps in the packaging phase of a build:
 * </p>
 *
 * <ol>
 *     <li>{@link BuildStep#PACKAGING_INITIALIZE}</li>
 *     <li>{@link BuildStep#PACKAGING_PREPROCESS}</li>
 *     <li>{@link BuildStep#PACKAGING_BUILD}</li>
 *     <li>{@link BuildStep#PACKAGING_POSTPROCESS}</li>
 *     <li>{@link BuildStep#PACKAGING_VERIFY}</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see Phase
 * @see BuildStep
 */
public class PackagingPhase extends BasePhase
{
    public PackagingPhase(Build build)
    {
        super(build);
    }

    public void onPackageBuild()
    {
    }

    public void onPackageDeploy()
    {
    }

    public void onPackageInitialize()
    {
    }

    public void onPackagePostprocess()
    {
    }

    public void onPackagePreprocess()
    {
    }

    public void onPackageVerify()
    {
    }

    public final void packageBuild()
    {
        onPackageBuild();
    }

    public final void packageInitialize()
    {
        onPackageInitialize();
    }

    public final void packagePostprocess()
    {
        onPackagePostprocess();
    }

    public final void packagePreprocess()
    {
        onPackagePreprocess();
    }

    public final void packageVerify()
    {
        onPackageVerify();
    }

    public void packagingPhase()
    {
        tryFinallyThrow(this::packageInitialize, this::nextStep);
        tryFinallyThrow(this::packagePreprocess, this::nextStep);
        tryFinallyThrow(this::packageBuild, this::nextStep);
        tryFinallyThrow(this::packagePostprocess, this::nextStep);
        tryFinallyThrow(this::packageVerify, this::nextStep);
    }
}
