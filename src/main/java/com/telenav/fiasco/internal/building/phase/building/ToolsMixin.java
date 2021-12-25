package com.telenav.fiasco.internal.building.phase.building;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.runtime.tools.compiler.JavaCompiler;
import com.telenav.fiasco.runtime.tools.repository.Librarian;
import com.telenav.kivakit.kernel.language.mixin.Mixin;

public interface ToolsMixin extends Mixin, Phase
{
    /**
     * @return The compiler to use when compiling Java sources
     */
    default JavaCompiler javaCompiler()
    {
        return tools().javaCompiler();
    }

    default Librarian librarian()
    {
        return require(Librarian.class);
    }

    default Tools tools()
    {
        return mixin(ToolsMixin.class, () -> new Tools(parentBuild()));
    }
}
