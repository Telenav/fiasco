package com.telenav.fiasco;

import com.telenav.fiasco.internal.building.BuildListener;
import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.Builder;
import com.telenav.fiasco.internal.building.builders.ParallelBuilder;
import com.telenav.fiasco.internal.building.planning.BuildPlan;
import com.telenav.fiasco.internal.building.planning.BuildPlanner;
import com.telenav.fiasco.internal.fiasco.FiascoCache;
import com.telenav.fiasco.internal.fiasco.FiascoCompiler;
import com.telenav.fiasco.internal.fiasco.FiascoProjectStore;
import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.BuildResult;
import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.commandline.ArgumentParser;
import com.telenav.kivakit.commandline.SwitchParser;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.filesystem.FolderGlobPattern;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.messaging.messages.status.Announcement;
import com.telenav.kivakit.kernel.project.Project;

import java.util.List;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;

/**
 * Fiasco build tool. <a href="https://en.wikipedia.org/wiki/Fiasco_(novel)"><i>Fiasco</i></a> is a science fiction
 * novel by Stanislaw Lem published in 1986.
 *
 * <p>
 * Fiasco maintains a list of project folders in the Java preferences store. Each project folder must have a "fiasco"
 * folder that contains one or more Java source files that end with "Build.java" and implement the {@link Build}
 * interface. Each such source file defines a build. Fiasco compiles these source files, loads them and executes them by
 * calling {@link Build#executeBuild()}.
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
    public static void main(String[] arguments)
    {
        new Fiasco().run(arguments);

        // Java 16 reference handling deadlocks sometimes
        System.exit(0);
    }

    /** Switch to add a build folder to Fiasco */
    private final SwitchParser<Folder> REMEMBER = Folder.folderSwitchParser(this, "remember", "Adds a project root folder to Fiasco")
            .optional()
            .build();

    /** Switch to remove a build folder to Fiasco */
    private final SwitchParser<String> FORGET = SwitchParser.stringSwitchParser(this, "forget", "Removes one or more project root folders from Fiasco by glob pattern")
            .optional()
            .build();

    /** List of projects to build */
    private final ArgumentParser<String> PROJECTS = ArgumentParser.stringArgumentParser(this, "Names of projects to build (for a list of remembered projects, run Fiasco with no arguments)")
            .optional()
            .zeroOrMore()
            .build();

    /** Java preferences settings for Fiasco */
    private final FiascoProjectStore projects = listenTo(register(new FiascoProjectStore()));

    /** Locations of fiasco resources */
    private final FiascoCache resources = listenTo(register(new FiascoCache()));

    /** Project-related utilities */
    private final FiascoCompiler compiler = listenTo(new FiascoCompiler());

    public Fiasco(Project... projects)
    {
        super(new FiascoProject());
    }

    @Override
    protected List<ArgumentParser<?>> argumentParsers()
    {
        return List.of(PROJECTS);
    }

    @Override
    protected void onRun()
    {
        // If we are asked to remember a project root,
        if (has(REMEMBER))
        {
            // store it in Java preferences.
            projects.rememberProject(get(REMEMBER));
        }

        // If we are asked to forget a project root,
        if (has(FORGET))
        {
            // remove it from Java preferences.
            projects.forgetProject(FolderGlobPattern.parse(this, get(FORGET)));
        }

        // If there are no arguments,
        if (argumentList().isEmpty())
        {
            // then show the available builds
            information(projects.projects()
                    .mapped(at -> at.name() + ": " + at.rootFolder())
                    .asStringList()
                    .titledBox("Available Builds"));
        }
        else
        {
            // otherwise, check that all specified projects are valid,
            for (var at : arguments(PROJECTS))
            {
                ensure(projects.isProjectName(at), "Not a valid build: $", at);
            }

            // and build, announcing each build as it completes.
            build(result -> announce(result.toString()), arguments(PROJECTS));
        }
    }

    @Override
    protected ObjectSet<SwitchParser<?>> switchParsers()
    {
        return ObjectSet.objectSet(REMEMBER, FORGET);
    }

    /**
     * Compiles and runs the named projects, reporting build completions to the given listener
     */
    private void build(BuildListener buildListener, ObjectList<String> projectNames)
    {
        // For each specified build,
        for (var projectName : projectNames)
        {
            // get the .java build file with the given name,
            var project = projects.project(projectName);
            if (isNonNullOr(project, "Not a valid Fiasco project name: \"$\"", projectName))
            {
                var source = project.buildFile();
                var classFile = compiler.compile(source);
                if (isNonNullOr(classFile, "Could not compile: $", source))
                {
                    trace("Compiled $", source);
                    var build = compiler.instantiate(classFile, Build.class);
                    if (isNonNullOr(build, "Unable to load: $", classFile))
                    {
                        announce("Loaded build $", project);
                        listenTo(build);
                        build.projectRootFolder(project.rootFolder());
                        build(buildListener, build);
                    }
                }
            }
        }
    }

    /**
     * Creates a {@link BuildPlan} for the buildables that were added to the given {@link Build} with {@link
     * Build#project(String)}, then executes the plan using the {@link Builder} returned by {@link #builder()}. As the
     * build proceeds the {@link BuildListener} is called when {@link Buildable}s finish building. If no build listener
     * is specified, the default listener broadcasts {@link Announcement} messages for each build that completes.
     * </p>
     */
    private void build(BuildListener listener, Build build)
    {
        trace("Executing build");
        var result = new BuildResult(name());
        result.start();
        try
        {
            new BuildPlanner()
                    .plan(build)
                    .build(listenTo(builder()), listener);
        }
        finally
        {
            result.end();
        }
    }

    /**
     * @return The {@link Builder} to use for this build
     */
    private Builder builder()
    {
        return new ParallelBuilder();
    }
}
