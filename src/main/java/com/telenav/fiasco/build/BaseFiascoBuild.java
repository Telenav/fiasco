package com.telenav.fiasco.build;

import com.telenav.fiasco.build.phase.Phase;
import com.telenav.fiasco.build.phase.compilation.CompilationPhaseMixin;
import com.telenav.fiasco.build.phase.installation.InstallationPhase;
import com.telenav.fiasco.build.phase.packaging.PackagingPhase;
import com.telenav.fiasco.build.phase.testing.TestingPhase;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.build.tools.repository.Librarian;
import com.telenav.fiasco.dependencies.BaseDependency;
import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.internal.FiascoCompiler;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.kernel.interfaces.naming.Named;

import java.io.StringWriter;
import java.util.Objects;

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
 *     <li>{@link CompilationPhaseMixin#buildSources()} - Builds sources into output files</li>
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
        ProjectLocationsTrait
{
    /** Metadata for this project */
    private BuildMetadata metadata;

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
     * {@inheritDoc}
     */
    @Override
    public BuildResult call()
    {
        return executeBuild();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object instanceof BaseFiascoBuild)
        {
            BaseFiascoBuild that = (BaseFiascoBuild) object;
            return this.projectRoot().equals(that.projectRoot());
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
        initialize();
        var result = new BuildResult(getClass().getSimpleName());
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
    public int hashCode()
    {
        return Objects.hash(projectRoot());
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
        this.step = step.next();
        narrate("[$]", step.name());
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
        var compiler = listenTo(JavaCompiler.compiler(output));

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
    public Folder projectRoot()
    {
        return ensureNotNull(root);
    }

    /**
     * @param root The project root folder
     */
    public FiascoBuild projectRoot(final Folder root)
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
     * @return The workspace where this project resides
     */
    @Override
    public Folder workspace()
    {
        return Folder.parse("${WORKSPACE}");
    }
}
