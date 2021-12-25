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
    default void onTestCompileSources()
    {
        testingPhaseMixin().onTestCompileSources();
    }

    default void onTestInitialize()
    {
        testingPhaseMixin().onTestInitialize();
    }

    default void onTestPostprocess()
    {
        testingPhaseMixin().onTestPostprocess();
    }

    default void onTestPreprocess()
    {
        testingPhaseMixin().onTestPreprocess();
    }

    default void onTestVerify()
    {
        testingPhaseMixin().onTestVerify();
    }

    @Override
    default Build parentBuild()
    {
        return testingPhaseMixin().parentBuild();
    }

    default void testCompileSources()
    {
        testingPhaseMixin().testCompileSources();
    }

    default void testGenerateSources()
    {
        testingPhaseMixin().testGenerateSources();
    }

    default void testInitialize()
    {
        testingPhaseMixin().testInitialize();
    }

    default void testPostprocess()
    {
        testingPhaseMixin().testPostprocess();
    }

    default void testPreprocess()
    {
        testingPhaseMixin().testPreprocess();
    }

    default void testResolveDependencies()
    {
        testingPhaseMixin().testResolveDependencies();
    }

    default void testRunTests()
    {
        testingPhaseMixin().testRunTests();
    }

    default void testVerify()
    {
        testingPhaseMixin().testVerify();
    }

    default void testingPhase()
    {
        testingPhaseMixin().testingPhase();
    }

    default TestingPhase testingPhaseMixin()
    {
        return mixin(TestingPhaseMixin.class, () -> new TestingPhase(parentBuild()));
    }
}
