package com.telenav.fiasco.internal.fiasco;

import com.telenav.fiasco.Fiasco;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.messaging.Message;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 *
 * @author jonathanl (shibo)
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
     * @return The Fiasco runtime jar
     */
    public File runtimeJar()
    {
        return folder("modules").file("fiasco-runtime-$.jar", require(Fiasco.class).version());
    }

    /**
     * @return The target folder for class files
     */
    public Folder targetFolder()
    {
        return folder("target");
    }
}
