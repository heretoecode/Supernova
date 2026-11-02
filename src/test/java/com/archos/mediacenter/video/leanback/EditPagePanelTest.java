package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.view.*;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/**
 * The approved Edit Page side panel over the live custom page
 * (docs/design/EDIT_PAGE_SIDE_PANEL_APPROVED_2026-11-02.md). 1920×1080 screen.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application=com.archos.mediacenter.video.CustomApplication.class,sdk=30,qualifiers="w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class EditPagePanelTest {
 @Before public void setup(){var app=RuntimeEnvironment.getApplication();Shadows.shadowOf(app).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");PreviewAsyncFixtures.offlineTransport();PreferenceManager.getDefaultSharedPreferences(app).edit().putBoolean("try_new_ui",true).putBoolean("supernova_onboarding_complete",true).remove(CustomLibraryPage.KEY).commit();}
 @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}

 static void layout(View view){for(int n=0;n<4;n++){view.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));view.layout(0,0,1920,1080);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(40));}}
 static void layoutPanel(Dialog dialog){View decor=dialog.getWindow().getDecorView();WindowManager.LayoutParams a=dialog.getWindow().getAttributes();for(int n=0;n<3;n++){decor.measure(View.MeasureSpec.makeMeasureSpec(a.width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.AT_MOST));decor.layout(0,0,decor.getMeasuredWidth(),decor.getMeasuredHeight());Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}}
 /** The page and the panel window composited as they appear on the TV. */
 static void capture(CustomLibraryActivity activity,String name)throws Exception{
  View root=activity.getWindow().getDecorView();layout(root);Dialog d=activity.editor().dialog();layoutPanel(d);View panel=d.getWindow().getDecorView();WindowManager.LayoutParams a=d.getWindow().getAttributes();
  android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(1920,1080,android.graphics.Bitmap.Config.ARGB_8888);android.graphics.Canvas canvas=new android.graphics.Canvas(image);canvas.drawColor(0xff05080d);root.draw(canvas);canvas.save();canvas.translate(1920-a.x-a.width,a.y);panel.draw(canvas);canvas.restore();
  java.io.File file=new java.io.File("build/reports/preview-ui/"+name+".png");file.getParentFile().mkdirs();try(java.io.FileOutputStream out=new java.io.FileOutputStream(file)){image.compress(android.graphics.Bitmap.CompressFormat.PNG,100,out);}image.recycle();
 }
 static Object workspace(CustomLibraryActivity a){return ReflectionHelpers.getField(a,"workspace");}
 static CustomLibraryPage draft(CustomLibraryActivity a){return ReflectionHelpers.getField(workspace(a),"draft");}
 static View in(CustomLibraryActivity a,String tag){return a.editor().findViewWithTag(tag);}
 static String value(View row){return ((TextView)row.findViewWithTag("value")).getText().toString();}
 static void key(CustomLibraryActivity a,View row,int code){assertTrue(row.requestFocus());row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,code));layoutPanel(a.editor().dialog());}
 static CustomLibraryPage saved(String name){CustomLibraryPage page=new CustomLibraryPage();page.name=name;assertTrue(page.save(RuntimeEnvironment.getApplication()));return page;}
 static TextView customTab(CustomLibraryActivity a){View root=a.getWindow().getDecorView();View tab=root.findViewWithTag("semantic:topnav.custom");return tab instanceof TextView?(TextView)tab:null;}

 @Test public void newPageOpensThePanelOverThePageWithAPlaceholderAndDraftTab()throws Exception{
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{CustomLibraryActivity a=host.get();layout(a.getWindow().getDecorView());
   assertNotNull("Editor opens for a page that does not exist yet",a.editor());assertTrue(a.editor().isShowing());
   WindowManager.LayoutParams w=a.editor().dialog().getWindow().getAttributes();assertEquals(690,w.width);assertEquals(Gravity.TOP|Gravity.END,w.gravity);assertEquals(40,w.x);assertEquals(96,w.y);assertEquals(0,w.flags&WindowManager.LayoutParams.FLAG_DIM_BEHIND);
   layoutPanel(a.editor().dialog());assertTrue("Page Name focused",in(a,"edit.row.name").hasFocus());assertEquals("New Page",value(in(a,"edit.row.name")));
   assertEquals("Save Page",((TextView)in(a,"edit.save")).getText().toString());assertNull("No Delete for a page never saved",in(a,"edit.row.delete"));
   assertNotNull(PreviewPagesTest.findText(a.getWindow().getDecorView(),CustomLibraryActivity.NEW_PAGE_PLACEHOLDER));
   TextView tab=customTab(a);if(tab!=null){assertTrue(tab.getText().toString().startsWith("New Page"));assertTrue(tab.getAlpha()<1f);}
   capture(a,"edit-page-new");
   in(a,"edit.row.type").performClick();layout(a.getWindow().getDecorView());assertEquals("movies",draft(a).type);
   assertNull("The page fills in once something is chosen",PreviewPagesTest.findText(a.getWindow().getDecorView(),CustomLibraryActivity.NEW_PAGE_PLACEHOLDER));
  }finally{host.pause().stop().destroy();}
 }

 @Test public void rowsAreInTheApprovedOrderAndSteppersEditTheDraft()throws Exception{
  CustomLibraryPage page=saved("Documentaries");page.language="fr";page.genres.add("Documentary");page.genres.add("Nature");page.genres.add("History");assertTrue(page.save(RuntimeEnvironment.getApplication()));
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{CustomLibraryActivity a=host.get();ReflectionHelpers.callInstanceMethod(workspace(a),"edit",ReflectionHelpers.ClassParameter.from(int.class,0));layout(a.getWindow().getDecorView());layoutPanel(a.editor().dialog());
   String[] tags={"edit.row.name","edit.row.type","edit.row.genres","edit.row.match","edit.row.year","edit.row.watched","edit.row.rating","edit.row.language","edit.row.view","edit.row.sort","edit.row.order","edit.row.delete"};int previous=-1;
   for(String tag:tags){View row=in(a,tag);assertNotNull(tag,row);int top=row.getTop();assertTrue(tag,top>previous);previous=top;}
   assertEquals("Save Changes",((TextView)in(a,"edit.save")).getText().toString());assertEquals("Documentary, History +1",value(in(a,"edit.row.genres")));
   assertEquals("Order labels follow the title sort","A–Z",((TextView)PreviewPagesTest.findText(in(a,"edit.row.order"),"A–Z")).getText().toString());
   capture(a,"edit-page-existing");
   key(a,in(a,"edit.row.watched"),KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals("watched",draft(a).watched);assertEquals("Watched",value(in(a,"edit.row.watched")));
   key(a,in(a,"edit.row.rating"),KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals(5d,draft(a).rating,0);
   key(a,in(a,"edit.row.match"),KeyEvent.KEYCODE_DPAD_RIGHT);assertTrue(draft(a).allGenres);
   key(a,in(a,"edit.row.sort"),KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals("year",draft(a).sort);
   key(a,in(a,"edit.row.order"),KeyEvent.KEYCODE_DPAD_LEFT);assertTrue("Newest is descending",draft(a).descending);
   key(a,in(a,"edit.row.name"),KeyEvent.KEYCODE_DPAD_UP);assertTrue("Up from the first row reaches Save",in(a,"edit.save").hasFocus());
   assertEquals("Nothing stored before Save","Documentaries",CustomLibraryPage.load(a).name);assertEquals("all",CustomLibraryPage.load(a).watched);
  }finally{host.pause().stop().destroy();}
 }

 @Test public void pickListsOpenInsideThePanelAndReturnToTheirRow()throws Exception{
  CustomLibraryPage page=saved("Films");page.language="fr";page.genres.add("Drama");assertTrue(page.save(RuntimeEnvironment.getApplication()));
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{CustomLibraryActivity a=host.get();ReflectionHelpers.callInstanceMethod(workspace(a),"edit",ReflectionHelpers.ClassParameter.from(int.class,0));layoutPanel(a.editor().dialog());
   Dialog before=org.robolectric.shadows.ShadowDialog.getLatestDialog();in(a,"edit.row.language").performClick();layoutPanel(a.editor().dialog());assertSame("No second pop-up",before,org.robolectric.shadows.ShadowDialog.getLatestDialog());
   assertNotNull(in(a,"edit.page.back"));View any=in(a,"edit.language.0");View french=in(a,"edit.language.1");assertTrue("Opens on the current value",french.hasFocus());any.requestFocus();any.performClick();layoutPanel(a.editor().dialog());
   assertEquals("",draft(a).language);assertTrue(in(a,"edit.row.language").hasFocus());
   in(a,"edit.row.genres").performClick();layoutPanel(a.editor().dialog());View drama=in(a,"edit.genre.0");assertEquals(View.VISIBLE,drama.findViewWithTag("tick").getVisibility());drama.performClick();assertTrue(draft(a).genres.isEmpty());assertEquals(View.INVISIBLE,drama.findViewWithTag("tick").getVisibility());
   assertTrue(a.editor().closePage());assertEquals("Any",value(in(a,"edit.row.genres")));assertFalse(a.editor().closePage());
  }finally{host.pause().stop().destroy();}
 }

 @Test public void backWithChangesOffersKeepEditingSaveDiscardAndDiscardRestores()throws Exception{
  saved("Kept page");var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{CustomLibraryActivity a=host.get();Object w=workspace(a);ReflectionHelpers.callInstanceMethod(w,"edit",ReflectionHelpers.ClassParameter.from(int.class,0));
   draft(a).name="Renamed";ReflectionHelpers.callInstanceMethod(w,"changed");TextView tab=customTab(a);if(tab!=null)assertEquals("The tab follows the draft name","Renamed",tab.getText().toString());
   ReflectionHelpers.callInstanceMethod(w,"back");View decor=org.robolectric.shadows.ShadowDialog.getLatestDialog().getWindow().getDecorView();assertNotNull(PreviewPagesTest.findText(decor,"Keep Editing"));assertNotNull(PreviewPagesTest.findText(decor,"Save"));
   PreviewPagesTest.findText(decor,"Discard").performClick();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
   assertEquals("Kept page",draft(a).name);assertFalse(ReflectionHelpers.getField(w,"editing"));assertNull(a.editor());assertEquals("Kept page",CustomLibraryPage.load(a).name);
   if(tab!=null)assertEquals("Kept page",customTab(a).getText().toString());
  }finally{host.pause().stop().destroy();}
 }

 @Test public void saveValidatesThenStoresAndClosesThePanel()throws Exception{
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{CustomLibraryActivity a=host.get();Object w=workspace(a);
   draft(a).name="";in(a,"edit.save").performClick();assertTrue("Invalid name keeps the editor",ReflectionHelpers.getField(w,"editing"));assertNull(CustomLibraryPage.load(a));org.robolectric.shadows.ShadowDialog.getLatestDialog().dismiss();
   draft(a).name="QA Films";in(a,"edit.save").performClick();layout(a.getWindow().getDecorView());
   assertEquals("QA Films",CustomLibraryPage.load(a).name);assertFalse(ReflectionHelpers.getField(w,"editing"));assertNull(a.editor());
   assertNotNull("The page shows its toolbar with Edit",a.getWindow().getDecorView().findViewWithTag("custom.control.edit"));
   a.getWindow().getDecorView().findViewWithTag("custom.control.edit").performClick();assertNotNull(a.editor());assertEquals("QA Films",value(in(a,"edit.row.name")));
  }finally{host.pause().stop().destroy();}
 }

 @Test public void restoredStateReopensTheEditorWithItsDraft(){
  saved("Saved name");android.os.Bundle state=new android.os.Bundle();CustomLibraryPage page=CustomLibraryPage.load(RuntimeEnvironment.getApplication());String original=page.encode();page.name="Draft name";state.putString("draft",page.encode());state.putString("original",original);state.putBoolean("editing",true);
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).create(state).start().resume().visible();try{CustomLibraryActivity a=host.get();assertNotNull(a.editor());assertEquals("Draft name",value(in(a,"edit.row.name")));}finally{host.pause().stop().destroy();}
 }

 @Test public void emptyResultIsAnAmberWarning(){
  saved("Nothing");var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{CustomLibraryActivity a=host.get();ReflectionHelpers.callInstanceMethod(workspace(a),"edit",ReflectionHelpers.ClassParameter.from(int.class,0));
   TextView matches=(TextView)in(a,"edit.matches");assertEquals("No titles match these settings",matches.getText().toString());assertEquals(SidePanelKit.WARNING,matches.getCurrentTextColor());
  }finally{host.pause().stop().destroy();}
 }
}
