package com.telenav.fiasco;

import com.telenav.fiasco.project.Project;
import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.commandline.SwitchParser;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.language.values.count.Count;
import com.telenav.kivakit.resource.ResourceProject;
import com.telenav.kivakit.resource.path.Extension;

import javax.tools.ToolProvider;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * @author jonathanl (shibo)
 */
public class Fiasco extends Application
{
    public static void main(final String[] arguments)
    {
        new Fiasco().run(arguments);
    }

    /** Number of threads to use when extracting and converting */
    final SwitchParser<Count> THREADS = SwitchParser.threadCountSwitchParser(Count.count(8));

    protected Fiasco()
    {
        super(ResourceProject.get());
    }

    @Override
    protected void onRun()
    {
        final var fiasco = Folder.current().folder("fiasco");
        if (compile(fiasco))
        {
            execute(fiasco);
        }
        else
        {
            fail("Fiasco '$' couldn't be compiled", fiasco);
        }
    }

    @Override
    protected ObjectSet<SwitchParser<?>> switchParsers()
    {
        return ObjectSet.of(THREADS);
    }

    private boolean compile(final Folder folder)
    {
        if (folder.exists())
        {
            final var sourceFile = folder.file("Fiasco.java");
            if (sourceFile.exists())
            {
                final var compiler = ToolProvider.getSystemJavaCompiler();
                final var fileManager = compiler.getStandardFileManager(null, null, null);
                final var files = fileManager.getJavaFileObjectsFromFiles(folder
                        .nestedFiles(Extension.JAVA.fileMatcher())
                        .asJavaFiles());

                return compiler.getTask(null, fileManager, null, null, null, files).call();
            }
            else
            {
                fail("Fiasco source file '$' does not exist", sourceFile);
                return false;
            }
        }
        fail("Fiasco folder '$' does not exist", folder);
        return false;
    }

    @SuppressWarnings("resource")
    private void execute(final Folder folder)
    {
        try
        {
            // Attempt to load the Fiasco class from the fiasco folder
            final URLClassLoader classLoader = new URLClassLoader(new URL[] { folder.asUrl() });
            final Class<?> type = classLoader.loadClass("Fiasco");
            if (type != null)
            {
                // get its constructor
                final Constructor<?> constructor = type.getConstructor(Folder.class);

                // create the project object
                final var project = (Project) constructor.newInstance(folder);

                // and ask it to build the project
                project.build(commandLine().get(THREADS));
            }
            else
            {
                problem("Unable to load Fiasco");
            }
        }
        catch (final Exception e)
        {
            problem(e, "Unable to build Fiasco");
        }
    }
}
