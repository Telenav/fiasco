package com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav;

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
