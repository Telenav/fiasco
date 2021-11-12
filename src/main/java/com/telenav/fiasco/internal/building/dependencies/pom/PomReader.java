package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.internal.building.dependencies.pom.Pom.Packaging;
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
 * Reads Maven {@link Pom}s (Project Object Model) from pom.xml resources.
 * </p>
 *
 * <p>
 * The {@link #read(MavenRepository, Artifact)} method reads a {@link Pom} model from the POM resource for the given
 * {@link MavenRepository} and {@link Artifact}. Dependency version numbers that contain property references (of the
 * form "${variable}") are resolved from any properties declared in the POM resource and its ancestor POM resource(s).
 * The &lt;dependencyManagement&gt; dependencies of ancestor {@link Pom}s are used to resolve the versions of any
 * dependencies that don't declare an explicit version at all. The {@link #read(Resource)} method should be used for
 * testing purposes only.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Pom
 * @see StaxReader
 * @see StaxPath
 */
public class PomReader extends BaseComponent
{
    private final StaxPath PARENT_PATH = parseXmlPath("project/parent");

    private final StaxPath PACKAGING_PATH = parseXmlPath("project/packaging");

    private final StaxPath PROPERTIES_PATH = parseXmlPath("project/properties");

    private final StaxPath DEPENDENCIES_PATH = parseXmlPath("project/dependencies");

    private final StaxPath DEPENDENCY_MANAGEMENT_DEPENDENCIES_PATH = parseXmlPath("project/dependencyManagement/dependencies");

    /** Cached {@link Pom} models to prevent re-reading of POMs */
    private final Map<Resource, Pom> poms = new HashMap<>();

    /**
     * <b>Not public API</b>
     *
     * <p>
     * The POM for the given artifact in the given repository
     * </p>
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
     * <b>Not public API</b>
     *
     * <p>
     * Testing entrypoint for reading a POM resource that's not in a repository
     * </p>
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
                    if (reader.isAt(PACKAGING_PATH))
                    {
                        pom.packaging = Packaging.valueOf(reader.enclosedText());
                    }

                    if (reader.isAt(PARENT_PATH))
                    {
                        var parentArtifact = readDependency(reader);
                        require(DependencyResolver.class).resolve(parentArtifact);
                        pom.parent = read(repository, parentArtifact);
                    }

                    if (reader.isAt(PROPERTIES_PATH))
                    {
                        pom.properties = readProperties(reader);
                    }

                    if (reader.isAt(DEPENDENCIES_PATH))
                    {
                        pom.dependencies = readDependencies(reader);
                    }

                    if (reader.isAt(DEPENDENCY_MANAGEMENT_DEPENDENCIES_PATH))
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
            var open = reader.nextAtOrInside(PROPERTIES_PATH, (StaxReader.BooleanMatcher) ignored -> reader.isAtOpenTag());
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
