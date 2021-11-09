package com.telenav.fiasco.internal.fiasco;

import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.kernel.language.reflection.Type;
import com.telenav.kivakit.resource.path.Extension;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;

import static com.telenav.fiasco.runtime.tools.compiler.JavaCompiler.JavaVersion.JAVA_16;
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
     * Compiles the given source file and returns the class file
     *
     * @param source The source file
     * @return The compiled class file
     */
    public File compile(File source)
    {
        if (isTrueOr(source.exists(), "Build source file does not exist: $", source))
        {
            var output = new StringWriter();
            var compiler = compiler(output);
            if (isNonNullOr(compiler.compile(source.parent()), "Could not compile source file: $\n\n$", source, output))
            {
                return compiler.targetFolder()
                        .file(source.fileName()
                                .withoutExtension()
                                .withExtension(Extension.CLASS));
            }
        }

        return null;
    }

    /**
     * @return A configured JavaCompiler for building FiascoBuild.java build files
     */
    public JavaCompiler compiler(Writer output)
    {
        var cache = require(FiascoCache.class);

        return JavaCompiler.create(this)
                .withOutput(output)
                .withSourceVersion(JAVA_16)
                .withTargetVersion(JAVA_16)
                .withTargetFolder(cache.targetFolder())
                .withClasspathJar(cache.runtimeJar())
                .withImplicitCompilation();
    }

    /**
     * Loads the given class file and creates an instance of it
     *
     * @param classFile The class file to load and instantiate
     * @param expectedType The expected type of the compiled class
     * @return The object, or null if the class could not be instantiated or is not assignable to the expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T instantiate(File classFile, Class<T> expectedType)
    {
        try
        {
            // Attempt to load the class file,
            var classFolder = classFile.parent();
            URLClassLoader classLoader = new URLClassLoader(new URL[] { classFolder.asUrl() });
            Class<?> loaded = classLoader.loadClass("fiasco." + classFile.baseName().name());

            // and if the class was loaded,
            if (isNonNullOr(loaded, "Could not load class: $", classFile))
            {
                // and it's not abstract,
                if (!isAbstract(loaded.getModifiers()))
                {
                    // and it is assignable to the expected type,
                    if (isTrueOr(expectedType.isAssignableFrom(loaded), "The class file $ does not contain a subclass of ${class}", classFile, expectedType))
                    {
                        // then return a new instance of the class.
                        return (T) Type.forClass(loaded).newInstance();
                    }
                }
                else
                {
                    // Silently skip abstract classes
                    return null;
                }
            }
        }
        catch (Exception e)
        {
            problem(e, "Could not load and instantiate class: $", classFile);
        }

        return null;
    }
}
