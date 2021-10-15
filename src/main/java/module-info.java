open module com.telenav.fiasco
{
    requires java.compiler;

    requires kivakit.application;

    exports com.telenav.fiasco.old;
    exports com.telenav.fiasco.old.artifact;
    exports com.telenav.fiasco.old.dependency;
    exports com.telenav.fiasco.old.project;
    exports com.telenav.fiasco.old.repository;
    exports com.telenav.fiasco.old.plugins;
    exports com.telenav.fiasco.old.plugins.archiver;
    exports com.telenav.fiasco.old.plugins.builder;
    exports com.telenav.fiasco.old.plugins.cleaner;
    exports com.telenav.fiasco.old.plugins.copier;
    exports com.telenav.fiasco.old.plugins.compiler;
    exports com.telenav.fiasco.old.plugins.librarian;
    exports com.telenav.fiasco.old.plugins.shader;
    exports com.telenav.fiasco.old.plugins.tester;
    exports com.telenav.fiasco.repository.maven;
    exports com.telenav.fiasco.dependencies;
    exports com.telenav.fiasco.project.metadata;
    exports com.telenav.fiasco;
    exports com.telenav.fiasco.module;
    exports com.telenav.fiasco.repository;
    exports com.telenav.fiasco.project;
    exports com.telenav.fiasco.tools;
    exports com.telenav.fiasco.tools.file;
    exports com.telenav.fiasco.tools.jar;
    exports com.telenav.fiasco.build;
    exports com.telenav.fiasco.tools.compiler;
    exports com.telenav.fiasco.tools.test;
    exports com.telenav.fiasco.tools.repository;
    exports com.telenav.fiasco.dependencies.graph;
    exports com.telenav.fiasco.build.builders;
}
