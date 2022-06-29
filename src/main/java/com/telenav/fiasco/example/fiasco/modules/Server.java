package com.telenav.fiasco.example.fiasco.modules;

import com.telenav.fiasco.Module;
import com.telenav.fiasco.Project;
import com.telenav.fiasco.example.fiasco.Libraries;

import static com.telenav.kivakit.core.io.IO.println;

/**
 * @author jonathanl (shibo)
 */
public class Server extends Module implements Libraries
{
    public Server(final Project project)
    {
        super(project, "server");

        artifact("com.telenav.fiasco:fiasco-example-server:1.0");

        requires(kryo());
        requires(wicket_core());
        requires(commons_logging());

        builder().onCompiled(() -> println("done"));
    }
}
