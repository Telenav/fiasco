open module com.telenav.fiasco
{
    // KivaKit
    requires kivakit.application;
    requires kivakit.network.core;

    // Java
    requires java.compiler;
    requires java.prefs;

    // Exports
    exports com.telenav.fiasco;
    exports com.telenav.fiasco.build;
}
