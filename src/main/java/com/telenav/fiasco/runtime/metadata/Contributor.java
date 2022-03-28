package com.telenav.fiasco.runtime.metadata;

import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.core.language.collections.set.ObjectSet;
import com.telenav.kivakit.core.language.values.name.Name;
import com.telenav.kivakit.network.core.EmailAddress;

import java.util.Arrays;

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
    private ObjectSet<EmailAddress> emails = ObjectSet.emptyObjectSet();

    /** A set of roles that the contributor provides */
    private ObjectSet<Role> roles = ObjectSet.emptyObjectSet();

    /** The organizations to which the contributor belongs */
    private ObjectSet<Organization> organizations = ObjectSet.emptyObjectSet();

    public Contributor(String name)
    {
        super(name);
    }

    public Contributor(Contributor that)
    {
        super(that.name());
        emails = that.emails.copy();
        roles = that.roles.copy();
        organizations = that.organizations.copy();
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

    public Contributor withEmail(EmailAddress email)
    {
        var copy = new Contributor(this);
        copy.emails.add(email);
        return copy;
    }

    public Contributor withOrganization(Organization organization)
    {
        var copy = new Contributor(this);
        copy.organizations.add(organization);
        return copy;
    }

    public Contributor withRoles(Role... roles)
    {
        var copy = new Contributor(this);
        this.roles.addAll(Arrays.asList(roles));
        return copy;
    }
}
