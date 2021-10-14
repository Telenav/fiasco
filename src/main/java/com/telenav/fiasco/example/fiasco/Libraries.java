package com.telenav.fiasco.example.fiasco;

import com.telenav.fiasco.Library;
import com.telenav.fiasco.repository.libraries.*;

/**
 * @author jonathanl (shibo)
 */
public interface Libraries
{
    Library kryo = EsotericSoftware.kryo.version("4.3.1");
    Library wicketCore = Apache.Wicket.core.version("9.2");
    Library commonsLogging = Apache.Commons.logging.version("1.0");
}
