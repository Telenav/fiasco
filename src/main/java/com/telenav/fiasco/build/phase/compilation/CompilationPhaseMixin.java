package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface CompilationPhaseMixin extends Component, Mixin
{
    default void buildSources(Build build)
    {
        compilationPhase().buildSources(build);
    }

    default CompilationPhase compilationPhase()
    {
        return state(CompilationPhaseMixin.class, () -> new CompilationPhase((Build) this));
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
