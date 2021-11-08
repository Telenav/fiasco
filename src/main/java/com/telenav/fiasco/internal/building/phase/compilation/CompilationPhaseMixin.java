package com.telenav.fiasco.internal.building.phase.compilation;

import com.telenav.fiasco.internal.building.ProjectFoldersTrait;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.Phase;
import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * <b>Not public API</b>
 * <p>
 * {@link Mixin} for {@link BaseCompilationPhase}
 *
 * @author jonathanl (shibo)
 */
public interface CompilationPhaseMixin extends
        Initializable,
        Phase,
        Mixin,
        Dependency,
        ProjectFoldersTrait
{
    default BaseCompilationPhase compilationPhase()
    {
        return state(CompilationPhaseMixin.class, () -> listenTo(new BaseCompilationPhase(build())));
    }

    default void compile()
    {
        onCompile();
    }

    default void compileDocumentation()
    {
        onCompileDocumentation();
    }

    default void compileSources()
    {
        tryFinally(this::initialize, this::nextStep);
        tryFinally(this::resolveArtifacts, this::nextStep);
        tryFinally(this::generate, this::nextStep);
        tryFinally(this::preprocess, this::nextStep);
        tryFinally(this::compile, this::nextStep);
        tryFinally(this::postprocess, this::nextStep);
        tryFinally(this::compileDocumentation, this::nextStep);
        tryFinally(this::verify, this::nextStep);
    }

    default void generate()
    {
        onGenerate();
    }

    @Override
    default void initialize()
    {
        onInitialize();
    }

    default JavaCompiler javaCompiler()
    {
        return compilationPhase().javaCompiler();
    }

    default Librarian librarian()
    {
        return compilationPhase().librarian();
    }

    @Override
    default void nextStep()
    {
        compilationPhase().nextStep();
    }

    default void onCompile()
    {
        javaCompiler().compile(sourceFolder());
    }

    default void onCompileDocumentation()
    {
    }

    default void onGenerate()
    {
    }

    @Override
    default void onInitialize()
    {
    }

    default void onPostprocess()
    {
    }

    default void onPreprocess()
    {
    }

    default void onResolveArtifacts()
    {
        listenTo(librarian()).resolveAll(this);
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

    default void resolveArtifacts()
    {
        onResolveArtifacts();
    }

    default void verify()
    {
        onVerify();
    }
}
