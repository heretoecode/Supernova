package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.Bundle;
import android.view.*;
import androidx.preference.*;
import com.archos.mediacenter.video.leanback.settings.PreviewSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSettingsEntryTest {
    public static class Settings extends PreferenceFragmentCompat {
        @Override public void onCreatePreferences(Bundle state,String rootKey){
            PreferenceScreen root=getPreferenceManager().createPreferenceScreen(requireContext());setPreferenceScreen(root);
            for(String name:new String[]{"Playback","Subtitles","Streaming"}){
                PreferenceCategory category=new PreferenceCategory(requireContext());category.setTitle(name);category.setKey(name);root.addPreference(category);
                SwitchPreferenceCompat option=new SwitchPreferenceCompat(requireContext());option.setKey(name+"-option");option.setTitle(name+" option");category.addPreference(option);
            }
            PreviewSettings.style(root);
        }
        @Override public androidx.recyclerview.widget.RecyclerView onCreateRecyclerView(LayoutInflater inflater,ViewGroup parent,Bundle state){return PreviewSettings.grid(requireContext());}
        @Override protected androidx.recyclerview.widget.RecyclerView.Adapter onCreateAdapter(PreferenceScreen root){return PreviewSettings.adapter(root);}
        @Override public void onViewCreated(View view,Bundle state){super.onViewCreated(view,state);PreviewSettings.sidebar(this);}
    }
    @Test public void subtitleShortcutTargetsRailWithoutChangingSettingsOrEnteringOptions(){
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            host.get().getIntent().putExtra("preview_settings_category","Subtitles");Settings fragment=new Settings();host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,fragment).commitNow();
            View root=fragment.requireView();PreviewPagesTest.layout(root);View subtitles=root.findViewWithTag("semantic:settings:category:Subtitles");
            assertTrue(subtitles.hasFocus());assertFalse(fragment.getListView().hasFocus());assertFalse(host.get().getIntent().hasExtra("preview_settings_category"));
            assertFalse(((SwitchPreferenceCompat)fragment.findPreference("Subtitles-option")).isChecked());
            subtitles.performClick();PreviewPagesTest.layout(root);assertTrue(fragment.getListView().hasFocus());
            fragment.getListView().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));PreviewPagesTest.layout(root);assertTrue(subtitles.hasFocus());
        }finally{host.pause().stop().destroy();}
    }
}
