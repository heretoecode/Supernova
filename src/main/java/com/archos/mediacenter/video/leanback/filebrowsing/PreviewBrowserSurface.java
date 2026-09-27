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
public final class PreviewBrowserSurface extends BrowseFrameLayout {
    private final TextView name,information;
    private final ImageView poster;
    private Runnable openSelected=()->{};private final TextView open;
    private final View dock;private final TextView sourceControl;private final TopNavigation navigation;
    private View lastDockFocus;
    PreviewBrowserSurface(Activity activity,View legacy,Uri uri,View titleCommands,Runnable options){
        this(activity,legacy,uri,titleCommands,options,false);
    }
    /** Provider listings use the same location/content/context composition and routing. */
    public PreviewBrowserSurface(Activity activity,View content,Uri uri,Runnable options){
        this(activity,content,uri,null,options,true);
    }
    private PreviewBrowserSurface(Activity activity,View legacy,Uri uri,View titleCommands,Runnable options,boolean suppliedDock){
        super(activity);setId(R.id.grid_frame);setTag("preview-browser");
        dock=suppliedDock?legacy:legacy.findViewById(R.id.browse_grid_dock);if(dock.getParent() instanceof ViewGroup)((ViewGroup)dock.getParent()).removeView(dock);
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

        poster=new ImageView(activity);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);context.addView(poster,new LinearLayout.LayoutParams(-1,dp(175)));
        name=text("File Information",18);name.setMaxLines(2);context.addView(name);information=text("Focus a file to see its available information.",13);information.setPadding(0,dp(12),0,0);context.addView(information);
        open=control("Open",()->openSelected.run());context.addView(open,new LinearLayout.LayoutParams(-1,dp(38)));context.addView(control("Source Options",options),new LinearLayout.LayoutParams(-1,dp(38)));
        navigation=new TopNavigation(activity,columns,index->navigate(activity,index),()->sourceControl.hasFocus());navigation.selectTab(3);addView(navigation,new android.widget.FrameLayout.LayoutParams(-1,-1));
        // The title object remains a detached command registry for protocol-specific actions.
        if(titleCommands!=null&&titleCommands.getParent() instanceof ViewGroup)((ViewGroup)titleCommands.getParent()).removeView(titleCommands);
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event){if(event.getAction()==KeyEvent.ACTION_DOWN&&dock.hasFocus()){
        lastDockFocus=dock.findFocus();
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){if(moveInsideDock(View.FOCUS_LEFT))return true;sourceControl.requestFocus();return true;}
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){if(moveInsideDock(View.FOCUS_RIGHT))return true;open.requestFocus();return true;}
    }if(event.getAction()==KeyEvent.ACTION_DOWN){
        if(sourceControl.hasFocus()&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT||open.hasFocus()&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){
            if(lastDockFocus==null||!lastDockFocus.isAttachedToWindow()||!lastDockFocus.requestFocus())dock.requestFocus();return true;
        }
    }return super.dispatchKeyEvent(event);}
    private boolean moveInsideDock(int direction){
        if(!(dock instanceof ViewGroup))return false;
        ViewGroup group=(ViewGroup)dock;
        View focused=dock.findFocus();if(focused==null)return false;
        View next=android.view.FocusFinder.getInstance().findNextFocus(group,focused,direction);
        if(next==null||next==focused)return false;
        android.graphics.Rect from=new android.graphics.Rect(),to=new android.graphics.Rect();
        focused.getDrawingRect(from);group.offsetDescendantRectToMyCoords(focused,from);
        next.getDrawingRect(to);group.offsetDescendantRectToMyCoords(next,to);
        if(direction==View.FOCUS_LEFT?to.centerX()>=from.centerX():to.centerX()<=from.centerX())return false;
        return next.requestFocus(direction);
    }
    private void navigate(Activity activity,int index){if(index==4){activity.startActivity(new Intent(activity,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));return;}if(index==5){activity.startActivity(new Intent(activity,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));return;}activity.startActivity(new Intent(activity,MainActivityLeanback.class).putExtra("preview_tab",index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));activity.finish();}
    void focusItem(Object item,Runnable action){openSelected=action;open.setText(item instanceof Video?"View Details":"Open");com.squareup.picasso.Picasso.get().cancelRequest(poster);poster.setImageDrawable(null);
        if(item instanceof Video){Video v=(Video)item;name.setText(v.getName());String detail=v.getFilenameNonCryptic();if(v.getSize()>0)detail+="\n\n"+android.text.format.Formatter.formatFileSize(getContext(),v.getSize());if(v.getDurationMs()>0)detail+="\n"+v.getDurationMs()/60000+" min";information.setText(detail);if(v.getPosterUri()!=null)com.squareup.picasso.Picasso.get().load(v.getPosterUri()).resize(dp(180),dp(175)).centerInside().into(poster);}
        else if(item instanceof MetaFile2){MetaFile2 file=(MetaFile2)item;name.setText(file.getName());information.setText((file.isDirectory()?"Folder":"File")+"\n\n"+file.getUri().getPath());}
    }
    public void providerItem(String title,String details,boolean folder,Runnable action){
        openSelected=action;open.setText(folder?"Open Folder":"File Information");poster.setVisibility(GONE);name.setText(title);information.setText(details);
    }
    private LinearLayout column(Activity a){LinearLayout v=new LinearLayout(a);v.setOrientation(LinearLayout.VERTICAL);v.setPadding(dp(8),dp(8),dp(8),dp(8));android.graphics.drawable.GradientDrawable panel=PreviewDialog.surface(a,false);panel.setColor(0x66071520);v.setBackground(panel);return v;}
    private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextSize(size);t.setTextColor(0xffd5e2ec);return t;}
    private TextView control(String value,Runnable action){TextView t=text(value,14);t.setGravity(Gravity.CENTER_VERTICAL);t.setFocusable(true);t.setFocusableInTouchMode(true);t.setPadding(dp(8),0,dp(8),0);t.setBackground(PreviewDialog.focus(getContext()));t.setOnClickListener(v->action.run());return t;}
    private int dp(int n){return PreviewDialog.dp(getContext(),n);}
}
