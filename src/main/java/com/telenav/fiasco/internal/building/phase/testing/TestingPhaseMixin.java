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

    @Override
    default Build parentBuild()
    {
        return testingPhase().parentBuild();
    }

    default void runTests()
    {
        testingPhase().runTests();
    }

    default TestingPhase testingPhase()
    {
        return mixin(TestingPhaseMixin.class, () -> new TestingPhase(parentBuild()));
    }
}
