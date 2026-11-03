package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.preference.PreferenceManager;
import java.util.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewMatchMembershipTest {
 @Before public void isolatePreviewTransport(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drainPreviewWorkers() throws Exception { PreviewAsyncFixtures.drain(); }
    @Test public void createRowAddsTitleAndReturnsToSelectedMembershipWithoutClosingParent(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();
        PreferenceManager.getDefaultSharedPreferences(host).edit().clear().commit();
        PreviewLibraryLoader.Entry entry=new PreviewPagesTest().episode(1,0,false,0,0);
        PreviewHomeRows.add(host,entry,()->{});Dialog original=ShadowDialog.getLatestDialog();
        View root=original.getWindow().getDecorView();
        View watch=root.findViewWithTag("preview-check:0");assertEquals(View.VISIBLE,watch.getVisibility());
        assertTrue(((View)watch.getParent()).getContentDescription().toString().endsWith("not selected"));
        ((View)PreviewPagesTest.findText(root,"Create New Row").getParent()).performClick();
        Dialog keyboard=ShadowDialog.getLatestDialog();EditText input=findInput(keyboard.getWindow().getDecorView());assertNotNull(input);input.setText("My favourites");
        PreviewPagesTest.findText(keyboard.getWindow().getDecorView(),"Done").performClick();
        Dialog returned=ShadowDialog.getLatestDialog();assertNotSame(original,returned);assertTrue(returned.isShowing());assertFalse(keyboard.isShowing());
        View added=(View)PreviewPagesTest.findText(returned.getWindow().getDecorView(),"My favourites").getParent();
        assertTrue(added.getContentDescription().toString().endsWith(", selected"));assertTrue(added.hasFocus());
        PreviewHomeRows model=new PreviewHomeRows(host);assertTrue(model.rows.stream().anyMatch(row->row.name.equals("My favourites")&&row.members.contains(entry.key())));
        added.performClick();assertTrue(returned.isShowing());assertTrue(added.getContentDescription().toString().endsWith(", not selected"));returned.dismiss();
    }
    private static EditText findInput(View view){
        if(view instanceof EditText)return (EditText)view;
        if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){EditText found=findInput(((ViewGroup)view).getChildAt(i));if(found!=null)return found;}
        return null;
    }
    @Test public void correctedSeriesKeepsRowOrderVisibilityMembershipAndDismissal(){
        Context context=RuntimeEnvironment.getApplication();SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString("preview_home_rows41","[{\"id\":\"custom:one\",\"name\":\"Favourites\",\"visible\":false,\"members\":[\"v1\",\"s10\",\"v2\"]},{\"id\":\"watchnext\",\"name\":\"Watch Next\",\"members\":[\"s10\",\"s20\"]}]").putLong("preview_cw_dismiss:s10",100).commit();
        PreviewHomeRows.reconcileShowIdentity(context,10,20);PreviewHomeRows model=new PreviewHomeRows(context);
        assertEquals("custom:one",model.rows.get(0).id);assertFalse(model.rows.get(0).visible);
        assertEquals(Arrays.asList("v1","s20","v2"),new ArrayList<>(model.rows.get(0).members));
        assertEquals(Collections.singleton("s20"),model.rows.get(1).members);assertEquals(100,prefs.getLong("preview_cw_dismiss:s20",0));
        String retained=prefs.getString("preview_home_rows41","");PreviewHomeRows.reconcileShowIdentity(context,20,20);PreviewHomeRows.reconcileShowIdentity(context,20,-1);
        assertEquals(retained,prefs.getString("preview_home_rows41",""));
    }
}
