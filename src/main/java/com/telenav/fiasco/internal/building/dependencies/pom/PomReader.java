package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactGroup;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.data.formats.xml.stax.StaxPath;
import com.telenav.kivakit.data.formats.xml.stax.StaxReader;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import java.util.HashMap;
import java.util.Map;

import static com.telenav.kivakit.data.formats.xml.stax.StaxPath.parseXmlPath;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensureNotNull;
import static com.telenav.kivakit.resource.path.Extension.POM;

/**
 * <b>Not public API</b>
 * <p>
 * Reads properties and {@link MavenArtifact} dependencies from a POM file. Maven artifacts are read from both the
 * dependencies section and the dependencyManagement section and a simple {@link Pom} model is returned.
 *
 * @author jonathanl (shibo)
 */
public class PomReader extends BaseComponent
{
    private final StaxPath PARENT = parseXmlPath("project/parent");

    private final StaxPath PROPERTIES = parseXmlPath("project/properties");

    private final StaxPath DEPENDENCIES = parseXmlPath("project/dependencies");

    private final StaxPath DEPENDENCY_MANAGEMENT_DEPENDENCIES = parseXmlPath("project/dependencyManagement/dependencies");

    private final Map<Resource, Pom> poms = new HashMap<>();

    /**
     * The POM for the given artifact in the given repository
     *
     * @param repository The repository where the artifact resides
     * @param artifact The artifact whose POM should be read
     * @return The POM
     */
    public Pom read(MavenRepository repository, Artifact artifact)
    {
        return read(repository, repository.resource(artifact, POM));
    }

    /**
     * Testing entrypoint for reading a POM resource that's not in a repository
     */
    public Pom read(Resource resource)
    {
        return read(null, resource);
    }

    /**
     * Reads the given pom resource
     *
     * @param repository The repository where the resource was found
     * @param resource The resource to read
     * @return The POM
     */
    private Pom read(MavenRepository repository, Resource resource)
    {
        if (poms.get(resource) == null)
        {
            narrate("Reading $", resource);
            try (var reader = StaxReader.open(resource))
            {
                var pom = new Pom(resource);

                for (reader.next(); reader.hasNext(); reader.next())
                {
                    if (reader.isAt(PARENT))
                    {
                        var parentArtifact = readDependency(reader);
                        require(DependencyResolver.class).resolve(parentArtifact);
                        pom.parent = read(repository, parentArtifact);
                    }

                    if (reader.isAt(PROPERTIES))
                    {
                        pom.properties = readProperties(reader);
                    }

                    if (reader.isAt(DEPENDENCIES))
                    {
                        pom.dependencies = readDependencies(reader);
                    }

                    if (reader.isAt(DEPENDENCY_MANAGEMENT_DEPENDENCIES))
                    {
                        pom.managedDependencies = readDependencies(reader);
                    }
                }

                pom.resolveDependencyVersions();
                poms.put(resource, pom);
            }
        }

        return poms.get(resource);
    }

    /**
     * Reads a list of dependencies from the given {@link StaxReader}. The reader must be at a &lt;dependencies&gt; open
     * tag when this method is called.
     *
     * @return List of dependent {@link MavenArtifact}s
     */
    private ObjectList<MavenArtifact> readDependencies(StaxReader reader)
    {
        ensure(reader.isAtOpenTag("dependencies"));

        var dependencies = new ObjectList<MavenArtifact>();

        // Get the path we're at,
        var scope = reader.path();

        // skip past <dependencies> open tag,
        reader.next();

        // and while we haven't left the scope of the dependencies tag,
        for (; !reader.isOutside(scope); reader.next())
        {
            // if we hit a <dependency> tag,
            if (reader.isAtOpenTag("dependency"))
            {
                // then read and add the dependency.
                dependencies.add(readDependency(reader));
            }
        }

        return dependencies;
    }

    /**
     * Reads a dependency from the given {@link StaxReader}. The reader must be at a &lt;dependency&gt; or
     * &lt;parent&gt; open tag when this method is called.
     *
     * @param reader The XML reader to read from
     * @return The dependency
     */
    @SuppressWarnings("ConstantConditions")
    private MavenArtifact readDependency(StaxReader reader)
    {
        ensure(reader.isAtOpenTag("dependency") || reader.isAtOpenTag("parent"));

        MavenArtifactGroup artifactGroup = null;
        String artifactIdentifier = null;
        String version = null;

        // Get the path we're at,
        var scope = reader.path();

        // skip past the dependency open tag,
        reader.next();

        // and while we're not outside the dependency tag scope,
        for (; !reader.isOutside(scope); reader.next())
        {
            // populate any group id,
            if (reader.isAtOpenTag("groupId"))
            {
                artifactGroup = MavenArtifactGroup.parse(this, reader.enclosedText());
            }

            // any artifact id,
            if (reader.isAtOpenTag("artifactId"))
            {
                artifactIdentifier = reader.enclosedText();
            }

            // and any version.
            if (reader.isAtOpenTag("version"))
            {
                version = reader.enclosedText();
            }
        }

        // We must have at least a group and artifact identifier
        ensureNotNull(artifactGroup);
        ensureNotNull(artifactIdentifier);

        // so we can return the artifact.
        return artifactGroup.artifact(artifactIdentifier).withVersion(version);
    }

    /**
     * Reads properties at /project/properties where each property is in Maven form, such as:
     * <p>
     * &lt;kivakit.version&gt;9.5&lt;/kivakit.version&gt;
     * </p>
     * This method must be at a &lt;properties&gt; open tag when it is called
     */
    private PropertyMap readProperties(StaxReader reader)
    {
        ensure(reader.isAtOpenTag("properties"));

        var properties = PropertyMap.create();

        // Skip past <properties> open tag
        reader.next();

        while (true)
        {
            // Get the next start tag inside the properties path,
            var open = reader.nextAtOrInside(PROPERTIES, (StaxReader.BooleanMatcher) ignored -> reader.isAtOpenTag());
            if (open != null)
            {
                // read the tag name as the property name,
                var propertyName = open.asStartElement().getName().getLocalPart();

                // get the next element,
                var next = reader.next();

                // and if it's not an end element (like <tag/>)
                if (!next.isEndElement())
                {
                    // read the character data inside the tag,
                    ensure(next.isCharacters(), "Expected characters, not: $", next);

                    // and put it in the property map
                    properties.put(propertyName, next.asCharacters().getData());
                }
            }
            else
            {
                break;
            }
        }

        // Return the properties expanded, meaning that ${x} is replaced in each variable
        return properties.expanded();
    }
}
