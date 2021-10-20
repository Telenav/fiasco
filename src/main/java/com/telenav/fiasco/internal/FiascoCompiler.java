package com.telenav.fiasco.internal;

import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.reflection.Type;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.path.Extension;

import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Class utilities used internally to build and instantiate user Java sources
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class FiascoCompiler extends BaseComponent
{
    /**
     * Compiles the given source file, instantiates the resulting class, and if the class is assignable to the expected
     * type, returns the build object
     *
     * @param source The source file
     * @param expectedType The expected type of the compiled class
     * @return The build object or null if the source file was not valid or was not assignable to the expected type
     */
    public <T> T compileAndInstantiate(Listener listener, final File source, Class<T> expectedType)
    {
        // compile the source file into a build class,
        var classFile = compile(source);
        if (classFile != null)
        {
            // and execute the build.
            return instantiate(listener, classFile, expectedType);
        }
        else
        {
            return fail("Build source file couldn't be compiled: $", source);
        }
    }

    /**
     * Loads the given class file and creates an instance of it
     *
     * @param listener The listener to call with any problems
     * @param classFile The class file to load and instantiate
     * @param expectedType The expected type of the compiled class
     */
    @SuppressWarnings({ "resource", "unchecked" })
    public <T> T instantiate(Listener listener, File classFile, Class<T> expectedType)
    {
        try
        {
            // Attempt to load the class file,
            final var classFolder = classFile.parent();
            final URLClassLoader classLoader = new URLClassLoader(new URL[] { classFolder.asUrl() });
            final Class<?> loaded = classLoader.loadClass(classFile.baseName().name());

            // and if the class was loaded,
            if (loaded != null)
            {
                // and it is assignable to the expected type,
                if (expectedType.isAssignableFrom(loaded))
                {
                    // then return a new instance of the class.
                    return (T) Type.forClass(loaded).newInstance();
                }
                else
                {
                    throw listener.problem("The class file $ does not contain a subclass of ${class}", classFile, expectedType).asException();
                }
            }
            else
            {
                throw listener.problem("Could not load class: $", classFile).asException();
            }
        }
        catch (final Exception e)
        {
            throw listener.problem(e, "Could not load and instantiate class: $", classFile).asException();
        }
    }

    /**
     * @return True if the give folder is a Fiasco project.
     */
    public boolean isFiascoProject(Folder project)
    {
        var fiasco = project.folder("fiasco");
        return fiasco.exists() && !fiasco.files(Pattern.compile(".*Build.java")).isEmpty();
    }

    /**
     * @param source The Java file to compile
     * @return The class file for the given source file
     */
    private File compile(final File source)
    {
        if (source.exists())
        {
            var output = new StringWriter();
            var compiler = JavaCompiler.compiler(output);
            announce("Compiling $ $", compiler, source);
            if (compiler.compile(source))
            {
                return compiler.targetFolder()
                        .file(source.fileName()
                                .withoutExtension()
                                .withExtension(Extension.CLASS));
            }
            else
            {
                fail("Could not compile Fiasco build source file: $\n\n$", source, output);
            }
        }
        else
        {
            fail("Build source file does not exist: $", source);
        }

        return fail();
    }
}
