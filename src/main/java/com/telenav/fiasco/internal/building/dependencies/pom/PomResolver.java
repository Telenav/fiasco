package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactResolver;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.telenav.kivakit.resource.path.Extension.POM;

/**
 * Resolves a POM resource by walking the chain of parent POMs to resolve the versions of any dependencies that are
 * specified by Maven properties or by propertyManagement sections.
 *
 * @author jonathanl (shibo)
 */
public class PomResolver extends BaseComponent
{
    /** Resolved POM resources */
    private final Map<Resource, Pom> resolved = new ConcurrentHashMap<>();

    /**
     * @return A POM model specifying resolved dependencies
     */
    public Pom resolve(Artifact artifact)
    {
        var resolver = require(MavenArtifactResolver.class);

        var root = PomReader.read(this, resolver.resolve(artifact).resource(POM));

        var parents = readParentPoms(root).reversed();

        var properties = PropertyMap.create();
        var managed = new ObjectList<MavenArtifact>();
        for (var parent : parents)
        {
            properties.addAll(parent.properties());
            properties = properties.expanded();
            managed.addAll(parent.dependencyManagementDependencies());
            for (var dependency : parent.dependencies)
            {
                if (!dependency.isResolved())
                {

                }
            }
            root.dependencies.addAll(parent.dependencies);
        }

        return root;
    }

    /**
     * @return A list of parent poms of the given pom in order from the most immediate parent to the most distant parent
     */
    private ObjectList<Pom> readParentPoms(Pom pom)
    {
        var resolver = require(MavenArtifactResolver.class);

        var parents = new ObjectList<Pom>();

        // For each parent artifact,
        for (var artifact = pom.parent; artifact != null; )
        {
            // resolve the artifact to the local repository,
            var resolved = resolver.resolve(artifact);

            // then read the pom from the local artifact,
            var parentPom = PomReader.read(this, resolved.resource(POM));

            // add it to the list of parent poms,
            parents.add(parentPom);

            // and move up the chain to the next parent.
            artifact = parentPom.parent();
        }

        return parents;
    }
}
