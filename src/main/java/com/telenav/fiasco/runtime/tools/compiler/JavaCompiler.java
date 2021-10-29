package com.telenav.fiasco.runtime.tools.compiler;

import com.telenav.fiasco.internal.fiasco.FiascoFolders;
import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.configuration.lookup.Registry;
import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.objects.Objects;
import com.telenav.kivakit.kernel.messaging.Listener;

import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;
import java.io.Writer;
import java.util.prefs.Preferences;

import static com.telenav.fiasco.runtime.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.resource.path.Extension.JAVA;

/**
 * Configures and runs the system Java compiler. To use Fiasco, the Java Virtual Machine must be at least Java 11.
 *
 * @author jonathanl (shibo)
 */
public class JavaCompiler extends BaseComponent
{
    /**
     * A compiler with a default configuration for building fiasco files
     *
     * @return The target files
     */
    public static JavaCompiler compiler(Listener listener, Writer output)
    {
        var resources = Registry.global().require(FiascoFolders.class);

        return JavaCompiler.create(listener)
                .withOutput(output)
                .withSourceVersion(JAVA_11)
                .withTargetVersion(JAVA_11)
                .withTargetFolder(resources.targetFolder())
                .withOption("-classpath")
                .withOption(resources.fiascoRuntimeJar().toString())
                .withOption("-implicit:class");
    }

    public static JavaCompiler create(Listener listener)
    {
        return new JavaCompiler(listener);
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

        JavaVersion(String version)
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
    private Folder target;

    /** Compiler options */
    private final StringList options = StringList.stringList();

    protected JavaCompiler(Listener listener)
    {
        addListener(ensureNotNull(listener));
    }

    protected JavaCompiler(JavaCompiler that)
    {
        sourceVersion = that.sourceVersion;
        targetVersion = that.targetVersion;
        output = that.output;
        target = that.target;
        options.addAll(that.options);
        copyListeners(that);
    }

    /**
     * Builds all the Java source files in the given folder, writing classes to the {@link #targetFolder()}
     *
     * @return True if the build succeeded, false if it failed
     */
    public boolean compile(Build build)
    {
        return compile(build.sourceFolder());
    }

    /**
     * Builds all the Java source files in the given folder, writing classes to the {@link #targetFolder()}
     *
     * @return True if the build succeeded, false if it failed
     */
    public boolean compile(Folder source)
    {
        ensureNotNull(sourceVersion, "No source version specified");
        ensureNotNull(targetVersion, "No target version specified");
        ensureNotNull(target, "No target folder specified");

        var node = Preferences.userNodeForPackage(getClass()).node("fiasco");

        var lastSourceDigest = node.getByteArray("sourceDigest", null);
        var lastTargetDigest = node.getByteArray("targetDigest", null);

        var sourceDigest = source.nestedFiles().digest();
        var targetDigest = target.nestedFiles().digest();

        // If the source and target folders are identical to the last compile,
        if (Objects.equalPairs(lastSourceDigest, sourceDigest, lastTargetDigest, targetDigest))
        {
            // then we can skip compiling
            trace("Source and target folders are unchanged, skipping compilation");
            return true;
        }
        else
        {
            // otherwise, build the source files.
            announce("Compiling $ $", this, source);
            var files = source.nestedFiles(JAVA.fileMatcher());
            if (task(files, output, options).call())
            {
                // and store the digests for next time
                node.putByteArray("sourceDigest", sourceDigest);
                node.putByteArray("targetDigest", targetDigest);

                tryCatch(node::flush, "Failed to flush preferences");
                return true;
            }
            else
            {
                problem("Compilation failed:\n\n$\n", output);
                return false;
            }
        }
    }

    public Writer out()
    {
        return output;
    }

    public Folder targetFolder()
    {
        return target;
    }

    @Override
    public String toString()
    {
        return "javac " + options.join(" ");
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
    public JavaCompiler withOutput(Writer output)
    {
        var copy = copy();
        copy.output = output;
        return copy;
    }

    /**
     * @return This compiler with the given source code version
     */
    public JavaCompiler withSourceVersion(JavaVersion version)
    {
        var copy = copy();
        copy.sourceVersion = version;
        copy.options.add("--source");
        copy.options.add(version.version());
        return copy;
    }

    /**
     * @return This compiler with the given target output folder
     */
    public JavaCompiler withTargetFolder(Folder folder)
    {
        var copy = copy();
        copy.target = folder;
        copy.options.add("-d");
        copy.options.add(folder.toString());
        return copy;
    }

    /**
     * @return This compiler with the given target bytecode version
     */
    public JavaCompiler withTargetVersion(JavaVersion version)
    {
        var copy = copy();
        copy.targetVersion = version;
        copy.options.add("--target");
        copy.options.add(version.version());
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
     * @param sourceFiles The Java files to compile
     * @param out The writer to write output to
     * @param options The options to use
     * @return A compilation task for the given source file and compiler options
     */
    private CompilationTask task(FileList sourceFiles, Writer out, StringList options)
    {
        var compiler = ToolProvider.getSystemJavaCompiler();
        var fileManager = compiler.getStandardFileManager(null, null, null);
        var sources = fileManager.getJavaFileObjectsFromFiles(sourceFiles.asJavaFiles());

        return compiler.getTask(out, fileManager, null, options, null, sources);
    }
}
