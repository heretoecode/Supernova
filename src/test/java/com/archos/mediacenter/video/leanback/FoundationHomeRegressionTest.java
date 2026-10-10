package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class FoundationHomeRegressionTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
 @org.junit.Before public void configuredLibraryFixture(){androidx.preference.PreferenceManager.getDefaultSharedPreferences(org.robolectric.RuntimeEnvironment.getApplication()).edit().putBoolean("supernova_onboarding_complete",true).commit();}

 private Snapshot source(){Snapshot s=new Snapshot();for(int id=1;id<=24;id++){Movie m=new Movie(id,"/fixture/"+id,"Film "+id,id,"A family discovers an unexpected path through a changing world.",2024,7,"12",null,100000,0,0,0,false,false,false,false,id,id,1920,1080,null,null,null,null,0,1,1000,0);s.movies.add(new Entry(m,id,0,"Drama"));}s.continuingMovies.addAll(s.movies.subList(0,12));s.recent.addAll(s.movies.subList(12,24));for(Entry e:s.recent){e.heroEligible=true;s.featured.add(e);}for(Entry e:s.recent)e.backdrop=android.net.Uri.parse("file:///fixture/backdrop"+e.key());return s;}
 private void init(){try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException e){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
 @Test public void reliableNextEpisodesPrecedeOrdinaryHeroRecommendations()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try{PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);Snapshot snapshot=source();
   Entry next=new Entry(new PreviewPagesTest().episode(4,0,false,0,200).media,200,123,"Drama");next.heroEligible=true;next.heroSequential=true;next.secondary="Next Episode · S1 E4";next.backdrop=android.net.Uri.parse("file:///fixture/next");
   Entry another=new Entry(new PreviewPagesTest().episode(2,0,false,0,199).media,199,456,"Drama");another.heroEligible=true;another.heroSequential=true;another.secondary="Next Episode · S1 E2";another.backdrop=android.net.Uri.parse("file:///fixture/another");snapshot.featured.add(next);snapshot.featured.add(another);pages.setSnapshot(snapshot);
   java.util.List<Entry> candidates=org.robolectric.util.ReflectionHelpers.callInstanceMethod(pages,"featuredCandidates");assertSame(next,candidates.get(0));assertSame(another,candidates.get(1));assertFalse(candidates.get(2).heroSequential);assertEquals(8,candidates.size());
  }finally{host.pause().stop().destroy();}
 }
 @Test public void homeViewportHasEqualNeighboursSixtyPercentTeaserAndNoBackgroundDuplicate()throws Exception{
  init();var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);java.util.List<android.net.Uri> background=new java.util.ArrayList<>();pages.setArtworkListener(background::add);pages.setDiscovery(new PreviewDiscovery());Snapshot s=source();for(Entry e:s.recent)e.backdrop=android.net.Uri.parse("file:///fixture/backdrop"+e.key());pages.setSnapshot(s);for(int i=0;i<8;i++)PreviewPagesTest.layout(nav);
   View active=nav.findViewWithTag("semantic:featured.active");assertNotNull(active);View previous=nav.findViewWithTag("semantic:featured.previous"),next=nav.findViewWithTag("semantic:featured.next");int[] a=new int[2],p=new int[2],n=new int[2];active.getLocationOnScreen(a);previous.getLocationOnScreen(p);next.getLocationOnScreen(n);assertEquals(a[1],p[1]);assertEquals(a[1],n[1]);assertEquals(active.getHeight(),previous.getHeight());assertEquals(active.getHeight(),next.getHeight());View home=nav.findViewWithTag("semantic:topnav.home");int[] homeAt=new int[2];home.getLocationOnScreen(homeAt);assertTrue(a[1]>homeAt[1]+home.getHeight());assertNull(nav.findViewWithTag("hero:play"));assertNotNull(nav.findViewWithTag("hero:info"));assertTrue(background.stream().allMatch(java.util.Objects::isNull));
   RecyclerView list=(RecyclerView)pages.getChildAt(0);GridLayoutManager layout=(GridLayoutManager)list.getLayoutManager();ViewGroup row=(ViewGroup)layout.findViewByPosition(2);assertNotNull(row);RecyclerView rail=(RecyclerView)row.getChildAt(0);View card=rail.findViewHolderForAdapterPosition(0).itemView;int[] cardAt=new int[2];card.getLocationOnScreen(cardAt);float visible=(540-cardAt[1])/(float)card.getHeight();assertTrue("Artwork teaser 55–65%, got "+visible,visible>=.55f&&visible<=.65f);View second=layout.findViewByPosition(3);if(second!=null){int[] at=new int[2];second.getLocationOnScreen(at);assertTrue("No second ordinary row visible",at[1]>=540);}assertFalse(row.getClipChildren());assertFalse(rail.getClipChildren());int[] railAt=new int[2];rail.getLocationOnScreen(railAt);assertEquals(0,railAt[0]);assertEquals(960,rail.getWidth());
   paintArtwork(nav);PreviewPagesTest.capture(nav,"foundation-home-initial");
  }finally{host.pause().stop().destroy();}
 }
 @Test public void variableLogoProportionsKeepStableMetadataAndSingleAction()throws Exception{
  init();var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{Snapshot s=source();s.movies.add(new Entry(new com.archos.mediacenter.video.browser.adapters.object.Tvshow(99,"TV fixture",null,2,10,0,"/show"),99,99,"Drama"));int baseline=-1;for(int[] dimensions:new int[][]{{320,35},{70,85},{140,60}}){PreviewFeaturedCard card=new PreviewFeaturedCard(host.get(),s.movies,dimensions[0]==70?s.movies.size()-1:0,()->{},()->{});host.get().setContentView(card);TextView title=card.findViewWithTag("semantic:featured.title");Bitmap logoBitmap=Bitmap.createBitmap(dimensions[0],dimensions[1],Bitmap.Config.ARGB_8888);logoBitmap.eraseColor(Color.WHITE);android.graphics.drawable.Drawable logo=(android.graphics.drawable.Drawable)org.robolectric.util.ReflectionHelpers.callConstructor(Class.forName("com.archos.mediacenter.video.leanback.OfficialTitleArtwork$Logo"),org.robolectric.util.ReflectionHelpers.ClassParameter.from(Bitmap.class,logoBitmap));title.setForeground(logo);title.setTextColor(Color.TRANSPARENT);card.measure(View.MeasureSpec.makeMeasureSpec(904,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(340,View.MeasureSpec.EXACTLY));card.layout(0,0,904,340);View meta=card.findViewWithTag("semantic:featured.metadata"),plot=card.findViewWithTag("semantic:featured.synopsis"),info=card.findViewWithTag("hero:info");assertEquals(86,title.getHeight());assertTrue(meta.getTop()-title.getBottom()>=24);if(plot.getVisibility()==View.VISIBLE)assertTrue(plot.getTop()-meta.getBottom()>=12);assertTrue(info.getTop()>=0);if(baseline<0)baseline=meta.getTop();else assertEquals(baseline,meta.getTop());assertNull(card.findViewWithTag("hero:play"));PreviewPagesTest.capture(card,"foundation-home-logo-"+dimensions[0]);}}finally{host.pause().stop().destroy();}
 }
 private void paintArtwork(View v){if(v instanceof ImageView)((ImageView)v).setImageDrawable(new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TL_BR,new int[]{0xff597c93,0xff253c54,0xff986842}));if(v instanceof ViewGroup)for(int i=0;i<((ViewGroup)v).getChildCount();i++)paintArtwork(((ViewGroup)v).getChildAt(i));}
 @Test public void ordinaryCardArtworkPaintsPastInsetWhileAcceptedUnitStaysAligned()throws Exception{
  init();var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);pages.setDiscovery(new PreviewDiscovery());pages.setSnapshot(source());PreviewPagesTest.layout(pages);RecyclerView list=(RecyclerView)pages.getChildAt(0);GridLayoutManager outer=(GridLayoutManager)list.getLayoutManager();outer.scrollToPositionWithOffset(2,100);PreviewPagesTest.layout(pages);RecyclerView rail=(RecyclerView)((ViewGroup)outer.findViewByPosition(2)).getChildAt(0);rail.scrollBy(90,0);PreviewPagesTest.layout(pages);for(int i=0;i<rail.getChildCount();i++){com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.Card card=(com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.Card)rail.getChildAt(i);card.image.setImageDrawable(new ColorDrawable(Color.MAGENTA));}View first=rail.findViewHolderForAdapterPosition(0).itemView;int[] at=new int[2];first.getLocationOnScreen(at);assertTrue(at[0]<28);Bitmap picture=Bitmap.createBitmap(960,540,Bitmap.Config.ARGB_8888);pages.draw(new Canvas(picture));assertEquals("Card artwork crosses old x=28 parent clip",Color.MAGENTA,picture.getPixel(10,at[1]+40));picture.recycle();PreviewPagesTest.capture(pages,"foundation-home-physical-edge");for(int position:new int[]{0,5,11}){((LinearLayoutManager)rail.getLayoutManager()).scrollToPositionWithOffset(position,0);PreviewPagesTest.layout(pages);View item=rail.findViewHolderForAdapterPosition(position).itemView;assertTrue(item.requestFocus());Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(200));assertEquals(1f,item.getScaleX(),.001f);View unit=((ViewGroup)item).getChildAt(0);assertEquals(1.08f,unit.getScaleX(),.001f);assertSame(unit,((com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.Card)item).image.getParent());paintArtwork(rail);PreviewPagesTest.capture(pages,"foundation-home-focus-position-"+position);if(position==11){pages.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame(item,pages.findFocus());}}
  }finally{host.pause().stop().destroy();}
 }


 @Test public void remoteCarouselCyclesBothDirectionsOpensSelectedDetailsAndPreservesRows() {
  init();var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   java.util.List<Object> opened=new java.util.ArrayList<>();PreviewPages pages=new PreviewPages(host.get(),(h,item)->opened.add(item));host.get().setContentView(pages);pages.setDiscovery(new PreviewDiscovery());Snapshot source=source();pages.setSnapshot(source);for(int i=0;i<6;i++)PreviewPagesTest.layout(pages);
   RecyclerView list=(RecyclerView)pages.getChildAt(0);GridLayoutManager manager=(GridLayoutManager)list.getLayoutManager();View firstRow=manager.findViewByPosition(2);assertNotNull(firstRow);RecyclerView rail=(RecyclerView)((ViewGroup)firstRow).getChildAt(0);Object rowAdapter=rail.getAdapter();View firstCard=rail.findViewHolderForAdapterPosition(0).itemView;
   TextView original=pages.findViewWithTag("semantic:featured.title");String title=original.getText().toString();assertEquals("Film 13",title);assertTrue(pages.findViewWithTag("hero:info").requestFocus());
   assertTrue(pages.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT)));for(int i=0;i<8;i++)PreviewPagesTest.layout(pages);assertEquals("Film 14",((TextView)pages.findViewWithTag("semantic:featured.title")).getText().toString());assertTrue(pages.findViewWithTag("hero:info").hasFocus());pages.findViewWithTag("hero:info").performClick();assertSame(source.recent.get(1).media,opened.get(0));
   assertTrue(pages.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT)));for(int i=0;i<8;i++)PreviewPagesTest.layout(pages);assertEquals(title,((TextView)pages.findViewWithTag("semantic:featured.title")).getText().toString());assertSame(rowAdapter,rail.getAdapter());assertSame(firstCard,rail.findViewHolderForAdapterPosition(0).itemView);
   assertTrue(pages.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN)));for(int i=0;i<6;i++)PreviewPagesTest.layout(pages);assertTrue(firstCard.hasFocus());
  }finally{host.pause().stop().destroy();}
 }
 @Test public void emptyHomeRemainsNavigableAndMovieArtworkStillUsesLibraryBackdrop() {
  init();var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});host.get().setContentView(pages);pages.setDiscovery(new PreviewDiscovery());java.util.List<android.net.Uri> artwork=new java.util.ArrayList<>();pages.setArtworkListener(artwork::add);pages.setSnapshot(new Snapshot());PreviewPagesTest.layout(pages);assertNull(pages.findViewWithTag("semantic:featured.active"));assertTrue(artwork.stream().allMatch(java.util.Objects::isNull));
   Snapshot source=source();source.movies.get(0).backdrop=android.net.Uri.parse("https://example.invalid/movie.jpg");pages.setSnapshot(source);PreviewPagesTest.layout(pages);pages.setTab(1);PreviewPagesTest.layout(pages);assertEquals(source.movies.get(0).backdrop,artwork.get(artwork.size()-1));pages.setTab(0);for(int i=0;i<5;i++)PreviewPagesTest.layout(pages);assertNull(artwork.get(artwork.size()-1));assertNotNull(pages.findViewWithTag("semantic:featured.active"));
  }finally{host.pause().stop().destroy();}
 }
}
