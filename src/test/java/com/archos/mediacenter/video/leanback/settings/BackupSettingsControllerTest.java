package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import java.io.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class BackupSettingsControllerTest {
    public static class Settings extends PreferenceFragmentCompat {
        BackupSettingsController backup;
        @Override public void onCreate(Bundle state){super.onCreate(state);backup=new BackupSettingsController(this);}
        @Override public void onCreatePreferences(Bundle state,String key){PreferenceScreen root=getPreferenceManager().createPreferenceScreen(requireContext());setPreferenceScreen(root);PreferenceCategory advanced=new PreferenceCategory(requireContext());advanced.setTitle("Advanced");advanced.setKey("advanced");root.addPreference(advanced);PreferenceCategory group=new PreferenceCategory(requireContext());group.setTitle("Backup & Restore");group.setKey("preview_backup_restore");advanced.addPreference(group);Preference option=new Preference(requireContext());option.setKey("old-export");option.setTitle("Old export placeholder");group.addPreference(option);}
        @Override public androidx.recyclerview.widget.RecyclerView onCreateRecyclerView(LayoutInflater inflater,ViewGroup parent,Bundle state){return PreviewSettings.grid(requireContext());}
        @Override protected androidx.recyclerview.widget.RecyclerView.Adapter onCreateAdapter(PreferenceScreen screen){return PreviewSettings.adapter(screen);}
        @Override public void onViewCreated(View view,Bundle state){super.onViewCreated(view,state);PreviewSettings.sidebar(this,backup);}
    }
    @Before public void isolate(){PreviewAsyncFixtures.offlineTransport();}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
    private void layout(View view){if("settings.backup.workspace".equals(view.getTag())){layoutOperation(view);return;}var resources=view.getResources();int width=Math.round(resources.getConfiguration().screenWidthDp*resources.getDisplayMetrics().density),height=Math.round(resources.getConfiguration().screenHeightDp*resources.getDisplayMetrics().density);view.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));view.layout(0,0,width,height);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    private void capture(View view,String name)throws Exception{Bitmap image=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);view.draw(new Canvas(image));File file=new File("build/reports/preview-ui/"+name+".png");file.getParentFile().mkdirs();try(FileOutputStream output=new FileOutputStream(file)){image.compress(Bitmap.CompressFormat.PNG,100,output);}}
    private Settings add(TopNavigationTest.Host host){host.getIntent().putExtra("preview_settings_category","Advanced");Settings settings=new Settings();host.getSupportFragmentManager().beginTransaction().add(android.R.id.content,settings,"backup-fixture").commitNow();layout(settings.requireView());return settings;}
    private View enter(Settings settings){View root=settings.requireView();root.findViewWithTag("semantic:settings:category:Advanced").performClick();layout(root);View child=root.findViewWithTag("semantic:settings:section:preview_backup_restore");assertNotNull(child);child.requestFocus();layout(root);assertTrue("Landing retains the permanent sidebar opener",child.hasFocus());assertNotNull(root.findViewWithTag("backup.action.export"));assertNotNull(root.findViewWithTag("backup.action.restore"));return child;}
    private View operation(Settings settings){View frame=org.robolectric.util.ReflectionHelpers.getField(settings.backup,"frame");layoutOperation(frame);return frame;}
    private void layoutOperation(View frame){int w=TvLayout.safeWidth(frame.getContext()),h=TvLayout.px(frame.getContext(),TvLayout.height(frame.getContext())-32);frame.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));frame.layout(0,0,w,h);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    private void export(Settings settings){settings.requireView().findViewWithTag("backup.action.export").performClick();layoutOperation(operation(settings));}
    private BackupOperationModel model(TopNavigationTest.Host host){return new ViewModelProvider(host).get(BackupOperationModel.class);}
    private Set<File> exports(BackupOperationModel model){File[] files=new File(model.getApplication().getCacheDir(),"supernova-logical-backups").listFiles(f->f.getName().endsWith(".zip"));return files==null?new HashSet<>():new HashSet<>(Arrays.asList(files));}
    private File newExport(BackupOperationModel model,Set<File> before){Set<File> after=exports(model);after.removeAll(before);assertEquals(1,after.size());return after.iterator().next();}
    // A worker can clear busy before postValue enqueues the completed state.
    // Wait for main-thread publication too, so tests click the rendered phase.
    private void await(BackupOperationModel model,BackupOperationModel.Phase phase)throws Exception{long deadline=System.nanoTime()+10_000_000_000L;while((model.current().phase!=phase||model.isBusy()||model.states().getValue()==null||model.states().getValue().phase!=phase)&&System.nanoTime()<deadline){Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();Thread.sleep(5);}Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(model.current().message,phase,model.current().phase);assertFalse(model.isBusy());assertNotNull("Published UI state",model.states().getValue());assertEquals("The UI observer must receive the completed phase before clicking its controls",phase,model.states().getValue().phase);}
    @Test public void realSettingsRailKeepsItsWidthAndAllCategoriesOccupyOnlyCentreList()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());View root=settings.requireView(),parent=root.findViewWithTag("semantic:settings:category:Advanced");int leftWidth=((View)parent.getParent()).getWidth();enter(settings);assertEquals(leftWidth,((View)parent.getParent()).getWidth());settings.backup.enter();assertTrue(root.findViewWithTag("backup.action.export").hasFocus());settings.backup.navigation(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(root.findViewWithTag("backup.action.restore").hasFocus());settings.backup.navigation(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(root.findViewWithTag("backup.category.APPEARANCE").hasFocus());for(BackupCategory category:BackupCategory.values())assertNotNull(root.findViewWithTag("backup.category."+category.name()));assertEquals(BackupOperationModel.Phase.IDLE,model(host.get()).current().phase);capture(root,"0140-settings-backup-main");}finally{host.pause().stop().destroy();}
    }
    @Test public void connectedFlatInformationBackRestoresOpenerThenPermanentSidebar()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());View child=enter(settings);for(String tag:List.of("backup.details","backup.instructions")){View opener=settings.requireView().findViewWithTag(tag);opener.requestFocus();opener.performClick();assertNotNull(operation(settings).findViewWithTag(tag+".page"));assertTrue(settings.backup.back());assertTrue(opener.hasFocus());}assertTrue(settings.backup.back());assertTrue(child.hasFocus());}finally{host.pause().stop().destroy();}
    }
    @Test public void defaultSelectionIncludesMatchesAndLaunchesPickerWithEverySelectedCodec()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreferenceManager.getDefaultSharedPreferences(host.get()).edit().clear().commit();Settings settings=add(host.get());enter(settings);
            // Read the selected workspace through its enclosing view without assuming the category-row structure.
            BackupWorkspace choices=org.robolectric.util.ReflectionHelpers.getField(settings.backup,"exportChoices");
            Set<BackupCategory> selected=choices.selection();assertTrue(selected.contains(BackupCategory.MATCHES));assertFalse(selected.contains(BackupCategory.LOCATIONS));assertFalse(selected.contains(BackupCategory.BOOKMARKS));
            Set<File> before=exports(model(host.get()));settings.requireView().findViewWithTag("backup.action.export").performClick();await(model(host.get()),BackupOperationModel.Phase.DESTINATION_PENDING);
            var request=Shadows.shadowOf(host.get()).getNextStartedActivityForResult();assertNotNull(request);assertEquals(Intent.ACTION_CREATE_DOCUMENT,request.intent.getAction());
            assertEquals(selected,LogicalBackupArchive.readCategories(newExport(model(host.get()),before),()->false).keySet());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void receivingCategorySelectionIsTemporaryAndDisablesEveryAbsentDomain()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{var prefs=PreferenceManager.getDefaultSharedPreferences(host.get());prefs.edit().clear().commit();BackupWorkspace workspace=new BackupWorkspace(host.get(),Set.of(BackupCategory.BOOKMARKS),Set.of(BackupCategory.BOOKMARKS),s->{},()->{},()->{},List.of(),List.of());host.get().setContentView(workspace);workspace.measure(View.MeasureSpec.makeMeasureSpec(628,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(468,View.MeasureSpec.EXACTLY));workspace.layout(0,0,628,468);assertEquals(Set.of(BackupCategory.BOOKMARKS),workspace.selection());for(BackupCategory category:BackupCategory.values()){View option=workspace.findViewWithTag("backup.category."+category.name());assertEquals(category==BackupCategory.BOOKMARKS,option.isEnabled());}View bookmark=workspace.findViewWithTag("backup.category.BOOKMARKS");bookmark.requestFocus();workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(workspace.findViewWithTag("backup.details").hasFocus());workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));assertTrue(bookmark.hasFocus());bookmark.performClick();assertTrue(workspace.selection().isEmpty());assertTrue(prefs.getAll().isEmpty());capture(workspace,"0139-settings-restore-selection");}finally{host.pause().stop().destroy();}
    }
    @Test public void exportLaunchesAndroidPickerOnlyAfterPrivateVerification()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());enter(settings);BackupOperationModel model=model(host.get());Set<File> before=exports(model);model.create(Set.of(BackupCategory.APPEARANCE));await(model,BackupOperationModel.Phase.DESTINATION_PENDING);var request=Shadows.shadowOf(host.get()).getNextStartedActivityForResult();assertNotNull(request);assertEquals(Intent.ACTION_CREATE_DOCUMENT,request.intent.getAction());assertEquals("application/zip",request.intent.getType());assertTrue(request.intent.hasCategory(Intent.CATEGORY_OPENABLE));File exported=newExport(model,before);assertEquals(Set.of(BackupCategory.APPEARANCE),LogicalBackupArchive.readCategories(exported,()->false).keySet());Shadows.shadowOf(host.get()).receiveResult(request.intent,android.app.Activity.RESULT_CANCELED,null);await(model,BackupOperationModel.Phase.CANCELLED);assertTrue(model.current().retryExport);assertTrue(exported.exists());}finally{host.pause().stop().destroy();}
    }
    @Test public void connectedRestoreRequiresPreviewAndExplicitConfirmationBeforeWriting()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());enter(settings);settings.backup.enter();var prefs=PreferenceManager.getDefaultSharedPreferences(model(host.get()).getApplication());prefs.edit().putInt("preview_accent41",7).commit();File file=new File(host.get().getCacheDir(),"incoming.zip");JSONObject payload=new JSONObject().put("schema",1).put("category","APPEARANCE").put("values",new JSONObject().put("preview_accent41",new JSONObject().put("type","INT").put("value",19)));LogicalBackupArchive.writeCategories(file,Map.of(BackupCategory.APPEARANCE,payload),()->false);BackupOperationModel model=model(host.get());model.open(new FileInputStream(file));await(model,BackupOperationModel.Phase.REVIEW);layout(settings.requireView());assertFalse(operation(settings).findViewWithTag("backup.category.HISTORY").isEnabled());assertEquals(7,prefs.getInt("preview_accent41",0));operation(settings).findViewWithTag("backup.create").performClick();await(model,BackupOperationModel.Phase.RESTORE_PREVIEW);assertEquals(7,prefs.getInt("preview_accent41",0));operation(settings).findViewWithTag("backup.operation:Restore selected categories").performClick();await(model,BackupOperationModel.Phase.RESTORED);assertEquals(19,prefs.getInt("preview_accent41",0));assertTrue(file.exists());}finally{host.pause().stop().destroy();}
    }
    @Test public void leftFromRestoreReviewClosesArchiveBeforeNextExport()throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            Settings settings=add(host.get());enter(settings);BackupOperationModel model=model(host.get());
            File file=new File(host.get().getCacheDir(),"left-review.zip");
            JSONObject payload=new JSONObject().put("schema",1).put("category","APPEARANCE").put("values",new JSONObject());
            LogicalBackupArchive.writeCategories(file,Map.of(BackupCategory.APPEARANCE,payload),()->false);
            model.open(new FileInputStream(file));await(model,BackupOperationModel.Phase.REVIEW);
            Map<String,?> unchanged=PreferenceManager.getDefaultSharedPreferences(host.get()).getAll();
            View frame=operation(settings);assertFalse(frame.findViewWithTag("backup.category.HISTORY").isEnabled());
            assertTrue(frame.findViewWithTag("backup.category.APPEARANCE").requestFocus());
            frame.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));
            await(model,BackupOperationModel.Phase.IDLE);
            View export=settings.requireView().findViewWithTag("backup.action.export");assertTrue(export.hasFocus());
            assertEquals(unchanged,PreferenceManager.getDefaultSharedPreferences(host.get()).getAll());
            assertTrue("Export choices are independent of the previous restore archive",settings.requireView().findViewWithTag("backup.category.HISTORY").isEnabled());
            export.performClick();await(model,BackupOperationModel.Phase.DESTINATION_PENDING);
            assertTrue(file.exists());
        } finally {host.pause().stop().destroy();}
    }
    @Test public void normalTvWorkspaceKeepsEveryCategoryWithinPhysicalBoundsWithCompleteLabels()throws Exception {
        verifyConnectedGeometry("0139-settings-backup-normal");
    }
    @Test @Config(qualifiers="w640dp-h360dp-land-mdpi") public void smallTvWorkspaceKeepsEveryCategoryWithinItsOwnPanelWithCompleteLabels()throws Exception {
        verifyConnectedGeometry("0139-settings-backup-small");
    }
    @Test @Config(qualifiers="w1280dp-h720dp-land-mdpi") public void hdPhysicalBounds()throws Exception{verifyConnectedGeometry("0139-backup-1280");}
    @Test @Config(qualifiers="w1920dp-h1080dp-land-mdpi") public void fullHdPhysicalBounds()throws Exception{verifyConnectedGeometry("0139-backup-1920");}
    @Test @Config(qualifiers="w3840dp-h2160dp-land-mdpi") public void ultraHdPhysicalBounds()throws Exception{verifyConnectedGeometry("0139-backup-3840");}
    private void verifyConnectedGeometry(String name)throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            Settings settings=add(host.get());enter(settings);View root=settings.requireView();settings.backup.enter();
            for(BackupCategory category:BackupCategory.ordered()){
                TextView option=root.findViewWithTag("backup.category."+category.name());assertTrue(category.name(),option.requestFocus());layout(root);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();android.graphics.Rect visible=new android.graphics.Rect();assertTrue(category.name(),option.getGlobalVisibleRect(visible));assertEquals(category.name(),option.getHeight(),visible.height());assertEquals(category.name(),option.getWidth(),visible.width());assertTrue(category.name(),option.getHeight()>=MenuFoundation.dp(root.getContext(),44));
            }
            root.findViewWithTag("backup.action.export").requestFocus();layout(root);capture(root,name.replace("0139","0140"));
        }finally{host.pause().stop().destroy();}
    }
}
