package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.runtime.BaseBuild;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenArtifact;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.test.UnitTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

@SuppressWarnings("SuspiciousMethodCalls") public class DependencyGraphTest extends UnitTest
{
    @Test
    public void testBuildGroups()
    {
        var commandLine = build().artifactDescriptor(commandLine().descriptor());
        var configuration = build().artifactDescriptor(configuration().descriptor());
        var resource = build().artifactDescriptor(resource().descriptor());
        var root = build().artifactDescriptor(application().descriptor());
        root.add(commandLine);
        configuration.add(resource);
        root.add(configuration);

        var graph = DependencyGraph.of(root);
        var group1 = ObjectList.objectList(commandLine, resource);
        var group2 = ObjectList.objectList(configuration);
        var group3 = ObjectList.objectList(root);

        var groups = graph.buildableGroups();
        ensureEqual(groups.size(), 3);
        ensureEqual(groups.get(0), group1);
        ensureEqual(groups.get(1), group2);
        ensureEqual(groups.get(2), group3);
    }

    @Test
    public void testTraversal()
    {
        var commandLine = commandLine();
        var configuration = configuration();
        var resource = resource();
        var root = application();
        root.add(commandLine);
        configuration.add(resource);
        root.add(configuration);

        var graph = DependencyGraph.of(root);
        var interiorNodes = ObjectList.objectList(root, configuration);
        var leafNodes = ObjectList.objectList(commandLine, resource);
        var order = new ObjectList<Dependency>();

        graph.depthFirstTraversal(new DependencyGraph.Visitor()
        {
            @Override
            public void atInteriorNode(Dependency node)
            {
                ensure(interiorNodes.contains(node));
            }

            @Override
            public void atLeaf(Dependency leaf)
            {
                ensure(leafNodes.contains(leaf));
            }

            @Override
            public void atNode(Dependency node)
            {
                order.add(node);
            }
        });

        ensureEqual(order, ObjectList.objectList(commandLine, resource, configuration, root));
    }

    @NotNull
    private MavenArtifact application()
    {
        return MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-application:0.1.1");
    }

    @NotNull
    private BaseBuild build()
    {
        return new BaseBuild().projectRootFolder(Folder.kivakitHome());
    }

    @NotNull
    private MavenArtifact commandLine()
    {
        return MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-commandline:0.1.1");
    }

    @NotNull
    private MavenArtifact configuration()
    {
        return MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-configuration:0.1.1");
    }

    @NotNull
    private MavenArtifact resource()
    {
        return MavenArtifact.parse(this, "com.telenav.kivakit:kivakit-resource:0.1.1");
    }
}