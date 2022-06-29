package com.telenav.fiasco;

import com.telenav.fiasco.dependency.DependencyList;
import com.telenav.fiasco.plugins.FilePattern;
import com.telenav.fiasco.plugins.archiver.Archiver;
import com.telenav.fiasco.plugins.builder.Builder;
import com.telenav.fiasco.plugins.compiler.Compiler;
import com.telenav.fiasco.plugins.copier.Copier;
import com.telenav.fiasco.plugins.librarian.Librarian;
import com.telenav.fiasco.plugins.shader.Shader;
import com.telenav.fiasco.plugins.tester.Tester;
import com.telenav.fiasco.repository.artifact.Artifact;
import com.telenav.kivakit.core.messaging.repeaters.BaseRepeater;
import com.telenav.kivakit.core.object.Lazy;
import com.telenav.kivakit.core.os.OperatingSystem;
import com.telenav.kivakit.core.version.Version;
import com.telenav.kivakit.filesystem.FileList;
import com.telenav.kivakit.filesystem.Folder;

import static com.telenav.kivakit.core.ensure.Ensure.ensure;

/**
 * @author jonathanl (shibo)
 */
@SuppressWarnings("unused")
public class Module extends BaseRepeater implements Dependency<Module>
{
    private final DependencyList<Module> dependencies = new DependencyList<>();

    private final DependencyList<Library> libraries = new DependencyList<>();

    /** The project that this module belongs to */
    private Project project;

    private Artifact artifact;

    /** The folder where this module exists */
    private final Folder folder;

    private final Lazy<Archiver> archiver = Lazy.of(() -> new Archiver(this));

    private final Lazy<Builder> builder = Lazy.of(() -> new Builder(this));

    private final Lazy<Compiler> compiler = Lazy.of(() -> new Compiler(this));

    private final Lazy<Copier> copier = Lazy.of(() -> new Copier(this));

    private final Lazy<Librarian> librarian = Lazy.of(() -> new Librarian(this));

    private final Lazy<Shader> shader = Lazy.of(() -> new Shader(this));

    private final Lazy<Tester> tester = Lazy.of(() -> new Tester(this));

    public Module(final Project project, final String relativePath)
    {
        this(project, Folder.parseFolder(relativePath));
    }

    public Module(final Project project, final Folder relativeFolder)
    {
        this.project = project;

        ensure(relativeFolder.path().isRelative());
        folder = project().folder().folder(relativeFolder);
    }

    public Archiver archiver()
    {
        return archiver.get();
    }

    public Builder builder()
    {
        return builder.get();
    }

    public Folder classesFolder()
    {
        return outputFolder().folder("classes");
    }

    public Compiler compiler()
    {
        return compiler.get();
    }

    public Copier copier()
    {
        return copier.get();
    }

    @Override
    public DependencyList<Module> dependencies()
    {
        return dependencies;
    }

    public Folder folder()
    {
        return folder;
    }

    public Folder folder(final String path)
    {
        return folder.folder(path);
    }

    public Librarian librarian()
    {
        return librarian.get();
    }

    public DependencyList<Library> libraries()
    {
        return libraries;
    }

    public FilePattern matching(final String pattern)
    {
        return FilePattern.parse(pattern);
    }

    public Folder outputFolder()
    {
        return folder("output");
    }

    public Project project()
    {
        return project;
    }

    public Module requires(final Library library)
    {
        libraries.add(library);
        return this;
    }

    public void requires(final Module module)
    {
        dependencies.add(module);
    }

    public Folder resourceFolder()
    {
        return folder("src/main/resources");
    }

    public Shader shader()
    {
        return shader.get();
    }

    public Folder sourceFolder()
    {
        return folder("src/main/java");
    }

    public FileList sources()
    {
        return sourceFolder().nestedFiles(matching("**/*.java"));
    }

    public Folder testResourceFolder()
    {
        return folder("src/test/resources");
    }

    public Folder testSourceFolder()
    {
        return folder("src/test/java");
    }

    public Tester tester()
    {
        return tester.get();
    }

    public Version version()
    {
        return artifact.version();
    }

    protected void artifact(final String descriptor)
    {
        artifact(Artifact.parse(descriptor));
    }

    protected void artifact(final Artifact artifact)
    {
        this.artifact = artifact;
    }

    protected boolean isMac()
    {
        return OperatingSystem.get().isMac();
    }

    protected boolean isUnix()
    {
        return OperatingSystem.get().isUnix();
    }

    protected boolean isWindows()
    {
        return OperatingSystem.get().isWindows();
    }

    protected void project(final Project project)
    {
        this.project = project;
    }
}
