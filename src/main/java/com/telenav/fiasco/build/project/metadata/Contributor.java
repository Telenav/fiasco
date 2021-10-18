package com.telenav.fiasco.build.project.metadata;

import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.language.values.name.Name;
import com.telenav.kivakit.network.core.EmailAddress;

/**
 * An individual, human {@link Project} contributor with:
 *
 * <ul>
 *     <li>A {@link #name()}</li>
 *     <li>One or more {@link #roles}</li>
 *     <li>One or more {@link #emails()}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class Contributor extends Name
{
    /** A set of email addresses where the contributor can be contacted */
    private final ObjectSet<EmailAddress> emails = ObjectSet.empty();

    /** A set of roles that the contributor provides */
    private final ObjectSet<Role> roles = ObjectSet.empty();

    public Contributor(final String name)
    {
        super(name);
    }

    public ObjectSet<EmailAddress> emails()
    {
        return emails;
    }

    public ObjectSet<Role> roles()
    {
        return roles;
    }

    public Contributor withEmail(final EmailAddress email)
    {
        this.emails.add(email);
        return this;
    }

    public Contributor withRoles(final Role... roles)
    {
        for (var role : roles)
        {
            this.roles.add(role);
        }
        return this;
    }
}
