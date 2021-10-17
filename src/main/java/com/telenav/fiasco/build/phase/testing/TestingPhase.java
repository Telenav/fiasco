package com.telenav.fiasco.build.phase.testing;

import com.telenav.fiasco.build.phase.BasePhase;
import com.telenav.fiasco.build.project.Project;

@SuppressWarnings("DuplicatedCode")
public class TestingPhase extends BasePhase
{
    public TestingPhase(final Project project)
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
