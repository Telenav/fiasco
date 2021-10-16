package com.telenav.fiasco.build.phase.testing;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.TestResult;
import com.telenav.fiasco.build.phase.Phase;

@SuppressWarnings("DuplicatedCode")
public class TestingPhase extends Phase
{
    public TestingPhase(final Build build)
    {
        super(build);
    }

    public void buildTestSources(Build build)
    {
        tryFinally(this::testInitialize, build::nextStep);
        tryFinally(this::testGenerate, build::nextStep);
        tryFinally(this::testPreprocess, build::nextStep);
        tryFinally(this::testCompile, build::nextStep);
        tryFinally(this::testPostprocess, build::nextStep);
        tryFinally(this::testVerify, build::nextStep);
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

    public TestResult runTests(Build build)
    {
        return tryFinallyReturn(this::testRunTests, build::nextStep);
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
