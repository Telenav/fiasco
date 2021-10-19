package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.project.Project;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.library.classes.Classes;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;

import java.io.StringWriter;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unsupported;
import static com.telenav.kivakit.resource.path.Extension.CLASS;

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
     * Builds the classes in the <i>fiasco</i> folder under the given root, then loads classes until one is loaded that
     * implements the {@link Project} interface. This class is instantiated and the resulting object is added to the set
     * of {@link #dependencies()}.
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

            // and try loading each class file
            var count = 0;
            for (var classFile : target.files(CLASS.fileMatcher()))
            {
                var project = Classes.instantiate(this, classFile);
                if (project instanceof Project)
                {
                    dependencies().add((Project) project);
                    count++;
                }
            }

            ensure(count > 0, "Could not find any .java files implementing Project in: $", fiasco);
        }
        else
        {
            fail("Unable to compile source files in $:\n\n$\n", fiasco, output);
        }
    }
}
