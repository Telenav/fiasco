package com.telenav.fiasco.internal.building.phase.testing;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

/**
 * <b>Not public API</b>
 *
 * @author jonathanl (shibo)
 */
public interface TestingPhaseMixin extends Phase, Mixin
{
    @Override
    default Build build()
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