package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifact;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.reflection.property.KivaKitIncludeProperty;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

/**
 * Simple model of properties and dependencies a Maven pom.xml file
 */
public class Pom
{
    MavenArtifact parent;

    ObjectList<MavenArtifact> dependencies = ObjectList.create();

    ObjectList<MavenArtifact> dependencyManagementDependencies = ObjectList.create();

    PropertyMap properties = PropertyMap.create();

    @KivaKitIncludeProperty
    public ObjectList<MavenArtifact> dependencies()
    {
        return dependencies;
    }

    @KivaKitIncludeProperty
    public ObjectList<MavenArtifact> dependencyManagementDependencies()
    {
        return dependencyManagementDependencies;
    }

    /**
     * Modifies this {@link Pom}'s unresolved dependencies so they inherit any version information from the
     * dependencyManagement section of the given parent {@link Pom}.
     */
    public void inheritFrom(final Pom parentPom)
    {
        var inherited = new ObjectList<MavenArtifact>();

        for (var at : dependencies)
        {
            if (!at.isResolved())
            {
                var dependencyManagementDependency = parentPom.dependencyManagementDependency(at);
                if (dependencyManagementDependency != null)
                {
                    at = at.withVersion(dependencyManagementDependency.version());
                }
            }
            inherited.add(at);
        }

        this.dependencies = inherited;
    }

    @KivaKitIncludeProperty
    public boolean isResolved()
    {
        for (var at : dependencies)
        {
            if (!at.isResolved())
            {
                return false;
            }
        }
        return true;
    }

    @KivaKitIncludeProperty
    public MavenArtifact parent()
    {
        return parent;
    }

    @KivaKitIncludeProperty
    public PropertyMap properties()
    {
        return properties;
    }

    public String toString()
    {
        var lines = new StringList();
        lines.add("parent: " + (parent == null ? "null" : parent.toString()));
        lines.add("properties: " + properties.toString());
        lines.add("dependencies: " + dependencies.join(", "));
        lines.add("managed: " + dependencyManagementDependencies.join(""));
        return lines.join("\n");
    }

    /**
     * @return Any dependency in the dependencyManagement section of this {@link Pom} that matches the given artifact
     * (which is unresolved and lacks a version)
     */
    private MavenArtifact dependencyManagementDependency(MavenArtifact artifact)
    {
        for (var at : dependencyManagementDependencies())
        {
            if (at.matches(artifact))
            {
                return at;
            }
        }

        return null;
    }
}
