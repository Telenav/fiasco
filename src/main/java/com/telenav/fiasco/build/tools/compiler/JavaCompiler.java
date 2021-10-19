package com.telenav.fiasco.build.tools.compiler;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.StringList;

import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;
import java.io.Writer;
import java.util.List;

import static com.telenav.fiasco.build.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;
import static com.telenav.kivakit.filesystem.Folder.Type.CLEAN_UP_ON_EXIT;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.resource.path.Extension.JAVA;

/**
 * Configures and runs the system Java compiler. To use Fiasco, the Java Virtual Machine must be at least Java 11.
 *
 * @author jonathanl (shibo)
 */
public class JavaCompiler extends BaseCompiler
{
    /**
     * A compiler with a default configuration for building fiasco files
     *
     * @return The target files
     */
    public static JavaCompiler compiler(Writer output)
    {
        return JavaCompiler.create()
                .withOutput(output)
                .withSourceVersion(JAVA_11)
                .withTargetVersion(JAVA_11)
                .withTargetFolder(Folder.temporaryForProcess(CLEAN_UP_ON_EXIT));
    }

    public static JavaCompiler create()
    {
        return new JavaCompiler();
    }

    public enum JavaVersion
    {
        JAVA_7("7"),
        JAVA_8("8"),
        JAVA_9("9"),
        JAVA_10("10"),
        JAVA_11("11"),
        JAVA_12("12"),
        JAVA_13("13"),
        JAVA_14("14"),
        JAVA_15("15"),
        JAVA_16("16"),
        JAVA_17("17");

        private final String version;

        JavaVersion(final String version)
        {
            this.version = version;
        }

        public String version()
        {
            return version;
        }
    }

    /** The Java source code version */
    private JavaVersion sourceVersion;

    /** The target bytecode version */
    private JavaVersion targetVersion;

    /** Compiler output */
    private Writer output;

    /** Folder for class files and other generated resources */
    private Folder targetFolder;

    /** Compiler options */
    private final StringList options = StringList.stringList();

    protected JavaCompiler()
    {
    }

    protected JavaCompiler(JavaCompiler that)
    {
        this.sourceVersion = that.sourceVersion;
        this.targetVersion = that.targetVersion;
        this.output = that.output;
        this.targetFolder = that.targetFolder;
        this.options.addAll(that.options);
    }

    /**
     * Builds all the Java source files in the given folder
     *
     * @return True if the build succeeded, false if it failed
     */
    public boolean compile(Folder folder)
    {
        // then for each source file,
        for (var sourceFile : folder.files(JAVA.fileMatcher()))
        {
            // build it.
            if (!compile(sourceFile))
            {
                problem("Unable to compile: $\n\n$\n", sourceFile, output);
                return false;
            }
        }

        return true;
    }

    /**
     * Compiles the given source file
     *
     * @param file The .java source file to compile
     * @return True if there were no errors compiling the source file
     */
    public boolean compile(File file)
    {
        ensure(file.hasExtension(JAVA));

        ensureNotNull(sourceVersion, "No source version specified");
        ensureNotNull(targetVersion, "No target version specified");
        ensureNotNull(targetFolder, "No target output folder specified");

        return task(file, output, options).call();
    }

    public Writer out()
    {
        return output;
    }

    public Folder targetFolder()
    {
        return targetFolder;
    }

    /**
     * @return This compiler object with deprecation warnings turned on
     */
    public JavaCompiler withDeprecationWarnings()
    {
        var copy = copy();
        copy.withOption("-deprecation");
        return copy;
    }

    /**
     * @return This compiler with the given option added
     */
    public JavaCompiler withOption(String option)
    {
        var copy = copy();
        copy.options.add(option);
        return copy;
    }

    /**
     * @return This compiler with the given output writer
     */
    public JavaCompiler withOutput(Writer out)
    {
        var copy = copy();
        copy.output = out;
        return copy;
    }

    /**
     * @return This compiler with the given source code version
     */
    public JavaCompiler withSourceVersion(final JavaVersion version)
    {
        var copy = copy();
        sourceVersion = version;
        copy.options.add("--source " + version);
        return copy;
    }

    /**
     * @return This compiler with the given target output folder
     */
    public JavaCompiler withTargetFolder(Folder folder)
    {
        var copy = copy();
        this.targetFolder = folder;
        copy.options.add("-d " + folder);
        return copy;
    }

    /**
     * @return This compiler with the given target bytecode version
     */
    public JavaCompiler withTargetVersion(final JavaVersion version)
    {
        var copy = copy();
        targetVersion = version;
        copy.options.add("--target " + version);
        return copy;
    }

    /**
     * @return A copy of this compiler
     */
    private JavaCompiler copy()
    {
        return new JavaCompiler(this);
    }

    /**
     * @param sourceFile The Java file to compile
     * @param out The writer to write output to
     * @param options The options to use
     * @return A compilation task for the given source file and compiler options
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
