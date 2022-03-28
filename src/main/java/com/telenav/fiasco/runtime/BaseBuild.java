package com.telenav.fiasco.runtime;

import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.ProjectDependency;
import com.telenav.fiasco.internal.building.ProjectTrait;
import com.telenav.fiasco.internal.building.dependencies.BaseDependency;
import com.telenav.fiasco.internal.building.dependencies.ResolvedDependency;
import com.telenav.fiasco.internal.building.phase.building.BuildingPhaseMixin;
import com.telenav.fiasco.internal.building.phase.installation.InstallationPhaseMixin;
import com.telenav.fiasco.internal.building.phase.packaging.PackagingPhaseMixin;
import com.telenav.fiasco.internal.building.phase.testing.TestingPhaseMixin;
import com.telenav.fiasco.internal.fiasco.FiascoCompiler;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.fiasco.spi.BuildListener;
import com.telenav.fiasco.spi.BuildResult;
import com.telenav.fiasco.spi.Buildable;
import com.telenav.kivakit.core.data.validation.BaseValidator;
import com.telenav.kivakit.core.data.validation.ValidationType;
import com.telenav.kivakit.core.data.validation.Validator;
import com.telenav.kivakit.core.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.core.interfaces.naming.Named;
import com.telenav.kivakit.core.language.collections.list.ObjectList;
import com.telenav.kivakit.core.language.reflection.Type;
import com.telenav.kivakit.core.language.strings.AsciiArt;
import com.telenav.kivakit.filesystem.Folder;

import java.io.StringWriter;
import java.util.List;

import static com.telenav.fiasco.internal.building.BuildStep.BUILDING_INITIALIZE;
import static com.telenav.fiasco.internal.building.BuildStep.FIASCO_STARTUP;
import static com.telenav.fiasco.internal.building.BuildStep.INSTALLATION_INSTALL;
import static com.telenav.fiasco.internal.building.BuildStep.PACKAGING_INITIALIZE;
import static com.telenav.fiasco.internal.building.BuildStep.TESTING_INITIALIZE;
import static com.telenav.kivakit.core.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.core.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.core.data.validation.ensure.Ensure.fail;

/**
 * Base class for Fiasco build definitions
 *
 * <p><b>Build Phases</b></p>
 *
 * <p>
 * Builds proceed in a series of {@link BuildStep}s which are grouped into {@link Phase}s:
 * </p>
 *
 * <ol>
 *     <li>{@link BuildingPhaseMixin#buildingPhase()} - Builds sources into output files with these steps:</li>
 *     <ol type="a">
 *         <li>{@link BuildingPhaseMixin#initialize()} - prepare to build</li>
 *         <li>{@link BuildingPhaseMixin#resolveDependencies} - resolve artifacts in remote repositories</li>
 *         <li>{@link BuildingPhaseMixin#generateSources} - generate source code, or other artifacts</li>
 *         <li>{@link BuildingPhaseMixin#preprocess} - transform source code</li>
 *         <li>{@link BuildingPhaseMixin#compile} - build sources into target folder</li>
 *         <li>{@link BuildingPhaseMixin#postprocess} - perform post-processing on output</li>
 *         <li>{@link BuildingPhaseMixin#buildDocumentation} - build documentation, such as Javadoc</li>
 *         <li>{@link BuildingPhaseMixin#verify} - check for a valid build</li>
 *     </ol>
 *     <li>{@link TestingPhaseMixin#testingPhase()} ()} - Builds test sources and executes them with these steps: </li>
 *     <ol type="a">
 *         <li>{@link TestingPhaseMixin#testInitialize()} - prepare to build</li>
 *         <li>{@link TestingPhaseMixin#testResolveDependencies()} - resolve artifacts in remote repositories</li>
 *         <li>{@link TestingPhaseMixin#testGenerateSources()} - generate source code, or other artifacts</li>
 *         <li>{@link TestingPhaseMixin#testPreprocess()} - transform source code</li>
 *         <li>{@link TestingPhaseMixin#testCompileSources()} - build sources into target folder</li>
 *         <li>{@link TestingPhaseMixin#testPostprocess()} - perform post-processing on output</li>
 *         <li>{@link TestingPhaseMixin#testVerify()} - check for a valid build</li>
 *         <li>{@link TestingPhaseMixin#testRunTests()} - executes tests</li>
 *     </ol>
 *     <li>{@link PackagingPhaseMixin#packagingPhase()} - Packages output files</li>
 *     <ol type="a">
 *         <li>{@link PackagingPhaseMixin#packageInitialize()}  - prepare to package artifacts</li>
 *         <li>{@link PackagingPhaseMixin#packagePreprocess()} - perform pre-processing on artifacts</li>
 *         <li>{@link PackagingPhaseMixin#packagingBuild()} - builds artifacts into packaged form</li>
 *         <li>{@link PackagingPhaseMixin#packagePostprocess()} - perform post-processing of target artifacts</li>
 *         <li>{@link PackagingPhaseMixin#packageVerify()} - check consistency of target artifacts</li>
 *     </ol>
 *     <li>{@link InstallationPhaseMixin#installationPhase()} ()} - Installs and deploys target artifacts</li>
 *     <ol type="a">
 *         <li>{@link InstallationPhaseMixin#installationInstall()} - installs target artifacts in local repository</li>
 *         <li>{@link InstallationPhaseMixin#installationDeploy()} - copies target artifacts to servers</li>
 *     </ol>
 * </ol>
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
        ProjectTrait
{
    /** The artifact descriptor for this project */
    private ArtifactDescriptor descriptor;

    /** Metadata for this project */
    private BuildMetadata metadata;

    /** The project root folder */
    private Folder root;

    /** The current build step */
    private BuildStep step;

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

            // Build source code into artifacts,
            step(BUILDING_INITIALIZE);
            buildingPhase();

            // build the test source code and execute it,
            ensure(isAt(TESTING_INITIALIZE));
            testingPhase();

            // package up the built artifacts,
            ensure(isAt(PACKAGING_INITIALIZE));
            packagingPhase();

            // and install them.
            ensure(isAt(INSTALLATION_INSTALL));
            installationPhase();

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
