package com.telenav.fiasco.internal.fiasco;

import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.core.language.reflection.Type;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
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
 * Used internally to compile and instantiate build sources
 * </p>
 *
 * <p>
 * The {@link #compile(Folder, File)} method compiles a <i>FiascoBuild.java</i> file using the compiler configured by
 * {@link #compiler(Folder, Writer)}. When the class file has been built, it can be loaded with {@link
 * #loadBuild(File)}. If the class file can be instantiated, and it is concrete and implements the {@link Build}
 * interface, the method returns the build, which can be executed by the caller with {@link Build#build}.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see JavaCompiler
 * @see Folder
 * @see File
 */
public class Compiler extends BaseComponent
{
    /**
     * <b>Not public API</b>
     *
     * <p>
     * Compiles the given source file and returns the class file
     * </p>
     *
     * @param target The target folder to put classes in
     * @param source The source file
     * @return The compiled class file
     */
    public File compile(Folder target, File source)
    {
        // If the source file exists,
        if (isTrueOr(source.exists(), "Build source file does not exist: $", source))
        {
            // get a compiler configured for Fiasco builds,
            var output = new StringWriter();
            var compiler = compiler(target, output);
            var sourceFolder = source.parent();
            var classFile = compiler.targetFolder()
                    .file(source.fileName()
                            .withoutExtension()
                            .withExtension(Extension.CLASS));

            // and if the source or target folder has changed (at all) since the last time,
            if (sourceFolder.hasChanged() || compiler.targetFolder().hasChanged())
            {
                // and the source folder compiles successfully,
                if (isNonNullOr(compiler.compile(sourceFolder), "Could not compile source file: $\n\n$", source, output))
                {
                    // then note the new change in the target folder,
                    compiler.targetFolder().hasChanged();

                    // and return the compiled class file.
                    return classFile;
                }
            }
            else
            {
                // otherwise, if the source and target are the same, just return the last class file.
                return classFile;
            }
        }

        return null;
    }

    /**
     * <b>Not public API</b>
     *
     * @return A configured JavaCompiler for building FiascoBuild.java build files
     */
    public JavaCompiler compiler(Folder target, Writer output)
    {
        var cache = require(FiascoCache.class);

        return JavaCompiler.create(this)
                .withOutput(output)
                .withSourceVersion(JAVA_16)
                .withTargetVersion(JAVA_16)
                .withTargetFolder(target)
                .withClasspathJar(cache.runtimeJar())
                .withImplicitCompilation();
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Loads the given class file, creates an instance of it and returns the object if it is a concreted class that
     * implements {@link Build}.
     * </p>
     *
     * @param classFile The class file to load and instantiate
     * @return The object, or null if the class could not be instantiated or is not a subclass of {@link Build}
     */
    public Build loadBuild(File classFile)
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
                    if (isTrueOr(Build.class.isAssignableFrom(loaded), "The class file $ does not contain a subclass of Build", classFile))
                    {
                        // then return a new instance of the class.
                        return (Build) Type.forClass(loaded).newInstance();
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
