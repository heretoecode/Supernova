package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import androidx.preference.*;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.filecorelibrary.ListingEngine;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.settings.PreviewSettings;
import com.archos.mediaprovider.video.Blacklist;
import com.archos.mediaprovider.video.VideoStoreImportImpl;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowLooper;

/** Checks settings reachability, file filters, committed history, keyboard actions and responsive UI bounds. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextQaReliabilityTest {
    @Before
    public void offline() {
        PreviewAsyncFixtures.offlineTransport();
    }

    @After
    public void drain() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    public static class Settings extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle state, String key) {
            setPreferencesFromResource(R.xml.preferences_video, key);
            PreviewSettings.organise(this);
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater i, ViewGroup p, Bundle s) {
            return PreviewSettings.grid(requireContext());
        }

        @Override
        protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen root) {
            return PreviewSettings.adapter(root);
        }

        @Override
        public void onViewCreated(View v, Bundle s) {
            super.onViewCreated(v, s);
            PreviewSettings.sidebar(this);
        }
    }

    @Test
    public void everyRealSettingsCategoryAndDiagnosticsRemainReachable() throws Exception {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            host.get().setTheme(R.style.MyLeanbackTheme);
            Settings settings = new Settings();
            host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content, settings).commitNow();
            View root = settings.requireView();
            PreviewPagesTest.layout(root);
            // Diagnostics always record in full, so there is no level choice any more.
            assertNull(settings.findPreference(Diagnostics.LEVEL));
            assertTrue(Diagnostics.defaultEnabled());
            for (String label : new String[] {
                "Playback", "Video", "Audio", "Subtitles", "Library & Metadata", "Home",
                     "Appearance", "Streaming", "Network", "Integrations", "Advanced", "About"
            }) {
                View row = root.findViewWithTag("semantic:settings:category:" + label);
                assertNotNull(label, row);
                if (label.equals("Playback"))
                    assertTrue(row.requestFocus());
                else
                    root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                PreviewPagesTest.layout(root);
                assertSame(label, row, root.findFocus());
            }
            View advanced = root.findViewWithTag("semantic:settings:category:Advanced");
            advanced.requestFocus();
            advanced.performClick();
            PreviewPagesTest.layout(root);
            View diagnostics = root.findViewWithTag("semantic:settings:section:preview_diagnostics_group");
            assertNotNull(diagnostics);
            diagnostics.requestFocus();
            PreviewPagesTest.layout(root);
            assertTrue(settings.findPreference("export_diagnostic_report").isVisible());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void allFilesUsesOneFilterForBothFilesAndHiddenDirectories() {
        class Filter extends ListingEngine {
            Filter() {
                super(RuntimeEnvironment.getApplication());
            }

            public void start() {
            }

            public void abort() {
            }

            boolean file(String name) {
                return keepFile(name);
            }

            boolean directory(String name) {
                return keepDirectory(name);
            }
        }

        Filter filter = new Filter();
        filter.setFilter(new String[] {
            "video/", "application/x-bittorrent"
        }, null);
        filter.setKeepHiddenFiles(false);
        assertTrue(filter.file("Film.MKV"));
        assertFalse(filter.file("notes.txt"));
        assertFalse(filter.file(".private.mkv"));
        assertFalse(filter.directory(".hidden"));
        assertTrue(filter.directory("Movies"));
        filter.setFilter(null, null);
        filter.setKeepHiddenFiles(true);
        assertTrue(filter.file("notes.txt"));
        assertTrue(filter.file(".private.mkv"));
        assertTrue(filter.directory(".hidden"));
        filter.setFilter(new String[] {
            "video/", "application/x-bittorrent"
        }, null);
        filter.setKeepHiddenFiles(false);
        assertFalse(filter.file("notes.txt"));
        assertTrue(filter.file("Film.MKV"));
    }

    @Test
    public void historyBroadcastFollowsCommittedEpisodeProgress() throws Exception {
        Context c = RuntimeEnvironment.getApplication();
        c.deleteDatabase(ViewingHistory.DATABASE);
        PreferenceManager.getDefaultSharedPreferences(c).edit().clear().commit();
        final int[] observed = {
            0
        };
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                assertEquals(5000, ViewingHistory.read(context).get("tmdb:tv:123:2:1").position);
                observed[0]++;
            }
        };
        c.registerReceiver(receiver, new IntentFilter(c.getPackageName() + ".VIEWING_STATE_CHANGED"));
        try {
            VideoDbInfo info =
                new VideoDbInfo(
                    Uri.parse("file:///storage/TV/Vigil/S02E01.mkv"));
            info.isShow = true;
            info.scraperShowId = "123";
            info.scraperSeasonNr = 2;
            info.scraperEpisodeNr = 1;
            info.duration = 100000;
            info.resume = 5000;
            info.lastTimePlayed = 100;
            ViewingHistory.record(c, info, false);
            ViewingHistory.awaitWrites();
            ShadowLooper.idleMainLooper();
            assertEquals(1, observed[0]);
        } finally {
            c.unregisterReceiver(receiver);
        }
    }

    @Test
    public void keyboardDoneEditsFieldAndCancelDoesNotCommitPage() {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            EditText input = new KeyboardInput(host.get());
            input.setText("Name");
            input.setSelection(4);
            PreviewKeyboard keyboard = new PreviewKeyboard(host.get(), input, null);
            host.get().setContentView(keyboard);
            final int[] committed = {
                0
            };
            keyboard.setActions(() -> committed[0]++, () -> {
            });
            PreviewPagesTest.layout(keyboard);
            keyboard.focusLastKey();
            assertEquals("T", keyboard.focusedKey());
            assertNotNull(keyboard.findViewWithTag("semantic:keyboard:Space"));
            keyboard.findViewWithTag("semantic:keyboard:Done").performClick();
            assertEquals(1, committed[0]);
            keyboard.findViewWithTag("semantic:keyboard:Clear").performClick();
            assertEquals("", input.getText().toString());
            assertEquals(1, committed[0]);
            assertTrue(input.getFocusables(View.FOCUS_DOWN).isEmpty());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void responsiveTokensRespectLogicalViewportAndBoundDialogs() {
        var c = RuntimeEnvironment.getApplication();
        assertEquals(6, TvLayout.gridColumns(c));
        assertEquals(1f, TvLayout.scale(c), .001f);
        assertTrue(TvLayout.dialogWidth(c) < TvLayout.safeWidth(c));
        assertEquals(44, PreviewDialog.dp(c, 44));
    }

    @Test(timeout = 20000)
    public void excludedFullPageCannotStarveLaterDriveRows() throws Exception {
        Context c = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(c)
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .putBoolean("supernova_library_policy_initialized", true)
            .putStringSet("supernova_library_roots", Set.of("file:///storage/drive-b"))
            .commit();
        ContentResolver resolver = Mockito.mock(ContentResolver.class);
        final List<Long> pages = new ArrayList<>();
        Mockito
            .when(resolver.query(ArgumentMatchers.any(Uri.class),
                ArgumentMatchers.any(String[].class),
                ArgumentMatchers.any(Bundle.class), ArgumentMatchers.isNull()))
            .thenAnswer(call -> {
                String selection = ((Bundle) call.getArgument(2))
                                       .getString(ContentResolver.QUERY_ARG_SQL_SELECTION);
                Matcher match = Pattern.compile("_id > (-?[0-9]+)").matcher(selection);
                long after = match.find() ? Long.parseLong(match.group(1)) : -1;
                pages.add(after);
                if (pages.size() > 4)
                    throw new AssertionError("Unscanned excluded page was revisited");
                MatrixCursor rows =
                    new MatrixCursor(new String[] {
                        "_id", "_data", "scraper_id"
                    });
                for (long id = Math.max(1, after + 1); id <= Math.min(2502, Math.max(1, after + 1) + 2499); id++)
                    rows.addRow(new Object[] {
                        id,
                        "file:///storage/drive-" + (id <= 2500 ? "a" : "b")
                            + (id <= 2500 ? "/excluded.mkv" : "/sentinel.txt"),
                        0
                    });
                return rows;
            });
        VideoStoreImportImpl importer = Mockito.mock(
            VideoStoreImportImpl.class, Mockito.CALLS_REAL_METHODS);
        Method scan = VideoStoreImportImpl.class.getDeclaredMethod(
            "doScan", ContentResolver.class, Context.class,
            Blacklist.class);
        scan.setAccessible(true);
        scan.invoke(importer, resolver, c, Mockito.mock(Blacklist.class));
        ArgumentCaptor<ArrayList<ContentProviderOperation>> batches =
            ArgumentCaptor.forClass(ArrayList.class);
        Mockito.verify(resolver, Mockito.times(2))
            .applyBatch(ArgumentMatchers.anyString(), batches.capture());
        assertEquals(0, batches.getAllValues().get(0).size());
        assertEquals(2, batches.getAllValues().get(1).size());
        assertEquals(List.of(-1L, 2500L), pages);
        Mockito.verify(resolver, Mockito.never())
            .delete(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }
}
