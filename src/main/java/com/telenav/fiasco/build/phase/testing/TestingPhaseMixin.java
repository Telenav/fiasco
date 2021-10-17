package com.telenav.fiasco.build.phase.testing;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.Component;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

@SuppressWarnings("DuplicatedCode")
public interface TestingPhaseMixin extends Component, Mixin
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

    default Project project()
    {
        return testingPhase().project();
    }

    default void runTests()
    {
        testingPhase().runTests();
    }

    default TestingPhase testingPhase()
    {
        return state(TestingPhaseMixin.class, () -> new TestingPhase(project()));
    }
}
