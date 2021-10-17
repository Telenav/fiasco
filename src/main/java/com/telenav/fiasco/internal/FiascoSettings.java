package com.telenav.fiasco.internal;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.strings.CaseFormat;
import com.telenav.kivakit.kernel.language.strings.Strip;
import com.telenav.kivakit.resource.path.Extension;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

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
    public void addBuildFolder(Folder folder)
    {
        var path = folder.path().asString();
        var buildFolders = buildFoldersNode();
        var existing = buildFolders.get(path, null);
        if (existing == null)
        {
            buildFolders.put(path, "true");
        }
        else
        {
            warning("Build folder already added");
        }
    }

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

    public ObjectList<File> buildFiles()
    {
        var files = new ObjectList<File>();
        for (var folder : buildFolders())
        {
            files.addAll(folder.files(Extension.JAVA.fileMatcher()));
        }
        return files;
    }

    public ObjectList<Folder> buildFolders()
    {
        try
        {
            var folders = new ObjectList<Folder>();
            for (var path : buildFoldersNode().keys())
            {
                folders.add(Folder.parse(path));
            }
            return folders;
        }
        catch (BackingStoreException e)
        {
            throw problem(e, "Cannot retrieve build folders").asException();
        }
    }

    public String buildName(final File file)
    {
        var name = file.baseName().name();
        return CaseFormat.camelCaseToHyphenated(Strip.ending(name, "Build"));
    }

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

    public void removeBuildFoldersMatching(Matcher<String> matcher)
    {
        try
        {
            for (var key : buildFoldersNode().keys())
            {
                if (matcher.matches(key))
                {
                    buildFoldersNode().remove(key);
                }
            }
        }
        catch (BackingStoreException e)
        {
            problem(e, "Cannot retrieve build folders");
        }
    }

    public void removeBuildFoldersMatching(Pattern pattern)
    {
        removeBuildFoldersMatching(key -> pattern.matcher(key).matches());
    }

    private Preferences buildFoldersNode()
    {
        return fiascoNode().node("build-folders");
    }

    private Preferences fiascoNode()
    {
        return Preferences.userNodeForPackage(getClass()).node("fiasco");
    }
}
