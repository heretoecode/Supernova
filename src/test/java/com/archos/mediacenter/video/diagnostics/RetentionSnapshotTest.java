package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class RetentionSnapshotTest
{
    private static final class ChangingFile extends File
    {
        private int reads;
        private final long initial;

        ChangingFile(String name, long initial)
        {
            super(name);
            this.initial = initial;
        }

        @Override
        public long lastModified()
        {
            reads++;
            return reads == 1 ? initial : Long.MAX_VALUE - reads;
        }

        @Override
        public long length()
        {
            return 10;
        }
    }

    @Test
    public void changingTimestampsAreReadOnceBeforeSortAndTiesUseNames()
    {
        List<File> files = new ArrayList<>();
        for (int i = 200; i >= 0; i--)
        {
            files.add(new ChangingFile(String.format("%03d", i), i / 2));
        }
        List<RetentionSnapshot.Entry> ordered = RetentionSnapshot.oldestFirst(files);
        for (int i = 0; i <= 200; i++)
        {
            assertEquals(String.format("%03d", i), ordered.get(i).name);
            assertEquals(1, ((ChangingFile)ordered.get(i).file).reads);
            assertEquals(10, ordered.get(i).bytes);
        }
    }
}
