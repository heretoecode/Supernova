package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.preference.*;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.leanback.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class SettingsWorkspaceTest {
    private org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host;
    private VideoSettingsFragment.PrefsFragment fragment;private View root;private SettingsWorkspace workspace;
    @Before public void setup(){
        Application app=RuntimeEnvironment.getApplication();Shadows.shadowOf(app.getPackageManager()).setSystemFeature("android.software.leanback",true);PreviewAsyncFixtures.offlineTransport();
        ReflectionHelpers.setStaticField(androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.class,"_instance",null);
        ReflectionHelpers.setStaticField(CustomApplication.class,"systemLocale",java.util.Locale.getDefault());ReflectionHelpers.setStaticField(CustomApplication.class,"mContext",app);ReflectionHelpers.setStaticField(CustomApplication.class,"log",org.slf4j.LoggerFactory.getLogger(CustomApplication.class));com.archos.environment.ArchosUtils.setGlobalContext(app);
        PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putBoolean("try_new_ui",true).commit();
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(app).build());}
        host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();fragment=new VideoSettingsFragment.PrefsFragment();host.get().getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commitNow();root=fragment.requireView();workspace=fragment.workspace;layout();
    }
    @After public void close()throws Exception{if(host!=null)host.pause().stop().destroy();PreviewAsyncFixtures.drain();}
    private void layout(){float density=root.getResources().getDisplayMetrics().density;int w=Math.round(root.getResources().getConfiguration().screenWidthDp*density),h=Math.round(root.getResources().getConfiguration().screenHeightDp*density);for(int i=0;i<4;i++){Shadows.shadowOf(Looper.getMainLooper()).idle();root.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));root.layout(0,0,w,h);}Shadows.shadowOf(Looper.getMainLooper()).idle();}
    private View tagged(String tag){View v=root.findViewWithTag(tag);assertNotNull(tag,v);return v;}
    private void key(int code){root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));layout();}
    @Test public void gridHasExactEightCardsNoWrapAndRestoresSelectedCategory(){
        assertTrue(tagged("settings.grid.0").hasFocus());key(KeyEvent.KEYCODE_DPAD_LEFT);assertTrue(tagged("settings.grid.0").hasFocus());key(KeyEvent.KEYCODE_DPAD_RIGHT);key(KeyEvent.KEYCODE_DPAD_DOWN);assertTrue(tagged("settings.grid.5").hasFocus());tagged("settings.grid.5").performClick();layout();assertNotNull(root.findViewWithTag("settings.category"));assertTrue(workspace.back());layout();assertTrue(tagged("settings.grid.5").hasFocus());assertFalse(workspace.back());
        for(int i=0;i<8;i++)assertTrue(tagged("settings.grid."+i).getContentDescription().toString().startsWith(SettingsRegistry.CATEGORIES[i]));
    }
    @Test public void railHasOneImmediateBoundaryDuringRapidFocusChanges(){
        workspace.open(0,"Playback");layout();View first=tagged("settings.section.Playback"),second=tagged("settings.section.Video & Display");first.requestFocus();second.requestFocus();first.requestFocus();second.requestFocus();ViewGroup rail=(ViewGroup)second.getParent();assertFalse("Child surface must not paint a second focus boundary",second.getBackground() instanceof android.graphics.drawable.StateListDrawable);assertNull("Selected state must not paint a second outline",second.getForeground());assertTrue(((TextView)second).getText().toString().startsWith("✓"));assertTrue(ReflectionHelpers.getField(rail,"visible"));android.graphics.RectF outline=ReflectionHelpers.getField(rail,"current");assertEquals(second.getTop(),outline.top,1);assertEquals(second.getBottom(),outline.bottom,1);assertNull(ReflectionHelpers.getField(rail,"movement"));workspace.enter();assertFalse(ReflectionHelpers.getField(rail,"visible"));
    }
    @Test public void togglePersistsWithoutMovingFocusAndHiddenRowsRemainAbsent(){
        workspace.open(0,"Playback");layout();View row=tagged("settings.preference.playback_speed");row.requestFocus();boolean before=((TwoStatePreference)fragment.findPreference("playback_speed")).isChecked();row.performClick();layout();assertEquals(!before,PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean("playback_speed",true));assertTrue(row.hasFocus());assertNull(root.findViewWithTag("settings.preference.try_new_ui"));assertNull(root.findViewWithTag("settings.preference.app_theme"));workspace.back();workspace.open(0,"Playback");layout();assertEquals(!before,((TwoStatePreference)fragment.findPreference("playback_speed")).isChecked());
    }
    @Test public void positiveSubtitleProxyImmediatelyUpdatesNativeLanguageDependency(){
        workspace.open(1,"Subtitles");layout();TwoStatePreference positive=fragment.findPreference("preview_subtitles_by_default");positive.setChecked(true);View row=tagged("settings.preference.preview_subtitles_by_default");row.performClick();layout();assertTrue(((TwoStatePreference)fragment.findPreference("subtitles_hide_default")).isChecked());assertFalse(fragment.findPreference("favSubLang").isEnabled());assertFalse(fragment.findPreference("subtitles_show_full_for_local_audio").isEnabled());row.performClick();layout();assertTrue(fragment.findPreference("favSubLang").isEnabled());assertTrue(fragment.findPreference("subtitles_show_full_for_local_audio").isEnabled());assertNotNull(tagged("settings.preference.subtitle_fonts_folder_uri"));assertNotNull(tagged("settings.preference.subtitle_default_font"));
    }
    @Test public void providerInlineChecksKeepRowIdentityGroupAndFocus(){
        LinearLayout target=new LinearLayout(host.get());target.setOrientation(LinearLayout.VERTICAL);MultiSelectListPreference p=new MultiSelectListPreference(host.get());p.setKey("streaming_providers_IE");p.setEntries(new String[]{"Alpha","Beta"});p.setEntryValues(new String[]{"1","2"});p.setValues(new java.util.HashSet<>(java.util.Set.of("1")));host.get().addContentView(target,new ViewGroup.LayoutParams(800,600));InlinePreferenceChoices.show(target,p);target.measure(View.MeasureSpec.makeMeasureSpec(800,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(600,View.MeasureSpec.EXACTLY));target.layout(0,0,800,600);View alpha=target.findViewWithTag("1");assertNotNull(alpha);alpha.requestFocus();alpha.performClick();assertSame(alpha,target.findViewWithTag("1"));assertTrue(alpha.hasFocus());assertFalse(p.getValues().contains("1"));alpha.performClick();assertSame(alpha,target.findViewWithTag("1"));assertTrue(p.getValues().contains("1"));
    }
    @Test public void mapAccountsForBaselineAndNativeSubtitleExtensionWithoutResurrectingHiddenState(){
        SettingsRegistry registry=new SettingsRegistry(host.get());assertEquals(170,registry.entries.size());assertEquals(165,registry.entries.stream().filter(e->e.id.startsWith("S")).count());assertEquals(5,registry.entries.stream().filter(e->e.id.startsWith("N146")&&!e.nested()).count());java.util.Set<String> ids=new java.util.HashSet<>();for(SettingsRegistry.Entry e:registry.entries){assertTrue(ids.add(e.id));assertFalse(e.disposition.isEmpty());if(e.excluded())assertFalse(e.integration());}
        for(int i=0;i<8;i++){workspace.open(i,null);layout();assertEquals(2,((ViewGroup)tagged("settings.category")).getChildCount());}
    }
    @Test public void navigationStateSurvivesRecreationWithoutReplayingAnAction(){
        workspace.open(6,"Diagnostics");layout();View row=tagged("settings.preference.supernova_diagnostic_level");row.requestFocus();Bundle state=new Bundle();fragment.onSaveInstanceState(state);SettingsWorkspace copy=new SettingsWorkspace(fragment,ReflectionHelpers.getField(fragment,"backupController"),state);assertNotNull(copy.findViewWithTag("settings.section.Diagnostics"));copy.dispose();
    }
    @Test public void choiceCancelAndAcceptedSelectionRestoreOpenerAndRespectVeto(){
        workspace.open(6,"Diagnostics");layout();View opener=tagged("settings.preference.supernova_diagnostic_level");
        ListPreference level=fragment.findPreference("supernova_diagnostic_level");assertNotNull(level);opener.requestFocus();
        android.app.Dialog dialog=SettingsChoiceDialog.show(host.get(),level);assertNotNull(dialog);dialog.getWindow().getDecorView().findViewWithTag("settings.choice.0").requestFocus();assertEquals("qa",level.getValue());dialog.getWindow().getDecorView().findViewWithTag("settings.choice.cancel").performClick();assertEquals("qa",level.getValue());assertTrue(opener.hasFocus());
        level.setOnPreferenceChangeListener((p,value)->false);dialog=SettingsChoiceDialog.show(host.get(),level);dialog.getWindow().getDecorView().findViewWithTag("settings.choice.0").performClick();assertTrue(dialog.isShowing());assertEquals("qa",level.getValue());dialog.dismiss();
        level.setOnPreferenceChangeListener((p,value)->true);dialog=SettingsChoiceDialog.show(host.get(),level);dialog.getWindow().getDecorView().findViewWithTag("settings.choice.0").performClick();layout();assertFalse(dialog.isShowing());assertEquals("normal",level.getValue());assertTrue(opener.hasFocus());
    }
    @Test public void integrationDisableAndReenableRetainsPolicyAndReconcilesChildren(){
        String master=com.archos.mediacenter.video.player.SegmentSkippingPolicy.MASTER;
        workspace.open(5,"IntroDB");layout();String child="introdb_mode_intro";
        SettingsRegistry registry=new SettingsRegistry(host.get());for(SettingsRegistry.Entry e:registry.in(5,"IntroDB"))if(!e.integration()){child=e.key;break;}
        Preference policy=fragment.findPreference(child);assertNotNull(policy);String value=((ListPreference)policy).getValue();assertNotNull(root.findViewWithTag("settings.preference."+child));
        workspace.open(5,"Service integrations");layout();tagged("settings.preference."+master).performClick();layout();workspace.open(5,"IntroDB");layout();assertNull(root.findViewWithTag("settings.preference."+child));
        workspace.open(5,"Service integrations");layout();tagged("settings.preference."+master).performClick();layout();workspace.open(5,"IntroDB");layout();assertNotNull(root.findViewWithTag("settings.preference."+child));assertEquals(value,((ListPreference)fragment.findPreference(child)).getValue());
    }
    @Test public void advancedUnlockReparentsNativePreferencesWithoutResettingSavedValues(){
        workspace.open(6,"Video compatibility");layout();View original=tagged("settings.preference.force_software_decoding");original.requestFocus();
        androidx.preference.TwoStatePreference force=fragment.findPreference("force_software_decoding");assertNotNull(force);
        for(int i=0;i<9;i++)force.performClick();layout();assertTrue(PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean(com.archos.mediacenter.video.utils.VideoPreferencesCommon.KEY_ADVANCED_VIDEO_ENABLED,false));
        assertNotNull(fragment.findPreference("dec_choice"));assertNull("Basic decoder control must leave the model after advanced unlock",fragment.findPreference("force_software_decoding"));assertNull(root.findViewWithTag("settings.preference.try_new_ui"));assertNotNull(root.findFocus());assertTrue(String.valueOf(root.findFocus().getTag()).startsWith("settings.preference."));
        Preference quit=fragment.findPreference(com.archos.mediacenter.video.utils.VideoPreferencesCommon.KEY_ADVANCED_VIDEO_QUIT);assertNotNull(quit);quit.performClick();layout();
        assertFalse(PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean(com.archos.mediacenter.video.utils.VideoPreferencesCommon.KEY_ADVANCED_VIDEO_ENABLED,true));assertNull(fragment.findPreference("dec_choice"));assertNotNull(fragment.findPreference("force_software_decoding"));assertNotNull(root.findFocus());
    }
    @Test public void externalRestoreReconcilesNativeValuesWithoutReplayingActions(){
        workspace.open(0,"Playback");layout();View row=tagged("settings.preference.playback_speed");row.requestFocus();TwoStatePreference nativeValue=fragment.findPreference("playback_speed");final int[] called={0};nativeValue.setOnPreferenceChangeListener((p,v)->{called[0]++;return true;});
        boolean updated=!nativeValue.isChecked();PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("playback_speed",updated).putBoolean("subtitles_hide_default",true).commit();layout();assertEquals(updated,nativeValue.isChecked());assertTrue(row.hasFocus());assertEquals(0,called[0]);workspace.open(1,"Subtitles");layout();assertFalse(((TwoStatePreference)fragment.findPreference("preview_subtitles_by_default")).isChecked());assertFalse(fragment.findPreference("favSubLang").isEnabled());
    }
    @Test public void allCategoriesRenderTheirRealContentAndRestoreGrid()throws Exception{
        for(int i=0;i<8;i++){workspace.open(i,null);layout();save("category-"+i);assertTrue(workspace.back());layout();assertTrue(tagged("settings.grid."+i).hasFocus());}
        workspace.open(6,"Backup & restore");layout();save("backup");workspace.open(5,"OpenSubtitles");layout();save("opensubtitles");
    }
    @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void render720p()throws Exception{render("720p");}
    @Test public void render1080p()throws Exception{render("1080p");}
    @Test @Config(qualifiers="w960dp-h540dp-land-xxxhdpi") public void render4k()throws Exception{render("4k");}
    @Test public void renderEnlargedText()throws Exception{RuntimeEnvironment.setFontScale(1.5f);try{workspace.showGrid();layout();render("large");}finally{RuntimeEnvironment.setFontScale(1f);}}
    private void render(String name)throws Exception{
        layout();for(int i=0;i<8;i++){ViewGroup card=(ViewGroup)tagged("settings.grid."+i);for(int j=0;j<card.getChildCount();j++)if(card.getChildAt(j) instanceof TextView){TextView t=(TextView)card.getChildAt(j);assertNotNull(t.getLayout());assertTrue(name+" text fits card "+i+" bottom="+t.getBottom()+" height="+card.getHeight(),t.getBottom()<=card.getHeight()-card.getPaddingBottom());for(int n=0;n<t.getLineCount();n++)assertEquals(0,t.getLayout().getEllipsisCount(n));}}
        save(name+"-grid");workspace.open(0,"Playback");layout();save(name+"-playback");
    }
    private void save(String name)throws Exception{android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(root.getWidth(),root.getHeight(),android.graphics.Bitmap.Config.ARGB_8888);root.draw(new android.graphics.Canvas(image));java.io.File file=new java.io.File("build/reports/settings-0143/"+name+".png");file.getParentFile().mkdirs();try(java.io.FileOutputStream out=new java.io.FileOutputStream(file)){image.compress(android.graphics.Bitmap.CompressFormat.PNG,100,out);}image.recycle();}
    @Test public void overviewUsesEqualGuttersAndPurposeSpecificAudioCopy(){
        View first=tagged("settings.grid.0"),secondRow=tagged("settings.grid.4");
        int horizontal=((LinearLayout.LayoutParams)first.getLayoutParams()).rightMargin;
        int vertical=((LinearLayout.LayoutParams)((View)first.getParent()).getLayoutParams()).bottomMargin;
        assertEquals(horizontal,vertical);
        workspace.open(1,"Audio output & tracks");layout();
        assertEquals("Configure audio output, playback compatibility and preferred audio tracks.",
            (String)ReflectionHelpers.callInstanceMethod(workspace,"sectionDescription"));
    }
    @Test public void switchThumbStaysCircularWhenCompatAssignsTallTrackBounds(){
        SettingsSwitchThumb thumb=new SettingsSwitchThumb(host.get());int diameter=thumb.getIntrinsicWidth();
        android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(diameter,diameter*2,android.graphics.Bitmap.Config.ARGB_8888);
        thumb.setBounds(0,0,diameter,diameter*2);thumb.draw(new android.graphics.Canvas(image));
        assertEquals(0,android.graphics.Color.alpha(image.getPixel(diameter/2,0)));
        assertEquals(255,android.graphics.Color.alpha(image.getPixel(diameter/2,diameter)));
        assertEquals(0,android.graphics.Color.alpha(image.getPixel(diameter/2,diameter*2-1)));image.recycle();
    }
    @Test public void sharedNavigationRetainsEightCardsAndAllCategoryFocusPaths()throws Exception{
        ViewGroup old=(ViewGroup)root.getParent();old.removeView(root);
        TopNavigation nav=new TopNavigation(host.get(),root,index->{},workspace::atNavigationBoundary);nav.selectTab(4);host.get().setContentView(nav);
        nav.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));nav.layout(0,0,1920,1080);
        tagged("settings.grid.2").requestFocus();nav.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));
        assertTrue(nav.findViewWithTag("semantic:topnav.settings").hasFocus());
        nav.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(tagged("settings.grid.2").hasFocus());
        for(int n=0;n<8;n++){
            workspace.open(n,null);layout();assertTrue(workspace.atNavigationBoundary());
            nav.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));assertTrue(nav.findViewWithTag("semantic:topnav.settings").hasFocus());
            nav.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(workspace.atNavigationBoundary());
            assertTrue(workspace.back());
        }
    }
    private void invalidateAll(View view){view.invalidate();if(view instanceof ViewGroup)for(int n=0;n<((ViewGroup)view).getChildCount();n++)invalidateAll(((ViewGroup)view).getChildAt(n));}
    @Test public void productionActivityRendersTheSharedHeaderOnGridAndCategory()throws Exception{
        // As in existing production-Activity fixtures, model AndroidX's installed signature permission.
        Shadows.shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
        var activity=Robolectric.buildActivity(VideoSettingsActivity.class).setup().visible();try{
            activity.get().getSupportFragmentManager().executePendingTransactions();Shadows.shadowOf(Looper.getMainLooper()).idle();
            View nav=((ViewGroup)activity.get().findViewById(android.R.id.content)).getChildAt(0);assertTrue(nav instanceof TopNavigation);
            for(int n=0;n<4;n++){nav.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));nav.layout(0,0,1920,1080);Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(50));}
            invalidateAll(nav);PreviewPagesTest.capture(nav,"0147-settings-global-grid");
            android.graphics.Bitmap pixels=android.graphics.BitmapFactory.decodeFile("build/reports/preview-ui/0147-settings-global-grid.png");int bright=0;for(int y=35;y<76;y++)for(int x=52;x<266;x++){int colour=pixels.getPixel(x,y);if(android.graphics.Color.red(colour)>180&&android.graphics.Color.green(colour)>180&&android.graphics.Color.blue(colour)>180)bright++;}assertTrue("Rendered SUPERNOVA brand remains visible",bright>100);
            assertNotNull(nav.findViewWithTag("semantic:topnav.home"));assertNotNull(nav.findViewWithTag("semantic:topnav.network"));
            var actual=(VideoSettingsFragment.PrefsFragment)activity.get().getSupportFragmentManager().findFragmentById(com.archos.mediacenter.video.R.id.settingsFragment);actual.workspace.open(1,null);
            for(int n=0;n<3;n++){nav.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));nav.layout(0,0,1920,1080);Shadows.shadowOf(Looper.getMainLooper()).idle();}
            invalidateAll(nav);PreviewPagesTest.capture(nav,"0147-settings-global-category");assertNotNull(nav.findViewWithTag("semantic:topnav.settings"));
        }finally{activity.pause().stop().destroy();}
    }
}
