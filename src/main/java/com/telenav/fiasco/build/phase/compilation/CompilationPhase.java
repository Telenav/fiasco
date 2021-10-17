package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.project.BaseProject;

/**
 * Executes the steps in the compilation phase of a build:
 *
 * <ol>
 *     <li>INITIALIZE</li>
 *     <li>GENERATE</li>
 *     <li>PREPROCESS</li>
 *     <li>COMPILE</li>
 *     <li>POSTPROCESS</li>
 *     <li>VERIFY</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see BuildStep
 * @see Phase
 */
@SuppressWarnings("DuplicatedCode")
public class CompilationPhase extends BasePhase
{
    public CompilationPhase(final BaseProject project)
    {
        super(project);
    }

    public void buildSources()
    {
        tryFinally(this::initialize, this::nextStep);
        tryFinally(this::generate, this::nextStep);
        tryFinally(this::preprocess, this::nextStep);
        tryFinally(this::compile, this::nextStep);
        tryFinally(this::postprocess, this::nextStep);
        tryFinally(this::verify, this::nextStep);
    }

    public final void compile()
    {
        onCompile();
    }

    public final void generate()
    {
        onGenerate();
    }

    public final void initialize()
    {
        onInitialize();
    }

    public void onCompile()
    {
    }

    public void onGenerate()
    {

    }

    public void onInitialize()
    {
    }

    public void onPostprocess()
    {

    }

    public void onPreprocess()
    {

    }

    public void onVerify()
    {

    }

    public final void postprocess()
    {
        onPostprocess();
    }

    public final void preprocess()
    {
        onPreprocess();
    }

    public final void verify()
    {
        onVerify();
    }

    protected void onRuns()
    {
    }
}
