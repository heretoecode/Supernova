package com.archos.mediacenter.video.player;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Native Android view render over a labelled synthetic scene, not hardware playback evidence. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Playback141RenderTest {
    @Test public void actualPanelRendersFourTabsAndScrollableSubtitleOptions()throws Exception{
        var host=Robolectric.buildActivity(PreviewPlaybackMenusTest.TopNavigationTestHost.class).setup().visible();try{
            Activity activity=host.get();FrameLayout scene=new FrameLayout(activity);scene.setBackground(new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TL_BR,new int[]{0xff243b4b,0xff0b121a,0xff4b3d30}));TextView label=new TextView(activity);label.setText("SUPERNOVA · synthetic playback scene\nNative UI render · physical Shield QA pending");label.setTextColor(0xffcad4dc);label.setTextSize(16);label.setPadding(32,32,0,0);scene.addView(label);activity.setContentView(scene);scene.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));scene.layout(0,0,960,540);
            TVMenu subtitles=new TVMenu(activity);subtitles.createAndAddTVMenuItem("None",true,false);TVMenuItem track=subtitles.createAndAddTVMenuItem("English · embedded text",true,true);track.setTag(R.id.preview_track_language,"en");subtitles.createAndAddTVMenuItem("Delay",false,false);subtitles.createAndAddTVMenuItem(activity.getString(R.string.menu_player_settings),false,false);subtitles.createAndAddTVMenuItem(activity.getString(R.string.get_subtitles_online),false,false);subtitles.createAndAddTVMenuItem(activity.getString(R.string.get_subtitles_on_drive),false,false);
            TVMenu audio=new TVMenu(activity);audio.createAndAddTVMenuItem("English · AAC 5.1",true,true).setTag(R.id.preview_track_language,"en-GB");audio.createAndAddTVSwitchableMenuItem("Audio Boost",false);audio.createAndAddTVSwitchableMenuItem("Night Mode",false);audio.createAndAddTVMenuItem("Audio delay",false,false);audio.createAndAddTVMenuItem(activity.getString(R.string.player_pref_audio_speed_title),false,false);
            TVMenu format=new TVMenu(activity);format.setItems(R.array.pref_format_mode_entries,0,true);TVMenu mode=new TVMenu(activity);mode.createAndAddTVMenuItem("Single",true,true);mode.createAndAddTVMenuItem("Binge watching",true,false);
            List<TVCardView> cards=new ArrayList<>();int[] titles={R.string.menu_subtitles,R.string.menu_audio,R.string.pref_format_mode_title,R.string.pref_play_mode_title};TVMenu[] menus={subtitles,audio,format,mode};for(int i=0;i<titles.length;i++){TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(activity.getString(titles[i]));when(card.previewMenu()).thenReturn(menus[i]);cards.add(card);}TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(cards);
            PreviewPlaybackMenus.show(activity,adapter,null);Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
            for(int tab=0;tab<4;tab++){
                dialog.getWindow().getDecorView().findViewWithTag("playback.tab:"+tab).requestFocus();View decor=dialog.getWindow().getDecorView();int width=dialog.getWindow().getAttributes().width,height=dialog.getWindow().getAttributes().height;decor.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));decor.layout(0,0,width,height);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                assertTrue("Two-column panel stays within the TV safe area",width<=960*.75);assertTrue("Compact panel leaves playback visible",height<=540*.75);for(int i=0;i<4;i++){TextView button=decor.findViewWithTag("playback.tab:"+i);assertNotNull(button.getLayout());assertEquals(0,button.getLayout().getEllipsisCount(0));assertTrue(button.getLayout().getLineWidth(0)<=button.getWidth()-button.getCompoundPaddingLeft()-button.getCompoundPaddingRight()+1);}
                Bitmap bitmap=Bitmap.createBitmap(960,540,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(bitmap);scene.draw(canvas);canvas.save();canvas.translate(960-width-24,(540-height)/2f);decor.draw(canvas);canvas.restore();java.io.File out=new java.io.File("build/reports/preview-ui/0141-playback-tab-"+tab+".png");out.getParentFile().mkdirs();try(java.io.FileOutputStream stream=new java.io.FileOutputStream(out)){bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);}bitmap.recycle();
            }
        }finally{PreviewPlaybackMenus.close();host.pause().stop().destroy();}
    }
}
