package com.archos.mediacenter.video.player;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real settings window and native callbacks; no decoder/hardware acceptance is implied. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewPlaybackMenusTest {
    Activity host;Button opener;
    @Before public void setup(){host=Robolectric.buildActivity(TopNavigationTestHost.class).setup().visible().get();opener=new Button(host);opener.setText("Settings");opener.setFocusableInTouchMode(true);host.setContentView(opener);opener.requestFocus();Shadows.shadowOf(host).setCurrentFocus(opener);}
    @After public void close(){PreviewPlaybackMenus.close();host.finish();}
    public static class TopNavigationTestHost extends Activity {@Override public void onCreate(android.os.Bundle state){setTheme(R.style.MyLeanbackTheme);super.onCreate(state);}}
    TVMenuAdapter source(String title,TVMenu menu){TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(title);when(card.previewMenu()).thenReturn(menu);TVMenuAdapter source=mock(TVMenuAdapter.class);when(source.previewCards()).thenReturn(Collections.singletonList(card));return source;}
    Dialog dialog(){return org.robolectric.shadows.ShadowDialog.getLatestDialog();}
    TextView option(String label){return dialog().getWindow().getDecorView().findViewWithTag("playback.option:"+label);}
    @Test public void exactHorizontalTabsSubtitlesFirstAndNoPlaybackDiagnosticEntries() throws Exception {
        PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_subtitles),new TVMenu(host)),null);View root=dialog().getWindow().getDecorView();
        for(int i=0;i<4;i++){TextView tab=root.findViewWithTag("playback.tab:"+i);assertEquals(new String[]{"Audio","Subtitles","Video","More"}[i],tab.getText());assertTrue(tab.getParent() instanceof LinearLayout);assertEquals(LinearLayout.HORIZONTAL,((LinearLayout)tab.getParent()).getOrientation());tab.requestFocus();assertTrue(tab.isSelected());assertNull(option("Technical Information"));assertNull(option("Report a Problem"));}
        assertTrue(PreviewPlaybackMenus.back());assertFalse(PreviewPlaybackMenus.isShowing());assertTrue(opener.hasFocus());
        for(String fieldName:new String[]{"current","owner","adapter","origin","tabs","rows","trackRows","adjustmentRows","content","scroll","restoreParent","backAction","nested","liveCard"}){
            java.lang.reflect.Field field=PreviewPlaybackMenus.class.getDeclaredField(fieldName);field.setAccessible(true);
            assertNull("Closed playback settings must release Activity-owned "+fieldName,field.get(null));
        }
    }
    @Test public void trackChangeRebuildsChecksAndKeepsSameWindow(){
        TVMenu menu=new TVMenu(host);TVMenuItem english=menu.createAndAddTVMenuItem("English",true,true),french=menu.createAndAddTVMenuItem("French",true,false);
        french.setOnClickListener(v->{english.setChecked(false);french.setChecked(true);});PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_audio),menu),host.getString(R.string.menu_audio));Dialog original=dialog();option("French").requestFocus();option("French").performClick();
        assertSame(original,dialog());assertTrue(original.isShowing());assertTrue(french.isChecked());assertFalse(english.isChecked());assertTrue(option("French").getText().toString().startsWith("✓"));assertTrue(option("French").hasFocus());
        PreviewPlaybackMenus.back();assertTrue(opener.hasFocus());
    }
    @Test public void selectingSecondIndistinguishableTrackKeepsFocusOnItsCheckedRow(){
        TVMenu menu=new TVMenu(host);TVMenuItem first=menu.createAndAddTVMenuItem("English · AAC",true,true),second=menu.createAndAddTVMenuItem("English · AAC",true,false);second.setOnClickListener(v->{first.setChecked(false);second.setChecked(true);});
        PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_audio),menu),host.getString(R.string.menu_audio));Dialog original=dialog();View firstRow=option("English · AAC");ViewGroup rows=(ViewGroup)firstRow.getParent();int position=rows.indexOfChild(firstRow);View secondRow=rows.getChildAt(position+1);assertEquals(firstRow.getTag(),secondRow.getTag());assertTrue(secondRow.requestFocus());secondRow.performClick();
        assertSame(original,dialog());assertTrue(second.isChecked());assertFalse(first.isChecked());TextView focused=(TextView)dialog().getCurrentFocus();assertNotNull(focused);assertTrue("Focus remains on the selected second track, not the first identically named track",focused.getText().toString().startsWith("✓"));
    }
    @Test public void trackRefreshUsesNewCallbacksAndPreservesFocusedTrackAfterInsertion(){
        TVMenu menu=new TVMenu(host);menu.createAndAddTVMenuItem("English",true,true);menu.createAndAddTVMenuItem("French",true,false);TVMenuAdapter source=source(host.getString(R.string.menu_audio),menu);
        PreviewPlaybackMenus.show(host,source,host.getString(R.string.menu_audio));option("French").requestFocus();Dialog original=dialog();int[] called={0};menu.clean();menu.createAndAddTVMenuItem("German",true,false);menu.createAndAddTVMenuItem("English",true,false);menu.createAndAddTVMenuItem("French",true,true).setOnClickListener(v->called[0]++);
        PreviewPlaybackMenus.refresh(host,source.previewCards().get(0));assertSame(original,dialog());assertTrue(option("French").hasFocus());assertTrue(option("French").getText().toString().startsWith("✓"));option("French").performClick();assertEquals(1,called[0]);
    }
    @Test public void subtitleAndAudioTracksUseCentralLanguageDrawable(){
        for(int kind:new int[]{R.string.menu_subtitles,R.string.menu_audio}){TVMenu menu=new TVMenu(host);TVMenuItem track=menu.createAndAddTVMenuItem("English",true,true);track.setTag(R.id.preview_track_language,"en-GB");PreviewPlaybackMenus.show(host,source(host.getString(kind),menu),host.getString(kind));assertTrue(option("English").getCompoundDrawables()[0] instanceof com.archos.mediacenter.video.leanback.PreviewLanguageIcon);assertTrue(track.isChecked());PreviewPlaybackMenus.close();}
    }
    @Test public void downloadAndLocalActionsRetainCallbacks(){
        TVMenu menu=new TVMenu(host);int[] calls={0,0};menu.createAndAddTVMenuItem(host.getString(R.string.get_subtitles_online),false,false).setOnClickListener(v->calls[0]++);menu.createAndAddTVMenuItem(host.getString(R.string.get_subtitles_on_drive),false,false).setOnClickListener(v->calls[1]++);
        PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_subtitles),menu),null);option("Download Subtitles").performClick();option("Choose Subtitles").performClick();assertArrayEquals(new int[]{1,1},calls);
    }
    @Test public void nestedAdjustmentRetainsTabsAndSavesOnBackTabChangeAndProgrammaticClose(){
        TVMenu menu=new TVMenu(host);PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_audio),menu),host.getString(R.string.menu_audio));Dialog original=dialog();int[] saves={0};
        for(int exit=0;exit<3;exit++){if(!PreviewPlaybackMenus.isShowing())PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_audio),menu),host.getString(R.string.menu_audio));
            ViewGroup container=(ViewGroup)LayoutInflater.from(host).inflate(R.layout.card_dialog_layout,null);TVCardDialog card=container.findViewById(R.id.card_view);card.setText("Adjustment");TVMenu controls=new TVMenu(host);controls.addView(LayoutInflater.from(host).inflate(R.layout.audio_speed_tv_picker,controls,false));card.addOtherView(controls);card.setOnDialogResultListener(code->saves[0]++);PreviewPlaybackMenus.showNested(host,card);
            assertSame(original,dialog());assertNotNull(dialog().getWindow().getDecorView().findViewWithTag("playback.tab:0"));if(exit==0)PreviewPlaybackMenus.back();else if(exit==1)dialog().getWindow().getDecorView().findViewWithTag("playback.tab:2").performClick();else PreviewPlaybackMenus.close();assertEquals(exit+1,saves[0]);
        }
    }
    @Test public void subtitlePreferencesStayInsidePlaybackAndPersist(){
        PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_subtitles),new TVMenu(host)),null);Dialog original=dialog();option("Subtitles by Default").performClick();assertTrue(androidx.preference.PreferenceManager.getDefaultSharedPreferences(host).getBoolean("subtitles_hide_default",false));assertFalse(option("Reading language").isEnabled());option("Download languages").performClick();assertSame(original,dialog());assertTrue(PreviewPlaybackMenus.back());assertNotNull(option("Prepare Subtitle Searches"));
    }
    @Test public void videoFormatsAreInlineWithOtherControlsInTheirOwnColumn(){
        TVMenu format=new TVMenu(host);format.createAndAddTVMenuItem("Original",true,true);format.createAndAddTVMenuItem("Fullscreen",true,false);
        TVCardView formats=mock(TVCardView.class),filters=mock(TVCardView.class);
        when(formats.previewTitle()).thenReturn(host.getString(R.string.pref_format_mode_title));when(formats.previewMenu()).thenReturn(format);
        when(filters.previewTitle()).thenReturn("Video filters");TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Arrays.asList(formats,filters));
        PreviewPlaybackMenus.show(host,adapter,null);dialog().getWindow().getDecorView().findViewWithTag("playback.tab:2").performClick();
        assertNull("No intermediate Format submenu",option(host.getString(R.string.pref_format_mode_title)));
        assertNotNull(option("Original"));assertNotNull(option("Fullscreen"));assertNotNull(option("Video filters"));
        assertNotSame(option("Original").getParent(),option("Video filters").getParent());
    }
    @Test public void audioColumnsSupportRightThenUpWithoutSearchingAnUnrelatedRoot(){
        TVMenu menu=new TVMenu(host);menu.createAndAddTVMenuItem("English",true,true);String delay=host.getString(R.string.player_pref_audio_delay_title);menu.createAndAddTVMenuItem(delay,false,false);
        PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_audio),menu),host.getString(R.string.menu_audio));
        View root=dialog().getWindow().getDecorView();int width=dialog().getWindow().getAttributes().width,height=dialog().getWindow().getAttributes().height;root.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));root.layout(0,0,width,height);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        option("English").requestFocus();option("English").dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertTrue(option(delay).hasFocus());
        option(delay).dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));assertTrue(root.findViewWithTag("playback.tab:0").hasFocus());
    }
    @Test public void moreChoicesExpandBelowParentThenCollapseAndRestoreFocus(){
        TVMenu menu=new TVMenu(host);TVMenuItem once=menu.createAndAddTVMenuItem("Once",true,true),repeat=menu.createAndAddTVMenuItem("Repeat",true,false);repeat.setOnClickListener(v->{once.setChecked(false);repeat.setChecked(true);});
        String title=host.getString(R.string.pref_play_mode_title);PreviewPlaybackMenus.show(host,source(title,menu),null);View root=dialog().getWindow().getDecorView();root.findViewWithTag("playback.tab:3").performClick();View parent=option(title);parent.requestFocus();parent.performClick();
        assertSame(parent,option(title));ViewGroup expansion=root.findViewWithTag("playback.inline");assertNotNull(expansion);assertSame(parent.getParent(),expansion.getParent());ViewGroup column=(ViewGroup)parent.getParent();assertEquals(column.indexOfChild(parent)+1,column.indexOfChild(expansion));
        option("Repeat").performClick();assertTrue(repeat.isChecked());assertNull(root.findViewWithTag("playback.inline"));assertTrue("Parent focus after inline selection/Back: "+dialog().getCurrentFocus()+" parent attached="+option(title).isAttachedToWindow(),option(title).hasFocus());
        option(title).performClick();PreviewPlaybackMenus.back();assertNull(root.findViewWithTag("playback.inline"));assertTrue("Parent focus after inline selection/Back: "+dialog().getCurrentFocus()+" parent attached="+option(title).isAttachedToWindow(),option(title).hasFocus());assertTrue(PreviewPlaybackMenus.isShowing());
    }
    @Test public void readingLanguagePreservesSiblingRowsAndReturnsToParent(){
        PreviewPlaybackMenus.show(host,source(host.getString(R.string.menu_subtitles),new TVMenu(host)),null);View parent=option("Reading language"),sibling=option("Download languages");parent.performClick();assertSame(sibling,option("Download languages"));ViewGroup inline=dialog().getWindow().getDecorView().findViewWithTag("playback.inline");assertNotNull(inline);PreviewPlaybackMenus.back();assertTrue("Reading parent after Back: "+dialog().getCurrentFocus()+" attached="+parent.isAttachedToWindow(),parent.hasFocus());assertNull(dialog().getWindow().getDecorView().findViewWithTag("playback.inline"));
    }

}
