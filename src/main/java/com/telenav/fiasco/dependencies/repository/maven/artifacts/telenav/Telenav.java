package com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav;

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