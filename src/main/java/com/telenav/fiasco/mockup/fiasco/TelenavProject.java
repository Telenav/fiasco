package com.telenav.fiasco.mockup.fiasco;

import com.telenav.fiasco.build.project.BaseProject;
import com.telenav.fiasco.build.project.metadata.Contributor;
import com.telenav.fiasco.build.project.metadata.Copyright;
import com.telenav.fiasco.build.project.metadata.License;
import com.telenav.fiasco.build.project.metadata.Organization;
import com.telenav.fiasco.build.project.metadata.ProjectMetadata;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.network.core.EmailAddress;

import static com.telenav.fiasco.build.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;

/**
 * Base class for Telenav projects
 *
 * @author jonathanl (shibo)
 */
public class TelenavProject extends BaseProject implements TelenavArtifacts
{
    protected Copyright copyright(int firstYear)
    {
        return new Copyright(StringSource.of("Copyright (C) " + firstYear));
    }

    protected JavaCompiler javaCompiler()
    {
        return JavaCompiler.create()
                .withSourceVersion(JAVA_11)
                .withTargetVersion(JAVA_11)
                .withTargetFolder(root().folder("fiasco"))
                .withOutput(System.console().writer());
    }

    protected Contributor jonathan()
    {
        return new Contributor("Jonathan Locke")
                .withEmail(EmailAddress.parse("jonathanl@telenav.com"));
    }

    protected License license()
    {
        return License.create().withBody(packageResource("LICENSE"));
    }

    protected ProjectMetadata projectMetadata()
    {
        var telenav = new Organization("Telenav")
                .withWebsite("https://www.telenav.com");

        return ProjectMetadata.create()
                .withOriginator(telenav)
                .withOwner(telenav)
                .withLicense(license());
    }
}
