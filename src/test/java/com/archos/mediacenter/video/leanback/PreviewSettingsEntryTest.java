package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import androidx.preference.*;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.leanback.settings.PreviewSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Checks Settings shortcuts and child returns preserve the correct rail and opener identity. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSettingsEntryTest {
 @Before public void isolatePreviewTransport(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drainPreviewWorkers() throws Exception { PreviewAsyncFixtures.drain(); }
    public static class Settings extends PreferenceFragmentCompat {
        @Override public void onCreatePreferences(Bundle state,String rootKey){
            PreferenceScreen root=getPreferenceManager().createPreferenceScreen(requireContext());setPreferenceScreen(root);
            for(String name:new String[]{"Playback","Subtitles","Streaming"}){
                PreferenceCategory category=new PreferenceCategory(requireContext());category.setTitle(name);category.setKey(name);root.addPreference(category);
                SwitchPreferenceCompat option=new SwitchPreferenceCompat(requireContext());option.setKey(name+"-option");option.setTitle(name+" option");category.addPreference(option);
            }
            PreviewSettings.style(root);
        }
        @Override public RecyclerView onCreateRecyclerView(LayoutInflater inflater,ViewGroup parent,Bundle state){return PreviewSettings.grid(requireContext());}
        @Override protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen root){return PreviewSettings.adapter(root);}
        @Override public void onViewCreated(View view,Bundle state){super.onViewCreated(view,state);PreviewSettings.sidebar(this);}
    }
    @Test public void subtitleShortcutTargetsRailWithoutChangingSettingsOrEnteringOptions(){
        ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            host.get().getIntent().putExtra("preview_settings_category","Subtitles");Settings fragment=new Settings();host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,fragment).commitNow();
            View root=fragment.requireView();PreviewPagesTest.layout(root);View subtitles=root.findViewWithTag("semantic:settings:category:Subtitles");
            assertTrue(subtitles.hasFocus());assertFalse(fragment.getListView().hasFocus());assertFalse(host.get().getIntent().hasExtra("preview_settings_category"));
            assertTrue(subtitles.getParent() instanceof LinearLayout);assertNotNull(subtitles.getBackground());
            assertFalse(((SwitchPreferenceCompat)fragment.findPreference("Subtitles-option")).isChecked());
            subtitles.performClick();PreviewPagesTest.layout(root);assertTrue(fragment.getListView().hasFocus());
            // ViewRoot dispatches an unhandled DPAD key through focusSearch; a direct child
            // dispatch in Robolectric does not execute that final platform traversal.
            View returned=fragment.getListView().focusSearch(fragment.getListView().findFocus(),View.FOCUS_LEFT);
            assertSame(subtitles,returned);assertTrue(returned.requestFocus());PreviewPagesTest.layout(root);assertTrue(subtitles.hasFocus());
        }finally{host.pause().stop().destroy();}
    }
    public static class NestedSettings extends Settings {
        @Override public void onCreatePreferences(Bundle state,String rootKey){
            super.onCreatePreferences(state,rootKey);
            PreferenceCategory parent=findPreference("Playback");
            for(String key:new String[]{"first","second"}){
                PreferenceCategory child=new PreferenceCategory(requireContext());child.setKey(key);child.setTitle("Shared label");parent.addPreference(child);
                Preference option=new Preference(requireContext());option.setKey(key+"-option");option.setTitle("Option");child.addPreference(option);
            }
        }
    }
    @Test public void childReturnUsesStableIdentityWhenLabelsMatch(){
        ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            NestedSettings fragment=new NestedSettings();host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,fragment).commitNow();
            View root=fragment.requireView();PreviewPagesTest.layout(root);
            root.findViewWithTag("semantic:settings:category:Playback").performClick();PreviewPagesTest.layout(root);
            View second=root.findViewWithTag("semantic:settings:section:second");assertNotNull(second);second.requestFocus();second.performClick();PreviewPagesTest.layout(root);
            ((Runnable)fragment.getListView().getTag(com.archos.mediacenter.video.R.id.preview_settings_return)).run();PreviewPagesTest.layout(root);
            assertTrue(root.findViewWithTag("semantic:settings:section:second").hasFocus());
            assertFalse(root.findViewWithTag("semantic:settings:section:first").hasFocus());
        }finally{host.pause().stop().destroy();}
    }
}
