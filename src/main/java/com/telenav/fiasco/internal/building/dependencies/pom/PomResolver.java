package com.telenav.fiasco.internal.building.dependencies.pom;

import com.telenav.fiasco.runtime.dependencies.repository.maven.MavenArtifactResolver;
import com.telenav.kivakit.component.BaseComponent;
import com.telenav.kivakit.resource.Resource;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;
import static com.telenav.kivakit.resource.path.Extension.POM;

public class PomResolver extends BaseComponent
{
    private final Resource resource;

    public PomResolver(Resource resource)
    {
        this.resource = resource;
    }

    public PomReader.Pom resolve()
    {
        var pom = PomReader.read(this, resource);

        var artifactResolver = listenTo(new MavenArtifactResolver());

        while (!pom.isResolved())
        {
            var parent = pom.parent();
            if (parent != null)
            {
                var resolved = artifactResolver.resolve(parent);
                var parentPomResource = resolved.resource(POM);
                var parentPom = PomReader.read(this, parentPomResource);

                pom.inheritFrom(parentPom);
            }
        }

        ensure(pom.isResolved(), "Unable to resolved POM: $", resource);

        return pom;
    }
}
