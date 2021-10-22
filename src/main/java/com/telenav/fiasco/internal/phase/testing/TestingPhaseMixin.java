package com.telenav.fiasco.internal.phase.testing;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.internal.phase.Phase;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface TestingPhaseMixin extends Phase, Mixin
{
    default FiascoBuild build()
    {
        return testingPhase().build();
    }

    default void buildTestSources()
    {
        testingPhase().buildTestSources();
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

    default void runTests()
    {
        testingPhase().runTests();
    }

    default TestingPhase testingPhase()
    {
        return state(TestingPhaseMixin.class, () -> new TestingPhase(build()));
    }
}
