package com.telenav.fiasco;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.internal.FiascoSettings;
import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.commandline.ArgumentParser;
import com.telenav.kivakit.commandline.SwitchParser;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.language.reflection.Type;
import com.telenav.kivakit.resource.ResourceProject;

import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * Fiasco build tool
 *
 * <p>
 * Fiasco maintains a list of build folders in the Java preferences store. Each build folder contains one or more
 * <i>.java</i> source files that implement the {@link Build} interface. Fiasco compiles and loads these source files.
 * It then executes loaded build classes through {@link Build#run()}.
 * </p>
 *
 * <p>
 * Arguments to Fiasco are the names of one or more builds to execute. If no arguments are provided, a list of available
 * builds is shown. Build names are derived from the names of the <i>.java</i> source files by removing the <i>.java</i>
 * suffix, and any "Build" suffix. The remaining base name is converted from camelcase to lowercase separated by
 * hyphens. For example:
 * </p>
 *
 * <pre>MyProjectBuild.java => my-project</pre>
 *
 * <p>
 * Build folders can be added to Fiasco with the <i>-add=[path]</i> switch and removed with the
 * <i>-remove=[pattern]</i> switch.
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class Fiasco extends Application
{
    public static void main(final String[] arguments)
    {
        new Fiasco().run(arguments);
    }

    /** Switch to add a build folder to Fiasco */
    private final SwitchParser<Folder> ADD = Folder.folderSwitchParser("add", "Adds a build folder to Fiasco")
            .optional()
            .build();

    /** Switch to remove a build folder to Fiasco */
    private final SwitchParser<String> REMOVE = SwitchParser.stringSwitchParser("remove", "Removes one or more build folders from Fiasco by simplified regular expression pattern")
            .optional()
            .build();

    /** List of build names to build */
    private final ArgumentParser<String> BUILDS = ArgumentParser.stringArgumentParser("Names of builds to perform (for a list of available builds, run Fiasco with no arguments)")
            .optional()
            .zeroOrMore()
            .build();

    /** Java preferences settings for Fiasco */
    private final FiascoSettings settings = new FiascoSettings();

    protected Fiasco()
    {
        super(ResourceProject.get());
    }

    @Override
    protected List<ArgumentParser<?>> argumentParsers()
    {
        return List.of(BUILDS);
    }

    @Override
    protected void onRun()
    {
        // If there are no arguments
        if (argumentList().isEmpty())
        {
            // then show the available builds
            information(settings.buildNames().titledBox("Available Builds"));
        }
        else
        {
            // otherwise, check that all builds exist,
            for (var buildName : arguments(BUILDS))
            {
                ensure(settings.buildNames().contains(buildName), "Not a valid build: $", buildName);
            }

            // and build.
            build(arguments(BUILDS));
        }
    }

    @Override
    protected ObjectSet<SwitchParser<?>> switchParsers()
    {
        return ObjectSet.of(ADD, REMOVE);
    }

    /**
     * Compiles and runs the named builds
     */
    private void build(ObjectList<String> buildNames)
    {
        // For each specified build,
        for (var buildName : buildNames)
        {
            // get the .java build file
            var file = settings.buildFile(buildName);

            // compile it,
            var classFile = compile(file);
            if (classFile != null)
            {
                // and execute it.
                execute(classFile);
            }
            else
            {
                fail("Build file couldn't be compiled: $", file);
            }
        }
    }

    /**
     * @param sourceFile The Java file to compile
     * @return The class file for the given source file
     */
    private File compile(final File sourceFile)
    {
        if (sourceFile.exists())
        {
            final var compiler = ToolProvider.getSystemJavaCompiler();
            final var fileManager = compiler.getStandardFileManager(null, null, null);
            final var files = fileManager.getJavaFileObjectsFromFiles(List.of(sourceFile.asJavaFile()));

            if (compiler.getTask(null, fileManager, null, null, null, files).call())
            {
                // TODO Shibo
                return null;
            }
            else
            {
                throw problem("Compile of build file failed: $", sourceFile).asException();
            }
        }
        else
        {
            throw problem("Build source file does not exist: $", sourceFile).asException();
        }
    }

    /**
     * Executes the given {@link Build} class file
     */
    @SuppressWarnings("resource")
    private void execute(final File classFile)
    {
        try
        {
            // Attempt to load the Fiasco class from the fiasco folder
            final URLClassLoader classLoader = new URLClassLoader(new URL[] { classFile.parent().asUrl() });
            final Class<?> loaded = classLoader.loadClass(classFile.baseName().name());

            // and if the class was loaded,
            if (loaded != null)
            {
                // and it implements the build interface,
                if (Build.class.isAssignableFrom(loaded))
                {
                    // then create an instance and run it.
                    Type.forClass((Class<Build>) loaded).newInstance().run();
                }
                else
                {
                    throw problem("Build class does not implement the Build interface: $", classFile).asException();
                }
            }
            else
            {
                throw problem("Unable to load build class file: $", classFile).asException();
            }
        }
        catch (final Exception e)
        {
            throw problem(e, "Build $ failed", settings.buildName(classFile)).asException();
        }
    }
}
