package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactGroup;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.data.formats.xml.stax.StaxPath;
import com.telenav.kivakit.data.formats.xml.stax.StaxReader;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.resource.resources.other.PropertyMap;

import javax.xml.stream.events.XMLEvent;

import static com.telenav.kivakit.data.formats.xml.stax.StaxPath.parseXmlPath;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;

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
     * Simple model of properties and dependencies a Maven pom.xml file
     */
    public static class Pom
    {
        private MavenArtifact parent;

        private ObjectList<MavenArtifact> dependencies = ObjectList.create();

        private ObjectList<MavenArtifact> dependencyManagementDependencies = ObjectList.create();

        private PropertyMap properties = PropertyMap.create();

        public ObjectList<MavenArtifact> dependencies()
        {
            return dependencies;
        }

        public ObjectList<MavenArtifact> dependencyManagementDependencies()
        {
            return dependencyManagementDependencies;
        }

        public MavenArtifact parent()
        {
            return parent;
        }

        public PropertyMap properties()
        {
            return properties;
        }
    }

    private final StaxReader reader;

    private final StaxPath PARENT = parseXmlPath("project/parent");

    private final StaxPath PROPERTIES = parseXmlPath("project/properties");

    private final StaxPath DEPENDENCIES = parseXmlPath("project/dependencies");

    private final StaxPath DEPENDENCY = parseXmlPath("project/dependencies/dependency");

    private final StaxPath DEPENDENCY_MANAGEMENT = parseXmlPath("project/dependencyManagement");

    private final StaxPath DEPENDENCY_MANAGEMENT_DEPENDENCY = parseXmlPath("project/dependencyManagement/dependency");

    public PomReader(StaxReader reader)
    {
        this.reader = listenTo(reader);
    }

    /**
     * Reads the dependencies in this pom.xml XML stream using {@link StaxReader}
     *
     * @return The POM model containing properties and dependencies
     */
    public Pom read()
    {
        var pom = new Pom();

        for (; reader.hasNext(); reader.next())
        {
            if (reader.isInside(PARENT))
            {
                pom.parent = readDependency(PARENT);
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
                pom.dependencyManagementDependencies = readDependencies
                        (DEPENDENCY_MANAGEMENT, DEPENDENCY_MANAGEMENT_DEPENDENCY);
            }
        }

        return pom;
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
     * @return The next dependency
     */
    private MavenArtifact readDependency(final StaxPath dependencyPath)
    {
        MavenArtifactGroup artifactGroup = null;
        String artifactIdentifier = null;
        Version version = null;

        for (; reader.isInside(dependencyPath); reader.next())
        {
            if (reader.isAtOpenTag("groupId"))
            {
                artifactGroup = MavenArtifactGroup.create(reader.enclosedText());
            }
            if (reader.isAtOpenTag("artifactId"))
            {
                artifactIdentifier = reader.enclosedText();
            }
            if (reader.isAtOpenTag("version"))
            {
                version = Version.parse(reader.enclosedText());
            }
        }

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
