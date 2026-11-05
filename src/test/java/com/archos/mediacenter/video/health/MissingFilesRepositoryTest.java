package com.archos.mediacenter.video.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import android.app.Application;
import android.net.Uri;
import com.archos.mediacenter.video.leanback.LibraryAttentionCount;
import com.archos.mediaprovider.video.MissingMediaActions.StoredFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class MissingFilesRepositoryTest
{
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void rowsUseLibraryTitlesAndSameIdentityUnionAsAttentionCount()
    {
        List<StoredFile> rows =
            List.of(new StoredFile(1, "smb://nas/Films/Old.mkv", 12), new StoredFile(2, "smb://nas/Films/Two.mkv", 14));
        Map<String, Object> markers = Map.of("media:1", "smb://nas/Films/Old.mkv", "missing_since:1", 123L,
                                             "incorrect:1", true, "media:999", "smb://nas/orphan.mkv");
        MissingFilesRepository.Inventory inventory = MissingFilesRepository.build(
            rows, Map.of(1L, "Library title", 2L, "Two"), Set.of(1L, 2L), markers,
            List.of(new MissingFilesRepository.Source(Uri.parse("smb://nas/Films"), "Film shelf")));
        assertEquals(LibraryAttentionCount.count(Set.of(1L, 2L), Set.of(1L, 2L), markers).count,
                     inventory.attention.count);
        assertEquals(2, inventory.attention.titles);
        assertEquals(1, inventory.missing.size());
        MissingFilesRepository.MissingFile file = inventory.missing.get(0);
        assertEquals("Library title", file.title);
        assertEquals("Film shelf", file.sourceName);
        assertEquals("/Films", file.folder);
        assertEquals("Old.mkv", file.oldFileName);
        assertEquals(123, file.sinceUtcMs);
        assertThrows(UnsupportedOperationException.class, inventory.missing::clear);
    }

    @Test public void deletedExcludedStaleAndOfflineRowsDoNotMasqueradeAsMissingFiles()
    {
        List<StoredFile> rows =
            List.of(new StoredFile(1, "smb://nas/Films/New.mkv", 12),
                    new StoredFile(2, "smb://offline/Films/Two.mkv", 14), new StoredFile(3, "/excluded.mkv", 1));
        Map<String, Object> markers =
            Map.of("media:1", "smb://nas/Films/Old.mkv", "media:2", "smb://offline/Films/Two.mkv",
                   "offline:smb://offline/Films", 123L, "problem:smb://offline/Films", 123L, "media:3",
                   "file:///excluded.mkv", "media:999", "file:///deleted.mkv");
        MissingFilesRepository.Inventory inventory =
            MissingFilesRepository.build(rows, Map.of(1L, "One", 2L, "Two"), Set.of(), markers, List.of());
        assertEquals(0, inventory.missing.size());
        assertEquals(1, inventory.attention.titles);
        assertEquals(1, inventory.attention.sources);
        assertEquals(2, inventory.attention.count);
    }

    @Test public void completeRealFileTreeMustProveNameAndSizeUniqueWithinTheSameSource() throws Exception
    {
        Path root = temporary.getRoot().toPath();
        Path folder = Files.createDirectory(root.resolve("Moved"));
        Path match = Files.write(folder.resolve("Old.mkv"), new byte[] {1, 2, 3});
        Files.write(root.resolve("Wrong-size.mkv"), new byte[] {1});
        MissingFileSearch.Listing listing = location ->
        {
            List<MissingFileSearch.FileEntry> result = new ArrayList<>();
            try (var entries = Files.list(Path.of(location.getPath())))
            {
                for (Path entry : entries.toList())
                    result.add(new MissingFileSearch.FileEntry(Uri.fromFile(entry.toFile()),
                                                               entry.getFileName().toString(), Files.size(entry),
                                                               Files.isDirectory(entry)));
            }
            return result;
        };
        Uri source = Uri.fromFile(root.toFile());
        assertEquals(Uri.fromFile(match.toFile()),
                     MissingFileSearch.unique(source, "Old.mkv", 3, listing, () -> false));
        Files.write(root.resolve("Old.mkv"), new byte[] {1, 2, 3});
        assertNull(MissingFileSearch.unique(source, "Old.mkv", 3, listing, () -> false));
        assertNull(MissingFileSearch.unique(source, "Old.mkv", 10, listing, () -> false));
        assertThrows(IOException.class, () -> MissingFileSearch.unique(source, "Old.mkv", 3, listing, () -> true));
    }

    @Test public void incompleteListingOrEscapedSourceNeverProducesARecoveryCandidate() throws Exception
    {
        Uri root = Uri.parse("smb://nas/Films");
        assertThrows(IOException.class,
                     () -> MissingFileSearch.unique(root, "Old.mkv", 3, folder -> null, () -> false));
        assertThrows(IOException.class,
                     ()
                         -> MissingFileSearch.unique(root, "Old.mkv", 3,
                                                     folder
                                                     -> List.of(new MissingFileSearch.FileEntry(
                                                         Uri.parse("smb://other/Films/Old.mkv"), "Old.mkv", 3, false)),
                                                     () -> false));
        assertThrows(IOException.class,
                     () -> MissingFileSearch.unique(root, "Old.mkv", -1, folder -> List.of(), () -> false));
    }
    @Test public void completeProviderReadPublishesOneInventoryAndFailureRetainsIt() throws Exception
    {
        android.content.Context context = org.robolectric.RuntimeEnvironment.getApplication();
        java.io.File file = com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(context);
        file.getParentFile().mkdirs();
        try (android.database.sqlite.SQLiteDatabase database =
                 android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(file, null))
        {
            database.execSQL("CREATE TABLE files (_id INTEGER,_data TEXT,_size INTEGER)");
            database.execSQL("INSERT INTO files VALUES (44,'smb://nas/Films/Old.mkv',123)");
            context.getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH, 0)
                .edit()
                .putString("media:44", "smb://nas/Films/Old.mkv")
                .commit();
            MissingFilesRepository.Inventory loaded =
                MissingFilesRepository.refresh(context, Map.of(44L, "Library title"), Set.of());
            assertEquals(1, loaded.missing.size());
            assertEquals(1, loaded.attention.count);
            assertEquals(loaded, MissingFilesRepository.current());
            database.execSQL("DROP TABLE files");
            assertThrows(Exception.class,
                         () -> MissingFilesRepository.refresh(context, Map.of(44L, "Library title"), Set.of()));
            assertEquals(loaded, MissingFilesRepository.current());
        }
    }
}
