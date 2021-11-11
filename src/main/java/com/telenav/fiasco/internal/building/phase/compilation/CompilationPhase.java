package com.telenav.fiasco.internal.building.phase.compilation;

import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.phase.BasePhase;
import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;

import java.io.StringWriter;

import static com.telenav.fiasco.runtime.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;

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
 * @see Build
 * @see BuildStep
 * @see Phase
 * @see JavaCompiler
 */
public class CompilationPhase extends BasePhase
{
    private final StringWriter output = new StringWriter();

    /**
     * @param build The build to which this compilation phase belongs
     */
    public CompilationPhase(Build build)
    {
        super(build);
    }

    /**
     * @return The output of compilation
     */
    public StringWriter output()
    {
        return output;
    }

    /**
     * @return The compiler to use when compiling Java sources
     */
    protected JavaCompiler javaCompiler()
    {
        return JavaCompiler.create(this)
                .withOutput(output)
                .withSourceVersion(JAVA_11)
                .withTargetVersion(JAVA_11)
                .withImplicitCompilation()
                .withTargetFolder(parentBuild()
                        .projectRootFolder()
                        .folder("target")
                        .mkdirs());
    }
}
