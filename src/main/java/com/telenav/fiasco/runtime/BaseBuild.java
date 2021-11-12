package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.BuildListener;
import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.ProjectDependency;
import com.telenav.fiasco.internal.building.ProjectFoldersTrait;
import com.telenav.fiasco.internal.building.dependencies.BaseDependency;
import com.telenav.fiasco.internal.building.dependencies.ResolvedDependency;
import com.telenav.fiasco.internal.building.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.internal.building.phase.installation.InstallationPhase;
import com.telenav.fiasco.internal.building.phase.packaging.PackagingPhase;
import com.telenav.fiasco.internal.building.phase.testing.TestingPhase;
import com.telenav.fiasco.internal.fiasco.FiascoCompiler;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.interfaces.naming.Named;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.reflection.Type;
import com.telenav.kivakit.kernel.language.strings.AsciiArt;

import java.io.StringWriter;
import java.util.List;

import static com.telenav.fiasco.internal.building.BuildStep.FIASCO_STARTUP;
import static com.telenav.fiasco.internal.building.BuildStep.INITIALIZE;
import static com.telenav.fiasco.internal.building.BuildStep.PACKAGE_INITIALIZE;
import static com.telenav.fiasco.internal.building.BuildStep.PACKAGE_INSTALL;
import static com.telenav.fiasco.internal.building.BuildStep.TEST_INITIALIZE;
import static com.telenav.fiasco.internal.building.BuildStep.TEST_RUN_TESTS;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

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
 * @see BaseBuild
 *
 * <p>
 * When a phase completes a step, it calls {@link #nextStep(), which advances the build to the next step, and calls the
 * {@link BuildListener#onBuildStep(BuildStep)} method with the new build step.
 * </p>
 */
public class BaseBuild extends BaseDependency implements
        Named,
        Buildable,
        ProjectDependency,
        Initializable,
        Build,
        ProjectFoldersTrait
{
    /** Metadata for this project */
    private BuildMetadata metadata;

    /** The project root folder */
    private Folder root;

    /** The current build step */
    private BuildStep step;

    /** The artifact descriptor for this project */
    private ArtifactDescriptor descriptor;

    /**
     * Initializes the build:
     *
     * <ol>
     *     <li>Adds each repository returned by {@link #repositories()} to the Librarian</li>
     * </ol>
     */
    public BaseBuild()
    {
        repositories().forEach(librarian()::addRepository);
    }

    public BaseBuild artifactDescriptor(ArtifactDescriptor descriptor)
    {
        this.descriptor = descriptor;
        return this;
    }

    public BaseBuild artifactDescriptor(String descriptor)
    {
        artifactDescriptor(parseArtifactDescriptor(descriptor));
        return this;
    }

    /**
     * Builds this project
     */
    @Override
    public BuildResult build()
    {
        var result = new BuildResult(getClass().getSimpleName());
        try
        {
            result.listenTo(this);
            result.start();

            // Compile code,
            step(INITIALIZE);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public BuildResult call()
    {
        step(FIASCO_STARTUP);
        return build();
    }

    @Override
    public BaseBuild copy()
    {
        return Type.forClass(getClass()).newInstance();
    }

    @Override
    public ArtifactDescriptor descriptor()
    {
        return descriptor;
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * True if the build is at the given build step
     * </p>
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    public boolean isAt(BuildStep at)
    {
        return step == at;
    }

    @Override
    public Librarian librarian()
    {
        return require(Librarian.class);
    }

    /**
     * Sets the metadata for this project
     */
    public void metadata(BuildMetadata metadata)
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
     * <b>Not public API</b>
     *
     * <p>
     * Updates the build step to the given step and calls the build listener with this information
     * </p>
     */
    @Override
    public void nextStep()
    {
        step(step.next());
    }

    /**
     * Builds the classes in the <i>fiasco</i> folder under the given root, then loads classes ending in "Project". Each
     * class is instantiated and the resulting object tested to see if it implements the {@link Build} interface. If it
     * does, the project object is added to the set of {@link #dependencies()}.
     *
     * @param projectRoot The project root folder
     */
    @Override
    public void project(Folder projectRoot)
    {
        // Get the fiasco sub-folder where the build files are,
        var fiasco = projectRoot.folder("src/main/java/fiasco");

        // create a compiler
        var output = new StringWriter();
        var compiler = require(FiascoCompiler.class).compiler(targetFolder(), output);

        // and if we can compile the source files,
        if (compiler.compile(fiasco))
        {
            // get the target folder
            var classes = compiler.targetFolder().folder("fiasco");

            // and try loading each class file ending in Project,
            var bootstrap = listenTo(new FiascoCompiler());
            for (var classFile : classes.files(file -> file.fileName().endsWith("Project.class")))
            {
                dependencies().addIfNotNull(bootstrap.loadBuild(classFile));
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
    @Override
    public Folder projectRootFolder()
    {
        return ensureNotNull(root);
    }

    /**
     * @param root The project root folder
     */
    @Override
    public BaseBuild projectRootFolder(Folder root)
    {
        this.root = root;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    @Override
    public ResolvedDependency resolve(Dependency dependency)
    {
        return librarian().resolve(dependency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("ClassEscapesDefinedScope")
    public ObjectList<ResolvedDependency> resolveTransitiveDependencies(Dependency dependency)
    {
        return librarian().resolveTransitiveDependencies(dependency);
    }

    @Override
    public void resolveVersionTo(String version)
    {

    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Sets the build step to the given step
     * </p>
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    public void step(BuildStep step)
    {
        narrate(AsciiArt.line(step.name().replaceAll("_", " ")));
        this.step = step;
    }

    @Override
    public String toString()
    {
        return descriptor().toString();
    }

    @Override
    public Validator validator(ValidationType type)
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
        return Folder.parse(this, "${WORKSPACE}");
    }

    /**
     * @return The repositories to search for this build
     */
    protected List<MavenRepository> repositories()
    {
        return List.of(MavenRepository.mavenCentral(this));
    }
}
