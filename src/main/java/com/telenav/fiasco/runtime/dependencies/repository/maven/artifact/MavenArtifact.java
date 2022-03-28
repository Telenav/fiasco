package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact;

import com.telenav.fiasco.internal.building.dependencies.BaseDependency;
import com.telenav.fiasco.internal.building.dependencies.pom.Pom;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactGroup;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.core.data.validation.BaseValidator;
import com.telenav.kivakit.core.data.validation.ValidationType;
import com.telenav.kivakit.core.data.validation.Validator;
import com.telenav.kivakit.core.interfaces.comparison.Matcher;
import com.telenav.kivakit.core.language.collections.list.ObjectList;
import com.telenav.kivakit.core.language.values.version.Version;
import com.telenav.kivakit.core.messaging.Listener;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.path.FilePath;

import static com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor.MatchType.EXACT;
import static com.telenav.kivakit.resource.path.Extension.JAR;
import static com.telenav.kivakit.resource.path.Extension.MD5;
import static com.telenav.kivakit.resource.path.Extension.POM;
import static com.telenav.kivakit.resource.path.Extension.SHA1;

/**
 * A Maven artifact with an identifier (artifactId), stored in a {@link MavenRepository} and belonging to a {@link
 * MavenArtifactGroup}. A Maven artifact has other artifacts as {@link #dependencies()}. An artifact can be created from
 * a descriptor with {@link #parse(Listener, String)}, where the descriptor is of the form
 * [group-identifier]:[artifact-identifier]:[version].
 *
 * <p><b>Properties</b></p>
 * <ul>
 *     <li>{@link #group()} - The group to which this artifact belongs</li>
 *     <li>{@link #identifier()} - The artifact identifier</li>
 *     <li>{@link #version()} - The artifact version</li>
 *     <li>{@link #descriptor()} - An artifact descriptor of the form: [group-name]:[artifact-name](:[version])?</li>
 *     <li>{@link #path()} - The complete path to the artifact from the root of a repository</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class MavenArtifact extends BaseDependency implements Artifact, Dependency
{
    /**
     * Parses an artifact descriptor of the form [group-identifier]:[artifact-identifier](:[version])?
     *
     * @return A {@link MavenArtifact} for the given descriptor
     */
    public static MavenArtifact parse(Listener listener, String text)
    {
        var descriptor = MavenArtifactDescriptor.parse(listener, text);
        return listener.listenTo(new MavenArtifact(descriptor));
    }

    /** The descriptor for this artifact */
    private MavenArtifactDescriptor descriptor;

    protected MavenArtifact(MavenArtifact that)
    {
        super(that);
        descriptor = that.descriptor.copy();
        copyListeners(that);
    }

    protected MavenArtifact(MavenArtifactDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    @Override
    public MavenArtifact copy()
    {
        return new MavenArtifact(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactDescriptor descriptor()
    {
        return descriptor;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MavenArtifact)
        {
            var that = (MavenArtifact) object;
            return descriptor.matches(that.descriptor, EXACT);
        }
        return false;
    }

    /**
     * @return This artifact without the matching dependencies
     */
    @Override
    public MavenArtifact excluding(Matcher<Dependency> matcher)
    {
        return (MavenArtifact) super.excluding(matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactGroup group()
    {
        return descriptor.group();
    }

    @Override
    public int hashCode()
    {
        return descriptor.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String identifier()
    {
        return descriptor.identifier();
    }

    /**
     * @return True if this artifact's descriptor includes a version (that is not an unresolved property)
     */
    @Override
    public boolean isResolved()
    {
        return descriptor.isResolved();
    }

    @Override
    public String name()
    {
        return descriptor.name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilePath path()
    {
        return descriptor.path();
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Sets this artifact version during POM property resolution
     * </p>
     */
    @Override
    public void resolveVersionTo(String version)
    {
        descriptor = descriptor.withVersion(version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource resource(Listener listener, FilePath artifactRoot, Extension extension)
    {
        return Resource.resolve(listener, artifactRoot.withChild(identifier() + "-" + version() + extension));
    }

    /**
     * <b>Not public API</b>
     *
     * <p>
     * Get the resources for this artifact associated with the given packaging under the given artifact root
     * </p>
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    public ObjectList<Resource> resources(Listener listener, FilePath artifactRoot, Pom.Packaging packaging)
    {
        var resources = new ObjectList<Resource>();
        for (var extension : extensions(packaging))
        {
            resources.add(resource(listener, artifactRoot, extension));
        }
        return resources;
    }

    @Override
    public String toString()
    {
        return descriptor.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator validator(ValidationType type)
    {
        return new BaseValidator()
        {
            @Override
            protected void onValidate()
            {
                problemIf(!descriptor.isValid(), "Invalid artifact descriptor");
            }
        };
    }

    /**
     * @return The version of this artifact
     */
    @Override
    public String version()
    {
        return descriptor.version();
    }

    @Override
    public MavenArtifact withGroup(ArtifactGroup group)
    {
        var copy = copy();
        copy.descriptor = descriptor.withGroup((MavenArtifactGroup) group);
        return copy;
    }

    @Override
    public MavenArtifact withIdentifier(String identifier)
    {
        var copy = copy();
        copy.descriptor = descriptor.withIdentifier(identifier);
        return copy;
    }

    @Override
    public MavenArtifact withVersion(Version version)
    {
        var copy = copy();
        copy.descriptor = descriptor.withVersion(version.toString());
        return copy;
    }

    @Override
    public MavenArtifact withVersion(String version)
    {
        var copy = copy();
        copy.descriptor = descriptor.withVersion(version);
        return copy;
    }

    @Override
    public MavenArtifact withoutVersion()
    {
        var copy = copy();
        copy.descriptor = descriptor.withoutVersion();
        return copy;
    }

    private Extension[] extensions(Pom.Packaging packaging)
    {
        if (packaging == Pom.Packaging.POM)
        {
            return new Extension[] {
                    POM,
                    POM.withExtension(MD5),
                    POM.withExtension(SHA1)
            };
        }
        else
        {
            return new Extension[] {
                    JAR,
                    JAR.withExtension(MD5),
                    JAR.withExtension(SHA1),
            };
        }
    }
}
