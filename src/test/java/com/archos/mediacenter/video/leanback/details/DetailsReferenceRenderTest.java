package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Illustrative reference-content fixture, not retrieved ratings, real portrait or device evidence. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class DetailsReferenceRenderTest {
 @Before public void isolate(){PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
 @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
 @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void referenceCompositionAt720p()throws Exception{render();}
 @Test @Config(qualifiers="w960dp-h540dp-land-xhdpi") public void referenceCompositionAt1080p()throws Exception{render();}
 @Test @Config(qualifiers="w960dp-h540dp-land-xxxhdpi") public void referenceCompositionAt4k()throws Exception{render();}
 @Test @Config(qualifiers="w960dp-h540dp-land-xhdpi") public void populatedGridAt150PercentText()throws Exception{RuntimeEnvironment.setFontScale(1.5f);try{render();}finally{RuntimeEnvironment.setFontScale(1f);}}
 private void render()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});TopNavigation root=new TopNavigation(host.get(),page,i->{},page::atTop);host.get().setContentView(root);
   String synopsis="A group of misfit kids in a small town embark on a daring adventure to find a legendary pirate's treasure and save their homes from foreclosure. Along the way, they encounter dangerous criminals, clever traps and unexpected friendships that test their courage and loyalty.";
   Movie movie=new Movie(1,"file:///Movies/The Goonies (1985)/The Goonies.mkv","The Goonies",1,synopsis,1985,7.7f,"",null,6840000,0,0,0,false,false,false,false,1,0,1920,1080,"DTS-HD MA 5.1","H.264",null,null,0,1,12400000000L,0);page.bind(movie);
   PreviewDetailsData.Result data=new PreviewDetailsData.Result();data.details=new JSONObject().put("original_title","The Goonies").put("release_date","1985-05-24").put("runtime",114).put("genres",new JSONArray("[{name:'Adventure'},{name:'Comedy'},{name:'Family'}]")).put("production_countries",new JSONArray("[{name:'United States'}]")).put("spoken_languages",new JSONArray("[{english_name:'English'}]")).put("production_companies",new JSONArray("[{name:'Amblin Entertainment'}]")).put("budget",19000000).put("revenue",61499270);
   data.credits=new JSONObject();JSONArray cast=new JSONArray(),crew=new JSONArray();String[] actors={"Sean Astin","Josh Brolin","Corey Feldman","Ke Huy Quan","Jeff Cohen","Kerri Green","Martha Plimpton","Robert Davi"},roles={"Mikey Walsh","Brand Walsh","Mouth","Data","Chunk","Andy","Stef","Jake Fratelli"},names={"Richard Donner","Steven Spielberg","Chris Columbus","Frank Marshall","Kathleen Kennedy","Harvey Bernhard","Richard Edlund","Michael Kahn"},jobs={"Director","Producer","Writer","Producer","Producer","Producer","Director of Photography","Editor"};for(int n=0;n<8;n++){cast.put(new JSONObject().put("id",n+1).put("name",actors[n]).put("character",roles[n]));crew.put(new JSONObject().put("id",n+11).put("name",names[n]).put("job",jobs[n]));}data.credits.put("cast",cast).put("crew",crew);
   // Fixture-only values from the illustrative reference; PRODUCTION remains denied.
   data.supplementary=OmdbSupplement.parse(new JSONObject("{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'7.7',imdbVotes:'447,000',Metascore:'62',Awards:'4 wins & 8 nominations. Including 1 Young Artist Award.',Ratings:[{Source:'Rotten Tomatoes',Value:'77%'}]}"),"tt0089218","movie");
   ReflectionHelpers.callInstanceMethod(page,"applyEnrichment",ReflectionHelpers.ClassParameter.from(PreviewDetailsData.Result.class,data),ReflectionHelpers.ClassParameter.from(int.class,(Integer)ReflectionHelpers.getField(page,"enrichmentGeneration")));
   layout(root);View tab=page.findViewWithTag("section:Details");assertTrue(tab.requestFocus());root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));layout(root);assertSame(tab,root.findFocus());assertFalse(root.findViewWithTag("semantic:topnav.home").isShown());
   android.graphics.Bitmap bitmap=android.graphics.Bitmap.createBitmap(root.getWidth(),root.getHeight(),android.graphics.Bitmap.Config.ARGB_8888);root.draw(new android.graphics.Canvas(bitmap));java.io.File out=new java.io.File("build/reports/preview-ui/0140-reference-fixture-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale+".png");out.getParentFile().mkdirs();try(java.io.FileOutputStream stream=new java.io.FileOutputStream(out)){bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,stream);}bitmap.recycle();writeMetrics(root,page);
   for(String name:actors)assertVisibleText(page,name);for(String name:names)assertVisibleText(page,name);assertWholeText(page,"Rotten Tomatoes");assertWholeText(page,"7.7/10");assertWholeText(page,"447k votes");
  }finally{host.pause().stop().destroy();}
 }
 private void layout(View view){var c=view.getResources().getConfiguration();float d=view.getResources().getDisplayMetrics().density;for(int n=0;n<4;n++){view.measure(View.MeasureSpec.makeMeasureSpec(Math.round(c.screenWidthDp*d),View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(Math.round(c.screenHeightDp*d),View.MeasureSpec.EXACTLY));view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}}
 private void writeMetrics(View root,PreviewMoviePage page)throws Exception{
  JSONObject metrics=new JSONObject().put("renderer","Robolectric native software canvas").put("viewport_width",root.getWidth()).put("viewport_height",root.getHeight()).put("font_scale",root.getResources().getConfiguration().fontScale);
  metrics.put("frame",rectangle(page.findViewWithTag("details.connected.frame"))).put("active_tab",rectangle(page.findViewWithTag("section:Details")));
  for(String name:new String[]{"Description","Key Information","Reception","Cast","Crew","Technical Information"}){View region=page.findViewWithTag("details.region."+name);if(region==null)region=page.findViewWithTag("semantic:details.panel."+name.toLowerCase(java.util.Locale.ROOT).replace(' ','.'));metrics.put(name,rectangle(region));}
  TextView actor=find(page,"Sean Astin");metrics.put("cast_name_text_px",actor.getTextSize()).put("portrait_diameter_px",((ViewGroup)actor.getParent()).getChildAt(0).getHeight());
  java.io.File file=new java.io.File("build/reports/preview-ui/0140-reference-fixture-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale+".json");try(java.io.FileWriter writer=new java.io.FileWriter(file)){writer.write(metrics.toString(2));}
 }
 private JSONObject rectangle(View view)throws Exception{int[] location=new int[2];view.getLocationOnScreen(location);return new JSONObject().put("left",location[0]).put("top",location[1]).put("width",view.getWidth()).put("height",view.getHeight());}
 private void assertVisibleText(View view,String text){TextView match=find(view,text);assertNotNull(text,match);android.graphics.Rect bounds=new android.graphics.Rect();assertTrue(text,match.getGlobalVisibleRect(bounds));assertEquals(text,match.getHeight(),bounds.height());assertFalse(text,match.isFocusable());ViewGroup cell=(ViewGroup)match.getParent();View portrait=cell.getChildAt(0);assertTrue("Portrait retains useful size: "+text+" height="+portrait.getHeight()+" cell="+cell.getHeight()+" grid="+((View)cell.getParent()).getHeight(),portrait.getHeight()>=DetailsDimensions.px(match.getContext(),24));}
 private void assertWholeText(View root,String text){TextView view=find(root,text);assertNotNull(text,view);android.graphics.Rect bounds=new android.graphics.Rect();assertTrue(text,view.getGlobalVisibleRect(bounds));assertEquals("No clipped rating/provider: "+text,view.getHeight(),bounds.height());assertNotNull(view.getLayout());assertTrue("Text lines fit: "+text,view.getLayout().getLineBottom(view.getLineCount()-1)<=view.getHeight());for(int n=0;n<view.getLineCount();n++)assertEquals("No lost caption: "+text,0,view.getLayout().getEllipsisCount(n));}
 private TextView find(View view,String text){if(view instanceof TextView&&text.equals(((TextView)view).getText().toString()))return (TextView)view;if(view instanceof ViewGroup)for(int n=0;n<((ViewGroup)view).getChildCount();n++){TextView match=find(((ViewGroup)view).getChildAt(n),text);if(match!=null)return match;}return null;}
}
