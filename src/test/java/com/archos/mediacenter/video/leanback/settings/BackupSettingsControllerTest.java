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
    private void layout(View view){var resources=view.getResources();int width=Math.round(resources.getConfiguration().screenWidthDp*resources.getDisplayMetrics().density),height=Math.round(resources.getConfiguration().screenHeightDp*resources.getDisplayMetrics().density);view.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));view.layout(0,0,width,height);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    private void capture(View view,String name)throws Exception{Bitmap image=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);view.draw(new Canvas(image));File file=new File("build/reports/preview-ui/"+name+".png");file.getParentFile().mkdirs();try(FileOutputStream output=new FileOutputStream(file)){image.compress(Bitmap.CompressFormat.PNG,100,output);}}
    private Settings add(TopNavigationTest.Host host){host.getIntent().putExtra("preview_settings_category","Advanced");Settings settings=new Settings();host.getSupportFragmentManager().beginTransaction().add(android.R.id.content,settings,"backup-fixture").commitNow();layout(settings.requireView());return settings;}
    private View enter(Settings settings){View root=settings.requireView();root.findViewWithTag("semantic:settings:category:Advanced").performClick();layout(root);View child=root.findViewWithTag("semantic:settings:section:preview_backup_restore");assertNotNull(child);child.requestFocus();layout(root);return child;}
    private BackupOperationModel model(TopNavigationTest.Host host){return new ViewModelProvider(host).get(BackupOperationModel.class);}
    private Set<File> exports(BackupOperationModel model){File[] files=new File(model.getApplication().getCacheDir(),"supernova-logical-backups").listFiles(f->f.getName().endsWith(".zip"));return files==null?new HashSet<>():new HashSet<>(Arrays.asList(files));}
    private File newExport(BackupOperationModel model,Set<File> before){Set<File> after=exports(model);after.removeAll(before);assertEquals(1,after.size());return after.iterator().next();}
    private void await(BackupOperationModel model,BackupOperationModel.Phase phase)throws Exception{long deadline=System.nanoTime()+10_000_000_000L;while((model.current().phase!=phase||model.isBusy())&&System.nanoTime()<deadline){Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();Thread.sleep(5);}Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertEquals(model.current().message,phase,model.current().phase);assertFalse(model.isBusy());}
    @Test public void realSettingsRailKeepsItsWidthAndAllCategoriesOccupyOnlyRightWorkspace()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());View root=settings.requireView(),parent=root.findViewWithTag("semantic:settings:category:Advanced");int leftWidth=((View)parent.getParent()).getWidth();View child=enter(settings);assertEquals(leftWidth,((View)parent.getParent()).getWidth());assertTrue(child.hasFocus());View frame=root.findViewWithTag("settings.backup.workspace");assertEquals(View.VISIBLE,frame.getVisibility());for(BackupCategory category:BackupCategory.values()){View option=frame.findViewWithTag("backup.category."+category.name());assertNotNull(category.name(),option);assertTrue(category.name(),option.getWidth()>0&&option.getHeight()>0);}child.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));layout(root);assertTrue(frame.findViewWithTag("backup.create").hasFocus());frame.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(frame.findViewWithTag("backup.category.APPEARANCE").hasFocus());capture(root,"0139-settings-backup-main");}finally{host.pause().stop().destroy();}
    }
    @Test public void connectedFlatInformationBackRestoresOpenerThenPermanentSidebar()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());View child=enter(settings),root=settings.requireView();settings.backup.enter();View details=root.findViewWithTag("backup.details");details.requestFocus();details.performClick();layout(root);assertNotNull(root.findViewWithTag("backup.details.page"));capture(root,"0139-settings-backup-details");assertTrue(settings.backup.back());layout(root);assertTrue("Details opener regained focus",root.findViewWithTag("backup.details").hasFocus());View instructions=root.findViewWithTag("backup.instructions");instructions.requestFocus();instructions.performClick();layout(root);assertNotNull(root.findViewWithTag("backup.instructions.page"));capture(root,"0139-settings-backup-instructions");assertTrue(settings.backup.back());layout(root);assertTrue("Instructions opener regained focus",root.findViewWithTag("backup.instructions").hasFocus());assertTrue(settings.backup.back());assertTrue("Permanent sidebar opener regained focus",child.hasFocus());}finally{host.pause().stop().destroy();}
    }
    @Test public void defaultSelectionIncludesMatchesAndLaunchesPickerWithEverySelectedCodec()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreferenceManager.getDefaultSharedPreferences(host.get()).edit().clear().commit();Settings settings=add(host.get());enter(settings);
            // Read the selected workspace through its enclosing view without assuming the category-row structure.
            View option=settings.requireView().findViewWithTag("backup.category.MATCHES");android.view.ViewParent parent=option.getParent();while(!(parent instanceof BackupWorkspace))parent=parent.getParent();
            Set<BackupCategory> selected=((BackupWorkspace)parent).selection();assertTrue(selected.contains(BackupCategory.MATCHES));assertFalse(selected.contains(BackupCategory.LOCATIONS));assertFalse(selected.contains(BackupCategory.BOOKMARKS));
            Set<File> before=exports(model(host.get()));settings.requireView().findViewWithTag("backup.create").performClick();await(model(host.get()),BackupOperationModel.Phase.DESTINATION_PENDING);
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
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Settings settings=add(host.get());enter(settings);settings.backup.enter();var prefs=PreferenceManager.getDefaultSharedPreferences(model(host.get()).getApplication());prefs.edit().putInt("preview_accent41",7).commit();File file=new File(host.get().getCacheDir(),"incoming.zip");JSONObject payload=new JSONObject().put("schema",1).put("category","APPEARANCE").put("values",new JSONObject().put("preview_accent41",new JSONObject().put("type","INT").put("value",19)));LogicalBackupArchive.writeCategories(file,Map.of(BackupCategory.APPEARANCE,payload),()->false);BackupOperationModel model=model(host.get());model.open(new FileInputStream(file));await(model,BackupOperationModel.Phase.REVIEW);layout(settings.requireView());assertFalse(settings.requireView().findViewWithTag("backup.category.HISTORY").isEnabled());assertEquals(7,prefs.getInt("preview_accent41",0));settings.requireView().findViewWithTag("backup.create").performClick();await(model,BackupOperationModel.Phase.RESTORE_PREVIEW);assertEquals(7,prefs.getInt("preview_accent41",0));settings.requireView().findViewWithTag("backup.operation:Restore selected categories").performClick();await(model,BackupOperationModel.Phase.RESTORED);assertEquals(19,prefs.getInt("preview_accent41",0));assertTrue(file.exists());}finally{host.pause().stop().destroy();}
    }
    @Test @Config(qualifiers="w640dp-h360dp-land-mdpi") public void smallTvWorkspaceKeepsEveryCategoryWithinItsOwnPanelWithCompleteLabels()throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            Settings settings=add(host.get());enter(settings);View root=settings.requireView();layout(root);capture(root,"0139-settings-backup-small");View panel=root.findViewWithTag("backup.options");assertNotNull(panel);
            org.json.JSONObject measured=new org.json.JSONObject().put("viewportWidth",root.getWidth()).put("viewportHeight",root.getHeight()).put("panelWidth",panel.getWidth()).put("panelHeight",panel.getHeight());org.json.JSONArray items=new org.json.JSONArray();
            ViewParent parent=panel.getParent();while(parent!=null&&!(parent instanceof SharedThreePanel))parent=parent.getParent();if(parent instanceof SharedThreePanel)measured.put("sidebarWidth",((SharedThreePanel)parent).getChildAt(0).getWidth());
            for(BackupCategory category:BackupCategory.values()){TextView option=root.findViewWithTag("backup.category."+category.name());int[] location=new int[2];option.getLocationOnScreen(location);items.put(new org.json.JSONObject().put("category",category.name()).put("x",location[0]).put("y",location[1]).put("width",option.getWidth()).put("height",option.getHeight()).put("textSizePixels",option.getTextSize()).put("textBottom",option.getLayout().getLineBottom(option.getLineCount()-1)).put("usableHeight",option.getHeight()-option.getPaddingBottom()));}
            measured.put("categories",items);java.nio.file.Files.writeString(new File("build/reports/preview-ui/0139-settings-backup-small.json").toPath(),measured.toString(2));
            for(BackupCategory category:BackupCategory.values()){
                TextView option=root.findViewWithTag("backup.category."+category.name());assertNotNull(option);int[] item=new int[2],container=new int[2];option.getLocationOnScreen(item);panel.getLocationOnScreen(container);
                assertTrue(category+" right edge outside physical screen",item[0]+option.getWidth()<=root.getWidth());
                assertTrue(category+" top in panel: "+(item[1]-container[1])+"; panel height="+panel.getHeight(),item[1]>=container[1]);assertTrue(category+" bottom in panel",item[1]+option.getHeight()<=container[1]+panel.getHeight());
                assertTrue(category+" full text",option.getLayout().getLineBottom(option.getLineCount()-1)<=option.getHeight()-option.getPaddingBottom());for(int line=0;line<option.getLineCount();line++)assertEquals(0,option.getLayout().getEllipsisCount(line));
            }
        }finally{host.pause().stop().destroy();}
    }

}
