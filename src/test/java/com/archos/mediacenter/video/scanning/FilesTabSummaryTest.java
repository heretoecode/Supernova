package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import com.archos.mediacenter.video.leanback.LibraryAttentionCount;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class FilesTabSummaryTest
{
    @Test public void headersShareTheAttentionInventoryAndDeduplicatePhysicalRows()
    {
        LibraryAttentionCount count = LibraryAttentionCount.count(Set.of(1L, 2L), Set.of(1L),
                                                                  Map.of("media:1", "x", "problem:a", 1L));
        List<FilesTabSummary.FileRow> rows = List.of(
            new FilesTabSummary.FileRow("a", true, true, false),
            new FilesTabSummary.FileRow("a", true, true, false),
            new FilesTabSummary.FileRow("b", true, false, false),
            new FilesTabSummary.FileRow("c", true, false, true),
            new FilesTabSummary.FileRow("d", false, false, false));
        FilesTabSummary result = new FilesTabSummary(
            List.of(new FilesTabSummary.Device("usb", 100), new FilesTabSummary.Device("usb", 100)), rows, rows,
            List.of(new FilesTabSummary.Connection("nas", true, 20)), 50, 2, count);
        assertEquals(new FilesTabSummary.Shield(1, 3, 100), result.shield);
        assertEquals(new FilesTabSummary.Folder(3, 1, 1), result.folder);
        assertEquals(new FilesTabSummary.Connections(1, 1, 20), result.connections);
        assertEquals(count.count, result.care.toCheck());
    }

    @Test public void unavailableAndOverflowedSizesStayUnknown()
    {
        LibraryAttentionCount count = LibraryAttentionCount.count(Set.of(), Set.of(), Map.of());
        FilesTabSummary value = new FilesTabSummary(
            List.of(new FilesTabSummary.Device("a", Long.MAX_VALUE), new FilesTabSummary.Device("b", 1)),
            List.of(), List.of(), List.of(new FilesTabSummary.Connection("a", false, -1)), 0, 0, count);
        assertEquals(-1, value.shield.freeBytes());
        assertEquals(-1, value.connections.videos());
    }
}
