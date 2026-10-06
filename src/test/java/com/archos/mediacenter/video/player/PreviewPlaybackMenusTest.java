package com.archos.mediacenter.video.player;

import android.app.*;
import android.view.View;
import android.widget.Button;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.util.Collections;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real menu/dialog and track callbacks; no native playback service or media decoding is started. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewPlaybackMenusTest {
    @After public void close(){PreviewPlaybackMenus.close();}
    @Test public void fullSettingsAreAbsentFromMoreAndSubtitlePanels(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();
        TVMenu menu=new TVMenu(host);menu.createAndAddTVMenuItem("English",true,true);
        menu.createAndAddTVMenuItem(host.getString(R.string.preferences),true,false);
        TVCardView subtitles=mock(TVCardView.class),settings=mock(TVCardView.class);
        when(subtitles.previewTitle()).thenReturn(host.getString(R.string.menu_subtitles));when(subtitles.previewMenu()).thenReturn(menu);
        when(settings.previewTitle()).thenReturn(host.getString(R.string.preferences));when(settings.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(java.util.Arrays.asList(subtitles,settings));
        PreviewPlaybackMenus.show(host,adapter,null);Dialog more=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        assertFalse(allText(more.getWindow().getDecorView()).contains("Supernova Settings"));
        assertFalse(allText(more.getWindow().getDecorView()).contains(host.getString(R.string.preferences)));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_subtitles));Dialog tracks=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        assertTrue(allText(tracks.getWindow().getDecorView()).contains("English"));assertFalse(allText(tracks.getWindow().getDecorView()).contains("Subtitle Settings"));
        assertFalse(allText(tracks.getWindow().getDecorView()).contains(host.getString(R.string.preferences)));host.finish();
    }
    private String allText(View view){String result=view instanceof android.widget.TextView?((android.widget.TextView)view).getText().toString()+"\n":"";if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++)result+=allText(((android.view.ViewGroup)view).getChildAt(i));return result;}
    @Test public void directSpeedAdjustmentBackReturnsToExactHudOpener(){
        Activity host=Robolectric.buildActivity(TopNavigationTestHost.class).setup().visible().get();
        Button opener=new Button(host);opener.setText("Speed");opener.setFocusableInTouchMode(true);host.setContentView(opener);opener.requestFocus();Shadows.shadowOf(host).setCurrentFocus(opener);
        TVMenu menu=new TVMenu(host);String title=host.getString(R.string.player_pref_audio_speed_title);
        menu.createAndAddTVMenuItem(title,true,false).setOnClickListener(v->{
            android.view.ViewGroup container=(android.view.ViewGroup)android.view.LayoutInflater.from(host).inflate(R.layout.card_dialog_layout,null);
            TVCardDialog adjustment=container.findViewById(R.id.card_view);adjustment.setText(title);
            TVMenu controls=new TVMenu(host);controls.addView(android.view.LayoutInflater.from(host).inflate(R.layout.audio_speed_tv_picker,controls,false));adjustment.addOtherView(controls);
            PreviewPlaybackMenus.showNested(host,adjustment);
        });
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_audio));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,title);Dialog adjustment=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(adjustment.isShowing());
        assertTrue(PreviewPlaybackMenus.back());Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        assertFalse(adjustment.isShowing());assertFalse("Direct Speed must not return through More",PreviewPlaybackMenus.isShowing());assertTrue(opener.hasFocus());host.finish();
    }
    @Test public void speedAndAudioDelayShareCompactBoundsAndRetainNativeDismissCallbacks(){
        Activity host=Robolectric.buildActivity(TopNavigationTestHost.class).setup().get();
        int[] size=null;java.util.concurrent.atomic.AtomicInteger dismissed=new java.util.concurrent.atomic.AtomicInteger();
        for(int layout:new int[]{R.layout.audio_speed_tv_picker,R.layout.audio_delay_tv_picker}){
            android.view.ViewGroup container=(android.view.ViewGroup)android.view.LayoutInflater.from(host).inflate(R.layout.card_dialog_layout,null);
            TVCardDialog card=container.findViewById(R.id.card_view);card.setText("Adjustment");
            TVMenu menu=new TVMenu(host);View picker=android.view.LayoutInflater.from(host).inflate(layout,menu,false);menu.addView(picker);
            menu.createAndAddTVSwitchableMenuItem("Keep setting",false);card.addOtherView(menu);card.setOnDialogResultListener(code->dismissed.incrementAndGet());
            PreviewPlaybackMenus.showNested(host,card);Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
            android.view.WindowManager.LayoutParams bounds=dialog.getWindow().getAttributes();
            if(size==null)size=new int[]{bounds.width,bounds.height};else{assertEquals(size[0],bounds.width);assertEquals(size[1],bounds.height);}
            assertTrue(bounds.height<270);assertTrue(PreviewPlaybackMenus.back());Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        }
        assertEquals(2,dismissed.get());host.finish();
    }
    public static class TopNavigationTestHost extends Activity {
        @Override public void onCreate(android.os.Bundle saved){setTheme(R.style.MyLeanbackTheme);super.onCreate(saved);}
    }
    @Test public void nativeTrackRefreshRebindsCallbacksAndTicksWithoutReplacingTheOpenDialog(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();TVMenu menu=new TVMenu(host);
        java.util.concurrent.atomic.AtomicInteger oldClicks=new java.util.concurrent.atomic.AtomicInteger(),newClicks=new java.util.concurrent.atomic.AtomicInteger();
        menu.createAndAddTVMenuItem("English",true,true).setOnClickListener(v->oldClicks.incrementAndGet());menu.createAndAddTVMenuItem("French",true,false);
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_audio));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_audio));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        View english=(View)dialog.getWindow().getDecorView().findViewWithTag("preview-label:1").getParent();english.requestFocus();
        menu.clean();menu.createAndAddTVMenuItem("English",true,false).setOnClickListener(v->newClicks.incrementAndGet());menu.createAndAddTVMenuItem("French",true,true);
        PreviewPlaybackMenus.refresh(host,card);
        assertSame(dialog,org.robolectric.shadows.ShadowDialog.getLatestDialog());assertTrue(dialog.isShowing());assertTrue(english.hasFocus());
        assertEquals(View.VISIBLE,dialog.getWindow().getDecorView().findViewWithTag("preview-check:2").getVisibility());
        english.performClick();assertEquals(0,oldClicks.get());assertEquals(1,newClicks.get());
    }
    @Test public void newlyDiscoveredTrackPreservesTheFocusedExistingChoice(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();TVMenu menu=new TVMenu(host);menu.createAndAddTVMenuItem("English",true,true);menu.createAndAddTVMenuItem("French",true,false);
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_subtitles));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_subtitles));Dialog before=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        ((View)before.getWindow().getDecorView().findViewWithTag("preview-label:2").getParent()).requestFocus();
        menu.clean();menu.createAndAddTVMenuItem("German",true,false);menu.createAndAddTVMenuItem("English",true,true);menu.createAndAddTVMenuItem("French",true,false);
        PreviewPlaybackMenus.refresh(host,card);Dialog after=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertFalse(before.isShowing());assertTrue(after.isShowing());
        assertTrue(((View)after.getWindow().getDecorView().findViewWithTag("preview-label:3").getParent()).hasFocus());
        PreviewPlaybackMenus.back();assertFalse(after.isShowing());assertFalse(PreviewPlaybackMenus.isShowing());
    }
    @Test public void subtitleTrackUsesSharedLanguageIconWithoutChangingItsSelection(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();TVMenu menu=new TVMenu(host);
        TVMenuItem track=menu.createAndAddTVMenuItem("English",true,true);track.setTag(R.id.preview_track_language,"eng");
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_subtitles));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_subtitles));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        View label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:1");android.view.ViewGroup row=(android.view.ViewGroup)label.getParent();
        assertTrue(((android.widget.ImageView)row.getChildAt(0)).getDrawable() instanceof com.archos.mediacenter.video.leanback.PreviewLanguageIcon);
        assertTrue(track.isChecked());assertEquals(View.VISIBLE,dialog.getWindow().getDecorView().findViewWithTag("preview-check:1").getVisibility());
    }
    @Test public void subtitleDownloadUsesDesignedLabelAndRetainsNativeAction(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();TVMenu menu=new TVMenu(host);
        TVMenuItem download=menu.createAndAddTVMenuItem(host.getString(R.string.get_subtitles_online),true,false);
        java.util.concurrent.atomic.AtomicInteger invoked=new java.util.concurrent.atomic.AtomicInteger();download.setOnClickListener(v->invoked.incrementAndGet());
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_subtitles));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_subtitles));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        android.widget.TextView label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:1");assertEquals("Download Subtitles",label.getText().toString());
        ((View)label.getParent()).performClick();assertEquals(1,invoked.get());
    }
    @Test public void audioTrackChangeStaysOpenAndBackReturnsToHudInsteadOfMore(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();Button opener=new Button(host);opener.setText("Audio");opener.setFocusableInTouchMode(true);host.setContentView(opener);opener.requestFocus();Shadows.shadowOf(host).setCurrentFocus(opener);
        TVMenu menu=new TVMenu(host);TVMenuItem english=menu.createAndAddTVMenuItem("English",true,true),french=menu.createAndAddTVMenuItem("French",true,false);
        english.setOnClickListener(v->{english.setChecked(true);french.setChecked(false);});french.setOnClickListener(v->{english.setChecked(false);french.setChecked(true);});
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_audio));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_audio));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        // A group heading precedes the two real track choices.
        View label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:2");assertNotNull(label);((View)label.getParent()).performClick();
        assertTrue(dialog.isShowing());assertTrue(french.isChecked());assertFalse(english.isChecked());
        assertEquals(View.VISIBLE,dialog.getWindow().getDecorView().findViewWithTag("preview-check:2").getVisibility());
        assertTrue(PreviewPlaybackMenus.back());assertFalse(dialog.isShowing());assertFalse("Back must not open More",PreviewPlaybackMenus.back());assertTrue(opener.hasFocus());
    }
}
