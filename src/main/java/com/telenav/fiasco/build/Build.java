package com.telenav.fiasco.build;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.installation.InstallationPhase;
import com.telenav.fiasco.build.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.build.phase.packaging.PackagingPhase;
import com.telenav.fiasco.build.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.build.phase.testing.TestingPhase;
import com.telenav.fiasco.build.phase.testing.TestingPhaseMixin;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.vm.JavaVirtualMachine;

/**
 * Base class for user build subclasses. The {@link #build()} method builds the set of {@link Buildables} added by
 * {@link #add(Buildable...)}. As the build proceeds the {@link BuildListener} specified by {@link
 * #listener(BuildListener)} is called with {@link BuildResult}s.
 *
 * <p>
 * Builds proceed in a series of {@link BuildStep}s which are grouped into {@link Phase}s:
 * </p>
 *
 * <ol>
 *     <li>{@link CompilationPhaseMixin#buildSources(Build)} - Builds sources into output files</li>
 *     <li>{@link TestingPhase#buildTestSources(Build)} - Builds test sources</li>
 *     <li>{@link TestingPhase#runTests(Build)} - Runs tests</li>
 *     <li>{@link PackagingPhase#buildPackages(Build)} - Packages output files</li>
 *     <li>{@link InstallationPhase#installPackages(Build)} - Installs packages</li>
 * </ol>
 *
 * <p>
 * When a phase completes a step, it calls {@link #nextStep(), which advances the build to the next step, and calls
 * the* {@link BuildListener#onBuildStep(BuildStep)} method with the new build step.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public abstract class Build extends BaseComponent implements
        CompilationPhaseMixin,
        TestingPhaseMixin,
        PackagingPhaseMixin,
        InstallationPhaseMixin
{
    /** Group of {@link Buildable}s to build */
    private final Buildables buildables = Buildables.create();

    /** The current build step */
    private BuildStep step = BuildStep.INITIALIZE;

    /** The build listener to call when the build step changes */
    private BuildListener listener;

    protected Build()
    {
    }

    public Build add(Buildable... buildables)
    {
        this.buildables.addAll(buildables);
        return this;
    }

    /**
     * True if the build is at the given step
     */
    public boolean atStep(BuildStep step)
    {
        return this.step == step;
    }

    /**
     * Runs this build with the given root folder
     */
    public final void build()
    {
        // Compile code,
        buildSources(this);

        // build the tests,
        buildTestSources(this);

        // run the tests,
        runTests(this);

        // package up the code,
        buildPackages(this);

        // and install it.
        installPackages(this);
    }

    public Buildables buildables()
    {
        return buildables;
    }

    public Folder folderForProperty(String environmentVariable)
    {
        return Folder.parse(JavaVirtualMachine.property(environmentVariable));
    }

    /**
     * @param listener The build listener to call at each new step
     */
    public void listener(BuildListener listener)
    {
        this.listener = listener;
    }

    /**
     * Updates the build step to the given step and calls the build listener with this information
     */
    public void nextStep()
    {
        this.step = step.next();
        listener.onBuildStep(step);
    }
}
