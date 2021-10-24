package com.telenav.fiasco.internal.fiasco;

import com.telenav.fiasco.Fiasco;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 *
 * @author jonathanl (shibo)
 */
public class FiascoFolders extends BaseComponent
{
    /**
     * @return The cache folder for Fiasco
     */
    public Folder cacheFolder()
    {
        final var version = require(Fiasco.class).version();
        if (version != null)
        {
            return Folder.parse("$/.fiasco/$", System.getProperty("user.home"), version);
        }
        return fail("Unable to get version for fiasco cache folder");
    }

    /**
     * @return The Fiasco runtime jar
     */
    public File fiascoRuntimeJar()
    {
        return cacheFolder()
                .folder("downloads")
                .file("fiasco-runtime-$.jar", require(Fiasco.class).version());
    }

    /**
     * @return The target folder for class files
     */
    public Folder targetFolder()
    {
        return cacheFolder().folder("target");
    }
}
