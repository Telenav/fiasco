package com.telenav.fiasco.build;

import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.build.phase.testing.TestingPhaseMixin;
import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.repository.maven.MavenArtifact;
import com.telenav.fiasco.repository.maven.MavenCommonArtifacts;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

public abstract class Build extends BaseComponent implements
        BuildListener,
        CompilationPhaseMixin,
        PackagingPhaseMixin,
        TestingPhaseMixin,
        MavenCommonArtifacts
{
    public enum Step
    {
        INITIALIZE,
        GENERATE,
        PREPROCESS,
        COMPILE,
        POSTPROCESS,
        VERIFY,

        TEST_INITIALIZE,
        TEST_GENERATE,
        TEST_PREPROCESS,
        TEST_COMPILE,
        TEST_POSTPROCESS,
        TEST_VERIFY,
        TEST_EXECUTE,

        PACKAGE_INITIALIZE,
        PACKAGE_PREPROCESS,
        PACKAGE_COMPILE,
        PACKAGE_POSTPROCESS,
        PACKAGE_VERIFY,
        PACKAGE_INSTALL,
        PACKAGE_DEPLOY;

        Step next()
        {
            var at = ordinal();
            for (var state : values())
            {
                if (state.ordinal() == at + 1)
                {
                    return state;
                }
            }
            return fail("No next step from $", this);
        }
    }

    /** The root folder of this build */
    private Folder root;

    /** The current build step */
    private Step step = Step.INITIALIZE;

    /** The build listener to call when the build step changes */
    private BuildListener listener;

    /**
     * True if the build is at the given step
     */
    public boolean atStep(Step step)
    {
        return this.step == step;
    }

    /**
     * Runs this build with the given root folder
     */
    public final void build(Folder root)
    {
        this.root = root;

        // Compile code,
        buildCode(this);

        // build the tests,
        buildTests(this);

        // run the tests,
        runTests(this);

        // and build and install the packages.
        buildPackages(this);
    }

    /**
     * @param listener The build listener to call at each new step
     */
    public void listener(BuildListener listener)
    {
        this.listener = listener;
    }

    /**
     * Advances this build to the next step
     */
    public void nextStep()
    {
        step(step.next());
    }

    /**
     * @return The root folder of this build
     */
    public Folder root()
    {
        return root;
    }

    /**
     * Updates the build step to the given step and calls the build listener with this information
     */
    public void step(Step step)
    {
        this.step = step;
        listener.onStep(step);
    }

    /**
     * Adds the given dependency
     *
     * @param descriptor The Maven artifact descriptor of the dependency
     */
    protected void require(String descriptor)
    {
        require(MavenArtifact.parse(descriptor).asLibrary());
    }

    /**
     * Adds the given dependency(ies)
     */
    protected void require(Dependency... dependency)
    {
    }
}
