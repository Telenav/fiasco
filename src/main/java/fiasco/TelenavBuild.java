package fiasco;

import com.telenav.fiasco.runtime.BaseBuild;
import com.telenav.fiasco.runtime.BuildMetadata;
import com.telenav.fiasco.runtime.metadata.Contributor;
import com.telenav.fiasco.runtime.metadata.Copyright;
import com.telenav.fiasco.runtime.metadata.License;
import com.telenav.fiasco.runtime.metadata.Organization;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.network.core.EmailAddress;

/**
 * Base class for Telenav projects
 *
 * @author jonathanl (shibo)
 */
public abstract class TelenavBuild extends BaseBuild implements TelenavArtifacts
{
    protected Copyright copyright(int firstYear)
    {
        return new Copyright(StringSource.of("Copyright (C) " + firstYear + ", distributed under Apache License 2.0"));
    }

    protected Contributor jonathan()
    {
        return new Contributor("Jonathan Locke")
                .withEmail(EmailAddress.parse(this, "jonathanl@telenav.com"));
    }

    protected License license()
    {
        return License.create().withBody(packageResource("LICENSE"));
    }

    protected BuildMetadata projectMetadata()
    {
        var telenav = new Organization("Telenav")
                .withWebsite("https://www.telenav.com");

        return BuildMetadata.create()
                .withOriginator(telenav)
                .withOwner(telenav)
                .withLicense(license());
    }
}
