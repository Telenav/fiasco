package com.telenav.fiasco.internal.fiasco;

import com.telenav.fiasco.Fiasco;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.core.messaging.Message;

import static com.telenav.kivakit.core.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 *
 * <p>
 * Cache for Fiasco temporary resources.
 * </p>
 *
 * <p>
 * The cache folder accessed with {@link #folder()} is located in the .fiasco/[fiasco-version] folder in the user's home
 * folder. The sub-folder "runtime" of this folder holds the Fiasco runtime JAR, which is used to build user code
 * against. The {@link #targetFolder()} contains class files that have been built from user code in the
 * src/main/java/fiasco folder of Fiasco-built projects.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Folder
 * @see File
 */
public class FiascoCache extends BaseComponent
{
    /**
     * @return The cache folder for Fiasco
     */
    public Folder folder()
    {
        var version = require(Fiasco.class).version();
        if (version != null)
        {
            var path = Message.format("$/.fiasco/$", System.getProperty("user.home"), version);
            var cache = Folder.parse(this, path);
            if (cache != null)
            {
                return cache.mkdirs();
            }
            return fail("Unable to parse fiasco cache folder: $", path);
        }
        return fail("Unable to get version for fiasco cache folder");
    }

    /**
     * @return The named sub-folder of the cache folder
     */
    public Folder folder(String child)
    {
        return folder().folder(child).mkdirs();
    }

    /**
     * <b>Not public API</b>
     *
     * @return The Fiasco runtime jar
     */
    public File runtimeJar()
    {
        return folder("runtime").file("fiasco-runtime-$.jar", require(Fiasco.class).version());
    }

    /**
     * @return The target folder for class files
     */
    public Folder targetFolder()
    {
        return folder("target");
    }
}
