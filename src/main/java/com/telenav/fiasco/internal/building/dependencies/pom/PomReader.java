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

    private final StaxPath DEPENDENCY = parseXmlPath("project/dependencies/dependency");

    private final StaxPath DEPENDENCY_MANAGEMENT = parseXmlPath("project/dependencyManagement");

    private final StaxPath DEPENDENCY_MANAGEMENT_DEPENDENCY = parseXmlPath("project/dependencyManagement/dependency");

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

    Pom read(MavenRepository repository, Resource resource)
    {
        try (var reader = StaxReader.open(resource))
        {
            var pom = new Pom();

            for (reader.next(); reader.hasNext(); reader.next())
            {
                if (reader.isUnder(PARENT))
                {
                    var parentArtifact = readDependency(reader, PARENT);
                    require(DependencyResolver.class).resolve(parentArtifact);
                    pom.parent = read(repository, parentArtifact);
                }
                if (reader.isUnder(PROPERTIES))
                {
                    pom.properties = readProperties(reader);
                }
                if (reader.isUnder(DEPENDENCIES))
                {
                    pom.dependencies = readDependencies(reader, DEPENDENCIES, DEPENDENCY);
                }
                if (reader.isUnder(DEPENDENCY_MANAGEMENT))
                {
                    pom.dependencyManagementDependencies = readDependencies(reader, DEPENDENCY_MANAGEMENT, DEPENDENCY_MANAGEMENT_DEPENDENCY);
                }
            }

            pom.resolveDependencyVersions();

            return pom;
        }
    }

    /**
     * @return List of dependent {@link MavenArtifact}s
     */
    private ObjectList<MavenArtifact> readDependencies(StaxReader reader,
                                                       StaxPath dependenciesPath,
                                                       StaxPath dependencyPath)
    {
        var dependencies = new ObjectList<MavenArtifact>();

        for (; reader.isUnder(dependenciesPath); reader.next())
        {
            if (reader.isAtOpenTag("dependency"))
            {
                dependencies.add(readDependency(reader, dependencyPath));
            }
        }

        return dependencies;
    }

    /**
     * @return The dependency at the given path
     */
    @SuppressWarnings("ConstantConditions")
    private MavenArtifact readDependency(StaxReader reader, StaxPath path)
    {
        MavenArtifactGroup artifactGroup = null;
        String artifactIdentifier = null;
        String version = null;

        for (; reader.isAtOrUnder(path); reader.next())
        {
            if (reader.isAtOpenTag("groupId"))
            {
                artifactGroup = MavenArtifactGroup.parse(this, reader.enclosedText());
            }
            if (reader.isAtOpenTag("artifactId"))
            {
                artifactIdentifier = reader.enclosedText();
            }
            if (reader.isAtOpenTag("version"))
            {
                version = reader.enclosedText();
            }
        }

        ensureNotNull(artifactGroup);
        ensureNotNull(artifactIdentifier);

        return artifactGroup.artifact(artifactIdentifier).withVersion(version);
    }

    /**
     * Reads properties at /project/properties where each property is in Maven form, such as:
     * <p>
     * &lt;kivakit.version&gt;9.5&lt;/kivakit.version&gt;
     * </p>
     */
    private PropertyMap readProperties(StaxReader reader)
    {
        var properties = PropertyMap.create();

        while (true)
        {
            // Get the next start tag inside the properties path,
            var open = reader.nextAtOrUnder(PROPERTIES, (StaxReader.BooleanMatcher) ignored -> reader.isAtOpenTag());
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
