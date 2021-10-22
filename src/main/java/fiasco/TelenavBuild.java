package fiasco;

import com.telenav.fiasco.build.BaseFiascoBuild;
import com.telenav.fiasco.build.BuildMetadata;
import com.telenav.fiasco.build.metadata.Contributor;
import com.telenav.fiasco.build.metadata.Copyright;
import com.telenav.fiasco.build.metadata.License;
import com.telenav.fiasco.build.metadata.Organization;
import com.telenav.kivakit.kernel.interfaces.string.StringSource;
import com.telenav.kivakit.network.core.EmailAddress;

/**
 * Base class for Telenav projects
 *
 * @author jonathanl (shibo)
 */
public abstract class TelenavBuild extends BaseFiascoBuild implements TelenavArtifacts
{
    protected Copyright copyright(int firstYear)
    {
        return new Copyright(StringSource.of("Copyright (C) " + firstYear));
    }

    protected Contributor jonathan()
    {
        return new Contributor("Jonathan Locke")
                .withEmail(EmailAddress.parse("jonathanl@telenav.com"));
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
