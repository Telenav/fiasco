import com.telenav.fiasco.build.Build;
import com.telenav.kivakit.filesystem.Folder;

/**
 * An example build class
 */
public class MockupBuild extends Build
{
    @Override
    public void onInitialize()
    {
        project("mockup");
    }

    @Override
    public Folder workspace()
    {
        return Folder.parse("${EXAMPLE_WORKSPACE}");
    }
}
