package com.archos.mediacenter.video.leanback;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/**
 * Definition of done 2 (owner, 17 Nov 2026): the old NOVA and three-panel screens may never open from a redesigned
 * page. Each redesigned page's code is listed here with the screens it must not name; the build fails if one does.
 * Page sheets: docs/design/pages/FILES.md, DETAILS.md ("Never opens from …").
 */
public class NoOldScreensTest
{
    /** Never opens from Files (FILES.md). */
    static final String[] NEVER_FROM_FILES = {
        "LibraryHealthActivity", "LibraryHealthPanel", "UniversalFileBrowser", "PreviewNetworkWorkspace",
        "NetworkListingFragment", "NetworkRootActivity", "NetworkShortcutDetailsFragment", "CreateShareDialog",
        "SharedThreePanel", "networkSection(", "InputMethodManager", "showSoftInput", "LocalListingFragment",
        "ListingActivity",
    };

    @Test
    public void filesPageNamesNoOldScreen() throws IOException
    {
        List<String> found = new ArrayList<>();
        try (Stream<Path> sources = Files.list(Path.of("src/main/java/com/archos/mediacenter/video/leanback/files")))
        {
            for (Path source : (Iterable<Path>) sources::iterator)
            {
                check(source.getFileName().toString(), read(source), NEVER_FROM_FILES, found);
            }
        }
        String pages = read(Path.of("src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java"));
        check("PreviewPages.filesLayout", between(pages, "private void filesLayout(", "private Uri storageRoot("),
              NEVER_FROM_FILES, found);
        assertTrue("Old screens reachable from Files: " + found, found.isEmpty());
    }

    /** Never opens from Details (DETAILS.md), including a streaming title's own Supernova page (RULES §18). */
    static final String[] NEVER_FROM_DETAILS = {
        "PreviewRemoteDetailsActivity", "InputMethodManager", "showSoftInput", "LibraryHealthActivity",
        "UniversalFileBrowser",
    };

    @Test
    public void detailsPageNamesNoOldScreen() throws IOException
    {
        List<String> found = new ArrayList<>();
        Path page = Path.of("src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java");
        check("PreviewMoviePage", read(page), NEVER_FROM_DETAILS, found);
        assertTrue("Old screens reachable from Details: " + found, found.isEmpty());
    }

    @Test
    public void filesHostOpensNoOldBrowserForADrive() throws IOException
    {
        String pages = read(Path.of("src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java"));
        String files = between(pages, "private void filesLayout(", "private Uri storageRoot(");
        assertTrue("A drive must browse inside Files, never through click.open(box)",
                   !files.contains("click.open(new Presenter.ViewHolder(container), box)"));
    }

    static void check(String name, String code, String[] forbidden, List<String> found)
    {
        for (String screen : forbidden)
        {
            if (withoutComments(code).contains(screen))
            {
                found.add(name + " → " + screen);
            }
        }
    }

    static String between(String code, String start, String end)
    {
        int from = code.indexOf(start);
        int to = code.indexOf(end, Math.max(0, from));
        assertTrue("Marker missing: " + start + " … " + end, from >= 0 && to > from);
        return code.substring(from, to);
    }

    /** Comments may name the old screens (to say they are gone); only code counts. */
    static String withoutComments(String code)
    {
        return code.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\\n]*", "");
    }

    static String read(Path path) throws IOException
    {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
