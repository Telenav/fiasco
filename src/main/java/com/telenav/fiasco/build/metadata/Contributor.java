package com.telenav.fiasco.build.metadata;

import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.kernel.language.collections.set.ObjectSet;
import com.telenav.kivakit.kernel.language.values.name.Name;
import com.telenav.kivakit.network.core.EmailAddress;

/**
 * An individual, human {@link Build} contributor with:
 *
 * <ul>
 *     <li>A {@link #name()}</li>
 *     <li>One or more {@link #roles}</li>
 *     <li>One or more {@link #emails()}</li>
 *     <li>Zero or more {@link #organizations()}</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class Contributor extends Name
{
    /** A set of email addresses where the contributor can be contacted */
    private ObjectSet<EmailAddress> emails = ObjectSet.empty();

    /** A set of roles that the contributor provides */
    private ObjectSet<Role> roles = ObjectSet.empty();

    /** The organizations to which the contributor belongs */
    private ObjectSet<Organization> organizations = ObjectSet.empty();

    public Contributor(final String name)
    {
        super(name);
    }

    public Contributor(Contributor that)
    {
        super(that.name());
        this.emails = that.emails.copy();
        this.roles = that.roles.copy();
        this.organizations = that.organizations.copy();
    }

    public ObjectSet<EmailAddress> emails()
    {
        return emails;
    }

    public ObjectSet<Organization> organizations()
    {
        return organizations;
    }

    public ObjectSet<Role> roles()
    {
        return roles;
    }

    public Contributor withEmail(final EmailAddress email)
    {
        var copy = new Contributor(this);
        copy.emails.add(email);
        return copy;
    }

    public Contributor withOrganization(final Organization organization)
    {
        var copy = new Contributor(this);
        copy.organizations.add(organization);
        return copy;
    }

    public Contributor withRoles(final Role... roles)
    {
        var copy = new Contributor(this);
        for (var role : roles)
        {
            this.roles.add(role);
        }
        return copy;
    }
}
