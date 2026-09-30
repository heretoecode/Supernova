package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import androidx.leanback.widget.ArrayObjectAdapter;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
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
}
