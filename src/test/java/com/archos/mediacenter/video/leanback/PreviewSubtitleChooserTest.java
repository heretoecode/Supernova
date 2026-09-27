package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.content.Intent;
import android.os.Looper;
import android.view.View;
import java.util.*;
import java.util.concurrent.Executor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSubtitleChooserTest {
    static final String PATH="file:///media/example.en.srt";
    static class Backend implements PreviewSubtitleChooser.Backend {
        List<PreviewSubtitleChooser.Entry> files=new ArrayList<>(Collections.singletonList(entry(true,true)));
        int associations,deletions;boolean fail;
        public List<PreviewSubtitleChooser.Entry> load(){if(fail)throw new IllegalStateException();return new ArrayList<>(files);}
        public String associate(PreviewSubtitleChooser.Entry e){associations++;return PATH;}
        public boolean delete(PreviewSubtitleChooser.Entry e){deletions++;files.clear();return true;}
    }
    static PreviewSubtitleChooser.Entry entry(boolean associated,boolean writable){return new PreviewSubtitleChooser.Entry(PATH,"example.en.srt","Media folder","2 KiB",0,associated,writable);}
    static Activity host(boolean playback){Activity host=Robolectric.buildActivity(Activity.class).setup().get();host.setIntent(new Intent().putExtra(PreviewSubtitleChooser.ALLOW_SELECTION,playback).putExtra(PreviewSubtitleChooser.ACTIVE_PATH,PATH));return host;}
    static void idle(){Shadows.shadowOf(Looper.getMainLooper()).idle();}
    static Dialog latest(){return ShadowDialog.getLatestDialog();}
    static View row(Dialog dialog,int index){return (View)dialog.getWindow().getDecorView().findViewWithTag("preview-label:"+index).getParent();}
    @Test public void explicitUseReturnsExactPathAndActiveStateWithoutFileMutation(){
        Activity host=host(true);Backend backend=new Backend();PreviewSubtitleChooser chooser=new PreviewSubtitleChooser(host,backend,Runnable::run);
        chooser.start();idle();Dialog choices=latest();assertTrue(row(choices,1).getContentDescription().toString().contains("Active"));
        row(choices,1).performClick();Dialog actions=latest();row(actions,0).performClick();
        assertTrue(host.isFinishing());assertEquals(Activity.RESULT_OK,Shadows.shadowOf(host).getResultCode());
        assertEquals(PATH,Shadows.shadowOf(host).getResultIntent().getStringExtra(PreviewSubtitleChooser.SELECTED_PATH));
        assertEquals(0,backend.deletions);assertEquals(0,backend.associations);chooser.close();
    }
    @Test public void readOnlyFileDoesNotOfferPlaybackFromDetailsOrEnableDeletion(){
        Activity host=host(false);Backend backend=new Backend();backend.files=Collections.singletonList(entry(true,false));
        PreviewSubtitleChooser chooser=new PreviewSubtitleChooser(host,backend,Runnable::run);chooser.start();idle();row(latest(),1).performClick();Dialog actions=latest();
        assertNull(PreviewPagesTest.findText(actions.getWindow().getDecorView(),"Use Subtitle"));
        assertFalse(row(actions,1).isEnabled());assertEquals(0,backend.deletions);chooser.close();
    }
    @Test public void associationRequiresAcceptanceAndBackLeavesOriginalChoices(){
        Activity host=host(true);Backend backend=new Backend();backend.files=Collections.singletonList(entry(false,true));
        PreviewSubtitleChooser chooser=new PreviewSubtitleChooser(host,backend,Runnable::run);chooser.start();idle();Dialog choices=latest();row(choices,1).performClick();Dialog actions=latest();
        row(actions,0).performClick();Dialog review=latest();assertEquals(0,backend.associations);review.onBackPressed();idle();
        assertTrue(choices.isShowing());assertTrue(actions.isShowing());assertEquals(0,backend.associations);
        row(actions,0).performClick();View accept=exact(latest().getWindow().getDecorView(),"Associate");assertNotNull(accept);assertTrue(accept.isClickable());accept.performClick();idle();
        assertEquals(1,backend.associations);assertEquals(PATH,Shadows.shadowOf(host).getResultIntent().getStringExtra(PreviewSubtitleChooser.SELECTED_PATH));chooser.close();
    }
    @Test public void cancelledLoadCannotDisplayLateChoices(){
        Activity host=host(true);Backend backend=new Backend();List<Runnable> queue=new ArrayList<>();
        PreviewSubtitleChooser chooser=new PreviewSubtitleChooser(host,backend,queue::add);chooser.start();Dialog progress=latest();progress.onBackPressed();idle();assertTrue(host.isFinishing());
        chooser.close();queue.get(0).run();idle();assertFalse(progress.isShowing());assertSame(progress,latest());
    }
    @Test public void failedListingIsVisibleAndNeverReportsSuccess(){
        Activity host=host(true);Backend backend=new Backend();backend.fail=true;
        PreviewSubtitleChooser chooser=new PreviewSubtitleChooser(host,backend,Runnable::run);chooser.start();idle();
        assertNotNull(PreviewPagesTest.findText(latest().getWindow().getDecorView(),"Subtitle files could not be listed. Check that the source is available."));
        assertEquals(Activity.RESULT_CANCELED,Shadows.shadowOf(host).getResultCode());assertFalse(host.isFinishing());chooser.close();
    }
    @Test public void localPathsCompareAcrossFileUriButRemoteIdentityIsNotGuessed(){
        assertTrue(PreviewSubtitleChooser.sameFile(PATH,"/media/example.en.srt"));
        assertFalse(PreviewSubtitleChooser.sameFile(null,null));
        assertFalse(PreviewSubtitleChooser.sameFile("https://a/file.srt","https://b/file.srt"));
    }
    private static View exact(View view,String label){if(view instanceof android.widget.TextView&&label.contentEquals(((android.widget.TextView)view).getText()))return view;if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++){View result=exact(((android.view.ViewGroup)view).getChildAt(i),label);if(result!=null)return result;}return null;}
}
