package com.telenav.fiasco.dependencies;

import com.telenav.fiasco.build.Buildable;
import com.telenav.fiasco.dependencies.repository.maven.MavenArtifact;
import com.telenav.fiasco.dependencies.repository.maven.MavenCommonArtifacts;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.lifecycle.Initializable;

/**
 * A module has source code and a list of dependencies
 *
 * @author jonathanl (shibo)
 */
public class BuildableModule extends BaseComponent implements
        Buildable,
        Dependency,
        MavenCommonArtifacts,
        Initializable
{
    private final Folder root;

    public BuildableModule(Folder root)
    {
        this.root = root;
    }

    @Override
    public DependencyList dependencies()
    {
        return null;
    }

    public Folder javaSources()
    {
        return mainSources().folder("java");
    }

    public Folder mainSources()
    {
        return sources().folder("main");
    }

    public Folder output()
    {
        return root().folder("target");
    }

    public Folder root()
    {
        return root();
    }

    public Folder sources()
    {
        return root().folder("source");
    }

    public Folder testSources()
    {
        return sources().folder("test");
    }

    /**
     * Adds the given dependency
     *
     * @param descriptor The Maven artifact descriptor of the dependency
     */
    protected void require(String descriptor)
    {
        require(MavenArtifact.parse(descriptor).asLibrary());
    }

    /**
     * Adds the given dependency(ies)
     */
    protected void require(Dependency... dependency)
    {
    }
}
