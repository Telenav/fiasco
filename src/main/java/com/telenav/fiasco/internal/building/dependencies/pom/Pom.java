package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.reflection.property.KivaKitIncludeProperty;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import static com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor.MatchType.EXCLUDING_VERSION;
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
    public enum Packaging
    {
        POM,
        JAR,
        BUNDLE
    }

    /** The parent artifact */
    Pom parent;

    /** The POM packaging (jar or pom) */
    Packaging packaging;

    /** List of dependencies */
    ObjectList<Dependency> dependencies = new ObjectList<>();

    /** List of "managed" Maven dependencies */
    ObjectList<Dependency> managedDependencies = new ObjectList<>();

    /** POM properties */
    PropertyMap properties = PropertyMap.create();

    /** The resource containing this POM */
    private final Resource resource;

    public Pom(Resource resource)
    {
        this.resource = resource;
    }

    /**
     * @return The dependencies declared in this POM file, with versions resolved from parent POM properties and
     * dependency management declarations
     */
    @KivaKitIncludeProperty
    public ObjectList<Dependency> dependencies()
    {
        return resolvePropertyReferences(dependencies);
    }

    /**
     * @return The list of all dependency-management dependencies in this POM and all ancestor POMs
     */
    public ObjectList<Dependency> inheritedManagedDependencies()
    {
        var inherited = new ObjectList<Dependency>();

        // If this POM has a parent,
        if (parent() != null)
        {
            // add all of its inherited managed dependencies,
            inherited.addAll(parent().inheritedManagedDependencies());
        }

        // then add all of our own managed dependencies.
        inherited.addAll(managedDependencies());

        return inherited;
    }

    /**
     * @return The map of all properties in this POM and all ancestor POMs. Properties containing references to other
     * properties are expanded to create a constant value. Resolution starts from the root of the POM inheritance path.
     */
    public PropertyMap inheritedProperties()
    {
        var inherited = new PropertyMap();

        // If this POM has a parent,
        if (parent() != null)
        {
            // add all of its inherited properties to our map,
            inherited.addAll(parent().inheritedProperties());
        }

        // then add all of our own properties
        inherited.addAll(properties());

        // and return the "expanded" properties, where all property references of the form ${property-name} have been resolved.
        return inherited.expanded();
    }

    /**
     * @return True if there is no unresolved (version-less) dependency in this POM
     */
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

    /**
     * @return The managed dependencies in this POM (but not any inherited from parent POMs)
     */
    @KivaKitIncludeProperty
    public ObjectList<Dependency> managedDependencies()
    {
        return resolvePropertyReferences(managedDependencies);
    }

    /**
     * @return The packaging (jar or pom) for this POM
     */
    public Packaging packaging()
    {
        return packaging;
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
     * @return The raw properties directly declared in this POM (unexpanded and not inheriting from ancestor POMs)
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
        // For each dependency (with property references of the form "${property-name}" resolved),
        for (var dependency : dependencies())
        {
            // if the dependency is not fully resolved,
            if (!dependency.isResolved())
            {
                // go through all the inherited managed dependencies of this POM,
                for (var at : inheritedManagedDependencies())
                {
                    // and if the dependency matches (except for the version),
                    var descriptor = at.descriptor();
                    if (descriptor.matches(dependency.descriptor(), EXCLUDING_VERSION))
                    {
                        // copy its version.
                        dependency.resolveVersionTo(at.descriptor().version());
                        break;
                    }
                }
            }

            ensure(dependency.isResolved(), "Dependency is unresolved: $", dependency);
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
        lines.add("parent: " + (parent() == null ? "none" : parent().resource.toString()));
        lines.add("dependencies: " + dependencies().bracketed(4));
        lines.add("managed dependencies: " + managedDependencies().bracketed(4));
        lines.add("properties:" + properties().asStringList().bracketed(4));
        return lines.join("\n");
    }

    /**
     * Resolves each dependency by expanding any property references of the form "${variable}" using the properties
     * contained in {@link #properties()}
     */
    private ObjectList<Dependency> resolvePropertyReferences(ObjectList<Dependency> dependencies)
    {
        for (var at : dependencies)
        {
            at.resolvePropertyReferences(inheritedProperties());
        }

        return dependencies;
    }
}
