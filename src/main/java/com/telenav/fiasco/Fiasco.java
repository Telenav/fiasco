package com.telenav.fiasco;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.internal.FiascoProject;
import com.telenav.fiasco.internal.FiascoSettings;
import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.commandline.ArgumentParser;
import com.telenav.kivakit.commandline.SwitchParser;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.filesystem.FolderGlobPattern;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.resource.ResourceProject;

import java.util.List;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;

/**
 * Fiasco build tool. <a href="https://en.wikipedia.org/wiki/Fiasco_(novel)"><i>Fiasco</i></a> is a science fiction
 * novel by Stanislaw Lem published in 1986.
 *
 * <p>
 * Fiasco maintains a list of project folders in the Java preferences store. Each project folder must have a "fiasco"
 * folder that contains one or more Java source files that end with "Build.java" and implement the {@link Build}
 * interface. Each such source file is a Fiasco build definition. Fiasco compiles these source files, loads them and
 * executes them by calling {@link Build#build()}.
 * </p>
 *
 * <p><b>Command Line Switches</b></p>
 * <ul>
 *     <li>-remember=[path] - Remembers the given path as a project folder</li>
 *     <li>-forget=[glob-pattern] - Forgets all project folders matching the given pattern</li>
 * </ul>
 *
 * <p><b>Examples</b></p>
 *
 * <pre>fiasco -remember=./example</pre>
 * <pre>fiasco -forget=./example</pre>
 * <pre>fiasco -forget=*</pre>
 *
 * <p><b>Command Line Arguments</b></p>
 *
 * <p>
 * Arguments to Fiasco are the names of one or more builds (found in the list of project folders that Fiasco remembers) to
 * execute. If no arguments are provided, a list of available builds is shown. Build names are derived from the names
 * of the <i>.java</i> source files (in the "fiasco" folder in each remembered project folder) by removing the <i>Build.java</i>
 * suffix. The remaining base name is converted from camelcase to lowercase separated by hyphens:
 * </p>
 *
 * <ul>MyExampleBuild.java => my-example</ul>
 *
 * <p><b>Examples</b></p>
 *
 * <pre>fiasco</pre>
 * <pre>fiasco my-example</pre>
 *
 * <p><b>NOTE</b></p>
 *
 * <p>
 * Because Fiasco remembers where projects are <i><b>it is not necessary to be in the project folder to build the
 * project</b></i>. Instead, the name of the build is specified and Fiasco locates the associated project folder.
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
    private final SwitchParser<Folder> REMEMBER = Folder.folderSwitchParser("remember", "Adds a project root folder to Fiasco")
            .optional()
            .build();

    /** Switch to remove a build folder to Fiasco */
    private final SwitchParser<String> FORGET = SwitchParser.stringSwitchParser("forget", "Removes one or more project root folders from Fiasco by glob pattern")
            .optional()
            .build();

    /** List of build names to build */
    private final ArgumentParser<String> BUILDS = ArgumentParser.stringArgumentParser("Names of builds to perform (for a list of available builds, run Fiasco with no arguments)")
            .optional()
            .zeroOrMore()
            .build();

    /** Java preferences settings for Fiasco */
    private final FiascoSettings settings = listenTo(new FiascoSettings());

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
        // If we are asked to remember a project root,
        if (has(REMEMBER))
        {
            // store it in Java preferences.
            settings.remember(get(REMEMBER));
        }

        // If we are asked to forget a project root,
        if (has(FORGET))
        {
            // remove it from Java preferences.
            settings.forget(FolderGlobPattern.parse(get(FORGET)));
        }

        // If there are no arguments,
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
        return ObjectSet.of(REMEMBER, FORGET);
    }

    /**
     * Compiles and runs the named builds
     */
    private void build(ObjectList<String> buildNames)
    {
        // For each specified build,
        for (var buildName : buildNames)
        {
            // get the .java build file with the given name,
            var source = settings.buildSourceFile(buildName);
            var build = FiascoProject.compileAndInstantiate(this, source, Build.class);
            if (build != null)
            {
                build.build();
            }
            else
            {
                problem("Unable to run build defined in: $", source);
            }
        }
    }
}
