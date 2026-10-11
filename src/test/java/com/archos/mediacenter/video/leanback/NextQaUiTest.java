package com.archos.mediacenter.video.leanback;
import android.app.Application;
import android.view.*;
import android.app.Dialog;
import androidx.preference.PreferenceManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextQaUiTest {
 @Before public void offline(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drain() throws Exception {PreviewAsyncFixtures.drain();}
 @Test public void welcomeDismissalPersistsAndPlaceholderRestoresOpenerWithoutMediaAction() throws Exception {
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{var prefs=PreferenceManager.getDefaultSharedPreferences(host.get());prefs.edit().clear().commit();final int[] opened={0};PreviewPages pages=new PreviewPages(host.get(),(holder,item)->opened[0]++);host.get().setContentView(pages);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());PreviewPagesTest.layout(pages);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
   Dialog welcome=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertNotNull("First-run welcome exists",welcome);assertTrue("Welcome is visible",welcome.isShowing());PreviewPagesTest.findText(welcome.getWindow().getDecorView(),"Explore Supernova").performClick();assertTrue("Explicit dismissal is persisted",prefs.getBoolean("supernova_welcome_dismissed",false));
   View info=pages.findViewWithTag("hero:info");assertNotNull(info);assertTrue(info.requestFocus());Shadows.shadowOf(host.get()).setCurrentFocus(info);info.performClick();Dialog explanation=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(explanation.isShowing());explanation.dismiss();assertSame(info,pages.findFocus());assertEquals(0,opened[0]);assertFalse(prefs.getBoolean("supernova_onboarding_complete",false));assertNull(pages.findViewWithTag("browser.view"));PreviewPagesTest.capture(pages,"next-qa-first-run-home");
  }finally{host.pause().stop().destroy();}
 }
 @Test @Config(qualifiers="w720dp-h405dp-land-mdpi") public void narrowWelcomeKeepsActionsInsideSafeViewport() throws Exception {
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Dialog dialog=PreviewDialog.message(host.get(),"Welcome to Supernova","Add folders from local storage, external drives or network shares. Scanning starts after confirmation.",true,new String[]{"Network & Files","Explore Supernova"},0,n->{});View panel=dialog.findViewById(android.R.id.content);PreviewPagesTest.layout(panel);assertEquals(4,TvLayout.gridColumns(host.get()));assertTrue(dialog.getWindow().getAttributes().width<=TvLayout.safeWidth(host.get()));assertNotNull(panel.findViewWithTag("semantic:dialog.action.1"));PreviewPagesTest.capture(PreviewHomeVisualTest.measure(dialog),"next-qa-welcome-narrow");dialog.dismiss();}finally{host.pause().stop().destroy();}
 }
 @Test public void keyboardCancelRollsBackAndDoneOnlyCommitsName() throws Exception {
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{final String[] value={"Previous"};PreviewTextInput.show(host.get(),"Page Name",value[0],40,v->value[0]=v);Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();View root=dialog.getWindow().getDecorView();root.findViewWithTag("semantic:keyboard:Clear").performClick();root.findViewWithTag("semantic:keyboard:A").performClick();root.findViewWithTag("semantic:keyboard:Cancel").performClick();assertEquals("Previous",value[0]);assertNull(CustomLibraryPage.load(host.get()));PreviewTextInput.show(host.get(),"Page Name",value[0],40,v->value[0]=v);dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();root=dialog.getWindow().getDecorView();root.findViewWithTag("semantic:keyboard:Clear").performClick();root.findViewWithTag("semantic:keyboard:A").performClick();root.findViewWithTag("semantic:keyboard:Done").performClick();assertEquals("A",value[0]);assertNull(CustomLibraryPage.load(host.get()));}finally{host.pause().stop().destroy();}
 }
 @Test @Config(qualifiers="w720dp-h405dp-land-mdpi") public void enlargedFontKeepsDialogAndKeyboardActionsReachable() throws Exception {
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   var resources=host.get().getResources();var configuration=new android.content.res.Configuration(resources.getConfiguration());configuration.fontScale=1.5f;var metrics=new android.util.DisplayMetrics();metrics.setTo(resources.getDisplayMetrics());metrics.scaledDensity=1.5f*metrics.density;resources.updateConfiguration(configuration,metrics);
   Dialog welcome=PreviewDialog.message(host.get(),"Welcome to Supernova","Add movies and TV shows from local storage, external drives or network shares. Supernova will only begin scanning once you've selected and confirmed your folders.",true,new String[]{"Network & Files","Explore Supernova"},0,n->{});View root=PreviewHomeVisualTest.measure(welcome);assertTrue(welcome.getWindow().getAttributes().height<=Math.round(TvLayout.safeHeight(host.get())*.8f));
   for(int i=0;i<2;i++){View action=root.findViewWithTag("semantic:dialog.action."+i);android.graphics.Rect bounds=new android.graphics.Rect();action.getDrawingRect(bounds);((android.view.ViewGroup)root).offsetDescendantRectToMyCoords(action,bounds);assertTrue(bounds.top>=0);assertTrue(bounds.bottom<=root.getHeight());assertTrue(action.requestFocus());}PreviewPagesTest.capture(root,"next-qa-welcome-large-font");welcome.dismiss();
   PreviewTextInput.show(host.get(),"Page Name","",40,value->{});Dialog keyboard=org.robolectric.shadows.ShadowDialog.getLatestDialog();root=PreviewHomeVisualTest.measure(keyboard);assertTrue(keyboard.getWindow().getAttributes().height<=Math.round(TvLayout.safeHeight(host.get())*.9f));android.widget.TextView backspace=root.findViewWithTag("semantic:keyboard:Backspace");assertTrue(backspace.getLayout().getHeight()<=backspace.getHeight());View done=root.findViewWithTag("semantic:keyboard:Done");assertTrue(done.requestFocus());Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertTrue(done.isShown());PreviewPagesTest.capture(root,"next-qa-keyboard-large-font");keyboard.dismiss();
  }finally{host.pause().stop().destroy();}
 }
 @Test public void cachedHeroRestartsCancelledColdArtworkAndRetainsReadyFrame(){
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();var picasso=org.mockito.Mockito.mock(com.squareup.picasso.Picasso.class);var request=org.mockito.Mockito.mock(com.squareup.picasso.RequestCreator.class,org.mockito.Mockito.RETURNS_SELF);
  try(var singleton=org.mockito.Mockito.mockStatic(com.squareup.picasso.Picasso.class)){singleton.when(com.squareup.picasso.Picasso::get).thenReturn(picasso);org.mockito.Mockito.when(picasso.load(org.mockito.ArgumentMatchers.any(android.net.Uri.class))).thenReturn(request);
   try{var movie=org.mockito.Mockito.mock(com.archos.mediacenter.video.browser.adapters.object.Movie.class);org.mockito.Mockito.when(movie.getName()).thenReturn("Cold fixture");var entry=new PreviewLibraryLoader.Entry(movie,1,0,"");entry.backdrop=android.net.Uri.parse("file:///fixture/backdrop.jpg");PreviewFeaturedCard card=new PreviewFeaturedCard(host.get(),java.util.List.of(entry),0,()->{},()->{});host.get().setContentView(card);PreviewPagesTest.layout(card);org.mockito.Mockito.verify(request).into(org.mockito.ArgumentMatchers.any(android.widget.ImageView.class),org.mockito.ArgumentMatchers.any(com.squareup.picasso.Callback.class));
    host.get().setContentView(new android.widget.FrameLayout(host.get()));host.get().setContentView(card);PreviewPagesTest.layout(card);org.mockito.Mockito.verify(request,org.mockito.Mockito.times(2)).into(org.mockito.ArgumentMatchers.any(android.widget.ImageView.class),org.mockito.ArgumentMatchers.any(com.squareup.picasso.Callback.class));
    android.widget.ImageView artwork=(android.widget.ImageView)((android.view.ViewGroup)card.findViewWithTag("semantic:featured.active")).getChildAt(0);var ready=new android.graphics.drawable.ColorDrawable(android.graphics.Color.BLUE);artwork.setImageDrawable(ready);host.get().setContentView(new android.widget.FrameLayout(host.get()));host.get().setContentView(card);PreviewPagesTest.layout(card);assertSame(ready,artwork.getDrawable());org.mockito.Mockito.verify(request,org.mockito.Mockito.times(2)).into(org.mockito.ArgumentMatchers.any(android.widget.ImageView.class),org.mockito.ArgumentMatchers.any(com.squareup.picasso.Callback.class));
   }finally{host.pause().stop().destroy();}
  }
 }
}
