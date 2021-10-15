package com.telenav.fiasco.build.phase.packaging;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.phase.BasePhase;

@SuppressWarnings("DuplicatedCode")
public class PackagingPhase extends BasePhase
{
    public PackagingPhase(final Build build)
    {
        super(build);
    }

    public void buildPackages(Build build)
    {
        tryFinally(this::packageInitialize, build::nextStep);
        tryFinally(this::packagePreprocess, build::nextStep);
        tryFinally(this::packageCompile, build::nextStep);
        tryFinally(this::packagePostprocess, build::nextStep);
        tryFinally(this::packageVerify, build::nextStep);
        tryFinally(this::packageInstall, build::nextStep);
        tryFinally(this::packageDeploy, build::nextStep);
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

    public void onPackageInstall()
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

    public final void packageDeploy()
    {
        onPackageDeploy();
    }

    public final void packageInitialize()
    {
        onPackageInitialize();
    }

    public final void packageInstall()
    {
        onPackageInstall();
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
