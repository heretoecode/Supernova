package com.archos.mediacenter.video.foundation;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceFragmentCompat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class FoundationAboutTest {
    public static class Prefs extends PreferenceFragmentCompat {
        @Override public void onCreatePreferences(Bundle saved,String key){setPreferenceScreen(getPreferenceManager().createPreferenceScreen(requireContext()));}
    }
    @Test public void fiveChildrenExpandInRailAndNormalPanelsRestore(){
        FragmentActivity activity=Robolectric.buildActivity(FragmentActivity.class).setup().get();
        Prefs prefs=new Prefs();activity.getSupportFragmentManager().beginTransaction().add(prefs,"test").commitNow();
        LinearLayout split=new LinearLayout(activity),rail=new LinearLayout(activity),middle=new LinearLayout(activity),help=new LinearLayout(activity);
        split.addView(rail);split.addView(middle);split.addView(help);activity.setContentView(split);
        TextView about=new TextView(activity);about.setText("About");about.setFocusable(true);rail.addView(about);
        FoundationAboutWorkspace workspace=new FoundationAboutWorkspace(prefs,split,middle,help,rail);
        workspace.show(about);
        assertEquals(6,rail.getChildCount());assertEquals(View.GONE,middle.getVisibility());assertEquals(View.GONE,help.getVisibility());
        for(int i=0;i<FoundationAboutWorkspace.SECTIONS.length;i++)assertEquals(FoundationAboutWorkspace.SECTIONS[i],((TextView)rail.getChildAt(i+1)).getText().toString());
        workspace.hide();assertEquals(View.VISIBLE,middle.getVisibility());assertEquals(View.VISIBLE,help.getVisibility());
        workspace.show(about);assertEquals("Children must not be duplicated",6,rail.getChildCount());
    }
    @Test public void appInformationUsesActualVersionAndRequiredLineageWithoutUpdateButton(){
        FragmentActivity activity=Robolectric.buildActivity(FragmentActivity.class).setup().get();
        Prefs prefs=new Prefs();activity.getSupportFragmentManager().beginTransaction().add(prefs,"test").commitNow();
        LinearLayout split=new LinearLayout(activity),rail=new LinearLayout(activity),middle=new LinearLayout(activity),help=new LinearLayout(activity);
        split.addView(rail);split.addView(middle);split.addView(help);activity.setContentView(split);
        FoundationAboutWorkspace workspace=new FoundationAboutWorkspace(prefs,split,middle,help,rail);TextView about=new TextView(activity);rail.addView(about);workspace.show(about);
        String text=allText(workspace);
        assertTrue(text.contains(com.archos.mediacenter.video.BuildConfig.VERSION_NAME));
        assertTrue(text.contains("Based on Nova Video Player 6.4.64"));
        assertFalse(text.contains("Check for Updates"));assertFalse(text.contains("Upstream SHA"));
    }
    @Test public void releaseExpansionPersistsAndCurrentReleaseDoesNotCollapse() throws Exception {
        FragmentActivity activity=Robolectric.buildActivity(FragmentActivity.class).setup().get();
        Prefs prefs=new Prefs();activity.getSupportFragmentManager().beginTransaction().add(prefs,"test").commitNow();
        LinearLayout split=new LinearLayout(activity),rail=new LinearLayout(activity),middle=new LinearLayout(activity),help=new LinearLayout(activity);
        split.addView(rail);split.addView(middle);split.addView(help);activity.setContentView(split);
        TextView about=new TextView(activity);about.setFocusable(true);rail.addView(about);
        FoundationAboutWorkspace workspace=new FoundationAboutWorkspace(prefs,split,middle,help,rail);workspace.show(about);
        rail.getChildAt(2).requestFocus();
        java.lang.reflect.Field field=FoundationAboutWorkspace.class.getDeclaredField("rows");field.setAccessible(true);
        java.util.List<TextView> rows=(java.util.List<TextView>)field.get(workspace);
        assertTrue(rows.size()>10);String current=rows.get(0).getText().toString();rows.get(0).performClick();assertEquals(current,rows.get(0).getText().toString());
        TextView older=rows.get(1),second=rows.get(2);assertTrue(older.getText().toString().startsWith("▸"));older.performClick();second.performClick();
        assertTrue(older.getText().toString().startsWith("▾"));assertTrue(second.getText().toString().startsWith("▾"));
        rail.getChildAt(1).requestFocus();rail.getChildAt(2).requestFocus();rows=(java.util.List<TextView>)field.get(workspace);
        assertTrue(rows.get(1).getText().toString().startsWith("▾"));assertTrue(rows.get(2).getText().toString().startsWith("▾"));
        rows.get(1).requestFocus();assertTrue(workspace.handleBack());assertTrue(rail.getChildAt(2).hasFocus());assertTrue(workspace.handleBack());assertTrue(about.hasFocus());
    }
    @Test public void officialQrRoundTripsAndRejectsNonPublicUrls() throws Exception {
        FragmentActivity activity=Robolectric.buildActivity(FragmentActivity.class).setup().get();
        Prefs prefs=new Prefs();activity.getSupportFragmentManager().beginTransaction().add(prefs,"test").commitNow();
        FoundationAboutWorkspace workspace=new FoundationAboutWorkspace(prefs,new LinearLayout(activity),new View(activity),new View(activity),new LinearLayout(activity));
        java.lang.reflect.Method qr=FoundationAboutWorkspace.class.getDeclaredMethod("qr",String.class);qr.setAccessible(true);
        String expected="https://www.themoviedb.org/";android.graphics.Bitmap bitmap=(android.graphics.Bitmap)qr.invoke(workspace,expected);
        int[] pixels=new int[bitmap.getWidth()*bitmap.getHeight()];bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        com.google.zxing.BinaryBitmap encoded=new com.google.zxing.BinaryBitmap(new com.google.zxing.common.HybridBinarizer(new com.google.zxing.RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),pixels)));
        assertEquals(expected,new com.google.zxing.MultiFormatReader().decode(encoded).getText());
        assertNull(qr.invoke(workspace,"https://example.com/?token=private"));assertNull(qr.invoke(workspace,"https://user@example.com/"));assertNull(qr.invoke(workspace,"http://example.com/"));
    }
    private String allText(View view){StringBuilder result=new StringBuilder();if(view instanceof TextView)result.append(((TextView)view).getText());if(view instanceof android.view.ViewGroup){android.view.ViewGroup group=(android.view.ViewGroup)view;for(int i=0;i<group.getChildCount();i++)result.append(allText(group.getChildAt(i)));}return result.toString();}
}
