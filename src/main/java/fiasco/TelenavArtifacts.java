package fiasco;

import com.telenav.fiasco.build.dependencies.repository.maven.MavenPopularArtifacts;
import com.telenav.fiasco.build.dependencies.repository.maven.artifact.popular.apache.ApacheCommons;
import com.telenav.fiasco.build.dependencies.repository.maven.artifact.popular.apache.ApacheWicket;
import com.telenav.fiasco.build.dependencies.repository.maven.artifact.popular.telenav.KivaKit;

public interface TelenavArtifacts extends MavenPopularArtifacts
{
    default ApacheCommons apacheCommons()
    {
        return apache().commons().withDefaultVersion("3.7");
    }

    default ApacheWicket apacheWicket()
    {
        return apache().wicket().withDefaultVersion("9.5.0");
    }

    default KivaKit kivakit()
    {
        return telenav().kivakit().withDefaultVersion("1.1.0");
    }
}
