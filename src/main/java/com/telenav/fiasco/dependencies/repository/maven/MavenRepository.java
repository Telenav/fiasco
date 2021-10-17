package com.telenav.fiasco.dependencies.repository.maven;

import com.telenav.fiasco.dependencies.repository.Artifact;
import com.telenav.fiasco.dependencies.repository.Repository;

/**
 * A maven repository containing {@link MavenArtifact}s, organized in {@link MavenGroup}s.
 *
 * @author jonathanl (shibo)
 */
public class MavenRepository implements Repository
{
    public static MavenRepository mavenCentral = new MavenRepository("Maven Central", "https://repo1.maven.org/maven2/");

    public static MavenRepository local()
    {
        return new MavenRepository("Maven Central", "file://~/.m2/repository");
    }

    public MavenRepository(final String name, final String url)
    {
    }

    @Override
    public void install(final Artifact artifact)
    {
    }

    @Override
    public Artifact resolve(final Artifact artifact)
    {
        return artifact;
    }
}
