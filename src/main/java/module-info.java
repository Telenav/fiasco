open module com.telenav.fiasco
{
    // KivaKit
    requires kivakit.application;
    requires kivakit.network.core;

    requires cactus.build.metadata;
    
    // Java
    requires java.compiler;
    requires java.prefs;

    // Exports
    exports com.telenav.fiasco;
}
