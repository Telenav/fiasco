package com.telenav.fiasco.build.phase.compilation;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface CompilationPhaseMixin extends Component, Mixin, Phase, Initializable
{
    default void buildSources()
    {
        compilationPhase().buildSources();
    }

    default CompilationPhase compilationPhase()
    {
        return state(CompilationPhaseMixin.class, () -> new CompilationPhase(project()));
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

    default Project project()
    {
        return compilationPhase().project();
    }
}
