package fiasco;

import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.MavenPopularArtifacts;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache.ApacheCommons;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.apache.ApacheWicket;
import com.telenav.fiasco.runtime.dependencies.repository.maven.artifact.popular.telenav.KivaKit;

public interface TelenavArtifacts extends MavenPopularArtifacts
{
    default ApacheCommons apacheCommons()
    {
        return apache().commons().defaultVersion("3.7");
    }

    default ApacheWicket apacheWicket()
    {
        return apache().wicket().defaultVersion("9.5.0");
    }

    default KivaKit kivakit()
    {
        return telenav().kivakit().defaultVersion("1.1.1");
    }
}
