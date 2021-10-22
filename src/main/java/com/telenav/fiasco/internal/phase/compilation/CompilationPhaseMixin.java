package com.telenav.fiasco.internal.phase.compilation;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.build.tools.repository.Librarian;
import com.telenav.fiasco.internal.ProjectFoldersTrait;
import com.telenav.fiasco.internal.dependencies.Dependency;
import com.telenav.fiasco.internal.phase.Phase;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
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
        return state(CompilationPhaseMixin.class, () -> new BaseCompilationPhase(build()));
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

    default void nextStep()
    {
        compilationPhase().nextStep();
    }

    default void onCompile()
    {
        javaCompiler().compile((FiascoBuild) this);
    }

    default void onCompileDocumentation()
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