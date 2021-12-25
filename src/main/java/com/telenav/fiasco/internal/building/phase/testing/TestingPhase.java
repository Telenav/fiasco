package com.telenav.fiasco.internal.building.phase.testing;

import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.phase.BasePhase;
import com.telenav.fiasco.runtime.Build;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Executes the steps in the installation phase of a build:
 * </p>
 *
 * <ol>
 *     <li>{@link BuildStep#TESTING_INITIALIZE}</li>
 *     <li>{@link BuildStep#TESTING_GENERATE_SOURCES}</li>
 *     <li>{@link BuildStep#TESTING_PREPROCESS}</li>
 *     <li>{@link BuildStep#TESTING_COMPILE}</li>
 *     <li>{@link BuildStep#TESTING_POSTPROCESS}</li>
 *     <li>{@link BuildStep#TESTING_VERIFY}</li>
 *     <li>{@link BuildStep#TESTING_RUN_TESTS}</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see Phase
 * @see BuildStep
 */
public class TestingPhase extends BasePhase
{
    public TestingPhase(Build build)
    {
        super(build);
    }

    public void onTestCompileSources()
    {
    }

    public void onTestGenerateSources()
    {
    }

    public void onTestInitialize()
    {
    }

    public void onTestPostprocess()
    {

    }

    public void onTestPreprocess()
    {

    }

    public void onTestResolveDependencies()
    {
    }

    public TestResult onTestRunTests()
    {
        return null;
    }

    public void onTestVerify()
    {
    }

    public final void testCompileSources()
    {
        onTestCompileSources();
    }

    public final void testGenerateSources()
    {
        onTestGenerateSources();
    }

    public final void testInitialize()
    {
        onTestInitialize();
    }

    public final void testPostprocess()
    {
        onTestPostprocess();
    }

    public final void testPreprocess()
    {
        onTestPreprocess();
    }

    public void testResolveDependencies()
    {
        onTestResolveDependencies();
    }

    public final TestResult testRunTests()
    {
        return onTestRunTests();
    }

    public final void testVerify()
    {
        onTestVerify();
    }

    public TestResult testingPhase()
    {
        tryFinallyThrow(this::testInitialize, this::nextStep);
        tryFinallyThrow(this::testResolveDependencies, this::nextStep);
        tryFinallyThrow(this::testGenerateSources, this::nextStep);
        tryFinallyThrow(this::testPreprocess, this::nextStep);
        tryFinallyThrow(this::testCompileSources, this::nextStep);
        tryFinallyThrow(this::testPostprocess, this::nextStep);
        tryFinallyThrow(this::testVerify, this::nextStep);
        return tryFinallyReturn(this::testRunTests, this::nextStep);
    }
}
