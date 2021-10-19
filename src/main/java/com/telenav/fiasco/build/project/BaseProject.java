package com.telenav.fiasco.build.project;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.BuildListener;
import com.telenav.fiasco.build.BuildResult;
import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.installation.InstallationPhase;
import com.telenav.fiasco.build.phase.packaging.PackagingPhase;
import com.telenav.fiasco.build.phase.testing.TestingPhase;
import com.telenav.fiasco.build.project.metadata.ProjectMetadata;
import com.telenav.fiasco.build.tools.repository.Librarian;
import com.telenav.fiasco.dependencies.BaseProjectDependency;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.ArtifactRepository;
import com.telenav.kivakit.filesystem.Folder;

import java.util.Objects;

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
public abstract class BaseProject extends BaseProjectDependency implements Project, ProjectLocationsTrait
{
    /** The build that is building this project */
    private Build build;

    /** Metadata for this project */
    private ProjectMetadata metadata;

    /** The project root folder */
    private Folder root;

    /** The current build step */
    private BuildStep step = BuildStep.INITIALIZE;

    /** The librarian to resolve artifacts for this project */
    private final Librarian librarian = new Librarian();

    /**
     * True if the build is at the given step
     */
    public boolean atStep(BuildStep step)
    {
        return this.step == step;
    }

    /**
     * Sets the build for this project
     */
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
        var result = new BuildResult(metadata.name());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public BuildResult call()
    {
        return build();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object instanceof BaseProject)
        {
            BaseProject that = (BaseProject) object;
            return this.root().equals(that.root());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(root());
    }

    /**
     * Sets the metadata for this project
     */
    public void metadata(final ProjectMetadata metadata)
    {
        this.metadata = metadata;
    }

    /**
     * @return This project's metadata
     */
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

    /**
     * Resolves the given artifact into the local repository
     *
     * @return The repository where the artifact was found
     */
    @Override
    public ArtifactRepository resolve(final Artifact artifact)
    {
        return librarian.resolve(artifact);
    }

    /**
     * @return The root folder of this project
     */
    public Folder root()
    {
        return ensureNotNull(root);
    }

    /**
     * @param root The project root folder
     */
    public Project root(final Folder root)
    {
        this.root = root;
        return this;
    }

    /**
     * @return The workspace where this project resides
     */
    @Override
    public Folder workspace()
    {
        return Folder.parse("${WORKSPACE}");
    }
}
