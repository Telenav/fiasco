package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.reflection.property.KivaKitIncludeProperty;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;

/**
 * A model of an <a href="https://maven.apache.org">ApacheMaven</a> pom.xml (Project Object Model) file. {@link Pom}
 * objects can be loaded from {@link Resource}s by {@link PomReader}.
 *
 * <p><b>Properties</b></p>
 *
 * <ul>
 *     <li>{@link #parent()} - Any parent {@link Pom} or null if there is none</li>
 *     <li>{@link #dependencies()} - The artifacts on which this POM depends</li>
 *     <li>{@link #managedDependencies()} - Any &lt;dependencyManagement&gt; dependencies declared in this POM (but not including any parent POMs)</li>
 *     <li>{@link #inheritedManagedDependencies()} - All &lt;dependencyManagement&gt; dependencies in this POM and its ancestors</li>
 *     <li>{@link #properties()} - Properties from the &lt;properties&gt; section of the POM</li>
 *     <li>{@link #isResolved()} - True if all dependencies have resolved versions</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 * @see PomReader
 * @see MavenArtifact
 * @see <a href="https://maven.apache.org">ApacheMaven</a>
 */
public class Pom extends BaseComponent
{
    /** The parent artifact */
    Pom parent;

    /** List of dependencies */
    ObjectList<MavenArtifact> dependencies = new ObjectList<>();

    /** List of "managed" Maven dependencies */
    ObjectList<MavenArtifact> managedDependencies = new ObjectList<>();

    /** POM properties */
    PropertyMap properties = PropertyMap.create();

    /** The resource containing this POM */
    private final Resource resource;

    public Pom(Resource resource)
    {
        this.resource = resource;
    }

    /**
     * @return The dependencies declared in this POM file
     */
    @KivaKitIncludeProperty
    public ObjectList<MavenArtifact> dependencies()
    {
        return dependencies;
    }

    /**
     * @return The list of all dependency-management dependencies in this POM and all ancestor POMs
     */
    public ObjectList<MavenArtifact> inheritedManagedDependencies()
    {
        var inherited = new ObjectList<MavenArtifact>();

        // Walk up the POM hierarchy,
        for (var pom = this; pom != null; pom = pom.parent())
        {
            inherited.addAll(pom.managedDependencies());
        }

        return inherited;
    }

    /**
     * @return True if there is no unresolved (version-less) dependency in this POM
     */
    @KivaKitIncludeProperty
    public boolean isResolved()
    {
        for (var at : dependencies)
        {
            if (!at.isVersionResolved())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The managed dependencies in this POM (but not any inherited from parent POMs)
     */
    @KivaKitIncludeProperty
    public ObjectList<MavenArtifact> managedDependencies()
    {
        return managedDependencies;
    }

    /**
     * @return The parent POM of this POM
     */
    @KivaKitIncludeProperty
    public Pom parent()
    {
        return parent;
    }

    /**
     * @return The properties declared in this POM
     */
    @KivaKitIncludeProperty
    public PropertyMap properties()
    {
        return properties;
    }

    /**
     * Resolves the versions of all dependencies in this POM
     */
    public void resolveDependencyVersions()
    {
        // For each dependency,
        for (var dependency : dependencies)
        {
            // If the dependency has no version,
            if (!dependency.isVersionResolved())
            {
                // go through all managed dependencies,
                for (var at : inheritedManagedDependencies())
                {
                    // and if the dependency matches,
                    if (at.withoutVersion().matches(dependency))
                    {
                        // copy its version.
                        dependency = dependency.withVersion(at.version());
                        break;
                    }
                }
            }

            ensure(dependency.isVersionResolved(), "Dependency version is unresolved: $", dependency);

            // If the dependency version needs to be expanded,
            if (dependency.version().contains("${"))
            {
                // then expand it.
                dependency.resolveVersion(properties().expand(dependency.version()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        var lines = new StringList();
        lines.add("resource: $", resource.path());
        lines.add("parent: " + (parent == null ? "none" : parent.resource.toString()));
        lines.add("dependencies: " + dependencies.bracketed(4));
        lines.add("managed dependencies: " + managedDependencies.bracketed(4));
        lines.add("properties:" + properties.asStringList().bracketed(4));
        return lines.join("\n");
    }
}
