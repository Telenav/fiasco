package com.telenav.fiasco.internal;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.strings.CaseFormat;
import com.telenav.kivakit.kernel.language.strings.Strip;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

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
     * Adds the given root folder to Fiasco
     */
    public void addProjectRoot(Folder root)
    {
        var path = root.path().asString();
        var node = rootFoldersNode();
        var existing = node.get(path, null);
        if (existing == null)
        {
            node.put(path, "true");
        }
        else
        {
            warning("Build folder already added");
        }
    }

    /**
     * @return The source file for the given build name
     */
    public File buildFile(String buildName)
    {
        for (var file : buildFiles())
        {
            if (buildName(file).equals(buildName))
            {
                return file;
            }
        }
        throw problem("No build file named: $", buildName).asException();
    }

    /**
     * @return The list of build files in all the project "fiasco" folders that Fiasco knows about
     */
    public ObjectList<File> buildFiles()
    {
        var files = new ObjectList<File>();
        for (var folder : projectRoots())
        {
            var fiascoFolder = folder.folder("fiasco");
            files.addAll(fiascoFolder.files(JAVA.fileMatcher()));
        }
        return files;
    }

    /**
     * @return The build name for the given Fiasco source file. The  file has "Build" and the file extension stripped
     * from the end, and then it is turned into lowercase hyphenated form. For example, "MyBuild.java" becomes
     * "my-build".
     */
    public String buildName(final File file)
    {
        var name = file.baseName().name();
        return CaseFormat.camelCaseToHyphenated(Strip.ending(name, "Build"));
    }

    /**
     * @return The names of all the builds that Fiasco knows about
     */
    public StringList buildNames()
    {
        var names = new StringList();
        for (var file : buildFiles())
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
     * @return The list of project roots that Fiasco knows about
     */
    public ObjectList<Folder> projectRoots()
    {
        try
        {
            var roots = new ObjectList<Folder>();
            for (var path : rootFoldersNode().keys())
            {
                roots.add(Folder.parse(path));
            }
            return roots;
        }
        catch (BackingStoreException e)
        {
            throw problem(e, "Cannot retrieve build folders").asException();
        }
    }

    /**
     * Removes all project root folders matching the given pattern
     */
    public void removeProjectRoots(Pattern pattern)
    {
        removeProjectRoots(key -> pattern.matcher(key).matches());
    }

    /**
     * Removes all root folders matching the given matcher
     */
    public void removeProjectRoots(Matcher<String> matcher)
    {
        try
        {
            for (var key : rootFoldersNode().keys())
            {
                if (matcher.matches(key))
                {
                    rootFoldersNode().remove(key);
                }
            }
        }
        catch (BackingStoreException e)
        {
            problem(e, "Cannot retrieve build folders");
        }
    }

    private Preferences rootFoldersNode()
    {
        return Preferences.userNodeForPackage(getClass())
                .node("fiasco")
                .node("root-folders");
    }
}
