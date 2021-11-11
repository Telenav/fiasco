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
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.commandline.ArgumentParser;
import com.telenav.kivakit.commandline.SwitchParser;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.filesystem.FolderGlobPattern;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.language.time.Duration;
import com.telenav.kivakit.kernel.language.values.count.Count;
import com.telenav.kivakit.kernel.language.vm.JavaVirtualMachine;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.kernel.messaging.messages.status.Announcement;
import com.telenav.kivakit.kernel.project.Project;

import java.util.List;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unsupported;

/**
 * Fiasco build tool. <a href="https://en.wikipedia.org/wiki/Fiasco_(novel)"><i>Fiasco</i></a> is a science fiction
 * novel by Stanislaw Lem published in 1986.
 *
 * <p>
 * Fiasco maintains a list of project folders in the Java preferences store. Each project folder must have a "fiasco"
 * folder that contains a "FiascoBuild.java" class which implements the {@link Build} interface. Fiasco compiles this
 * file, along with any other referenced source files. Once the build has been loaded in this way, Fiasco executes it by
 * calling {@link Build#build()}.
 * </p>
 *
 * <p><b>Command Line Switches</b></p>
 *
 * <ul>
 *     <li>-remember=[path] - Remembers the given path as a project folder</li>
 *     <li>-forget=[glob-pattern] - Forgets all project folders matching the given pattern</li>
 *     <li>-download-threads=[count] - Sets the number of threads to use when downloading artifacts</li>
 *     <li>-dependency-graph=[uml|text] - Outputs a text or UML representation of the dependency graph for each specified build</li>
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

    private enum DependencyTreeOutput
    {
        NONE,
        UML,
        TEXT
    }

    /** Switch to add a build folder to Fiasco */
    private final SwitchParser<Folder> REMEMBER = Folder.folderSwitchParser(this, "remember", "Adds a project root folder to Fiasco")
            .optional()
            .build();

    /** Switch to configure the number of threads to use when downloading artifacts */
    private final SwitchParser<Count> DOWNLOAD_THREADS = SwitchParser.countSwitchParser(this, "download-threads", "The number of threads used by the Librarian to download artifact resources")
            .optional()
            .defaultValue(Count._8)
            .build();

    /** Switch to configure the number of threads to use for building */
    private final SwitchParser<Count> BUILD_THREADS = SwitchParser.countSwitchParser(this, "build-threads", "The number of build threads to use")
            .optional()
            .defaultValue(JavaVirtualMachine.local().processors().times(2))
            .build();

    /** Switch to configure the maximum amount of time to wait for a possibly hung build */
    private final SwitchParser<Duration> BUILD_TIMEOUT = SwitchParser.durationSwitchParser(this, "build-timeout", "The maximum amount of time to wait for a build")
            .optional()
            .defaultValue(Duration.minutes(5))
            .build();

    /** Switch to show dependency graphs for the given projects */
    private final SwitchParser<DependencyTreeOutput> DEPENDENCY_GRAPH = SwitchParser.enumSwitchParser(this, "dependency-graph", "Outputs dependency information for the given builds", DependencyTreeOutput.class)
            .optional()
            .defaultValue(DependencyTreeOutput.NONE)
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

        // Register a librarian to resolve artifacts
        register(listenTo(new Librarian(this, get(DOWNLOAD_THREADS))));

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
            build(arguments(PROJECTS), result ->
            {
                switch (result.endedBecause())
                {
                    case TERMINATED:
                        announce("Build \"$\" terminated abnormally: $", result.buildName(), result.terminationCause());
                        break;

                    case INTERRUPTED:
                    case TIMED_OUT:
                        announce("Build \"$\" timed out or was interrupted. To increase the build timeout duration, use the -build-timeout switch", result.buildName());
                        break;

                    case COMPLETED:
                        announce(result.toString());
                        break;
                }
            });
        }
    }

    @Override
    protected ObjectSet<SwitchParser<?>> switchParsers()
    {
        return ObjectSet.objectSet(
                REMEMBER,
                FORGET,
                DOWNLOAD_THREADS,
                BUILD_THREADS,
                BUILD_TIMEOUT,
                DEPENDENCY_GRAPH);
    }

    /**
     * Compiles and runs the named projects, reporting build completions to the given listener
     */
    private void build(ObjectList<String> projectNames, BuildListener buildListener)
    {
        // For each specified build,
        for (var projectName : projectNames)
        {
            // get the .java build file with the given name,
            var project = projects.project(projectName);
            if (isNonNullOr(project, "Not a valid Fiasco project name: \"$\"", projectName))
            {
                var source = project.buildSourceFile();
                var classFile = compiler.compile(project.target(), source);
                if (isNonNullOr(classFile, "Could not compile: $", source))
                {
                    trace("Compiled $", source);
                    var build = compiler.loadBuild(classFile);
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
        if (shouldBuild(build))
        {
            trace("Executing build");
            var result = new BuildResult(name());
            result.start();
            try
            {
                new BuildPlanner()
                        .plan(build)
                        .build(listenTo(builder()), listener, get(BUILD_TIMEOUT));
            }
            finally
            {
                result.end();
            }
        }
    }

    /**
     * @return The {@link Builder} to use for this build
     */
    private Builder builder()
    {
        return new ParallelBuilder(get(BUILD_THREADS));
    }

    /**
     * @return True if a build should be executed given the switches passed on the command line
     */
    private boolean shouldBuild(Build build)
    {
        switch (get(DEPENDENCY_GRAPH))
        {
            case NONE:
                return true;

            case UML:
                Message.println(build.dependencyGraph().uml());
                return false;

            case TEXT:
                Message.println(build.dependencyGraph().text());
                return false;

            default:
                return unsupported();
        }
    }
}
