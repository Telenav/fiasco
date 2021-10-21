package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * {@link Mixin} for {@link CompilationPhase}
 *
 * @author jonathanl (shibo)
 */
public interface CompilationPhaseMixin extends Initializable, Phase, Mixin
{
    default void buildSources()
    {
        compilationPhase().buildSources();
    }

    default CompilationPhase compilationPhase()
    {
        return state(CompilationPhaseMixin.class, () -> new CompilationPhase(build()));
    }

    default void onCompile()
    {
        compilationPhase().onCompile();
    }

    default void onInitialize()
    {
        compilationPhase().onInitialize();
    }

    default void onPostprocess()
    {
        compilationPhase().onPostprocess();
    }

    default void onPreprocess()
    {
        compilationPhase().onPreprocess();
    }

    default void onVerify()
    {
        compilationPhase().onVerify();
    }
}
