package com.telenav.fiasco.internal.building.phase.compilation;

import com.telenav.fiasco.build.Build;
import com.telenav.fiasco.build.dependencies.repository.maven.MavenRepository;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.build.tools.repository.Librarian;
import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.fiasco.FiascoFolders;
import com.telenav.fiasco.internal.building.phase.BasePhase;

import java.io.StringWriter;

import static com.telenav.fiasco.build.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;

/**
 * <b>Not public API</b>
 * <p>
 * Executes the steps in the compilation phase of a build:
 *
 * <ol>
 *     <li>INITIALIZE</li>
 *     <li>GENERATE</li>
 *     <li>PREPROCESS</li>
 *     <li>COMPILE</li>
 *     <li>POSTPROCESS</li>
 *     <li>VERIFY</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see BuildStep
 * @see Phase
 */
@SuppressWarnings("DuplicatedCode")
public class BaseCompilationPhase extends BasePhase
{
    private final StringWriter output = new StringWriter();

    public BaseCompilationPhase(final Build build)
    {
        super(build);
    }

    public StringWriter output()
    {
        return output;
    }

    protected JavaCompiler javaCompiler()
    {
        var resources = require(FiascoFolders.class);

        return JavaCompiler.create(this)
                .withOutput(output)
                .withSourceVersion(JAVA_11)
                .withTargetVersion(JAVA_11)
                .withTargetFolder(build().projectRootFolder().folder("target").mkdirs())
                .withOption("-classpath")
                .withOption(resources.fiascoRuntimeJar().toString())
                .withOption("-implicit:class");
    }

    protected Librarian librarian()
    {
        return new Librarian(build())
                .addRemoteRepository(MavenRepository.mavenCentral(this));
    }
}
