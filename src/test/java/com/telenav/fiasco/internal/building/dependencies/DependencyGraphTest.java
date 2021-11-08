package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifact;
import com.telenav.kivakit.kernel.messaging.Message;
import com.telenav.kivakit.test.UnitTest;
import org.junit.Test;

public class DependencyGraphTest extends UnitTest
{
    @Test
    public void test()
    {
        var root = MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-application:0.1.1");
        root.add(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-commandline:0.1.1"));
        var configuration = MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-configuration:0.1.1");
        configuration.add(MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-resource:0.1.1"));
        root.add(configuration);

        var graph = DependencyGraph.of(root);
        Message.println(graph.uml());
    }
}
