package com.telenav.fiasco.build.phase.packaging;

import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.project.Project;

@SuppressWarnings("DuplicatedCode")
public class PackagingPhase extends BasePhase
{
    public PackagingPhase(final Project project)
    {
        super(project);
    }

    public void buildPackages()
    {
        tryFinally(this::packageInitialize, this::nextStep);
        tryFinally(this::packagePreprocess, this::nextStep);
        tryFinally(this::packageCompile, this::nextStep);
        tryFinally(this::packagePostprocess, this::nextStep);
        tryFinally(this::packageVerify, this::nextStep);
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
