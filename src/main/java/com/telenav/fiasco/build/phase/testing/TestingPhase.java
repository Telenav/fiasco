package com.telenav.fiasco.build.phase.testing;

import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.project.BaseProject;

/**
 * Executes the steps in the installation phase of a build:
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
    public TestingPhase(final BaseProject project)
    {
        super(project);
    }

    public void buildTestSources()
    {
        tryFinally(this::testInitialize, this::nextStep);
        tryFinally(this::testGenerate, this::nextStep);
        tryFinally(this::testPreprocess, this::nextStep);
        tryFinally(this::testCompile, this::nextStep);
        tryFinally(this::testPostprocess, this::nextStep);
        tryFinally(this::testVerify, this::nextStep);
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
