package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.project.Project;
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
 * Base class for dependencies that can have {@link Project}s as dependencies. Not all dependencies can have dependent
 * projects. For example, {@link Artifact}s cannot have {@link Project} dependencies (because they are already built).
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
     * class is instantiated and the resulting object tested to see if it implements the {@link Project} interface. If
     * it does, the project object is added to the set of {@link #dependencies()}.
     *
     * @param projectRoot The project root folder
     */
    public void project(final Folder projectRoot)
    {
        // Get the fiasco sub-folder where the build files are,
        var fiasco = projectRoot.folder("fiasco");

        // create a compiler
        var output = new StringWriter();
        var compiler = JavaCompiler.compiler(output);

        // and if we can compile the source files,
        if (compiler.compile(fiasco))
        {
            // get the target folder
            var target = compiler.targetFolder();

            // and try loading each class file ending in Project,
            var count = 0;
            var bootstrap = listenTo(new FiascoCompiler());
            for (var classFile : target.files(file -> file.fileName().endsWith("Project.java")))
            {
                dependencies().add(bootstrap.instantiate(this, classFile, Project.class));
                count++;
            }

            ensure(count > 0, "Could not find any '*Project.java' files implementing Project in: $", fiasco);
        }
        else
        {
            fail("Unable to compile source files in $:\n\n$\n", fiasco, output);
        }
    }
}
