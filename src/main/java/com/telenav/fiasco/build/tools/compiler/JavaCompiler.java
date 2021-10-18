package com.telenav.fiasco.build.tools.compiler;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.values.version.Version;

import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;
import java.io.Writer;
import java.util.List;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;

/**
 * Configures and runs the system Java compiler. To use Fiasco, the Java Virtual Machine must be at least version 11.
 *
 * @author jonathanl (shibo)
 */
public class JavaCompiler extends BaseCompiler
{
    public static JavaCompiler create()
    {
        return new JavaCompiler();
    }

    private Version sourceVersion;

    private Version targetVersion;

    private Writer out;

    private Folder targetFolder;

    private final StringList options = StringList.stringList();

    public JavaCompiler(JavaCompiler that)
    {
        this.sourceVersion = that.sourceVersion;
        this.targetVersion = that.targetVersion;
        this.out = that.out;
        this.targetFolder = that.targetFolder;
        this.options.addAll(that.options);
    }

    public JavaCompiler()
    {
    }

    /**
     * Compiles the given source file
     *
     * @param file The file to compile
     * @return True if there were no errors compiling the source file
     */
    public boolean compile(File file)
    {
        ensureNotNull(sourceVersion, "No source version specified");
        ensureNotNull(targetVersion, "No target version specified");
        ensureNotNull(targetFolder, "No target output folder specified");

        return task(file, out, options).call();
    }

    public JavaCompiler withDeprecationWarnings()
    {
        var copy = copy();
        copy.withOption("-deprecation");
        return copy;
    }

    public JavaCompiler withOption(String option)
    {
        var copy = copy();
        copy.options.add(option);
        return copy;
    }

    public JavaCompiler withOutput(Writer out)
    {
        var copy = copy();
        copy.out = out;
        return copy;
    }

    public JavaCompiler withSourceVersion(final String version)
    {
        return withSourceVersion(Version.parse(version));
    }

    public JavaCompiler withSourceVersion(final Version version)
    {
        var copy = copy();
        sourceVersion = version;
        copy.option("--source " + version);
        return copy;
    }

    public JavaCompiler withTargetFolder(Folder folder)
    {
        var copy = copy();
        this.targetFolder = folder;
        copy.option("-d " + folder);
        return copy;
    }
 
    public JavaCompiler withTargetVersion(final String version)
    {
        return withSourceVersion(Version.parse(version));
    }

    public JavaCompiler withTargetVersion(final Version version)
    {
        var copy = copy();
        targetVersion = version;
        copy.option("--target " + version);
        return copy;
    }

    private JavaCompiler copy()
    {
        return new JavaCompiler(this);
    }

    private void option(String option)
    {
        options.add(option);
    }

    /**
     * @param sourceFile The Java file to compile
     * @param out The writer to write output to
     * @param options The options to use
     * @return The class file for the given source file
     */
    private CompilationTask task(final File sourceFile, Writer out, StringList options)
    {
        // If the source file exists,
        if (sourceFile.exists())
        {
            final var compiler = ToolProvider.getSystemJavaCompiler();
            final var fileManager = compiler.getStandardFileManager(null, null, null);
            final var sources = fileManager.getJavaFileObjectsFromFiles(List.of(sourceFile.asJavaFile()));

            return compiler.getTask(out, fileManager, null, options, null, sources);
        }
        else
        {
            throw problem("Build source file does not exist: $", sourceFile).asException();
        }
    }
}
