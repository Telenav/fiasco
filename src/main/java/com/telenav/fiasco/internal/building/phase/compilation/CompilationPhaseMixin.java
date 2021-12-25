package com.telenav.fiasco.internal.building.phase.compilation;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.ProjectFoldersTrait;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.mixin.Mixin;
import com.telenav.kivakit.kernel.messaging.messages.MessageException;

/**
 * <b>Not public API</b>
 *
 * <p>
 * {@link Mixin} for {@link CompilationPhase}
 * </p>
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
    default CompilationPhase compilationPhase()
    {
        return mixin(CompilationPhaseMixin.class, () -> listenTo(new CompilationPhase(parentBuild())));
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
        tryFinallyThrow(this::initialize, this::nextStep);
        tryFinallyThrow(this::resolveDependencies, this::nextStep);
        tryFinallyThrow(this::generate, this::nextStep);
        tryFinallyThrow(this::preprocess, this::nextStep);
        tryFinallyThrow(this::compile, this::nextStep);
        tryFinallyThrow(this::postprocess, this::nextStep);
        tryFinallyThrow(this::compileDocumentation, this::nextStep);
        tryFinallyThrow(this::verify, this::nextStep);
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
        return require(Librarian.class);
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

    default void onResolveDependencies()
    {
        librarian().resolveTransitiveDependencies(this);
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

    default void resolveDependencies()
    {
        try
        {
            onResolveDependencies();
        }
        catch (MessageException e)
        {
            transmit(e.messageObject().cause(null));
        }
        catch (Exception e)
        {
            problem(e, "Unable to resolve dependencies");
        }
    }

    default void verify()
    {
        onVerify();
    }
}
