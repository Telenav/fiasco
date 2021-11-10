package com.telenav.fiasco.internal.building.dependencies;

import com.telenav.fiasco.internal.building.Buildable;
import com.telenav.fiasco.internal.building.planning.BuildableGroup;
import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.kivakit.kernel.data.validation.ensure.Ensure;
import com.telenav.kivakit.kernel.interfaces.function.BooleanFunction;
import com.telenav.kivakit.kernel.language.collections.list.ObjectList;
import com.telenav.kivakit.kernel.language.collections.list.StringList;
import com.telenav.kivakit.kernel.language.strings.AsciiArt;

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
     * Visitor with methods that are invoked, as appropriate, at each node during a graph traversal
     */
    public interface Visitor
    {
        /**
         * @param traversal The state of the traversal
         * @param node The node we're visiting
         */
        default void atInteriorNode(TraversalState traversal, Dependency node)
        {
        }

        /**
         * @param traversal The state of the traversal
         * @param leaf The lead we're visiting
         */
        default void atLeaf(TraversalState traversal, Dependency leaf)
        {
        }

        /**
         * @param traversal The state of the traversal
         * @param node The node we're visiting
         */
        default void atNode(TraversalState traversal, Dependency node)
        {
        }
    }

    /**
     * The state of a traversal in progress, including the recursion level and indent text
     */
    public static class TraversalState
    {
        private int recursionLevel = 0;

        public String indent()
        {
            return AsciiArt.repeat(recursionLevel * 2, ' ') + "├─ ";
        }

        public void pop()
        {
            recursionLevel++;
        }

        public void push()
        {
            recursionLevel++;
        }

        public int recursionLevel()
        {
            return recursionLevel;
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
     * the next group can be built). How this works is described in detail in the documentation for {@link
     * #isLogicalLeaf(Dependency, Set)}.
     * @see #isLogicalLeaf(Dependency, Set)
     */
    public ObjectList<BuildableGroup> buildableGroups()
    {
        // Create a list of buildable groups,
        var groups = new ObjectList<BuildableGroup>();

        // and a set of visited leaves,
        var visitedLeaves = new HashSet<Dependency>();
        var previouslyVisitedLeaves = new HashSet<Dependency>();

        // then loop,
        while (true)
        {
            // creating a new group,
            var group = new BuildableGroup();

            // and visiting all the "logical leaf" nodes from the root.
            depthFirstTraversal(new TraversalState(), root, new HashSet<>(), dependency -> isLogicalLeaf(dependency, previouslyVisitedLeaves), new Visitor()
            {
                @Override
                public void atLeaf(TraversalState traversal, Dependency leaf)
                {
                    // If a leaf node is Buildable,
                    if (leaf instanceof Buildable)
                    {
                        // add it to the group
                        group.add((Buildable) leaf);

                        // and to the leaves we've visited on this pass.
                        visitedLeaves.add(leaf);
                    }
                }
            });

            // If the group is empty,
            if (group.isEmpty())
            {
                // return the groups we've found,
                return groups;
            }

            // otherwise, add the group of leaves we found,
            groups.add(group);

            // and we have previously visited all the leaves
            previouslyVisitedLeaves.addAll(visitedLeaves);
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
        depthFirstTraversal(new TraversalState(), root, new HashSet<>(), Dependency::isLeaf, visitor);
    }

    /**
     * @return Indented text describing this dependency graph
     */
    public String text()
    {
        var text = new StringList();
        depthFirstTraversal(new Visitor()
        {
            @Override
            public void atNode(TraversalState traversal, Dependency node)
            {
                text.append(traversal.indent() + node.descriptor());
            }
        });
        return text.join("\n");
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
            public void atNode(TraversalState traversal, Dependency node)
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
     * @param traversal Information about the state of the traversal, such as the indentation level
     * @param at The node to traverse from
     * @param visited The set of nodes already visited
     * @param isLeaf Function to identify leaf nodes
     * @param visitor The visitor to call with nodes
     */
    private void depthFirstTraversal(TraversalState traversal, Dependency at,
                                     Set<Dependency> visited,
                                     BooleanFunction<Dependency> isLeaf,
                                     Visitor visitor)
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
            traversal.push();
            depthFirstTraversal(traversal, child, visited, isLeaf, visitor);
            traversal.pop();
        }

        // If the node we're at is a leaf,
        if (isLeaf.isTrue(at))
        {
            // then visit the leaf,
            visitor.atLeaf(traversal, at);
        }
        else
        {
            // otherwise, visit the interior node.
            visitor.atInteriorNode(traversal, at);
        }

        visitor.atNode(traversal, at);
        visited.add(at);
    }

    /**
     * @return True if the given dependency is a "logical leaf" node given previous traversals which have populated the
     * given visited set. On the first traversal, physical leaf nodes will be placed into the visited set. On the second
     * traversal, interior nodes which had all dependencies visited on the prior traversal are logically considered
     * leaves. This algorithm essentially "shaves off" sets of leaf nodes from the original graph until there are no
     * nodes left. The group of leaf nodes found on each pass can be built in parallel since those nodes have no
     * dependencies (so long as all prior groups have been built).
     */
    private boolean isLogicalLeaf(Dependency node, Set<Dependency> visited)
    {
        // If we already visited this node,
        if (visited.contains(node))
        {
            // then it can't be a leaf
            return false;
        }

        // otherwise, go though the node's dependencies
        for (var at : node.dependencies())
        {
            // and if there's a child of the node that has not been visited,
            if (!visited.contains(at))
            {
                // we don't have a logical leaf
                return false;
            }
        }

        // We have not visited this dependency and it has no unvisited children, so it's a logical leaf node.
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
