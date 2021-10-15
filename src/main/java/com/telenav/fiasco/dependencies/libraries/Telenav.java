package com.telenav.fiasco.dependencies.libraries;

import com.telenav.fiasco.dependencies.Library;

/**
 * @author jonathanl (shibo)
 */
public interface Telenav
{
    interface Tdk
    {
        Library core = Library.parse("com.telenav.kivakit.core:tdk-core-kernel");
        Library resource = Library.parse("com.telenav.kivakit.core:tdk-core-resource");
    }
}
