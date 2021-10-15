package com.telenav.fiasco.build.phase.testing;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface TestingPhaseMixin extends Component, Mixin
{
    default void buildTests(Build build)
    {
        testingPhase().buildAndRunTests(build);
    }

    default void onTestCompile()
    {
        testingPhase().onTestCompile();
    }

    default void onTestInitialize()
    {
        testingPhase().onTestInitialize();
    }

    default void onTestPostprocess()
    {
        testingPhase().onTestPostprocess();
    }

    default void onTestPreprocess()
    {
        testingPhase().onTestPreprocess();
    }

    default void onTestVerify()
    {
        testingPhase().onTestVerify();
    }

    default void runTests(Build build)
    {
        testingPhase().runTests(build);
    }

    default TestingPhase testingPhase()
    {
        return state(TestingPhaseMixin.class, () -> new TestingPhase((Build) this));
    }
}
