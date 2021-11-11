package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.resource.resources.packaged.PackageResource;
import com.telenav.kivakit.test.UnitTest;
import org.junit.Test;

public class PomReaderTest extends UnitTest
{
    @Test
    public void testNoParent()
    {
        var resource = PackageResource.packageResource(this, getClass(), "no-parent-pom.xml");

        var pom = listenTo(new PomReader()).read(resource);
        ensure(pom.parent() == null);
        ensure(pom.dependencies().size() == 4);
        ensure(has(pom, "com.telenav.kivakit:kivakit-test"));
        ensure(has(pom, "com.telenav.kivakit:kivakit-application"));
        ensure(has(pom, "com.telenav.kivakit:kivakit-data-formats-xml"));
        ensure(has(pom, "com.telenav.kivakit:kivakit-network-http"));
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
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-test")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-application")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-data-formats-xml")));
        ensure(pom.dependencies().contains(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-network-http")));
        ensure(pom.managedDependencies().size() == 0);
        ensure(pom.properties().get("project.build.sourceEncoding").equals("UTF-8"));
    }

    private boolean has(Pom pom, String dependency)
    {
        return pom.dependencies().matching(at -> at.matches(MavenArtifact.parse(this, dependency))).isNonEmpty();
    }
}
