package com.telenav.fiasco.internal;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.strings.CaseFormat;
import com.telenav.kivakit.kernel.language.strings.Strip;
import com.telenav.kivakit.kernel.language.values.version.Version;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;
import static com.telenav.kivakit.resource.path.Extension.JAVA;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Stores Fiasco build settings in Java preferences
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class FiascoSettings extends BaseComponent
{
    /**
     * @return The build name for the given Fiasco source file. The  file has "Build" and the file extension stripped
     * from the end, and then it is turned into lowercase hyphenated form. For example, "MyBuild.java" becomes
     * "my-build".
     */
    public String buildName(final File file)
    {
        return CaseFormat.camelCaseToHyphenated(Strip.ending(file.fileName().name(), "Build.java"));
    }

    /**
     * @return The names of all the builds that Fiasco knows about
     */
    public StringList buildNames()
    {
        var names = new StringList();
        for (var file : sourceFiles())
        {
            names.add(buildName(file));
        }
        if (names.isEmpty())
        {
            names.add("[none]");
        }
        return names;
    }

    /**
     * @return The cache folder for Fiasco
     */
    public Folder cacheFolder()
    {
        final var version = fiascoVersion();
        if (version != null)
        {
            return Folder.parse("$/.fiasco/$", System.getProperty("user.home"), version);
        }
        return fail("Unable to get version for fiasco cache folder");
    }

    /**
     * @return The Fiasco runtime jar
     */
    public File fiascoRuntimeJar()
    {
        return cacheFolder()
                .folder("downloads")
                .file("fiasco-runtime-$.jar", fiascoVersion());
    }

    /**
     * @return The version of Fiasco running in this Java virtual machine
     */
    public Version fiascoVersion()
    {
        return Application.get().version();
    }

    /**
     * Removes all project folders matching the given matcher
     */
    public void forget(Matcher<Folder> matcher)
    {
        for (var folder : projects())
        {
            if (matcher.matches(folder))
            {
                projectFoldersNode().remove(folder.toString());
                information("Forgot $", folder);
            }
        }
    }

    /**
     * @return True if the give folder is a Fiasco project.
     */
    public boolean isFiascoProject(Folder project)
    {
        var fiasco = project.folder("src/main/java/fiasco");
        return fiasco.exists() && !fiasco.files(Pattern.compile(".*Build.java")).isEmpty();
    }

    /**
     * @return The list of project folders that Fiasco knows about
     */
    public ObjectList<Folder> projects()
    {
        try
        {
            var projects = new ObjectList<Folder>();
            for (var path : projectFoldersNode().keys())
            {
                projects.add(Folder.parse(path));
            }
            return projects;
        }
        catch (BackingStoreException e)
        {
            throw problem(e, "Cannot retrieve build folders").asException();
        }
    }

    /**
     * Adds the given project folder to Fiasco
     */
    public void remember(Folder project)
    {
        if (isFiascoProject(project))
        {
            var path = project.path().asString();
            var node = projectFoldersNode();
            var existing = node.get(path, null);
            if (existing == null)
            {
                node.put(path, "true");
                information("Remembering $", project);
            }
            else
            {
                warning("Build folder already added");
            }
        }
        else
        {
            fail("Folder is not a fiasco project: $", project);
        }
    }

    /**
     * @return The source file for the given build name
     */
    public File sourceFile(String buildName)
    {
        for (var file : sourceFiles())
        {
            if (buildName(file).equals(buildName))
            {
                return file;
            }
        }
        throw problem("No build file named: $", buildName).asException();
    }

    /**
     * @return The list of build files (ending in Build.java) in all the projects that Fiasco knows about
     */
    public ObjectList<File> sourceFiles()
    {
        var files = new ObjectList<File>();

        // Go through each project,
        for (var project : projects())
        {
            // build the 'fiasco' folder,
            final var compiler = listenTo(new FiascoCompiler());
            final var fiasco = project.folder("src/main/java/fiasco");
            compiler.compile(fiasco);

            // then go through the target files,
            files.addAll(targetFolder()
                    .folder("fiasco")
                    .files(file ->
                    {
                        // and add the file to the list if it is a build class file and it can be instantiated,
                        return file.fileName().endsWith("Build.class") && compiler.instantiate(file, FiascoBuild.class) != null;
                    })
                    .mapped(file -> fiasco.file(file.fileName().withoutExtension().withExtension(JAVA))));
        }

        return files;
    }

    public Folder targetFolder()
    {
        return cacheFolder().folder("target");
    }

    private Preferences projectFoldersNode()
    {
        return Preferences.userNodeForPackage(getClass())
                .node("fiasco")
                .node("project-folders");
    }
}
