package com.telenav.fiasco.dependencies.repository;

import com.telenav.fiasco.dependencies.Dependency;
import com.telenav.fiasco.dependencies.Library;
import com.telenav.kivakit.kernel.interfaces.naming.Named;

public interface Artifact extends Named, Dependency
{
    /**
     * @return This artifact as a library that can be included in a dependency graph
     */
    Library asLibrary();

    /**
     * @param version The version
     * @return The given version of this artifact as a library
     */
    Library version(String version);
}
