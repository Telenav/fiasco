package com.telenav.fiasco.example.fiasco.modules;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.Project;
import com.telenav.fiasco.example.fiasco.Libraries;

/**
 * @author jonathanl (shibo)
 */
public class Client extends Module implements Libraries
{
    public Client(final Project project)
    {
        super(project, "client");

        artifact("com.telenav.fiasco:fiasco-example-client:1.0");

        requires(kryo());
        requires(wicket_core());
        requires(commons_logging());
    }
}
