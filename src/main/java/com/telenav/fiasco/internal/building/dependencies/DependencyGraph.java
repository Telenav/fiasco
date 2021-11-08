package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.kivakit.kernel.data.validation.ensure.Ensure;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;

import java.util.HashSet;
import java.util.Set;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 * <p>
 * Traverses the graph of dependencies from the root {@link Build}.
 *
 * @author shibo
 */
public class DependencyGraph
{
    /**
     * @return The dependency graph formed by traversing dependencies starting at the given root
     */
    public static DependencyGraph of(Dependency root)
    {
        return new DependencyGraph(root);
    }

    /**
     * Called with each {@link BuildableGroup} of leaves in the graph
     */
    public interface Visitor
    {
        default void atInteriorNode(Dependency node)
        {
        }

        default void atLeaf(Dependency leaf)
        {
        }

        default void atNode(Dependency node)
        {
        }

        default void onGroup(BuildableGroup leaves)
        {
        }
    }

    /** The root of this project graph */
    private final Dependency root;

    private DependencyGraph(Dependency root)
    {
        this.root = root;
    }

    /**
     * @return A list of groups of {@link Buildable}s that can be built simultaneously (when the first group is done,
     * the next group can be built)
     */
    public ObjectList<BuildableGroup> buildableGroups()
    {
        var groups = new ObjectList<BuildableGroup>();
        var visitedLeaves = new HashSet<Dependency>();

        while (true)
        {
            var group = new BuildableGroup();

            depthFirstTraversal(new Visitor()
            {
                @Override
                public void atLeaf(Dependency leaf)
                {
                    if (leaf instanceof Buildable)
                    {
                        group.add((Buildable) leaf);
                    }
                }
            });

            if (group.isEmpty())
            {
                return groups;
            }

            groups.add(group);

            if (group.size() == 1 && group.get(0).equals(root))
            {
                return groups;
            }
        }
    }

    /**
     * Calls the visitor with the dependencies of the given node in depth-first order. If a circular dependency is
     * detected, {@link Ensure#fail()} is called.
     *
     * @param visitor THe visitor to call with nodes
     */
    public void depthFirstTraversal(Visitor visitor)
    {
        depthFirstTraversal(root, new HashSet<>(), visitor);
    }

    /**
     * @return PlantUML string for this dependency graph
     */
    public String uml()
    {
        var uml = new StringList();

        uml.append("@startuml");
        uml.append("");
        uml.append("!include lexakai.theme");
        uml.append("title \"dependency graph\"");
        uml.append("");

        depthFirstTraversal(new Visitor()
        {
            @Override
            public void atNode(Dependency node)
            {
                uml.append("artifact " + umlName(node));
                node.dependencies().forEach(at -> uml.append(umlName(node) + " --> " + umlName(at)));
            }
        });

        uml.append("");
        uml.append("@enduml");

        return uml.join("\n");
    }

    /**
     * Calls the visitor with the dependencies of the given node in depth-first order. If a circular dependency is
     * detected, {@link Ensure#fail()} is called.
     *
     * @param at The node to traverse from
     * @param visited The set of nodes already visited
     * @param visitor THe visitor to call with nodes
     */
    private void depthFirstTraversal(Dependency at, Set<Dependency> visited, Visitor visitor)
    {
        // If we already visited this node,
        if (visited.contains(at))
        {
            // we have a circular dependency.
            fail("Circular dependency detected at: $", at);
        }

        // Go through each child dependency,
        for (var child : at.dependencies())
        {
            // explore it (in a depth-first traversal)
            depthFirstTraversal(child, visited, visitor);
        }

        // If the node we're at is a leaf,
        if (isLeaf(at, visited))
        {
            // then visit the leaf,
            visitor.atLeaf(at);
        }
        else
        {
            // otherwise, visit the interior node.
            visitor.atInteriorNode(at);
        }

        visitor.atNode(at);
        visited.add(at);
    }

    /**
     * @return True if the given node doesn't have an unvisited dependency
     */
    private boolean isLeaf(Dependency node, Set<Dependency> visited)
    {
        for (var at : node.dependencies())
        {
            if (!visited.contains(at))
            {
                return false;
            }
        }

        return true;
    }

    private String umlName(Dependency node)
    {
        var descriptor = node.descriptor();
        return descriptor.identifier().replaceAll("-", "_")
                + "_"
                + descriptor.version();
    }
}
