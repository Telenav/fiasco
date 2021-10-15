package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.tools.compiler.JavaCompiler;
import com.telenav.kivakit.resource.path.Extension;

@SuppressWarnings("DuplicatedCode")
public class CompilationPhase extends BasePhase
{
    public CompilationPhase(final Build build)
    {
        super(build);
    }

    public void build(Build build)
    {
        tryFinally(this::initialize, build::nextStep);
        tryFinally(this::generate, build::nextStep);
        tryFinally(this::preprocess, build::nextStep);
        tryFinally(this::compile, build::nextStep);
        tryFinally(this::postprocess, build::nextStep);
        tryFinally(this::verify, build::nextStep);
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
        javaCompiler().sources(javaSources().nestedFiles(Extension.JAVA.fileMatcher())).run();
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
