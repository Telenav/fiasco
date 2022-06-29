open module com.telenav.fiasco
{
    requires java.compiler;

    requires kivakit.application;
    requires kivakit.resource;

    exports com.telenav.fiasco;
    exports com.telenav.fiasco.repository.artifact;
    exports com.telenav.fiasco.dependency;
    exports com.telenav.fiasco.metadata;
    exports com.telenav.fiasco.repository;
    exports com.telenav.fiasco.plugins;
    exports com.telenav.fiasco.plugins.archiver;
    exports com.telenav.fiasco.plugins.builder;
    exports com.telenav.fiasco.plugins.cleaner;
    exports com.telenav.fiasco.plugins.copier;
    exports com.telenav.fiasco.plugins.compiler;
    exports com.telenav.fiasco.plugins.librarian;
    exports com.telenav.fiasco.plugins.shader;
    exports com.telenav.fiasco.plugins.tester;
}
