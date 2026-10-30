package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.details.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import java.util.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova138UiTest {
    @Before public void before(){PreviewAsyncFixtures.offlineTransport();PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().clear().putBoolean("supernova_onboarding_complete",true).putBoolean("supernova_welcome_dismissed",true).commit();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void after()throws Exception{PreviewAsyncFixtures.drain();}
    @Test public void allFiveDetailsSectionsFitAndRemainReadOnly()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});TopNavigation nav=new TopNavigation(host.get(),page,n->{},page::atTop);host.get().setContentView(nav);
            org.json.JSONObject details=new org.json.JSONObject().put("title","A Long Official Movie Title").put("release_date","2024-03-01").put("runtime",134).put("vote_average",8.1).put("vote_count",8200).put("original_title","An Original Title").put("tagline","One genuine provider tagline").put("budget",12345678).put("revenue",87654321).put("overview","An actual synopsis fixture.").put("production_countries",new org.json.JSONArray().put(new org.json.JSONObject().put("name","United Kingdom")));
            page.bindRemote(details,"movie",0);com.archos.mediascraper.MovieTags tags=new com.archos.mediascraper.MovieTags();for(int n=1;n<=12;n++){tags.addActorIfAbsent("Actor "+n,"Role "+n);tags.addDirectorIfAbsent("Director "+n);}tags.addGenreIfAbsent("Drama");page.setTags(tags,Collections.emptyList(),Collections.emptyList());PreviewPagesTest.layout(nav);
            View tab=page.findViewWithTag("section:Details");assertTrue(tab.requestFocus());tab.performClick();PreviewPagesTest.layout(nav);assertSame(tab,page.findFocus());
            for(String heading:new String[]{"Cast","Crew","Key Information","Reception","Technical Information"}){View text=PreviewPagesTest.findText(page,heading);assertNotNull(heading,text);assertReadableInOverflow(page,text);}
            for(View focus:page.getFocusables(View.FOCUS_FORWARD))assertFalse("Informational focus target "+focus.getTag(),String.valueOf(focus.getTag()).startsWith("person:")||String.valueOf(focus.getTag()).startsWith("semantic:details.panel."));assertNull(PreviewPagesTest.findText(page,"Actor 9"));assertNull(PreviewPagesTest.findText(page,"Director 9"));
            page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertSame(tab,page.findFocus());PreviewPagesTest.capture(nav,RuntimeEnvironment.getApplication().getResources().getConfiguration().fontScale>1?"0138-details-five-sections-150-text":"0138-details-five-sections");
        }finally{host.pause().stop().destroy();}
    }
    /** FD-033 v3 fixes the six read-only regions in one viewport; retain 8+8 checks. */
    private void assertReadableInOverflow(PreviewMoviePage page,View text){
        ScrollView reading=page.findViewWithTag("details.reading.overflow");android.graphics.Rect rect=new android.graphics.Rect(0,0,text.getWidth(),text.getHeight());reading.offsetDescendantRectToMyCoords(text,rect);reading.scrollTo(0,rect.top-reading.getPaddingTop());
        android.graphics.Rect visible=new android.graphics.Rect();assertTrue(text.getContentDescription()+" reachable",text.getGlobalVisibleRect(visible));assertEquals(text.getHeight(),visible.height());assertSame(page.findViewWithTag("section:Details"),page.findFocus());
        if(text instanceof TextView)assertTrue(((TextView)text).getLineCount()<=1);
    }
    @Test public void fiveSectionsFitAt150PercentText()throws Exception{org.robolectric.RuntimeEnvironment.setFontScale(1.5f);try{allFiveDetailsSectionsFitAndRemainReadOnly();navigationLabelsRemainReadableAndFocusedTabsScrollIntoView();}finally{org.robolectric.RuntimeEnvironment.setFontScale(1f);}}
    private void navigationLabelsRemainReadableAndFocusedTabsScrollIntoView()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{TopNavigation nav=new TopNavigation(host.get(),new android.widget.FrameLayout(host.get()),n->{},()->true);host.get().setContentView(nav);PreviewPagesTest.layout(nav);TextView brand=(TextView)PreviewPagesTest.findText(nav,"SUPERNOVA");assertEquals(1,brand.getLineCount());assertTrue(brand.getWidth()>=brand.getPaint().measureText("SUPERNOVA"));TextView clock=nav.findViewWithTag("preview-default-clock");assertTrue(clock.getWidth()>=clock.getPaint().measureText(clock.getText().toString()));for(String tag:new String[]{"home","movies","tv","network","search","settings"}){TextView tab=nav.findViewWithTag("semantic:topnav."+tag);tab.requestFocus();PreviewPagesTest.layout(nav);assertTrue("Enlarged "+tag+" text has vertical space",tab.getHeight()>=tab.getPaint().getFontMetrics().bottom-tab.getPaint().getFontMetrics().top+tab.getPaddingTop()+tab.getPaddingBottom());android.graphics.Rect visible=new android.graphics.Rect();assertTrue(tag,tab.getGlobalVisibleRect(visible));assertEquals("Focused "+tag+" entirely visible",tab.getWidth(),visible.width());}PreviewPagesTest.capture(nav,"0138-navigation-150-text");}finally{host.pause().stop().destroy();}
    }
    @Test public void smallViewportKeepsDetailsActionsAndTabsVisible()throws Exception{
        org.robolectric.RuntimeEnvironment.setQualifiers("w640dp-h360dp-land-mdpi");var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});TopNavigation nav=new TopNavigation(host.get(),page,n->{},page::atTop);host.get().setContentView(nav);Movie movie=new Movie(1,"/fixture/movie.mkv","A Long Movie Title That Needs A Fallback",7,"A long synopsis with enough words to exercise the compact card, without losing Play or the Details tab.",2024,7,null,null,7200000,0,0,0,false,false,false,false,0,0,3840,2160,"AAC","h264",null,null,0,1,1000,0);page.bind(movie);for(int n=0;n<5;n++){nav.measure(View.MeasureSpec.makeMeasureSpec(640,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(360,View.MeasureSpec.EXACTLY));nav.layout(0,0,640,360);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}for(String tag:new String[]{"action:Play","action:More","section:Details"}){View control=page.findViewWithTag(tag);android.graphics.Rect visible=new android.graphics.Rect();assertTrue(tag,control.getGlobalVisibleRect(visible));assertEquals(tag,control.getHeight(),visible.height());assertTrue(tag,visible.bottom<=360);}PreviewPagesTest.capture(nav,"0138-details-small-viewport");}finally{host.pause().stop().destroy();org.robolectric.RuntimeEnvironment.setQualifiers("w960dp-h540dp-land-mdpi");}
    }
    @Test public void localTechnicalTracksAndEightPeopleFitTogetherAtEnlargedText()throws Exception{
        RuntimeEnvironment.setFontScale(1.5f);var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});TopNavigation nav=new TopNavigation(host.get(),page,n->{},page::atTop);host.get().setContentView(nav);Movie movie=new Movie(1,"smb://server/films/film.mkv","Fixture",7,"Synopsis",2024,7,null,null,7200000,0,0,0,false,false,false,false,0,0,3840,2160,"AAC","h264",null,null,0,1,1000000000,0);
            com.archos.medialib.MediaMetadata data=org.mockito.Mockito.mock(com.archos.medialib.MediaMetadata.class);org.mockito.Mockito.when(data.has(org.mockito.ArgumentMatchers.anyInt())).thenReturn(true);org.mockito.Mockito.when(data.getString(org.mockito.ArgumentMatchers.anyInt())).thenAnswer(call->{int key=call.getArgument(0);if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_AUDIO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_AUDIO_TRACK_FORMAT)return "AAC";if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_AUDIO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_AUDIO_TRACK_CHANNELS)return "5.1";if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK_LANGUAGE)return "eng";return "";});org.mockito.Mockito.when(data.getInt(org.mockito.ArgumentMatchers.anyInt())).thenAnswer(call->{int key=call.getArgument(0);if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_NB_VIDEO_TRACK||key==com.archos.medialib.IMediaPlayer.METADATA_KEY_NB_AUDIO_TRACK||key==com.archos.medialib.IMediaPlayer.METADATA_KEY_NB_SUBTITLE_TRACK)return 1;if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK_FPS_SCALE)return 1001;if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK_FPS_RATE)return 24000;if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK_BIT_RATE)return 12000;if(key==com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_VIDEO_TRACK_COLOR_TRC)return 16;return 0;});com.archos.mediacenter.video.utils.VideoMetadata metadata=new com.archos.mediacenter.video.utils.VideoMetadata();metadata.setData(data);movie.setMetadata(metadata);page.bind(movie);com.archos.mediascraper.MovieTags tags=new com.archos.mediascraper.MovieTags();tags.addGenreIfAbsent("Drama");for(int n=1;n<=12;n++){tags.addActorIfAbsent("Actor "+n,"Role "+n);tags.addDirectorIfAbsent("Director "+n);}page.setTags(tags,Collections.emptyList(),Collections.emptyList());PreviewPagesTest.layout(nav);View tab=page.findViewWithTag("section:Details");tab.requestFocus();tab.performClick();PreviewPagesTest.layout(nav);
            PreviewPagesTest.capture(nav,"0138-details-local-tracks-150-text");for(String heading:new String[]{"Cast","Crew","Key Information","Reception","Technical Information"}){TextView title=(TextView)PreviewPagesTest.findText(page,heading);assertNotNull(heading,title);assertReadableInOverflow(page,title);}assertNotNull(PreviewPagesTest.findText(page,"23.976 fps"));assertNotNull(PreviewPagesTest.findText(page,"AAC"));assertNotNull(PreviewPagesTest.findText(page,"Subtitles: eng"));PreviewPagesTest.capture(nav,"0138-details-local-tracks-150-text");
        }finally{host.pause().stop().destroy();RuntimeEnvironment.setFontScale(1f);}
    }
    @Test public void customFiltersIsNotEditorAndReturnsToInvoker(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});CustomLibraryPage config=new CustomLibraryPage();config.name="Documentaries";config.sort="added";int[] edited={0};pages.configureCustomLibrary(config,()->edited[0]++);host.get().setContentView(pages);pages.setSnapshot(new Snapshot());PreviewPagesTest.layout(pages);
            View filters=pages.findViewWithTag("custom.control.filters");assertNotNull(filters);filters.requestFocus();filters.performClick();android.app.Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertNotNull(PreviewPagesTest.findText(dialog.getWindow().getDecorView(),"Clear Filters"));assertNull(PreviewPagesTest.findText(dialog.getWindow().getDecorView(),"Page Details"));assertEquals(0,edited[0]);dialog.cancel();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertSame(filters,pages.findFocus());pages.findViewWithTag("custom.control.edit").performClick();assertEquals(1,edited[0]);
        }finally{host.pause().stop().destroy();}
    }
    @Test public void breadcrumbUpdatesCannotStealLeftDeviceFocusAndAllFilesActuallyToggles(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{var browser=new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());host.get().setContentView(browser);List<com.archos.mediacenter.video.leanback.adapter.object.Box> volumes=new ArrayList<>();for(int n=0;n<10;n++){var box=new com.archos.mediacenter.video.leanback.adapter.object.Box(com.archos.mediacenter.video.leanback.adapter.object.Box.ID.USB,"USB "+n,0,"/storage/usb"+n);volumes.add(box);}browser.locations(volumes,Collections.emptyList(),Collections.emptyList(),s->{});PreviewPagesTest.layout(browser);View local=browser.findViewWithTag("semantic:network.category.local_storage");local.requestFocus();local.performClick();PreviewPagesTest.layout(browser);
            for(int n=0;n<10;n++){browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();PreviewPagesTest.layout(browser);assertEquals("storage:file:///storage/usb"+n,browser.findFocus().getTag());}
            TextView toggle=browser.findViewWithTag("browser.all_files");assertNotNull(toggle);toggle.performClick();assertTrue(toggle.getText().toString().endsWith("On"));toggle.performClick();assertTrue(toggle.getText().toString().endsWith("Off"));browser.close();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void settingsBottomRowsAndActualFirstLicenceRemainReachable()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
            try{
            host.get().setTheme(com.archos.mediacenter.video.R.style.MyLeanbackTheme);
            NextQaReliabilityTest.Settings settings=new NextQaReliabilityTest.Settings();
            host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,settings).commitNow();
            View root=settings.requireView();
            PreviewPagesTest.layout(root);
            View about=root.findViewWithTag("semantic:settings:category:About");
            assertNotNull(about);
            about.requestFocus();
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            PreviewPagesTest.layout(root);
            assertFullyVisible(about);
            about.performClick();
            PreviewPagesTest.layout(root);
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(250));
            PreviewPagesTest.layout(root);
            View technical=root.findViewWithTag("semantic:settings:about:Technical Information");
            assertNotNull(technical);
            technical.requestFocus();
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            PreviewPagesTest.layout(root);
            PreviewPagesTest.capture(root,"0138-settings-technical-bottom");
            assertFullyVisible(technical);

            View licences=root.findViewWithTag("semantic:settings:about:Open-source Licences");
            licences.requestFocus();
            licences.performClick();
            com.archos.mediacenter.video.foundation.FoundationAboutWorkspace workspace=findAbout(root);
            assertNotNull(workspace);
            java.util.concurrent.ExecutorService reader=ReflectionHelpers.getField(workspace,"reader");
            reader.submit(()->{}).get(5,java.util.concurrent.TimeUnit.SECONDS);
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            reader.submit(()->{}).get(5,java.util.concurrent.TimeUnit.SECONDS);
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            PreviewPagesTest.layout(root);
            List<TextView> rows=ReflectionHelpers.getField(workspace,"rows");
            assertFalse(rows.isEmpty());
            assertTrue(rows.get(0).getText().toString().startsWith("AndroidX"));
            assertEquals("Licence component labels use the approved single-line rows",1,rows.get(0).getMaxLines());
            assertSame(rows.get(0),root.findFocus());
            com.archos.mediacenter.video.foundation.BundledNoticeReader pager=ReflectionHelpers.getField(workspace,"noticeReader");
            assertNotNull("Actual large first notice opens bounded reader",pager);
            rows.get(0).dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));
            assertTrue("Right enters the actual notice reader",pager.hasFocus());
            pager.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));
            PreviewPagesTest.layout(root);
            PreviewPagesTest.capture(root,"0138-licences-androidx-reader");
            assertTrue(workspace.handleBack());
            assertSame(rows.get(0),root.findFocus());
            rows.get(0).dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));
            assertSame(rows.get(1),root.findFocus());


        }finally{
            host.pause().stop().destroy();
            }
    }
    private void assertFullyVisible(View view){android.graphics.Rect visible=new android.graphics.Rect();int[] at=new int[2];view.getLocationOnScreen(at);String message=String.valueOf(view.getTag())+" at="+Arrays.toString(at)+" size="+view.getWidth()+"x"+view.getHeight()+" shown="+view.isShown()+" focus="+view.hasFocus();assertTrue(message,view.getGlobalVisibleRect(visible));assertEquals(message,view.getHeight(),visible.height());}
    private com.archos.mediacenter.video.foundation.FoundationAboutWorkspace findAbout(View root){if(root instanceof com.archos.mediacenter.video.foundation.FoundationAboutWorkspace)return (com.archos.mediacenter.video.foundation.FoundationAboutWorkspace)root;if(root instanceof ViewGroup)for(int n=0;n<((ViewGroup)root).getChildCount();n++){var found=findAbout(((ViewGroup)root).getChildAt(n));if(found!=null)return found;}return null;}
    @Test public void overviewScanTextIsReadOnlyAndContained(){var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{var browser=new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());host.get().setContentView(browser);PreviewPagesTest.layout(browser);TextView scan=ReflectionHelpers.getField(browser,"scanOverview");assertFalse(scan.isFocusable());View centre=ReflectionHelpers.getField(browser,"centre");assertTrue("Read-only Overview has no centre focus stop",centre.getFocusables(View.FOCUS_FORWARD).isEmpty());android.graphics.Rect visible=new android.graphics.Rect();assertTrue(scan.getGlobalVisibleRect(visible));assertTrue(visible.bottom<=browser.getHeight());browser.close();}finally{host.pause().stop().destroy();}}
    @Test public void hudTransportAndFocusedTooltipMatch141Contract()throws Exception{var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{View root=LayoutInflater.from(host.get()).inflate(com.archos.mediacenter.video.R.layout.player_controller_experimental,null);host.get().setContentView(root);View bar=root.findViewById(com.archos.mediacenter.video.R.id.control_bar);bar.setVisibility(View.VISIBLE);
        com.archos.mediacenter.video.player.PlayerController controller=org.mockito.Mockito.mock(com.archos.mediacenter.video.player.PlayerController.class,org.mockito.Mockito.CALLS_REAL_METHODS);ReflectionHelpers.setField(controller,"mContext",host.get());androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("try_new_ui",true).putString("uimode","2").commit();ReflectionHelpers.setField(controller,"mControllerViewLeft",root);ReflectionHelpers.setField(controller,"mControlBar",bar);ReflectionHelpers.setField(controller,"mProgress",root.findViewById(com.archos.mediacenter.video.R.id.seek_progress));ReflectionHelpers.setField(controller,"mPauseButton",root.findViewById(com.archos.mediacenter.video.R.id.pause));ReflectionHelpers.setField(controller,"mCurrentTime",root.findViewById(com.archos.mediacenter.video.R.id.time_current));ReflectionHelpers.callInstanceMethod(controller,"configurePreviewTransport");((TextView)root.findViewById(com.archos.mediacenter.video.R.id.time)).setText("01:05:26");((TextView)root.findViewById(com.archos.mediacenter.video.R.id.preview_end_time)).setText("Ends at 03:58");((TextView)root.findViewById(com.archos.mediacenter.video.R.id.preview_scrub_time)).setText("12:34");((TextView)root.findViewById(com.archos.mediacenter.video.R.id.time_current)).setText("12:34");PreviewPagesTest.layout(root);ReflectionHelpers.callInstanceMethod(controller,"updatePreviewScrubber");View seek=root.findViewById(com.archos.mediacenter.video.R.id.seek_progress),pause=root.findViewById(com.archos.mediacenter.video.R.id.preview_pause_group),end=root.findViewById(com.archos.mediacenter.video.R.id.time);seek.requestFocus();ReflectionHelpers.callInstanceMethod(controller,"updatePreviewScrubber");assertEquals(centreY(pause),centreY(seek),1);assertEquals(centreY(seek),centreY(end),1);assertEquals(View.GONE,root.findViewById(com.archos.mediacenter.video.R.id.time_current).getVisibility());assertEquals(View.VISIBLE,root.findViewById(com.archos.mediacenter.video.R.id.preview_scrub_time).getVisibility());assertFalse(bar.isFocusable());assertEquals(View.GONE,root.findViewById(com.archos.mediacenter.video.R.id.preview_subtitles_group).getVisibility());assertEquals(View.GONE,root.findViewById(com.archos.mediacenter.video.R.id.preview_audio_group).getVisibility());assertEquals("Settings",((TextView)root.findViewById(com.archos.mediacenter.video.R.id.preview_more_label)).getText().toString());PreviewPagesTest.capture(root,"0141-playback-hud");}finally{host.pause().stop().destroy();}}
    private float centreY(View view){int[] point=new int[2];view.getLocationInWindow(point);return point[1]+view.getHeight()/2f;}
}
