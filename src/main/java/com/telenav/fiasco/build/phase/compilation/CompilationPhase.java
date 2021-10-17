package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.project.Project;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;

@SuppressWarnings("DuplicatedCode")
public class CompilationPhase extends BasePhase
{
    public CompilationPhase(final Project project)
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

        // javaCompiler().sources(javaSources().nestedFiles(Extension.JAVA.fileMatcher())).run();
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

    protected JavaCompiler javaCompiler()
    {
        return new JavaCompiler();
    }

    protected void onRuns()
    {
    }
}
