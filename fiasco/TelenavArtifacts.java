import com.telenav.fiasco.dependencies.repository.maven.MavenPopularArtifacts;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.ApacheCommons;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.apache.ApacheWicket;
import com.telenav.fiasco.dependencies.repository.maven.artifacts.telenav.KivaKit;

public interface TelenavArtifacts extends MavenPopularArtifacts
{
    default ApacheCommons apacheCommons()
    {
        return apache().commons().version("3.7");
    }

    default ApacheWicket apacheWicket()
    {
        return apache().wicket().version("9.5.0");
    }

    default KivaKit kivakit()
    {
        return telenav().kivakit().version("1.1.0");
    }
}
