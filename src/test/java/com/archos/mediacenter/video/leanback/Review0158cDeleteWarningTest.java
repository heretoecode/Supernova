package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowDialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The delete warning (LIBRARY.md › Delete warnings; RULES §7b): one file, two versions of a film, a show's episodes;
 * Keep focused first, the total and the button follow the ticks, "This can't be undone." only beside a focused Delete.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0158cDeleteWarningTest
{
    private static DeleteWarning.File file(String name, String facts, long gb)
    {
        return new DeleteWarning.File(Uri.parse("smb://home-server/Films/" + name), name, facts, "Home server",
                                      gb * 1_000_000_000L);
    }

    @Test
    public void words()
    {
        DeleteWarning.File one = file("The.Glass.Orchard.2025.2160p.mkv", "2160p · HDR10", 58);
        assertEquals("Deletes The.Glass.Orchard.2025.2160p.mkv (2160p · 58.0 GB) from Home server.",
                     DeleteWarning.oneLine(one).toString());
        List<DeleteWarning.File> two = Arrays.asList(one, file("The.Glass.Orchard.2025.1080p.mkv", "1080p", 14));
        assertEquals("Deletes 2 files · 72.0 GB.", DeleteWarning.totalLine(two).toString());
        assertEquals("Delete 2 files", DeleteWarning.deleteWords(two, false));
        assertEquals("Delete file", DeleteWarning.deleteWords(Arrays.asList(one), false));
        assertEquals("", DeleteWarning.size(0));
        assertEquals("700 MB", DeleteWarning.size(700_000_000L));
    }

    @Test
    public void filesNotInLibraryAreFoundInTheShowsFolders() throws Exception
    {
        java.io.File folder = java.nio.file.Files.createTempDirectory("tidelands").toFile();
        java.io.File known = new java.io.File(folder, "Tidelands.S01E01.mkv");
        assertTrue(known.createNewFile());
        assertTrue(new java.io.File(folder, "Tidelands.S01E02.mkv").createNewFile());
        assertTrue(new java.io.File(folder, "poster.jpg").createNewFile());
        DeleteWarning.File episode = new DeleteWarning.File(Uri.fromFile(known), known.getName(), "1080p",
                                                            "This device", 2_000_000_000L);
        episode.season = 1;
        List<DeleteWarning.File> all = LibraryActions.withFilesNotInLibrary(new ArrayList<>(Arrays.asList(episode)));
        assertEquals("The other episode file, not the picture", 2, all.size());
        assertEquals(false, all.get(1).inLibrary);
        assertEquals(1, all.get(1).season);
        assertEquals(2, LibraryActions.seasonOf("Show.s02e05.1080p.mkv"));
        assertEquals(-1, LibraryActions.seasonOf("Show.Special.mkv"));
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void threeKindsRendered() throws Exception
    {
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try
        {
            FrameLayout page = new FrameLayout(host.get());
            page.setBackgroundColor(0xFF16181C);
            host.get().setContentView(page);
            List<Uri> deleted = new ArrayList<>();
            DeleteWarning.show(host.get(), "The Glass Orchard", false,
                               Arrays.asList(file("The.Glass.Orchard.2025.2160p.mkv", "2160p · HDR10 · Atmos", 58)),
                               true, (files, history, trakt, folder) -> deleted.addAll(files));
            Review0158Fixture.settle(page);
            Dialog dialog = ShadowDialog.getLatestDialog();
            View root = dialog.getWindow().getDecorView();
            assertTrue("Keep file focused first", root.findViewWithTag("delete.keep").isFocused());
            assertEquals(View.INVISIBLE, root.findViewWithTag("delete.undone").getVisibility());
            assertNotNull("Trakt's Clean Up row when connected", root.findViewWithTag("delete.cleanup.trakt"));
            Review0158Fixture.saveWithDialog(page, "0158c-delete-01-one-file");
            View delete = root.findViewWithTag("delete.confirm");
            delete.requestFocus();
            Review0158Fixture.settle(page);
            assertEquals(View.VISIBLE, root.findViewWithTag("delete.undone").getVisibility());
            Review0158Fixture.saveWithDialog(page, "0158c-delete-02-delete-focused");
            delete.performClick();
            assertEquals(1, deleted.size());

            DeleteWarning.show(host.get(), "The Glass Orchard", false,
                               Arrays.asList(file("The.Glass.Orchard.2025.2160p.mkv", "2160p · HDR10 · Atmos", 58),
                                             file("The.Glass.Orchard.2025.1080p.mkv", "1080p · SDR · 5.1", 14)),
                               false, (files, history, trakt, folder) -> {
                               });
            Review0158Fixture.settle(page);
            dialog = ShadowDialog.getLatestDialog();
            root = dialog.getWindow().getDecorView();
            assertEquals("Delete 2 files", ((TextView) root.findViewWithTag("delete.confirm")).getText().toString());
            root.findViewWithTag("delete.file:The.Glass.Orchard.2025.1080p.mkv").performClick();
            Review0158Fixture.settle(page);
            assertEquals("Delete 1 file", ((TextView) root.findViewWithTag("delete.confirm")).getText().toString());
            Review0158Fixture.saveWithDialog(page, "0158c-delete-03-versions");
            dialog.dismiss();

            List<DeleteWarning.File> episodes = new ArrayList<>();
            for (int season = 1; season <= 2; season++)
            {
                for (int number = 1; number <= 4; number++)
                {
                    DeleteWarning.File episode = file("Tidelands.S0" + season + "E0" + number + ".mkv", "1080p", 2);
                    episode.season = season;
                    episode.episode = "S" + season + " E" + number + " · The Long Tide, part " + number;
                    episodes.add(episode);
                }
            }
            DeleteWarning.File stranger = file("Tidelands.S02E05.mkv", "", 2);
            stranger.inLibrary = false;
            stranger.season = 2;
            episodes.add(stranger);
            DeleteWarning.show(host.get(), "Tidelands of the Northern Coast", true, episodes, false,
                               (files, history, trakt, folder) -> {
                               });
            Review0158Fixture.settle(page);
            root = ShadowDialog.getLatestDialog().getWindow().getDecorView();
            assertEquals("Delete 9 files", ((TextView) root.findViewWithTag("delete.confirm")).getText().toString());
            assertNotNull("A file the library doesn't know has its pill",
                          root.findViewWithTag("delete.notInLibrary"));
            Review0158Fixture.saveWithDialog(page, "0158c-delete-04-show-episodes");
        }
        finally
        {
            host.pause().stop().destroy();
        }
    }
}
