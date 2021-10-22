package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * {@link Mixin} for {@link BaseCompilationPhase}
 *
 * @author jonathanl (shibo)
 */
public interface CompilationPhaseMixin extends Initializable, Phase, Mixin
{
    default BaseCompilationPhase compilationPhase()
    {
        return state(CompilationPhaseMixin.class, () -> new BaseCompilationPhase(build()));
    }

    default void compile()
    {
        onCompile();
    }

    default void compileSources()
    {
        tryFinally(this::initialize, this::nextStep);
        tryFinally(this::generate, this::nextStep);
        tryFinally(this::preprocess, this::nextStep);
        tryFinally(this::compile, this::nextStep);
        tryFinally(this::postprocess, this::nextStep);
        tryFinally(this::verify, this::nextStep);
    }

    default void generate()
    {
        onGenerate();
    }

    default void initialize()
    {
        onInitialize();
    }

    default JavaCompiler javaCompiler()
    {
        return compilationPhase().javaCompiler();
    }

    default void nextStep()
    {
        compilationPhase().nextStep();
    }

    default void onCompile()
    {
    }

    default void onGenerate()
    {
    }

    default void onInitialize()
    {
    }

    default void onPostprocess()
    {
    }

    default void onPreprocess()
    {
    }

    default void onVerify()
    {
    }

    default void postprocess()
    {
        onPostprocess();
    }

    default void preprocess()
    {
        onPreprocess();
    }

    default void verify()
    {
        onVerify();
    }
}
