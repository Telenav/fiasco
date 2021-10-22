open module com.telenav.fiasco
{
    // KivaKit
    requires kivakit.application;
    requires kivakit.network.http;

    // Java
    requires java.compiler;
    requires java.prefs;

    // Exports
    exports com.telenav.fiasco;
    exports com.telenav.fiasco.build;
    exports com.telenav.fiasco.build.repository;
    exports com.telenav.fiasco.build.repository.maven;
    exports com.telenav.fiasco.build.repository.maven.artifact.popular;
    exports com.telenav.fiasco.build.repository.maven.artifact.popular.apache;
    exports com.telenav.fiasco.build.repository.maven.artifact.popular.telenav;
    exports com.telenav.fiasco.build.tools.compiler;
    exports com.telenav.fiasco.build.tools.file;
    exports com.telenav.fiasco.build.tools.jar;
    exports com.telenav.fiasco.build.tools.network;
    exports com.telenav.fiasco.build.tools.repository;
    exports com.telenav.fiasco.build.tools.test;
}
