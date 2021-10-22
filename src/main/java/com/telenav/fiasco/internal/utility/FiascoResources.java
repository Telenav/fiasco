package com.telenav.fiasco.internal.utility;

import com.telenav.kivakit.application.Application;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.language.values.version.Version;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

public class FiascoResources extends BaseComponent
{
    /**
     * @return The cache folder for Fiasco
     */
    public Folder cacheFolder()
    {
        final var version = fiascoVersion();
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
                .file("fiasco-runtime-$.jar", fiascoVersion());
    }

    /**
     * @return The version of Fiasco running in this Java virtual machine
     */
    public Version fiascoVersion()
    {
        return Application.get().version();
    }

    /**
     * @return The target folder for class files
     */
    public Folder targetFolder()
    {
        return cacheFolder().folder("target");
    }
}
