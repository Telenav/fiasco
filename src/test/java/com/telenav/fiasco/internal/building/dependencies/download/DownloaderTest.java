package com.telenav.fiasco.internal.building.dependencies.download;

import com.telenav.fiasco.internal.building.dependencies.download.Downloader.Download;
import com.telenav.kivakit.filesystem.Folder;
import com.telenav.kivakit.network.http.HttpNetworkLocation;
import com.telenav.kivakit.resource.path.Extension;
import com.telenav.kivakit.test.UnitTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Future;

import static com.telenav.kivakit.resource.CopyMode.OVERWRITE;
import static com.telenav.kivakit.resource.path.Extension.JAR;
import static com.telenav.kivakit.resource.path.Extension.MD5;
import static com.telenav.kivakit.resource.path.Extension.POM;
import static com.telenav.kivakit.resource.path.Extension.SHA1;

@Ignore
public class DownloaderTest extends UnitTest
{
    @Test
    public void test()
    {
        var destination = Folder.temporaryForProcess(Folder.Type.NORMAL);

        var base = HttpNetworkLocation.parse(this,
                "https://repo1.maven.org/maven2/com/telenav/kivakit/kivakit-application/1.0.0");

        var futures = new ArrayList<Future<Download>>();
        var downloader = new Downloader();
        for (var extension : extensions())
        {
            var download = new Download(base.child("kivakit-application-1.0.0" + extension).get(), destination, OVERWRITE);
            futures.add(downloader.download(download));
        }

        for (var future : futures)
        {
            try
            {
                var download = future.get();
                ensure(download.destination().file(download.source().fileName()).exists());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private Extension[] extensions()
    {
        return new Extension[] {
                JAR,
                JAR.withExtension(MD5),
                JAR.withExtension(SHA1),
                POM,
                POM.withExtension(MD5),
                POM.withExtension(SHA1)
        };
    }
}
