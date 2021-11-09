package com.telenav.fiasco.runtime.tools.execution;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.resource.path.Extension.JAR;

public class Java
{
    public enum Verbosity
    {
        CLASS_LOADING("class"),
        MODULE_LOADING("module"),
        GARBAGE_COLLECTION("gc"),
        JNI_METHOD_REGISTRATION("jni");

        private String value;

        Verbosity(String value)
        {
            this.value = value;
        }

        public String option()
        {
            return "-verbose:" + value;
        }
    }

    /** Virtual machine properties */
    private final PropertyMap properties = PropertyMap.create();

    /** JAR files on the class path */
    private StringList classPath = new StringList();

    /** Modules to include on the module path with --module-path */
    private ObjectList<Folder> modulePath = new ObjectList<>();

    /** The list of modules to add with --add-modules */
    private StringList addModules = new StringList();

    /** Compiler options */
    private StringList options = new StringList();

    public Java(Java that)
    {
        properties.putAll(that.properties);
    }

    public StringList options()
    {
        return resolved().options;
    }

    @Override
    public String toString()
    {
        return "java\n  " + options().join("\n  ");
    }

    /**
     * Adds the given jar file to the class path for this compiler
     *
     * @param folder The folder to add to the classpath
     */
    public Java withClasspathFolder(Folder folder)
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
    public Java withClasspathJar(File jar)
    {
        ensure(jar.hasExtension(JAR));

        var copy = copy();
        copy.classPath.add(jar.path().toString());
        return copy;
    }

    /**
     * Adds the module to the modules to resolve (with the --add-modules switch)
     */
    public Java withModule(String moduleName)
    {
        var copy = copy();
        copy.addModules.add(moduleName);
        return copy;
    }

    /**
     * Adds the named module to the module path (the --module-path switch) for this compiler
     *
     * @param module The name of the module
     */
    public Java withModulePathFolder(Folder module)
    {
        var copy = copy();
        copy.modulePath.add(module);
        return copy;
    }

    /**
     * @return This compiler with the given option added
     */
    public Java withOption(String option)
    {
        var copy = copy();
        copy.options.add(option);
        return copy;
    }

    /**
     * Adds the given property with -Dkey=value
     *
     * @param key The property key
     * @param value The property value
     */
    public Java withProperty(String key, String value)
    {
        var copy = copy();
        copy.properties.put(key, value);
        return copy;
    }

    /**
     * @return A copy of this Java virtual machine
     */
    private Java copy()
    {
        return new Java(this);
    }

    /**
     * @return A copy of this compiler with module and classpath options resolved
     */
    private Java resolved()
    {
        var java = this;

        // Add any -D properties,
        for (var key : properties.keySet())
        {
            java = java.withOption("-D" + key + "=" + properties.get(key));
        }

        // any class-path,
        if (classPath.isNonEmpty())
        {
            java = java.withOption("--class-path")
                    .withOption(classPath.join(";"));
        }

        // any module-path,
        if (modulePath.isNonEmpty())
        {
            java = java
                    .withOption("--module-path")
                    .withOption(modulePath.join(";"));
        }

        // and any modules to include in the compilation process.
        if (addModules.isNonEmpty())
        {
            java = java.withOption("--add-modules")
                    .withOption(addModules.join(","));
        }

        return java;
    }
}
