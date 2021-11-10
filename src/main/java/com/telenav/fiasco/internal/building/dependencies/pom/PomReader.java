package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.internal.building.dependencies.repository.maven.MavenArtifactResolver;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifactGroup;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.configuration.lookup.Registry;
import com.telenav.kivakit.data.formats.xml.stax.StaxPath;
import com.telenav.kivakit.data.formats.xml.stax.StaxReader;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import javax.xml.stream.events.XMLEvent;

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
    /**
     * The POM for the given artifact in the given repository
     *
     * @param repository The repository where the artifact resides
     * @param artifact The artifact whose POM should be read
     * @return The POM
     */
    public static Pom read(MavenRepository repository, Artifact artifact)
    {
        // Ensure that the artifact is resolved into the local repository,
        Registry.global().require(MavenArtifactResolver.class).resolve(artifact);

        // then the artifact's POM resource with a StaxReader,
        var resource = repository.resource(artifact, POM);
        try (var reader = StaxReader.open(resource))
        {
            // and return the parsed POM information.
            return repository.listenTo(new PomReader(resource, repository, reader)).read();
        }
    }

    private final Resource resource;

    private final MavenRepository repository;

    private final StaxReader reader;

    private final StaxPath PARENT = parseXmlPath("project/parent");

    private final StaxPath PROPERTIES = parseXmlPath("project/properties");

    private final StaxPath DEPENDENCIES = parseXmlPath("project/dependencies");

    private final StaxPath DEPENDENCY = parseXmlPath("project/dependencies/dependency");

    private final StaxPath DEPENDENCY_MANAGEMENT = parseXmlPath("project/dependencyManagement");

    private final StaxPath DEPENDENCY_MANAGEMENT_DEPENDENCY = parseXmlPath("project/dependencyManagement/dependency");

    /**
     * @param resource The POM resource to read
     * @param repository The repository that contains the resource
     * @param reader The XML reader
     */
    private PomReader(Resource resource, MavenRepository repository, StaxReader reader)
    {
        this.resource = resource;
        this.repository = repository;
        this.reader = listenTo(reader);
    }

    /**
     * Reads this POM resource using {@link StaxReader}
     *
     * @return The POM model containing properties and dependencies
     */
    public Pom read()
    {
        var pom = new Pom();

        for (reader.next(); reader.hasNext(); reader.next())
        {
            if (reader.isInside(PARENT))
            {
                pom.parent = PomReader.read(repository, readDependency(PARENT));
            }
            if (reader.isInside(PROPERTIES))
            {
                pom.properties = readProperties();
            }
            if (reader.isInside(DEPENDENCIES))
            {
                pom.dependencies = readDependencies(DEPENDENCIES, DEPENDENCY);
            }
            if (reader.isInside(DEPENDENCY_MANAGEMENT))
            {
                pom.dependencyManagementDependencies = readDependencies(DEPENDENCY_MANAGEMENT, DEPENDENCY_MANAGEMENT_DEPENDENCY);
            }
        }

        pom.resolveDependencyVersions();

        return pom;
    }

    @Override
    public String toString()
    {
        return resource.path().asString();
    }

    /**
     * @return List of dependent {@link MavenArtifact}s
     */
    private ObjectList<MavenArtifact> readDependencies(StaxPath dependenciesPath,
                                                       StaxPath dependencyPath)
    {
        var dependencies = new ObjectList<MavenArtifact>();

        for (; reader.isInside(dependenciesPath); reader.next())
        {
            if (reader.isAtOpenTag("dependency"))
            {
                dependencies.add(readDependency(dependencyPath));
            }
        }

        return dependencies;
    }

    /**
     * @return The dependency at the given path
     */
    @SuppressWarnings("ConstantConditions")
    private MavenArtifact readDependency(StaxPath path)
    {
        MavenArtifactGroup artifactGroup = null;
        String artifactIdentifier = null;
        String version = null;

        for (; reader.isInside(path); reader.next())
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
    private PropertyMap readProperties()
    {
        var properties = PropertyMap.create();

        while (true)
        {
            // Get the next start tag inside the properties path,
            var open = reader.nextInside(PROPERTIES, (StaxReader.BooleanMatcher) XMLEvent::isStartElement);
            if (open != null)
            {
                // read the tag name as the property name,
                var propertyName = open.asStartElement().getName().getLocalPart();

                // read the character data inside the tag
                var characters = reader.next();
                if (ensure(characters.isCharacters()))
                {
                    // and put it in the property map
                    properties.put(propertyName, characters.asCharacters().getData());
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
