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
        assertNull(page.findViewWithTag("person:Crew Other:Production Assistant"));
        PreviewPeopleRail rail=(PreviewPeopleRail)first.getParent().getParent();assertTrue(rail.isHorizontalFadingEdgeEnabled());
        assertTrue(first.isFocusable());assertFalse(first.isClickable());
        page.setTags(null,java.util.Collections.emptyList(),java.util.Collections.emptyList());
        assertNotNull("A local tag refresh must retain cached individual crew",page.findViewWithTag("person:Director One:Director"));
        assertNotNull(page.findViewWithTag("person:Director Two:Director"));
        assertNotNull(page.findViewWithTag("person:Actor One:Lead"));
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
