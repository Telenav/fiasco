package com.telenav.fiasco.runtime.tools.compiler;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.core.language.collections.list.ObjectList;
import com.telenav.kivakit.core.language.collections.list.StringList;
import com.telenav.kivakit.core.messaging.Listener;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;
import java.io.Writer;

import static com.telenav.kivakit.core.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.core.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.resource.path.Extension.JAR;
import static com.telenav.kivakit.resource.path.Extension.JAVA;

/**
 * Configures and runs the system Java compiler. To use Fiasco, the Java Virtual Machine must be at least Java 11.
 *
 * @author jonathanl (shibo)
 */
public class JavaCompiler extends BaseComponent
{
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

    /**
     * -Xlint warnings used with {@link #withWarning(Warning)} and {@link #withoutWarning(Warning)}
     */
    @SuppressWarnings("SpellCheckingInspection")
    public enum Warning
    {
        ABNORMAL_FINALLY_CLAUSE_TERMINATION("finally"),
        ACCESS_OF_STATIC_MEMBER_WITH_REFERENCE("static"),
        ALL_WARNINGS("all"),
        ANNOTATION_PROCESSING_ISSUES("processing"),
        CASE_FALLTHROUGH_ISSUES("fallthrough"),
        CLASS_FILE_ISSUES("classfile"),
        COMMAND_LINE_ISSUES("options"),
        DIVIDE_BY_ZERO("divzero"),
        EMPTY_IF_BLOCK("empty"),
        INVALID_PATH_ELEMENTS("path"),
        METHOD_OVERLOAD_ISSUES("overloads"),
        METHOD_OVERRIDE_ISSUES("overrides"),
        MISSING_EXPLICIT_CONSTRUCTORS("missing-explicit-ctor"),
        MISSING_SERIALIZATION_VERSION_IDENTIFIERS("serial"),
        MISSING_TYPE_PARAMETERS("rawtypes"),
        MODULE_EXPORT_ISSUES("exports"),
        MODULE_ISSUES("module"),
        MODULE_OPENS_ISSUES("opens"),
        NO_WARNINGS("none"),
        REQUIRED_AUTOMATIC_MODULES("requires-automatic"),
        REQUIRED_TRANSITIVE_AUTOMATIC_MODULES("requires-transitive-automatic"),
        RISKY_SYNCHRONIZATION_ISSUES("synchronization"),
        TEXT_BLOCK_INDENTATION_ISSUES("text-blocks"),
        TRY_BLOCK_ISSUES("try"),
        UNCHECKED_OPERATIONS("unchecked"),
        UNNECESSARY_CASTS("cast"),
        UNSAFE_VARIABLE_ARGUMENT_METHODS("varargs"),
        USE_OF_API_MARKED_FOR_REMOVAL("removal"),
        USE_OF_DEPRECATED_CODE("deprecation"),
        USE_OF_PREVIEW_LANGUAGE_FEATURES("preview");

        private String value;

        Warning(String value)
        {
            this.value = value;
        }

        public String option()
        {
            return "-Xlint:" + value;
        }

        public String removeOption()
        {
            return "-Xlint:-" + value;
        }
    }

    /** The Java source code version */
    private JavaVersion sourceVersion;

    /** The target bytecode version */
    private JavaVersion targetVersion;

    /** Compiler output */
    private Writer output;

    /** True to include all debugging information */
    private boolean debugInformation = true;

    /** Folder for class files and other generated resources */
    private Folder target;

    /** Compiler options */
    private StringList options = new StringList();

    /** JAR files on the class path */
    private StringList classPath = new StringList();

    /** Folders containing modules to include on the module path with --module-path */
    private ObjectList<Folder> modulePath = new ObjectList<>();

    /** Folders to include on the source path with --source-path */
    private ObjectList<Folder> sourcePath = new ObjectList<>();

    /** The list of modules to add with --add-modules */
    private StringList modules = new StringList();

    /** Virtual machine properties */
    private final PropertyMap properties = PropertyMap.create();

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
        options = that.options.copy();
        modulePath = that.modulePath.copy();
        sourcePath = that.sourcePath.copy();
        modules = that.modules.copy();
        classPath = that.classPath.copy();
        debugInformation = that.debugInformation;
        copyListeners(that);
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

