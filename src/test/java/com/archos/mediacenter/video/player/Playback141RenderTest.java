package com.archos.mediacenter.video.player;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Native Android view render over a labelled synthetic scene, not hardware playback evidence. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Playback141RenderTest {
    @Test
    public void actualSidePanelRendersThreeTabsOverTheFilm() throws Exception {
        var host = Robolectric.buildActivity(PreviewPlaybackMenusTest.TopNavigationTestHost.class).setup().visible();
        try {
            Activity activity = host.get();
            FrameLayout scene = new FrameLayout(activity);
            scene.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {0xff243b4b, 0xff0b121a, 0xff4b3d30}));
            TextView label = new TextView(activity);
            label.setText("SUPERNOVA · synthetic playback scene\nNative UI render · physical Shield QA pending");
            label.setTextColor(0xffcad4dc);
            label.setTextSize(16);
            label.setPadding(32, 32, 0, 0);
            scene.addView(label);
            activity.setContentView(scene);
            scene.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                          View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
            scene.layout(0, 0, 960, 540);
            TVMenu subtitles = new TVMenu(activity);
            subtitles.createAndAddTVMenuItem("None", true, false);
            TVMenuItem track = subtitles.createAndAddTVMenuItem("English · embedded text", true, true);
            track.setTag(R.id.preview_track_language, "en");
            subtitles.createAndAddTVMenuItem("Delay", false, false);
            subtitles.createAndAddTVMenuItem(activity.getString(R.string.menu_player_settings), false, false);
            subtitles.createAndAddTVMenuItem(activity.getString(R.string.get_subtitles_online), false, false);
            subtitles.createAndAddTVMenuItem(activity.getString(R.string.get_subtitles_on_drive), false, false);
            TVMenu audio = new TVMenu(activity);
            audio.createAndAddTVMenuItem("English · AAC 5.1", true, true).setTag(R.id.preview_track_language, "en-GB");
            audio.createAndAddTVSwitchableMenuItem("Audio Boost", false);
            audio.createAndAddTVSwitchableMenuItem("Night Mode", false);
            audio.createAndAddTVMenuItem("Audio delay", false, false);
            audio.createAndAddTVMenuItem(activity.getString(R.string.player_pref_audio_speed_title), false, false);
            TVMenu format = new TVMenu(activity);
            format.setItems(R.array.pref_format_mode_entries, 0, true);
            TVMenu mode = new TVMenu(activity);
            mode.createAndAddTVMenuItem("Single", true, true);
            mode.createAndAddTVMenuItem("Binge watching", true, false);
            List<TVCardView> cards = new ArrayList<>();
            int[] titles = {R.string.menu_subtitles, R.string.menu_audio, R.string.pref_format_mode_title,
                            R.string.pref_play_mode_title};
            TVMenu[] menus = {subtitles, audio, format, mode};
            for (int i = 0; i < titles.length; i++) {
                TVCardView card = mock(TVCardView.class);
                when(card.previewTitle()).thenReturn(activity.getString(titles[i]));
                when(card.previewMenu()).thenReturn(menus[i]);
                cards.add(card);
            }
            TVMenuAdapter adapter = mock(TVMenuAdapter.class);
            when(adapter.previewCards()).thenReturn(cards);
            PreviewPlaybackMenus.show(activity, adapter, null);
            Dialog dialog = org.robolectric.shadows.ShadowDialog.getLatestDialog();
            // Approved side panel (owner, 2 Nov 2026): three tabs, one third wide, top-right, fitted height.
            for (int tab : new int[] {PlaybackSubtitlePanel.TAB_SUBTITLES, PlaybackSubtitlePanel.TAB_AUDIO,
                                      PlaybackSubtitlePanel.TAB_PLAYBACK}) {
                View button = dialog.getWindow().getDecorView().findViewWithTag("playback.tab:" + tab);
                assertTrue(button.requestFocus());
                button.performClick();
                View decor = dialog.getWindow().getDecorView();
                int width = dialog.getWindow().getAttributes().width;
                decor.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                              View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.AT_MOST));
                decor.layout(0, 0, decor.getMeasuredWidth(), decor.getMeasuredHeight());
                Shadows.shadowOf(Looper.getMainLooper()).idle();
                int height = decor.getHeight();
                assertEquals("One third of the screen wide", 320, width);
                assertTrue("Fitted inside the 20px margins: " + height, height <= 540 - 40);
                assertNull("No fourth tab", decor.findViewWithTag("playback.tab:3"));
                Bitmap bitmap = Bitmap.createBitmap(960, 540, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                scene.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                              View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
                scene.layout(0, 0, 960, 540);
                scene.draw(canvas);
                canvas.save();
                canvas.translate(960 - width - 20, 20);
                decor.draw(canvas);
                canvas.restore();
                File out =
                    new File("build/reports/preview-ui/0152-playback-side-panel-tab-" + tab + ".png");
                out.getParentFile().mkdirs();
                try (FileOutputStream stream = new FileOutputStream(out)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                }
                bitmap.recycle();
            }
        } finally {
            PreviewPlaybackMenus.close();
            host.pause().stop().destroy();
        }
    }
}
