package com.telenav.fiasco.internal;

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

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

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
        for (var file : buildSourceFiles())
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
     * @return The source file for the given build name
     */
    public File buildSourceFile(String buildName)
    {
        for (var file : buildSourceFiles())
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
    public ObjectList<File> buildSourceFiles()
    {
        var files = new ObjectList<File>();
        for (var project : projects())
        {
            files.addAll(project.folder("fiasco")
                    .files(file -> file.fileName().endsWith("Build.java")));
        }
        return files;
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
        if (new FiascoCompiler().isFiascoProject(project))
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
