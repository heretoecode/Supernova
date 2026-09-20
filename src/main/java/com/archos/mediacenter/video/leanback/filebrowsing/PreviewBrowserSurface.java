package com.archos.mediacenter.video.leanback.filebrowsing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.BrowseFrameLayout;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.filecorelibrary.MetaFile2;

/** Primary Preview composition. The existing listing engine/credentials/actions are retained;
 * the classic title/orbs and layout are not attached beneath this surface. */
final class PreviewBrowserSurface extends BrowseFrameLayout {
    private final TextView name,information;
    private final ImageView poster;
    private final View dock;private final TextView sourceControl;private final TopNavigation navigation;
    PreviewBrowserSurface(Activity activity,View legacy,Uri uri,View titleCommands,Runnable options){
        super(activity);setId(R.id.grid_frame);setTag("preview-browser");setBackground(new PreviewUtilityBackground(activity));
        dock=legacy.findViewById(R.id.browse_grid_dock);((ViewGroup)dock.getParent()).removeView(dock);
        LinearLayout columns=new LinearLayout(activity);columns.setPadding(dp(24),dp(14),dp(24),dp(16));
        LinearLayout rail=column(activity),centre=column(activity),context=column(activity);
        LinearLayout.LayoutParams left=new LinearLayout.LayoutParams(0,-1,.21f);left.rightMargin=dp(16);columns.addView(rail,left);
        columns.addView(centre,new LinearLayout.LayoutParams(0,-1,.54f));LinearLayout.LayoutParams right=new LinearLayout.LayoutParams(0,-1,.25f);right.leftMargin=dp(18);columns.addView(context,right);
        rail.addView(text("Sources",18));String source=uri.getHost();if(source==null||source.isEmpty())source="Local Storage";
        TextView current=control(source,()->dock.requestFocus());sourceControl=current;current.setId(View.generateViewId());PreviewIcon.apply(current,"storage",19);rail.addView(current,new LinearLayout.LayoutParams(-1,dp(42)));
        if(uri.getScheme()!=null&&!uri.getScheme().equals("file"))rail.addView(control("Local Storage",()->activity.startActivity(new Intent(activity,LocalListingActivity.class))),new LinearLayout.LayoutParams(-1,dp(42)));
        rail.addView(control("Network & Files",()->navigate(activity,3)),new LinearLayout.LayoutParams(-1,dp(42)));
        TextView breadcrumb=text((uri.getHost()==null?"Files":uri.getHost())+"  ›  "+(uri.getPath()==null?"":uri.getPath()),14);breadcrumb.setSingleLine(true);breadcrumb.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);centre.addView(breadcrumb,new LinearLayout.LayoutParams(-1,dp(38)));
        dock.setPadding(0,0,0,0);centre.addView(dock,new LinearLayout.LayoutParams(-1,0,1));
        centre.addView(control("Options",options),new LinearLayout.LayoutParams(-1,dp(36)));
        poster=new ImageView(activity);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);context.addView(poster,new LinearLayout.LayoutParams(-1,dp(175)));
        name=text("File Information",18);name.setMaxLines(2);context.addView(name);information=text("Focus a file to see its available information.",13);information.setPadding(0,dp(12),0,0);context.addView(information);
        navigation=new TopNavigation(activity,columns,index->navigate(activity,index),()->sourceControl.hasFocus());navigation.selectTab(3);addView(navigation,new android.widget.FrameLayout.LayoutParams(-1,-1));
        // The title object remains a detached command registry for protocol-specific actions.
        if(titleCommands.getParent() instanceof ViewGroup)((ViewGroup)titleCommands.getParent()).removeView(titleCommands);
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event){if(event.getAction()==KeyEvent.ACTION_DOWN&&dock.hasFocus()){
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){sourceControl.requestFocus();return true;}
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
    }if(event.getAction()==KeyEvent.ACTION_DOWN&&sourceControl.hasFocus()&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){dock.requestFocus();return true;}return super.dispatchKeyEvent(event);}
    private void navigate(Activity activity,int index){if(index==4){activity.startActivity(new Intent(activity,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));return;}if(index==5){activity.startActivity(new Intent(activity,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));return;}activity.startActivity(new Intent(activity,MainActivityLeanback.class).putExtra("preview_tab",index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));activity.finish();}
    void focusItem(Object item){com.squareup.picasso.Picasso.get().cancelRequest(poster);poster.setImageDrawable(null);
        if(item instanceof Video){Video v=(Video)item;name.setText(v.getName());String detail=v.getFilenameNonCryptic();if(v.getSize()>0)detail+="\n\n"+android.text.format.Formatter.formatFileSize(getContext(),v.getSize());if(v.getDurationMs()>0)detail+="\n"+v.getDurationMs()/60000+" min";information.setText(detail);if(v.getPosterUri()!=null)com.squareup.picasso.Picasso.get().load(v.getPosterUri()).resize(dp(180),dp(175)).centerInside().into(poster);}
        else if(item instanceof MetaFile2){MetaFile2 file=(MetaFile2)item;name.setText(file.getName());information.setText((file.isDirectory()?"Folder":"File")+"\n\n"+file.getUri().getPath());}
    }
    private LinearLayout column(Activity a){LinearLayout v=new LinearLayout(a);v.setOrientation(LinearLayout.VERTICAL);v.setPadding(dp(8),dp(8),dp(8),dp(8));android.graphics.drawable.GradientDrawable panel=PreviewDialog.surface(a,false);panel.setColor(0x66071520);v.setBackground(panel);return v;}
    private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextSize(size);t.setTextColor(0xffd5e2ec);return t;}
    private TextView control(String value,Runnable action){TextView t=text(value,14);t.setGravity(Gravity.CENTER_VERTICAL);t.setFocusable(true);t.setPadding(dp(8),0,dp(8),0);t.setBackground(PreviewDialog.focus(getContext()));t.setOnClickListener(v->action.run());return t;}
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
