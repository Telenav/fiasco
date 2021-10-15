package com.telenav.fiasco.build.phase.testing;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.phase.BasePhase;

@SuppressWarnings("DuplicatedCode")
public class TestingPhase extends BasePhase
{
    public TestingPhase(final Build build)
    {
        super(build);
    }

    public void buildAndRunTests(Build build)
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

    public void runTests(Build build)
    {
        tryFinally(this::testRunTests, build::nextStep);
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

    public final void testRunTests()
    {
        onTestRunTests();
    }

    public final void testVerify()
    {
        onTestVerify();
    }

    protected void onTestRunTests()
    {
    }
}
