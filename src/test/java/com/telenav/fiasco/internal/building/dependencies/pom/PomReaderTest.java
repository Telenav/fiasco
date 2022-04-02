package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.internal.building.dependencies.DependencyResolver;
import com.telenav.fiasco.internal.building.dependencies.ResolvedDependency;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.Artifact;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.configuration.lookup.RegistryTrait;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.resource.resources.packaged.PackageResource;
import com.telenav.kivakit.test.UnitTest;
import org.junit.Test;

import static com.telenav.fiasco.runtime.dependencies.repository.ArtifactDescriptor.MatchType.EXACT;
import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.unsupported;

public class PomReaderTest extends UnitTest implements RegistryTrait
{
    @Test
    public void testNoParent()
    {
        var resource = PackageResource.packageResource(this, getClass(), "no-parent-pom.xml");

        var pom = listenTo(new PomReader()).read(resource);
        ensure(pom.parent() == null);
        ensure(pom.dependencies().size() == 4);
        ensure(has(pom, "com.telenav.kivakit:kivakit-test:1.0.0"));
        ensure(has(pom, "com.telenav.kivakit:kivakit-application:1.0.0"));
        ensure(has(pom, "com.telenav.kivakit:kivakit-data-formats-xml:1.0.0"));
        ensure(has(pom, "com.telenav.kivakit:kivakit-network-http:1.0.0"));
        ensure(pom.managedDependencies().size() == 0);
        ensure(pom.properties().get("project.build.sourceEncoding").equals("UTF-8"));
    }

    @Test
    public void testProperties()
    {
        var resource = PackageResource.packageResource(this, getClass(), "properties-pom.xml");
        var pom = listenTo(new PomReader()).read(resource);
        ensure(pom.parent() == null);
        ensure(pom.dependencies().size() == 4);
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-test:1.1.0-SNAPSHOT")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-application:1.1.0-SNAPSHOT")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-data-formats-xml:1.1.0-SNAPSHOT")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-network-http:1.1.0-SNAPSHOT")));
        ensure(pom.managedDependencies().size() == 0);
        ensure(pom.properties().get("project.build.sourceEncoding").equals("UTF-8"));
    }

    @Test
    public void testInheritedProperties()
    {
        var resource = PackageResource.packageResource(this, getClass(), "child-pom.xml");

        var pom = listenTo(new PomReader()).read(resource);
        ensure(pom.parent() == null);
        ensure(pom.dependencies().size() == 4);
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-test:9.9.9")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-application:9.9.9")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-data-formats-xml:9.9.9")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-network-http:9.9.9")));
        ensure(pom.managedDependencies().size() == 0);
        ensure(pom.properties().get("project.build.sourceEncoding").equals("UTF-8"));
    }

    private boolean has(Pom pom, String dependency)
    {
        return pom.dependencies().matching(at -> ((Artifact) at).matches(MavenArtifact.parse(this, dependency), EXACT)).isNonEmpty();
    }
}
