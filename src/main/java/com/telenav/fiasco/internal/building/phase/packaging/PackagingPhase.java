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
 *     <li>PACKAGE_INITIALIZE</li>
 *     <li>PACKAGE_PREPROCESS</li>
 *     <li>PACKAGE_COMPILE</li>
 *     <li>PACKAGE_POSTPROCESS</li>
 *     <li>PACKAGE_VERIFY</li>
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

    public void buildPackages()
    {
        tryFinallyThrow(this::packageInitialize, this::nextStep);
        tryFinallyThrow(this::packagePreprocess, this::nextStep);
        tryFinallyThrow(this::packageCompile, this::nextStep);
        tryFinallyThrow(this::packagePostprocess, this::nextStep);
        tryFinallyThrow(this::packageVerify, this::nextStep);
    }

    public void onPackageCompile()
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

    public final void packageCompile()
    {
        onPackageCompile();
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
}