        // otherwise, we do a full compile of the build source files.
        var files = source.nestedFiles(JAVA.fileMatcher());
        if (isDebugOn())
        {
            trace("Compiling: " + this + files.join("\n  "));
        }
        else
        {
            announce("Compiling $", source);
        }
        return isNonNullOr(task(files, output, options()).call(), "Compilation failed:\n\n$\n", output);
    }

    public StringList options()
    {
        return resolved().options;
    }

    public Folder targetFolder()
    {
        return target;
    }

    @Override
    public String toString()
    {
        return "javac\n  " + join(options());
    }

    /**
     * Adds the given jar file to the class path for this compiler
     *
     * @param folder The folder to add to the classpath
     */
    public JavaCompiler withClasspathFolder(Folder folder)
    {
        var copy = copy();
        copy.classPath.add(folder.path().toString());
        return copy;
    }

    /**
     * Adds the given jar file to the class path for this compiler
     *
     * @param jar the JAR file to add to the classpath
     */
    public JavaCompiler withClasspathJar(File jar)
    {
        ensure(jar.hasExtension(JAR));

        var copy = copy();
        copy.classPath.add(jar.path().toString());
        return copy;
    }

    /**
     * @return This compiler with debug information turned on or off
     */
    public JavaCompiler withDebugInformation(boolean enable)
    {
        var copy = copy();
        copy.debugInformation = enable;
        return copy;
    }

    /**
     * @return This compiler object with deprecation warnings turned on
     */
    public JavaCompiler withDeprecationWarnings()
    {
        return withOption("-deprecation");
    }

    /**
     * @return This compiler with warnings causing termination
     */
    public JavaCompiler withFailOnWarnings()
    {
        return withOption("-Werror");
    }

    /**
     * @return This compiler with implicit compilation enabled. Referenced classes are compiled transitively.
     */
    public JavaCompiler withImplicitCompilation()
    {
        return withOption("-implicit:class");
    }

    /**
     * @return This compiler with the given virtual machine option
     */
    public JavaCompiler withJavaOption(String option)
    {
        return withOption("-J" + option);
    }

    /**
     * Adds the module to the modules to resolve (with the --add-modules switch)
     */
    public JavaCompiler withModule(String moduleName)
    {
        var copy = copy();
        copy.modules.add(moduleName);
        return copy;
    }

    /**
     * Adds the named module to the module path (the --module-path switch) for this compiler
     *
     * @param module The name of the module
     */
    public JavaCompiler withModulePathFolder(Folder module)
    {
        var copy = copy();
        copy.modulePath.add(module);
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
     * @return This compiler with preview features enabled
     */
    public JavaCompiler withPreviewFeatures()
    {
        return withOption("--enable-preview");
    }

    /**
     * Adds the given property with -Dkey=value
     *
     * @param key The property key
     * @param value The property value
     */
    public JavaCompiler withProperty(String key, String value)
    {
        var copy = copy();
        copy.properties.put(key, value);
        return copy;
    }

    /**
     * @return This compiler with the given source encoding, typically "UTF-8"
     */
    public JavaCompiler withSourceEncoding(String encoding)
    {
        return withOption("-encoding").withOption(encoding);
    }

    /**
     * @return This compiler with the given source folder
     */
    public JavaCompiler withSourceFolder(Folder folder)
    {
        var copy = copy();
        copy.sourcePath.add(folder);
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
        copy.options.add("\"" + folder.toString() + "\"");
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
     * @return This compiler with verbose output
     */
    public JavaCompiler withVerboseOutput()
    {
        return withOption("-verbose");
    }

    /**
     * @return This compiler with the given lint warning
     */
    public JavaCompiler withWarning(Warning warning)
    {
        return withOption(warning.option());
    }

    /**
     * @return This compiler without the given lint warning
     */
    public JavaCompiler withoutWarning(Warning warning)
    {
        return withOption(warning.removeOption());
    }

    /**
     * @return This compiler without warnings
     */
    public JavaCompiler withoutWarnings()
    {
        return withOption("-nowarn");
    }

    /**
     * @return A copy of this compiler
     */
    private JavaCompiler copy()
    {
        return new JavaCompiler(this);
    }

    /**
     * @return The given options joined together
     */
    private String join(StringList options)
    {
        var builder = new StringBuilder();

        // For each option string,
        for (var index = 0; index < options.size(); index++)
        {
            // append the option,
            builder.append(options.get(index));

            // and if the option is a switch and the next option is not a switch,
            if (options.get(index).startsWith("-") &&
                    (index + 1 < options.size() && !options.get(index + 1).startsWith("-")))
            {
                // then append the switch value
                builder.append(" " + options.get(index + 1));
                index++;
            }
            builder.append("\n  ");
        }
        return builder.toString();
    }

    /**
     * @return A copy of this compiler with all options resolved
     */
    private JavaCompiler resolved()
    {
        var compiler = this;

        // Add any -D properties,
        for (var key : properties.keySet())
        {
            compiler = compiler.withOption("-D" + key + "=" + properties.get(key));
        }

        // debug information switch,
        compiler = compiler.withOption(debugInformation ? "-g" : "-g:none");

        // class-path,
        if (classPath.isNonEmpty())
        {
            compiler = compiler
                    .withOption("--class-path")
                    .withOption(classPath.join(";"));
        }

        // source-path,
        if (sourcePath.isNonEmpty())
        {
            compiler = compiler
                    .withOption("--source-path")
                    .withOption(sourcePath.join(";"));
        }

        // module-path,
        if (modulePath.isNonEmpty())
        {
            compiler = compiler
                    .withOption("--module-path")
                    .withOption(modulePath.join(";"));
        }

        // and any modules to reference during compilation.
        if (modules.isNonEmpty())
        {
            compiler = compiler
                    .withOption("--add-modules")
                    .withOption(modules.join(","));
        }

        return compiler;
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
