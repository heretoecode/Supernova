package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewMatchMembershipTest {
    @Test public void createRowAddsTitleAndReturnsToSelectedMembershipWithoutClosingParent(){
        android.app.Activity host=Robolectric.buildActivity(android.app.Activity.class).setup().get();
        PreferenceManager.getDefaultSharedPreferences(host).edit().clear().commit();
        PreviewLibraryLoader.Entry entry=new PreviewPagesTest().episode(1,0,false,0,0);
        PreviewHomeRows.add(host,entry,()->{});android.app.Dialog original=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        android.view.View root=original.getWindow().getDecorView();
        android.view.View watch=root.findViewWithTag("preview-check:0");assertEquals(android.view.View.VISIBLE,watch.getVisibility());
        assertTrue(((android.view.View)watch.getParent()).getContentDescription().toString().endsWith("not selected"));
        ((android.view.View)PreviewPagesTest.findText(root,"Create New Row").getParent()).performClick();
        android.app.Dialog keyboard=org.robolectric.shadows.ShadowDialog.getLatestDialog();android.widget.EditText input=findInput(keyboard.getWindow().getDecorView());assertNotNull(input);input.setText("My favourites");
        PreviewPagesTest.findText(keyboard.getWindow().getDecorView(),"Select").performClick();
        android.app.Dialog returned=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertNotSame(original,returned);assertTrue(returned.isShowing());assertFalse(keyboard.isShowing());
        android.view.View added=(android.view.View)PreviewPagesTest.findText(returned.getWindow().getDecorView(),"My favourites").getParent();
        assertTrue(added.getContentDescription().toString().endsWith(", selected"));assertTrue(added.hasFocus());
        PreviewHomeRows model=new PreviewHomeRows(host);assertTrue(model.rows.stream().anyMatch(row->row.name.equals("My favourites")&&row.members.contains(entry.key())));
        added.performClick();assertTrue(returned.isShowing());assertTrue(added.getContentDescription().toString().endsWith(", not selected"));returned.dismiss();
    }
    private static android.widget.EditText findInput(android.view.View view){
        if(view instanceof android.widget.EditText)return (android.widget.EditText)view;
        if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++){android.widget.EditText found=findInput(((android.view.ViewGroup)view).getChildAt(i));if(found!=null)return found;}
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
