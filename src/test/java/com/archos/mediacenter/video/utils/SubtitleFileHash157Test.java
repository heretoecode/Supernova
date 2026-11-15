package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import android.app.Application;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SubtitleFileHash157Test
{
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void aShortFileCountsOverlappingHeadAndTailInLittleEndianOrder() throws Exception
    {
        File file = temporary.newFile();
        Files.write(file.toPath(), new byte[] {1, 0, 0, 0, 0, 0, 0, 0});
        assertEquals("000000000000000a", OpenSubtitlesHasher.computeHash(file));
    }

    @Test public void theMiddleChunkDoesNotAffectThePublishedHash() throws Exception
    {
        File file = temporary.newFile();
        byte[] bytes = new byte[3 * 65536];
        for (int i = 0; i < 65536; i += 8)
        {
            bytes[i] = 1;
            bytes[2 * 65536 + i] = 2;
        }
        Files.write(file.toPath(), bytes);
        assertEquals("0000000000036000", OpenSubtitlesHasher.computeHash(file));
        java.util.Arrays.fill(bytes, 65536, 2 * 65536, (byte)127);
        Files.write(file.toPath(), bytes);
        assertEquals("0000000000036000", OpenSubtitlesHasher.computeHash(file));
    }

    @Test public void aSparseFileBeyondFourGiBUsesTheRealTailOffsetAndMissingFilesReturnNull() throws Exception
    {
        File file = temporary.newFile();
        try (RandomAccessFile writer = new RandomAccessFile(file, "rw"))
        {
            writer.setLength(0x100010000L);
            writer.write(1);
            writer.seek(0x100000000L);
            writer.write(2);
        }
        assertEquals("0000000100010003", OpenSubtitlesHasher.computeHash(file));
        assertNull(OpenSubtitlesHasher.computeHash(new File(temporary.getRoot(), "missing")));
    }
}
