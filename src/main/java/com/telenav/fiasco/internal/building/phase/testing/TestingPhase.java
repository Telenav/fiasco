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
 *     <li>TEST_INITIALIZE</li>
 *     <li>TEST_GENERATE</li>
 *     <li>TEST_PREPROCESS</li>
 *     <li>TEST_COMPILE</li>
 *     <li>TEST_POSTPROCESS</li>
 *     <li>TEST_VERIFY</li>
 *     <li>TEST_RUN_TESTS</li>
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

    public void buildTestSources()
    {
        tryFinallyThrow(this::testInitialize, this::nextStep);
        tryFinallyThrow(this::testResolveDependencies, this::nextStep);
        tryFinallyThrow(this::testGenerate, this::nextStep);
        tryFinallyThrow(this::testPreprocess, this::nextStep);
        tryFinallyThrow(this::testCompile, this::nextStep);
        tryFinallyThrow(this::testPostprocess, this::nextStep);
        tryFinallyThrow(this::testVerify, this::nextStep);
    }

    public void onTestCompile()
    {
    }

    public void onTestGenerate()
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

    public void onTestVerify()
    {

    }

    public TestResult runTests()
    {
        return tryFinallyReturn(this::testRunTests, this::nextStep);
    }

    public final void testCompile()
    {
        onTestCompile();
    }

    public final void testGenerate()
    {
        onTestGenerate();
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
        onTestRunTests();
        return null;
    }

    public final void testVerify()
    {
        onTestVerify();
    }

    protected void onTestRunTests()
    {
    }
}
