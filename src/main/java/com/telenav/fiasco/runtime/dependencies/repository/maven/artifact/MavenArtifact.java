package com.telenav.fiasco.runtime.dependencies.repository.maven.artifact;

import com.telenav.fiasco.internal.building.dependencies.BaseDependency;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactGroup;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.kivakit.kernel.data.validation.BaseValidator;
import com.telenav.kivakit.kernel.data.validation.ValidationType;
import com.telenav.kivakit.kernel.data.validation.Validator;
import com.telenav.kivakit.kernel.interfaces.comparison.Matcher;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.values.version.Version;
import com.telenav.kivakit.kernel.messaging.Listener;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.path.FilePath;

import java.util.Objects;

import static com.telenav.kivakit.resource.path.Extension.JAR;
import static com.telenav.kivakit.resource.path.Extension.MD5;
import static com.telenav.kivakit.resource.path.Extension.POM;
import static com.telenav.kivakit.resource.path.Extension.SHA1;

/**
 * A Maven artifact with an identifier (artifactId), stored in a {@link MavenRepository} and belonging to a {@link
 * MavenArtifactGroup}. A Maven artifact has other artifacts as {@link #dependencies()}. An artifact can be created from
 * a descriptor with {@link #parse(Listener, String)}, where the descriptor is of the form
 * [group-identifier]:[artifact-identifier]:[version]. An artifact can be created from an existing descriptor with
 * {@link MavenArtifactDescriptor#asArtifact()}.
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
        return new MavenArtifact(MavenArtifactDescriptor.parse(listener, text));
    }

    /** The descriptor for this artifact */
    private MavenArtifactDescriptor descriptor;

    protected MavenArtifact(MavenArtifact that)
    {
        super(that);
        descriptor = that.descriptor.copy();
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
            return descriptor.matches(that.descriptor);
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
        return Objects.hash(descriptor);
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
     * @return True if this artifact's descriptor is complete
     */
    public boolean isResolved()
    {
        return descriptor.isResolved();
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
     * {@inheritDoc}
     */
    @Override
    public Resource resource(FilePath repositoryRoot, Extension extension)
    {
        return Resource.resolve(this, repositoryRoot.withChild(identifier() + "-" + version() + extension));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectList<Resource> resources(FilePath path)
    {
        var resources = new ObjectList<Resource>();
        for (var extension : extensions())
        {
            resources.add(resource(path, extension));
        }
        return resources;
    }

    @Override
    public String toString()
    {
        return descriptor.toString();
    }

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
    public Version version()
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
        copy.descriptor = descriptor.withVersion(version);
        return copy;
    }

    private Extension[] extensions()
    {
        return new Extension[] {
                JAR,
                JAR.withExtension(MD5),
                JAR.withExtension(SHA1),
                POM,
                POM.withExtension(MD5),
                POM.withExtension(SHA1)
        };
    }
}