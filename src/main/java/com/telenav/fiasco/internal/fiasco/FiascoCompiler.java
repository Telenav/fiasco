package com.telenav.fiasco.internal.fiasco;

import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.reflection.Type;
import com.telenav.kivakit.resource.path.Extension;

import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Class utilities used internally to compile and instantiate build sources
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class FiascoCompiler extends BaseComponent
{
    /**
     * @return True if the source files in the given folder were successfully compiled
     */
    public boolean compile(Folder folder)
    {
        var output = new StringWriter();
        var compiler = JavaCompiler.compiler(this, output);
        if (!compiler.compile(folder))
        {
            problem("Could not compile folder: $\n\n$", folder, output);
            return false;
        }

        return true;
    }

    /**
     * Compiles the given source file, instantiates the resulting class, and if the class is assignable to the expected
     * type, returns the build object
     *
     * @param source The source file
     * @param expectedType The expected type of the compiled class
     * @return The build object or null if the source file was not valid or was not assignable to the expected type
     */
    public <T> T compileAndInstantiate(final File source, Class<T> expectedType)
    {
        // compile the source file into a build class,
        var classFile = compile(source);
        if (classFile != null)
        {
            // and execute the build.
            return instantiate(classFile, expectedType);
        }
        else
        {
            problem("Build source file couldn't be compiled: $", source);
            return null;
        }
    }

    /**
     * Loads the given class file and creates an instance of it
     *
     * @param classFile The class file to load and instantiate
     * @param expectedType The expected type of the compiled class
     * @return The object, or null if the class could not be instantiated or is not assignable to the expected type
     */
    @SuppressWarnings({ "resource", "unchecked" })
    public <T> T instantiate(File classFile, Class<T> expectedType)
    {
        try
        {
            // Attempt to load the class file,
            final var classFolder = classFile.parent();
            final URLClassLoader classLoader = new URLClassLoader(new URL[] { classFolder.asUrl() });
            final Class<?> loaded = classLoader.loadClass("fiasco." + classFile.baseName().name());

            // and if the class was loaded,
            if (loaded != null)
            {
                // and it's not abstract,
                if (!isAbstract(loaded.getModifiers()))
                {
                    // and it is assignable to the expected type,
                    if (expectedType.isAssignableFrom(loaded))
                    {
                        // then return a new instance of the class.
                        return (T) Type.forClass(loaded).newInstance();
                    }
                    else
                    {
                        problem("The class file $ does not contain a subclass of ${class}", classFile, expectedType);
                    }
                }
                else
                {
                    // Silently skip abstract classes
                    return null;
                }
            }
            else
            {
                problem("Could not load class: $", classFile);
            }
        }
        catch (final Exception e)
        {
            problem(e, "Could not load and instantiate class: $", classFile);
        }

        return null;
    }

    /**
     * @param source The Java file to compile
     * @return The class file for the given source file or null if the compilation process failed
     */
    private File compile(final File source)
    {
        if (source.exists())
        {
            var output = new StringWriter();
            var compiler = JavaCompiler.compiler(this, output);
            if (compiler.compile(source.parent()))
            {
                return compiler.targetFolder()
                        .file(source.fileName()
                                .withoutExtension()
                                .withExtension(Extension.CLASS));
            }
            else
            {
                problem("Could not compile Fiasco build source file: $\n\n$", source, output);
            }
        }
        else
        {
            problem("Build source file does not exist: $", source);
        }

        return null;
    }
}
