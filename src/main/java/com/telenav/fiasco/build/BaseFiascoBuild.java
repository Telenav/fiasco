package com.telenav.fiasco.build;

import com.telenav.fiasco.build.repository.Artifact;
import com.telenav.fiasco.build.repository.ArtifactRepository;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.build.tools.repository.Librarian;
import com.telenav.fiasco.internal.BuildListener;
import com.telenav.fiasco.internal.BuildResult;
import com.telenav.fiasco.internal.BuildStep;
import com.telenav.fiasco.internal.Buildable;
import com.telenav.fiasco.internal.BuildableProject;
import com.telenav.fiasco.internal.ProjectFoldersTrait;
import com.telenav.fiasco.internal.dependencies.BaseDependency;
import com.telenav.fiasco.internal.dependencies.Dependency;
import com.telenav.fiasco.internal.phase.Phase;
import com.telenav.fiasco.internal.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.internal.phase.installation.InstallationPhase;
import com.telenav.fiasco.internal.phase.packaging.PackagingPhase;
import com.telenav.fiasco.internal.phase.testing.TestingPhase;
import com.telenav.fiasco.internal.utility.FiascoCompiler;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.strings.AsciiArt;

import java.io.StringWriter;
import java.util.Objects;

import static com.telenav.fiasco.internal.BuildStep.FIASCO_STARTUP;
import static com.telenav.fiasco.internal.BuildStep.INITIALIZE;
import static com.telenav.fiasco.internal.BuildStep.PACKAGE_INITIALIZE;
import static com.telenav.fiasco.internal.BuildStep.PACKAGE_INSTALL;
import static com.telenav.fiasco.internal.BuildStep.TEST_INITIALIZE;
import static com.telenav.fiasco.internal.BuildStep.TEST_RUN_TESTS;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unsupported;

/**
 * Base class for Fiasco build definitions
 *
 * <p>
 * Builds proceed in a series of {@link BuildStep}s which are grouped into {@link Phase}s:
 * </p>
 *
 * <ol>
 *     <li>{@link CompilationPhaseMixin#compileSources()} - Builds sources into output files</li>
 *     <li>{@link TestingPhase#buildTestSources()} - Builds test sources</li>
 *     <li>{@link TestingPhase#runTests()} ()} - Runs tests</li>
 *     <li>{@link PackagingPhase#buildPackages()} - Packages output files</li>
 *     <li>{@link InstallationPhase#installPackages()} - Installs packages</li>
 * </ol>
 * <p>
 * <p>
 *
 * @author jonathanl (shibo)
 * @author jonathanl (shibo)
 * @see Buildable
 * @see BuildListener
 * @see BuildResult
 * @see BaseFiascoBuild
 *
 * <p>
 * When a phase completes a step, it calls {@link #nextStep(), which advances the build to the next step, and calls the
 * {@link BuildListener#onBuildStep(BuildStep)} method with the new build step.
 * </p>
 */
public abstract class BaseFiascoBuild extends BaseDependency implements
        Named,
        Buildable,
        BuildableProject,
        Initializable,
        FiascoBuild,
        ProjectFoldersTrait
{
    /** Metadata for this project */
    private BuildMetadata metadata;

    /** The project root folder */
    private Folder root;

    /** The current build step */
    private BuildStep step;

    /** The librarian to resolve artifacts for this project */
    private final Librarian librarian = new Librarian(this);

    /**
     * {@inheritDoc}
     */
    @Override
    public BuildResult call()
    {
        step(FIASCO_STARTUP);
        return executeBuild();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object instanceof BaseFiascoBuild)
        {
            BaseFiascoBuild that = (BaseFiascoBuild) object;
            return this.projectRootFolder().equals(that.projectRootFolder());
        }
        return false;
    }

    @Override
    public Dependency excluding(final Matcher<Dependency> matcher)
    {
        return unsupported();
    }

    /**
     * Builds this project
     */
    public BuildResult executeBuild()
    {
        step(INITIALIZE);
        initialize();

        var result = new BuildResult(getClass().getSimpleName());
        try
        {
            result.listenTo(this);
            result.start();

            // Compile code,
            ensure(isAt(INITIALIZE));
            compileSources();

            // build the tests,
            ensure(isAt(TEST_INITIALIZE));
            buildTestSources();

            // run the tests,
            ensure(isAt(TEST_RUN_TESTS));
            runTests();

            // package up the code,
            ensure(isAt(PACKAGE_INITIALIZE));
            buildPackages();

            // and install it.
            ensure(isAt(PACKAGE_INSTALL));
            installPackages();

            return result;
        }
        finally
        {
            result.end();
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(projectRootFolder());
    }

    public boolean isAt(final BuildStep at)
    {
        return step == at;
    }

    /**
     * Sets the metadata for this project
     */
    public void metadata(final BuildMetadata metadata)
    {
        this.metadata = metadata;
    }

    /**
     * @return This project's metadata
     */
    public BuildMetadata metadata()
    {
        return metadata;
    }

    /**
     * Updates the build step to the given step and calls the build listener with this information
     */
    public void nextStep()
    {
        step(step.next());
    }

    /**
     * Builds the classes in the <i>fiasco</i> folder under the given root, then loads classes ending in "Project". Each
     * class is instantiated and the resulting object tested to see if it implements the {@link FiascoBuild} interface.
     * If it does, the project object is added to the set of {@link #dependencies()}.
     *
     * @param projectRoot The project root folder
     */
    public void project(final Folder projectRoot)
    {
        // Get the fiasco sub-folder where the build files are,
        var fiasco = projectRoot.folder("src/main/java/fiasco");

        // create a compiler
        var output = new StringWriter();
        var compiler = JavaCompiler.compiler(this, output);

        // and if we can compile the source files,
        if (compiler.compile(fiasco))
        {
            // get the target folder
            var classes = compiler.targetFolder().folder("fiasco");

            // and try loading each class file ending in Project,
            var bootstrap = listenTo(new FiascoCompiler());
            for (var classFile : classes.files(file -> file.fileName().endsWith("Build.class")))
            {
                dependencies().addIfNotNull(bootstrap.instantiate(classFile, FiascoBuild.class));
            }

            ensure(dependencies().size() > 0, "Could not find any '*Project.java' files implementing FiascoProject in: $", fiasco);
        }
        else
        {
            fail("Unable to compile source files in $:\n\n$\n", fiasco, output);
        }
    }

    /**
     * @return The root folder of this project
     */
    public Folder projectRootFolder()
    {
        return ensureNotNull(root);
    }

    /**
     * @param root The project root folder
     */
    public FiascoBuild projectRootFolder(final Folder root)
    {
        this.root = root;
        return this;
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
     * True if the build is at the given step
     */
    public void step(BuildStep step)
    {
        narrate(AsciiArt.line(step.name()));
        this.step = step;
    }

    @Override
    public Validator validator(final ValidationType type)
    {
        return new BaseValidator()
        {
            @Override
            protected void onValidate()
            {
                problemIf(root == null, "No build root folder");
                problemIf(!root.exists(), "Root folder does not exist: $", root);
            }
        };
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
