package com.archos.mediacenter.video.leanback.network;

import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.browser.ShortcutDb;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
import com.archos.mediaprovider.NetworkScanner;

/** Complete modern source screen; no DetailsSupportFragment or legacy overlay is inflated. */
final class PreviewSourceManagement {
    static View create(Activity a,Shortcut source){
        boolean indexed=ShortcutDbAdapter.VIDEO.isShortcut(a,source.getUri().toString())>0;
        LinearLayout columns=new LinearLayout(a);columns.setPadding(dp(a,28),dp(a,22),dp(a,28),dp(a,22));
        LinearLayout rail=panel(a),centre=panel(a),actions=panel(a);
        columns.addView(rail,new LinearLayout.LayoutParams(0,-1,.22f));
        LinearLayout.LayoutParams middle=new LinearLayout.LayoutParams(0,-1,.48f);middle.setMargins(dp(a,14),0,dp(a,14),0);columns.addView(centre,middle);
        columns.addView(actions,new LinearLayout.LayoutParams(0,-1,.30f));
        rail.addView(text(a,indexed?"Library Sources":"Saved Locations",18));
        rail.addView(button(a,"Back",a::finish));
        centre.addView(text(a,source.getName(),23));
        centre.addView(text(a,(indexed?"Library source":"Saved browsing location")+"\n\n"+source.getUri().getScheme()+" · "+source.getUri().getHost()+"\n"+source.getUri().getPath(),14));
        centre.addView(text(a,indexed?"Media from this source appears in your library.":"This location is saved for browsing.",14));
        actions.addView(text(a,"Source actions",18));
        TextView open=button(a,"Open / Browse",()->a.startActivity(new Intent(a,ListingActivity.getActivityForUri(source.getUri())).putExtra(ListingActivity.EXTRA_ROOT_URI,source.getUri()).putExtra(ListingActivity.EXTRA_ROOT_NAME,source.getName())));actions.addView(open);
        if(indexed)actions.addView(button(a,"Scan Source",()->NetworkScanner.scanVideos(a,source.getUri())));
        else actions.addView(button(a,"Add to Library",()->{if(ShortcutDbAdapter.VIDEO.addShortcut(a,new ShortcutDbAdapter.Shortcut(source.getName(),source.getUri().toString()))){ShortcutDb.STATIC.removeShortcut(a,source.getUri());NetworkScanner.scanVideos(a,source.getUri());a.setResult(NetworkRootFragment.RESULT_CODE_SHORTCUTS_MODIFIED);a.finish();}}));
        actions.addView(button(a,indexed?"Remove from Library":"Remove Saved Location",()->PreviewDialog.choose(a,indexed?"Remove from Library? Actual files will not be deleted.":"Remove saved location? Actual files will not be deleted.",new String[]{"Cancel","Remove"},0,n->{if(n!=1)return;
            if(indexed){if(ShortcutDbAdapter.VIDEO.deleteShortcut(a,source.getId()))NetworkScanner.removeIndexedVideos(a,source.getUri());}
            else ShortcutDb.STATIC.removeShortcut(a,source.getUri());
            a.setResult(NetworkRootFragment.RESULT_CODE_SHORTCUTS_MODIFIED);a.finish();
        })));
        TopNavigation nav=new TopNavigation(a,columns,tab->{if(tab==4)a.startActivity(new Intent(a,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));else if(tab==5)a.startActivity(new Intent(a,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));else{a.startActivity(new Intent(a,MainActivityLeanback.class).putExtra("preview_tab",tab).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));a.finish();}},()->open.hasFocus());nav.selectTab(3);open.post(open::requestFocus);return nav;
    }
    private static int dp(Activity a,int value){return PreviewDialog.dp(a,value);}
    private static LinearLayout panel(Activity a){LinearLayout p=new LinearLayout(a);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(dp(a,16),dp(a,16),dp(a,16),dp(a,16));p.setBackground(PreviewDialog.surface(a,false));return p;}
    private static TextView text(Activity a,String text,int size){TextView t=new TextView(a);t.setText(text);t.setTextSize(size);t.setTextColor(0xffd6e5ef);t.setPadding(0,0,0,dp(a,16));return t;}
    private static TextView button(Activity a,String text,Runnable run){TextView t=text(a,text,14);t.setMinHeight(dp(a,42));t.setGravity(Gravity.CENTER_VERTICAL);t.setFocusable(true);t.setBackground(PreviewDialog.focus(a));t.setOnClickListener(v->run.run());return t;}
}
