package com.telenav.fiasco.library.classes;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.kernel.language.reflection.Type;
import com.telenav.kivakit.kernel.messaging.Listener;

import java.net.URL;
import java.net.URLClassLoader;

public class Classes
{
    /**
     * Loads the given class file and creates an instance of it
     */
    @SuppressWarnings({ "resource", "unchecked" })
    public static Object instantiate(Listener listener, File classFile)
    {
        try
        {
            // Attempt to load the Fiasco class from the fiasco folder
            final URLClassLoader classLoader = new URLClassLoader(new URL[] { classFile.parent().asUrl() });
            final Class<?> loaded = classLoader.loadClass(classFile.baseName().name());

            // and if the class was loaded,
            if (loaded != null)
            {
                // then create an instance and run the build on the root folder.
                return Type.forClass((Class<Build>) loaded).newInstance();
            }
            else
            {
                throw listener.problem("Could not instantiate class in: $, classFile").asException();
            }
        }
        catch (final Exception e)
        {
            throw listener.problem(e, "Could not load and instantiate class in: $, classFile").asException();
        }
    }
}
