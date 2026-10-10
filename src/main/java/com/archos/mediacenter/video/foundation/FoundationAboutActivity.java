package com.archos.mediacenter.video.foundation;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewUtilityBackground;

/** About-only fallback host; other classic/mobile Settings remain unchanged. */
public final class FoundationAboutActivity extends FragmentActivity {
    @Override protected void onCreate(Bundle saved) {
        setTheme(R.style.FoundationEntry);super.onCreate(saved);
        if(!BuildConfig.FOUNDATION){finish();return;}
        FrameLayout container=new FrameLayout(this);container.setId(R.id.foundation_about_host);setContentView(container);
        if(saved==null)getSupportFragmentManager().beginTransaction().replace(container.getId(),new AboutFragment()).commitNow();
    }
    public static final class AboutFragment extends Fragment {
        private FoundationAboutWorkspace workspace;
        @Override public View onCreateView(android.view.LayoutInflater inflater,ViewGroup parent,Bundle saved) {
            LinearLayout split=new LinearLayout(requireContext());split.setBackground(new PreviewUtilityBackground(requireContext()));split.setPadding(dp(28),dp(24),dp(28),dp(24));
            LinearLayout rail=new LinearLayout(requireContext());rail.setOrientation(LinearLayout.VERTICAL);
            ScrollView navigation=new ScrollView(requireContext());navigation.addView(rail);split.addView(navigation,new LinearLayout.LayoutParams(0,-1,.23f));
            View middle=new View(requireContext()),help=new View(requireContext());split.addView(middle);split.addView(help);
            TextView about=new TextView(requireContext());about.setText("About");about.setTextSize(20);about.setTextColor(android.graphics.Color.WHITE);about.setGravity(Gravity.CENTER_VERTICAL);about.setFocusable(true);about.setBackground(PreviewDialog.focus(requireContext()));rail.addView(about,new LinearLayout.LayoutParams(-1,dp(48)));
            workspace=new FoundationAboutWorkspace(requireContext(),split,middle,help,rail);workspace.show(about);
            about.setOnClickListener(v->workspace.enter());about.setOnKeyListener((v,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&(key==KeyEvent.KEYCODE_DPAD_RIGHT||key==KeyEvent.KEYCODE_DPAD_DOWN)){workspace.enter();return true;}return false;});
            split.post(()->{if(requireActivity().getIntent().hasExtra("section"))workspace.openSection(requireActivity().getIntent().getIntExtra("section",0));else about.requestFocus();});
            return split;
        }
        @Override public void onViewCreated(View view,Bundle saved){
            // This fragment owns an About workspace, not a preference RecyclerView.
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),new OnBackPressedCallback(true){@Override public void handleOnBackPressed(){if(!workspace.handleBack())requireActivity().finish();}});
        }
        private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
    }
}
