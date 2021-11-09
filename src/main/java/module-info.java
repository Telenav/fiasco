open module com.telenav.fiasco
{
    // KivaKit
    requires kivakit.application;
    requires kivakit.network.http;
    requires kivakit.data.formats.xml;

    // Java
    requires java.compiler;
    requires java.prefs;
    requires java.xml;
    requires kivakit.test;

    // Exports
    exports com.telenav.fiasco;
    exports com.telenav.fiasco.runtime;
    exports com.telenav.fiasco.runtime.dependencies.repository;
    exports com.telenav.fiasco.runtime.dependencies.repository.maven;
    exports com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular;
    exports com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache;
    exports com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.telenav;
    exports com.telenav.fiasco.runtime.metadata;
    exports com.telenav.fiasco.runtime.tools.compiler;
    exports com.telenav.fiasco.runtime.tools.file;
    exports com.telenav.fiasco.runtime.tools.jar;
    exports com.telenav.fiasco.runtime.tools.network;
    exports com.telenav.fiasco.runtime.tools.repository;
    exports com.telenav.fiasco.runtime.tools.test;
    exports com.telenav.fiasco.runtime.dependencies.repository.maven.artifact;
}
