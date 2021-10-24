package fiasco;

import com.telenav.fiasco.runtime.metadata.SourceCode;
import com.telenav.kivakit.filesystem.Folder;

import static com.telenav.fiasco.runtime.metadata.Role.ARCHITECT;
import static com.telenav.fiasco.runtime.metadata.Role.ARTIST;
import static com.telenav.fiasco.runtime.metadata.Role.COMMITTER;
import static com.telenav.fiasco.runtime.metadata.Role.DEVELOPER;
import static com.telenav.fiasco.runtime.metadata.Role.LEAD;
import static com.telenav.fiasco.runtime.metadata.Role.ORIGINATOR;
import static com.telenav.fiasco.runtime.metadata.Role.WRITER;

/**
 * @author jonathanl (shibo)
 */
public class MockupBuild extends TelenavBuild
{
    @Override
    public void onInitialize()
    {
        metadata(super.projectMetadata()
                .withCopyright(copyright(2021))
                .withSourceCode(SourceCode.create().withRepository("https://github.com/Telenav/example"))
                .withContributor(jonathan().withRoles(ARCHITECT, ARTIST, COMMITTER, DEVELOPER, LEAD, ORIGINATOR, WRITER)));

        require(apacheWicket().core(),
                apacheWicket().util(),
                apacheCommons().lang3(),
                kivakit().application(),
                kivakit().networkHttp());
    }

    @Override
    public Folder workspace()
    {
        return Folder.parse("${KIVAKIT_WORKSPACE}");
    }
}
