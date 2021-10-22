package com.telenav.fiasco.internal.phase.compilation;

import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.fiasco.build.repository.maven.MavenRepository;
import com.telenav.fiasco.build.tools.compiler.JavaCompiler;
import com.telenav.fiasco.build.tools.repository.Librarian;
import com.telenav.fiasco.internal.BuildStep;
import com.telenav.fiasco.internal.phase.BasePhase;
import com.telenav.fiasco.internal.phase.Phase;
import com.telenav.fiasco.internal.utility.FiascoResources;

import java.io.StringWriter;

import static com.telenav.fiasco.build.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;

/**
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

    public BaseCompilationPhase(final FiascoBuild build)
    {
        super(build);
    }

    public StringWriter output()
    {
        return output;
    }

    protected JavaCompiler javaCompiler()
    {
        var resources = require(FiascoResources.class);

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
                .addRepository(MavenRepository.local(this))
                .addRepository(MavenRepository.mavenCentral(this));
    }
}
