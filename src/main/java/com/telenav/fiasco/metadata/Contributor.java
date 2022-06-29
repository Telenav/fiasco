package com.telenav.fiasco.metadata;

import com.telenav.kivakit.core.value.name.Name;

@SuppressWarnings("unused")
public class Contributor extends Name
{
    private String email;

    private String role;

    public Contributor(final String name)
    {
        super(name);
    }

    public String email()
    {
        return email;
    }

    public String role()
    {
        return role;
    }

    public Contributor withEmail(final String email)
    {
        this.email = email;
        return this;
    }

    public Contributor withRole(final String role)
    {
        this.role = role;
        return this;
    }
}
