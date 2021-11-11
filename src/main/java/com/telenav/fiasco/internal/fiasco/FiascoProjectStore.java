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
 * Stores information about {@link FiascoProject}s and their locations in Java preferences
 * </p>
 *
 * <p><b>Methods</b></p>
 *
 * <ul>
 *     <li>{@link #rememberProject(Folder)} - Remembers the Fiasco project in the given folder</li>
 *     <li>{@link #forgetProject(Matcher)} - Forgets all projects matching the given {@link Folder} matcher</li>
 *     <li>{@link #projects()} - The list of remembered {@link FiascoProject}s</li>
 *     <li>{@link #project(String)} - The remembered {@link FiascoProject} with the given name</li>
 *     <li>{@link #isProjectName(String)} - True if the given name is the name of a remembered {@link FiascoProject}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 * @see Preferences
 * @see FiascoProject
 * @see Folder
 */
public class FiascoProjectStore extends BaseComponent
{
    /** The Fiasco projects in this store */
    private ObjectList<FiascoProject> projects;

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Removes all project folders matching the given matcher
     * </p>
     */
    public void forgetProject(Matcher<Folder> matcher)
    {
        for (var at : projects())
        {
            if (matcher.matches(at.rootFolder()))
            {
                rememberedProjectFoldersNode().remove(at.rootFolder().toString());
                information("Forgot $", at);
            }
        }
    }

    /**
     * <b>Not public API</b>
     *
     * @return True if the given name is the name of a remembered Fiasco project
     */
    public boolean isProjectName(String name)
    {
        return project(name) != null;
    }

    /**
     * <b>Not public API</b>
     *
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
     * <b>Not public API</b>
     *
     * @return The list of project folders that Fiasco knows about
     */
    public ObjectList<FiascoProject> projects()
    {
        if (projects == null)
        {
            try
            {
                for (var path : rememberedProjectFoldersNode().keys())
                {
                    projects.add(listenTo(new FiascoProject(Folder.parse(this, path))));
                }
            }
            catch (BackingStoreException e)
            {
                throw problem(e, "Cannot retrieve build folders").asException();
            }
        }
        return projects;
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Adds the given project folder to Fiasco
     * </p>
     */
    public void rememberProject(Folder project)
    {
        if (isValidProject(project))
        {
            var path = project.path().asString();
            var node = rememberedProjectFoldersNode();
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
     * <b>Not public API</b>
     *
     * @return True if the given folder is a valid Fiasco project.
     */
    private boolean isValidProject(Folder project)
    {
        return project.folder("src/main/java/fiasco/FiascoBuild.java").exists();
    }

    /**
     * <b>Not public API</b>
     *
     * @return The Java preferences node for remembered Fiasco project folders
     */
    private Preferences rememberedProjectFoldersNode()
    {
        return Preferences.userNodeForPackage(getClass())
                .node("fiasco")
                .node("project-folders");
    }
}
