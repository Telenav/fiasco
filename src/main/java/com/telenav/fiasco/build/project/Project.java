package com.telenav.fiasco.build.project;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildResult;
import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.installation.InstallationPhase;
import com.telenav.fiasco.build.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.build.phase.packaging.PackagingPhase;
import com.telenav.fiasco.build.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.build.phase.testing.TestingPhase;
import com.telenav.fiasco.build.phase.testing.TestingPhaseMixin;
import com.telenav.fiasco.build.project.metadata.ProjectMetadata;
import com.telenav.fiasco.dependencies.BaseDependency;
import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.DependencyList;
import com.telenav.fiasco.dependencies.repository.maven.MavenArtifact;
import com.telenav.fiasco.dependencies.repository.maven.MavenPopularArtifacts;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.language.vm.JavaVirtualMachine;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;

/**
 * <p>
 * Builds proceed in a series of {@link BuildStep}s which are grouped into {@link Phase}s:
 * </p>
 *
 * <ol>
 *     <li>{@link CompilationPhaseMixin#buildSources()} - Builds sources into output files</li>
 *     <li>{@link TestingPhase#buildTestSources()} - Builds test sources</li>
 *     <li>{@link TestingPhase#runTests()} ()} - Runs tests</li>
 *     <li>{@link PackagingPhase#buildPackages()} - Packages output files</li>
 *     <li>{@link InstallationPhase#installPackages()} - Installs packages</li>
 * </ol>
 *
 * <p>
 * When a phase completes a step, it calls {@link #nextStep(), which advances the build to the next step, and calls
 * the {@link BuildListener#onBuildStep(BuildStep)} method with the new build step.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public abstract class Project extends BaseDependency implements
        Buildable,
        MavenPopularArtifacts,
        Initializable,
        CompilationPhaseMixin,
        TestingPhaseMixin,
        PackagingPhaseMixin,
        InstallationPhaseMixin

{
    /** The build that is building this project */
    private Build build;

    /** Metadata for this project */
    private final ProjectMetadata metadata = new ProjectMetadata();

    /** The project root folder */
    private Folder root;

    /** The current build step */
    private BuildStep step = BuildStep.INITIALIZE;

    /**
     * True if the build is at the given step
     */
    public boolean atStep(BuildStep step)
    {
        return this.step == step;
    }

    public void build(final Build build)
    {
        this.build = build;
    }

    /**
     * Builds this project
     */
    @Override
    public BuildResult build()
    {
        var result = new BuildResult(project().name());
        try
        {
            result.listenTo(this);
            result.start();

            // Compile code,
            buildSources();

            // build the tests,
            buildTestSources();

            // run the tests,
            runTests();

            // package up the code,
            buildPackages();

            // and install it.
            installPackages();

            return result;
        }
        finally
        {
            result.end();
        }
    }

    @Override
    public BuildResult call()
    {
        return build();
    }

    @Override
    public DependencyList dependencies()
    {
        return null;
    }

    public Folder folderForProperty(String environmentVariable)
    {
        return Folder.parse(JavaVirtualMachine.property(environmentVariable));
    }

    public Project in(String child)
    {
        return in(workspace().folder(child));
    }

    public Project in(final Folder root)
    {
        this.root = root;
        return this;
    }

    public Folder javaSources()
    {
        return mainSources().folder("java");
    }

    public Folder mainSources()
    {
        return sources().folder("main");
    }

    public ProjectMetadata metadata()
    {
        return metadata;
    }

    /**
     * Updates the build step to the given step and calls the build listener with this information
     */
    public void nextStep()
    {
        this.step = step.next();
        narrate("[$]", step.name());
    }

    public Folder output()
    {
        return root().folder("target");
    }

    @Override
    public Project project()
    {
        return CompilationPhaseMixin.super.project();
    }

    public Folder root()
    {
        return ensureNotNull(root);
    }

    public Folder sources()
    {
        return root().folder("source");
    }

    public Folder testSources()
    {
        return sources().folder("test");
    }

    public Folder workspace()
    {
        return Folder.parse("${WORKSPACE}");
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
