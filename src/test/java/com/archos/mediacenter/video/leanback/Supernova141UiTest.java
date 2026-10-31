package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Rect;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import java.time.Duration;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova141UiTest {
    @Before public void setup(){PreviewAsyncFixtures.offlineTransport();androidx.preference.PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().putBoolean("supernova_onboarding_complete",true).commit();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException ignored){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
    private Snapshot source(int count){Snapshot s=new Snapshot();for(int i=1;i<=count;i++){Movie m=new Movie(i,"/fixture/"+i,"Film "+i,i,"Fixture synopsis",2026,7,"",null,100000,0,0,0,false,false,false,false,i,i,1920,1080,"AAC",null,null,null,0,1,1000,0);Entry e=new Entry(m,i,0,"Drama");e.heroEligible=true;s.movies.add(e);s.technical.add(e);s.recent.add(e);s.featured.add(e);}return s;}
    private void settle(View root){for(int i=0;i<6;i++){PreviewPagesTest.layout(root);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(Duration.ofMillis(50));}}
    @Test public void punctuationAndCapsLockUseSharedKeysAndPreserveSelection()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{LinearLayout panel=new LinearLayout(host.get());panel.setOrientation(LinearLayout.VERTICAL);KeyboardInput input=new KeyboardInput(host.get());panel.addView(input,new LinearLayout.LayoutParams(-1,KeyboardInput.compactHeight(host.get())));PreviewKeyboard keyboard=new PreviewKeyboard(host.get(),input,null);panel.addView(keyboard);host.get().setContentView(panel);settle(panel);
            for(String key:new String[]{".",",","/","\\"})panel.findViewWithTag("semantic:keyboard:"+key).performClick();assertEquals(".,/\\",input.getText().toString());
            View caps=panel.findViewWithTag("semantic:keyboard:Caps Lock");assertEquals(0,((ViewGroup)caps.getParent()).indexOfChild(caps));assertTrue(keyboard.capsLocked());assertEquals("ON",((android.widget.TextView)caps).getText().toString());caps.performClick();assertFalse(keyboard.capsLocked());assertEquals("OFF",((android.widget.TextView)caps).getText().toString());panel.findViewWithTag("semantic:keyboard:A").performClick();assertEquals(".,/\\a",input.getText().toString());caps.performClick();panel.findViewWithTag("semantic:keyboard:A").performClick();assertEquals(".,/\\aA",input.getText().toString());assertTrue(caps.isSelected());
            input.setSelection(0,2);panel.findViewWithTag("semantic:keyboard:/").performClick();assertEquals("//\\aA",input.getText().toString());assertEquals(1,input.getMaxLines());assertTrue(input.getHeight()<36);
            for(String action:new String[]{"Space","Backspace","Clear","Done","Cancel"})assertNotNull(panel.findViewWithTag("semantic:keyboard:"+action));
        }finally{host.pause().stop().destroy();}
    }
    @Test public void cachedHeroReversalsNeverRetainInnerTranslationsAndEveryInputIsAccepted()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);pages.setSnapshot(source(8));settle(pages);View info=pages.findViewWithTag("hero:info");assertNotNull(info);info.requestFocus();
            for(int i=0;i<24;i++){int key=i%2==0?KeyEvent.KEYCODE_DPAD_RIGHT:KeyEvent.KEYCODE_DPAD_LEFT;pages.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,key));pages.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,key));PreviewPagesTest.layout(pages);assertEquals(i%2==0?1:0,(int)ReflectionHelpers.getField(pages,"featuredIndex"));}
            settle(pages);View card=pages.findViewWithTag("semantic:featured.active");assertEquals(0,card.getTranslationX(),.01);assertEquals(0,card.getTranslationY(),.01);assertNotNull(pages.findViewWithTag("semantic:featured.previous"));assertNotNull(pages.findViewWithTag("semantic:featured.next"));assertEquals("hero:info",pages.findFocus().getTag());PreviewPagesTest.capture(pages,"0141-hero-after-rapid-reversals");
        }finally{host.pause().stop().destroy();}
    }
    @Test public void focusedGridAndListRowsSettleInLibraryViewportIncludingFinalRow()throws Exception{
        for(boolean listMode:new boolean[]{false,true}){var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("preview_library_1_list",listMode).commit();PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);pages.setSnapshot(source(60));pages.setTab(1);settle(pages);RecyclerView list=(RecyclerView)pages.getChildAt(0);GridLayoutManager manager=(GridLayoutManager)list.getLayoutManager();java.util.List<PreviewPages.Cell> cells=ReflectionHelpers.getField(pages,"cells");
            for(int position:new int[]{Math.min(cells.size()-1,25),cells.size()-1}){list.scrollToPosition(position);settle(pages);View item=manager.findViewByPosition(position);assertNotNull(item);assertTrue(item.requestFocus());settle(pages);item=manager.findViewByPosition(position);float centre=(list.getPaddingTop()+list.getHeight()-PreviewDialog.dp(host.get(),12))/2f;assertEquals("Focused row centre (list="+listMode+")",centre,(item.getTop()+item.getBottom())/2f,2f);assertTrue(item.hasFocus());}
        }finally{host.pause().stop().destroy();}}
    }
    @Test public void customToolbarPinsEditToRightAndSkipsFlexibleSpaceDuringRemoteTraversal()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);pages.setSnapshot(source(8));int[] edited={0};pages.configureCustomLibrary(new CustomLibraryPage(),()->edited[0]++);settle(pages);View edit=pages.findViewWithTag("custom.control.edit"),view=pages.findViewWithTag("custom.control.view"),filters=pages.findViewWithTag("custom.control.filters");assertNotNull(edit);Rect e=new Rect(),f=new Rect();assertTrue(edit.getGlobalVisibleRect(e));assertTrue(filters.getGlobalVisibleRect(f));assertTrue(e.left>f.right);assertEquals(960-PreviewDialog.dp(host.get(),31),e.right,3);view.requestFocus();view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertTrue(edit.hasFocus());edit.performClick();assertEquals(1,edited[0]);PreviewPagesTest.capture(pages,"0141-custom-toolbar");
        }finally{host.pause().stop().destroy();}
    }
    @Test @Config(qualifiers="w640dp-h360dp-land-mdpi") public void editRemainsVisibleAtSmallViewportWithLargeText()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{android.content.res.Configuration config=new android.content.res.Configuration(host.get().getResources().getConfiguration());config.fontScale=1.5f;host.get().getResources().updateConfiguration(config,host.get().getResources().getDisplayMetrics());PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);pages.setSnapshot(source(4));pages.configureCustomLibrary(new CustomLibraryPage(),()->{});
            for(int i=0;i<4;i++){pages.measure(View.MeasureSpec.makeMeasureSpec(640,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(360,View.MeasureSpec.EXACTLY));pages.layout(0,0,640,360);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(Duration.ofMillis(50));}
            View edit=pages.findViewWithTag("custom.control.edit");Rect bounds=new Rect();assertTrue(edit.getGlobalVisibleRect(bounds));assertEquals(edit.getWidth(),bounds.width());assertTrue(bounds.right<=640);assertTrue(edit.requestFocus());edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertEquals("custom.control.view",pages.findFocus().getTag());PreviewPagesTest.capture(pages,"0141-custom-toolbar-small-large-text");
        }finally{host.pause().stop().destroy();}
    }
    @Test public void movieAndSeriesKeepCardAndTabsSeparated()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{for(boolean television:new boolean[]{false,true}){var page=new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(host.get(),androidx.leanback.widget.ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);if(television)page.bindShow(new com.archos.mediacenter.video.browser.adapters.object.Tvshow(7,"Series",null,1,2,0,"/series"),()->{});else page.bind((Movie)source(1).movies.get(0).media);settle(page);View card=page.findViewWithTag("semantic:details.card"),tab=page.findViewWithTag("section:Details");Rect cardBounds=new Rect(),tabBounds=new Rect();card.getGlobalVisibleRect(cardBounds);tab.getGlobalVisibleRect(tabBounds);assertEquals("Card retains its original bottom alignment inside the hero",0,((View)card.getParent()).getPaddingBottom());assertEquals(PreviewDialog.dp(host.get(),22),tabBounds.top-cardBounds.bottom,2);assertTrue(tab.requestFocus());PreviewPagesTest.capture(page,"0141-details-gap-"+television);}
        }finally{host.pause().stop().destroy();}
    }
    @Test public void mixedMetadataPreservesNamesAndPerTrackIconsWithoutDecoratingUnknownLabels(){
        android.content.Context context=RuntimeEnvironment.getApplication();android.text.SpannableStringBuilder text=new android.text.SpannableStringBuilder();
        text.append(PreviewLanguageIcon.inline(context,"en-GB","English (United Kingdom) · AAC"));text.append("\n");text.append(PreviewLanguageIcon.inline(context,"pt","Portuguese · text"));
        assertEquals(2,text.getSpans(0,text.length(),android.text.style.ImageSpan.class).length);assertTrue(text.toString().contains("English (United Kingdom) · AAC"));assertTrue(text.toString().endsWith("Portuguese · text"));assertEquals("Commentary",PreviewLanguageIcon.inline(context,"commentary","Commentary"));assertEquals("Unknown",PreviewLanguageIcon.inline(context,"und","Unknown"));
    }
    @Test public void flagsRequireValidExplicitRegionAndProviderAliasesCannotInventNationality()throws Exception{
        assertEquals("",PreviewLanguageIcon.country("zh-ca"));assertEquals("GB",PreviewLanguageIcon.country("en-GB"));assertEquals("BR",PreviewLanguageIcon.country("pt_BR"));assertEquals("",PreviewLanguageIcon.country("en"));assertEquals("",PreviewLanguageIcon.country("eng"));assertEquals("",PreviewLanguageIcon.country("und-US"));assertEquals("",PreviewLanguageIcon.country("qqq-US"));assertEquals("",PreviewLanguageIcon.country("commentary"));assertEquals("",PreviewLanguageIcon.country(PreviewLanguageIcon.providerCode("zh-ca")));
    }
}
