package com.telenav.fiasco.mocks;

import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.internal.building.dependencies.ResolvedDependency;
import com.telenav.fiasco.internal.building.dependencies.pom.PomReader;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.ArtifactRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.resource.Resource;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.resource.resources.packaged.PackageResource;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocks extends BaseComponent
{
    enum Resolution
    {
        PARENT,
        NO_PARENT
    }

    public MavenArtifact artifactChild()
    {
        return artifact("child", "3.3.3");
    }

    public MavenArtifact artifactParent()
    {
        return artifact("parent", "5.5.5");
    }

    public MavenArtifact artifactTest()
    {
        return artifact("test", "7.7.7");
    }

    public List<MavenArtifact> artifacts()
    {
        return List.of(artifactChild(), artifactParent(), artifactTest());
    }

    public MavenRepository repository()
    {
        var mock = mock(MavenRepository.class);
        artifacts().forEach(at -> add(mock, at));
        return mock;
    }

    public DependencyResolver resolver(Resolution resolution)
    {
        var resolver = mock(DependencyResolver.class);
        artifacts().forEach(at -> add(resolver, resolution, at));
        return resolver;
    }

    private void add(DependencyResolver resolver, Resolution resolution, Artifact artifact)
    {
        when(resolver.resolve(artifact)).thenReturn(resolved(artifact, resolution));
    }

    private void add(ArtifactRepository repository, Artifact artifact)
    {
        when(repository.name()).thenReturn("mock");
        when(repository.resource(artifact, Extension.POM)).thenReturn(pom(artifact));
        when(repository.contains(artifact)).thenReturn(true);
        when(repository.folder(artifact)).thenReturn(folder(artifact));
        when(repository.isRemote()).thenReturn(false);
        when(repository.pathTo(artifact)).thenReturn(folder(artifact).path());
    }

    private MavenArtifact artifact(String name, String version)
    {
        return MavenArtifact.parse(this, "com.telenav.fiasco.test:" + name + ":" + version);
    }

    private Folder folder(Artifact artifact)
    {
        return Folder.parse(this, "test/java/com/telenav/fiasco/mocks/repository/" + artifact.name());
    }

    private UnsupportedOperationException notMocked()
    {
        return new UnsupportedOperationException("Not mocked");
    }

    private Resource pom(Artifact artifact)
    {
        return PackageResource.packageResource(this, getClass(), artifact.name() + "/" + artifact.version() + ".pom");
    }

    private ResolvedDependency resolved(Artifact artifact, Resolution resolution)
    {
        var repository = resolution == Resolution.PARENT ? repository() : null;
        var pom = new PomReader().read(repository, pom(artifact));
        return new ResolvedDependency(repository(), artifactChild(), pom);
    }
}
