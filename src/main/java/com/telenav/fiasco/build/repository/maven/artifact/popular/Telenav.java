package com.telenav.fiasco.build.repository.maven.artifact.popular;

import com.telenav.fiasco.build.repository.maven.artifact.popular.telenav.KivaKit;

/**
 * Telenav, inc.
 *
 * @author jonathanl (shibo)
 */
public class Telenav
{
    public static Telenav get()
    {
        return new Telenav();
    }

    public KivaKit kivakit()
    {
        return new KivaKit();
    }
}
