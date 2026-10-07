package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.app.Application;
import androidx.leanback.widget.ArrayObjectAdapter;
import org.json.JSONObject;
import org.robolectric.util.ReflectionHelpers;
import android.view.View;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewPeopleRailTest {
    @Test public void nativePrincipalCrewUsesIndividualNamesAndRetainsFocusedPerson(){
        Activity activity=Robolectric.buildActivity(Activity.class).setup().visible().get();
        PreviewMoviePage page=new PreviewMoviePage(activity,ArrayObjectAdapter::new,a->{},()->{},uri->{});activity.setContentView(page);
        com.archos.mediascraper.MovieTags tags=new com.archos.mediascraper.MovieTags();
        tags.setDirectors(java.util.Arrays.asList("Director One","Director Two"));tags.setWriters(java.util.Arrays.asList("Director One","Writer, Jr."));
        page.setTags(tags,java.util.Collections.emptyList(),java.util.Collections.emptyList());
        View second=page.findViewWithTag("person:Director Two:Director");assertNotNull(second);assertNotNull(page.findViewWithTag("person:Director One:Director"));
        assertNotNull(page.findViewWithTag("person:Writer, Jr.:Writer"));assertNull(page.findViewWithTag("person:Director One:Writer"));assertTrue(second.requestFocus());
        page.setTags(tags,java.util.Collections.emptyList(),java.util.Collections.emptyList());
        assertEquals("person:Director Two:Director",page.findFocus().getTag());
    }
    @Test public void creditsCreateIndividualPrincipalCrewAndRemoteCast()throws Exception{
        Activity activity=Robolectric.buildActivity(Activity.class).setup().get();
        PreviewMoviePage page=new PreviewMoviePage(activity,ArrayObjectAdapter::new,a->{},()->{},uri->{});activity.setContentView(page);
        PreviewDetailsData.Result data=new PreviewDetailsData.Result();
        data.credits=new JSONObject("{cast:[{id:1,name:'Actor One',character:'Lead'}],crew:[{id:2,name:'Director One',job:'Director'},{id:3,name:'Director Two',job:'Director'},{id:4,name:'Crew Other',job:'Production Assistant'}]}");
        ReflectionHelpers.setField(page,"enrichment",data);ReflectionHelpers.callInstanceMethod(page,"renderEnrichedPeople");
        assertNotNull(page.findViewWithTag("person:Actor One:Lead"));
        View first=page.findViewWithTag("person:Director One:Director"),second=page.findViewWithTag("person:Director Two:Director");
        assertNotNull(first);assertNotNull(second);assertNotSame(first,second);
        assertEquals("semantic:details.crew.person.2",first.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic));
        assertEquals("semantic:details.crew.person.3",second.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic));
        View other=page.findViewWithTag("person:Crew Other:Production Assistant");assertNotNull("Prioritise creative roles without discarding available remaining crew",other);assertTrue(((LinearLayout)first.getParent()).indexOfChild(first)<((LinearLayout)first.getParent()).indexOfChild(other));
        LinearLayout people=(LinearLayout)first.getParent();assertEquals("Approved people lists are vertical text, not a carousel",LinearLayout.VERTICAL,people.getOrientation());assertEquals(2,((android.view.ViewGroup)first).getChildCount());for(int i=0;i<2;i++)assertTrue(((android.view.ViewGroup)first).getChildAt(i) instanceof android.widget.TextView);
        assertTrue(first.isFocusable());assertFalse(first.isClickable());
        page.setTags(null,java.util.Collections.emptyList(),java.util.Collections.emptyList());
        assertNotNull("A local tag refresh must retain cached individual crew",page.findViewWithTag("person:Director One:Director"));
        assertNotNull(page.findViewWithTag("person:Director Two:Director"));
        assertNotNull(page.findViewWithTag("person:Actor One:Lead"));
    }
    @Test public void crewPrioritisesCreativeRolesAndCapsEightDistinctPeople()throws Exception{
        Activity activity=Robolectric.buildActivity(Activity.class).setup().get();PreviewMoviePage page=new PreviewMoviePage(activity,ArrayObjectAdapter::new,a->{},()->{},uri->{});activity.setContentView(page);
        PreviewDetailsData.Result data=new PreviewDetailsData.Result();org.json.JSONArray crew=new org.json.JSONArray();
        crew.put(new JSONObject().put("id",1).put("name","Director").put("job","Writer"));crew.put(new JSONObject().put("id",1).put("name","Director").put("job","Director"));
        for(int i=2;i<=12;i++)crew.put(new JSONObject().put("id",i).put("name","Crew "+i).put("job",i==5?"Screenplay":"Other creative role"));
        data.credits=new JSONObject().put("cast",new org.json.JSONArray()).put("crew",crew);ReflectionHelpers.setField(page,"enrichment",data);ReflectionHelpers.callInstanceMethod(page,"renderEnrichedPeople");
        View director=page.findViewWithTag("person:Director:Director"),writer=page.findViewWithTag("person:Director:Writer");assertNotNull(director);assertNull("One person occupies one row using their highest-priority job",writer);LinearLayout people=(LinearLayout)director.getParent();assertEquals(8,people.getChildCount());assertSame(director,people.getChildAt(0));assertEquals("person:Crew 5:Screenplay",people.getChildAt(1).getTag());activity.finish();
    }
    @Test public void awkwardViewportWidthKeepsWholeCardsOnFocusScroll(){
        Activity activity=Robolectric.buildActivity(Activity.class).setup().get();
        PreviewPeopleRail rail=new PreviewPeopleRail(activity);LinearLayout row=new LinearLayout(activity);rail.addView(row);activity.setContentView(rail);
        int margin=com.archos.mediacenter.video.leanback.PreviewDialog.dp(activity,10);
        for(int n=0;n<12;n++){View card=new View(activity);card.setFocusable(true);card.setFocusableInTouchMode(true);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(96,108);lp.rightMargin=margin;row.addView(card,lp);}
        int width=com.archos.mediacenter.video.leanback.PreviewDialog.dp(activity,651)+1;
        rail.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(160,View.MeasureSpec.EXACTLY));rail.layout(0,0,width,160);
        int available=width-rail.getPaddingLeft()-rail.getPaddingRight();
        int count=Math.max(1,available/com.archos.mediacenter.video.leanback.PreviewDialog.dp(activity,106));
        assertEquals(available,row.getChildAt(count).getLeft());
        row.getChildAt(count).requestFocus();
        assertEquals(row.getChildAt(1).getLeft(),rail.getScrollX());
        assertEquals(available,row.getChildAt(count).getRight()+margin-rail.getScrollX());
        row.getChildAt(0).requestFocus();assertEquals(0,rail.getScrollX());
        assertTrue(rail.isHorizontalFadingEdgeEnabled());
    }
}
