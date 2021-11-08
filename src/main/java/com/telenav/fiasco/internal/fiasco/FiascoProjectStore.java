package com.telenav.fiasco.internal.fiasco;

import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Stores information about Fiasco builds in Java preferences
 * </p>
 *
 * @author jonathanl (shibo)
 */
public class FiascoProjectStore extends BaseComponent
{
    /**
     * Removes all project folders matching the given matcher
     */
    public void forgetProject(Matcher<Folder> matcher)
    {
        for (var at : projects())
        {
            if (matcher.matches(at.rootFolder()))
            {
                projectFoldersNode().remove(at.rootFolder().toString());
                information("Forgot $", at);
            }
        }
    }

    /**
     * @return True if the given name is the name of a remembered Fiasco project
     */
    public boolean isProjectName(String name)
    {
        return project(name) != null;
    }

    /**
     * @return True if the given folder is a valid Fiasco project.
     */
    public boolean isValidProject(Folder project)
    {
        return project.folder("src/main/java/fiasco/FiascoBuild.java").exists();
    }

    /**
     * @return The project of the given name
     */
    public FiascoProject project(String name)
    {
        for (var at : projects())
        {
            if (at.name().equals(name))
            {
                return at;
            }
        }
        return null;
    }

    /**
     * @return The list of project folders that Fiasco knows about
     */
    public ObjectList<FiascoProject> projects()
    {
        try
        {
            var projects = new ObjectList<FiascoProject>();
            for (var path : projectFoldersNode().keys())
            {
                projects.add(new FiascoProject(Folder.parse(this, path)));
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
    public void rememberProject(Folder project)
    {
        if (isValidProject(project))
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

    private Preferences projectFoldersNode()
    {
        return Preferences.userNodeForPackage(getClass())
                .node("fiasco")
                .node("project-folders");
    }
}
