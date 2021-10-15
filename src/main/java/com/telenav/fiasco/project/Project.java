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

package com.telenav.fiasco.project;

import com.telenav.fiasco.module.Module;
import com.telenav.fiasco.project.metadata.Contributor;
import com.telenav.fiasco.project.metadata.Organization;
import com.telenav.fiasco.project.metadata.ProjectMetadata;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.kernel.interfaces.code.Callback;
import com.telenav.kivakit.kernel.language.values.count.Count;
import com.telenav.kivakit.kernel.messaging.listeners.MessageList;
import com.telenav.kivakit.kernel.messaging.messages.status.Problem;
import com.telenav.kivakit.kernel.messaging.messages.status.Quibble;
import com.telenav.kivakit.kernel.messaging.messages.status.Warning;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.ensure;

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
        final var issues = new MessageList(message -> !message.status().succeeded());
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

    @SuppressWarnings("ConstantConditions")
    public Project module(final String path, final Callback<Module> configure)
    {
        final var folder = Folder.parse(path);
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

    @SuppressWarnings("SameParameterValue")
    protected void copyright(final String copyright)
    {
        metadata = metadata.withCopyright(copyright);
    }

    protected void organization(final Organization organization)
    {
        metadata = metadata.withOrganization(organization);
    }
}
