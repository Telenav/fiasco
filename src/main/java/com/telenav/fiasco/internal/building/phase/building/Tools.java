package com.telenav.fiasco.internal.building.phase.building;

import com.telenav.fiasco.runtime.Build;
import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.kivakit.component.BaseComponent;

import java.io.StringWriter;

import static com.telenav.fiasco.runtime.tools.compiler.JavaCompiler.JavaVersion.JAVA_11;

public class Tools extends BaseComponent
{
    private final Build build;

    private final StringWriter output = new StringWriter();

    public Tools(Build build)
    {

        this.build = build;
    }

    /**
     * @return The compiler to use when compiling Java sources
     */
    public JavaCompiler javaCompiler()
    {
        return JavaCompiler.create(this)
                .withOutput(output)
                .withSourceVersion(JAVA_11)
                .withTargetVersion(JAVA_11)
                .withImplicitCompilation()
                .withTargetFolder(build.parentBuild()
                        .projectRootFolder()
                        .folder("target")
                        .mkdirs());
    }

    public Librarian librarian()
    {
        return require(Librarian.class);
    }

    /**
     * @return The output of compilation
     */
    public StringWriter output()
    {
        return output;
    }
}
