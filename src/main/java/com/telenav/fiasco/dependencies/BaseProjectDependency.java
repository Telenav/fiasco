package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.internal.FiascoCompiler;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

import java.io.StringWriter;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unsupported;

/**
 * Base class for dependencies that can have {@link FiascoBuild}s as dependencies. Not all dependencies can have
 * dependent projects. For example, {@link Artifact}s cannot have {@link FiascoBuild} dependencies (because they are
 * already built).
 *
 * @author jonathanl (shibo)
 */
public abstract class BaseProjectDependency extends BaseDependency implements ProjectDependency
{
    @Override
    public Dependency excluding(final Matcher<Dependency> matcher)
    {
        return unsupported();
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
}
