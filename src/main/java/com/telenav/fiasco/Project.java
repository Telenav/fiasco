//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// © 2020 Telenav  - All rights reserved.                                                                    /
// This software is the confidential and proprietary information of Telenav ("Confidential Information").    /
// You shall not disclose such Confidential Information and shall use it only in accordance with the         /
// terms of the license agreement you entered into with Telenav.                                             /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.telenav.fiasco;

import com.telenav.fiasco.project.*;
import com.telenav.tdk.core.filesystem.Folder;
import com.telenav.tdk.core.kernel.interfaces.code.Callback;
import com.telenav.tdk.core.kernel.messaging.messages.MessageList;
import com.telenav.tdk.core.kernel.messaging.messages.status.*;
import com.telenav.tdk.core.kernel.scalars.counts.Count;

import static com.telenav.tdk.core.kernel.validation.Validate.ensure;

public abstract class Project extends Module
{
    private ProjectMetadata metadata = new ProjectMetadata();

    public Project(final Folder root)
    {
        super(null, root);
        project(this);
    }

    /**
     * Builds this project with the given number of worker threads
     *
     * @return True if the build succeeded without any problems
     */
    public boolean build(final Count threads)
    {
        final var issues = new MessageList<>(message -> !message.status().succeeded());
        dependencies().process(this, threads, module -> module.builder().run());
        final var statistics = issues.statisticsByType(Problem.class, Warning.class, Quibble.class);
        information(statistics.titledBox("Build Results"));
        return issues.count(Problem.class).isZero();
    }

    public ProjectMetadata metadata()
    {
        return metadata;
    }

    public Project module(final String path)
    {
        return module(path, module ->
        {
        });
    }

    public Project module(final String path, final Callback<Module> configure)
    {
        final var folder = new Folder(path);
        ensure(folder.path().isRelative());
        final var module = new Module(this, folder);
        configure.callback(module);
        requires(module);
        return this;
    }

    protected void contributor(final Contributor contributor)
    {
        metadata = metadata.withContributor(contributor);
    }

    protected void copyright(final String copyright)
    {
        metadata = metadata.withCopyright(copyright);
    }

    protected void organization(final Organization organization)
    {
        metadata = metadata.withOrganization(organization);
    }
}
