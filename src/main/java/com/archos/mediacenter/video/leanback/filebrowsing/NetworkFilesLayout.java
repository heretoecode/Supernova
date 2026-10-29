package com.archos.mediacenter.video.leanback.filebrowsing;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.archos.mediacenter.video.leanback.SharedThreePanel;
import com.archos.mediacenter.video.leanback.TvLayout;

/** Full-width Settings-style landing and dedicated browser workspace; no persistent rail. */
final class NetworkFilesLayout extends LinearLayout {
    final FrameLayout workspace;
    private final LinearLayout centre, right, body;
    private final ScrollView details, scroll;
    private final FrameLayout listing;
    private final LinearLayout items;
    private boolean browsing,initialised,landing;
    final TextView back;
    private View landingGrid;

    NetworkFilesLayout(Context c, LinearLayout rail, LinearLayout centre, LinearLayout right,
                       ScrollView details, FrameLayout listing, LinearLayout items) {
        super(c); this.centre=centre; this.right=right; this.details=details;
        this.listing=listing; this.items=items;
        setOrientation(VERTICAL); setClipChildren(false); setClipToPadding(false);
        setPadding(dp(16),dp(7),dp(16),dp(8));
        detach(rail); detach(centre); detach(details);
        back=SharedThreePanel.accentAction(c,"Network & Files  ‹",()->{});
        back.setTag("network.back");addView(back,new LayoutParams(-1,-2));
        workspace=new FrameLayout(c);surface(workspace,10);workspace.setPadding(dp(10),dp(10),dp(10),dp(10));
        addView(workspace,new LayoutParams(-1,0,1));
        centre.setBackground(null);centre.setPadding(0,0,0,0);details.setBackground(null);details.setPadding(0,0,0,0);
        right.setPadding(0,0,0,0);
        body=new LinearLayout(c);body.setOrientation(VERTICAL);
        scroll=new ScrollView(c);scroll.setFillViewport(true);scroll.setFocusable(false);scroll.addView(body);
        mode(false);
    }
    void landing(View grid){
        landing=true;landingGrid=grid;back.setVisibility(GONE);workspace.removeAllViews();
        detach(grid);workspace.addView(grid,new FrameLayout.LayoutParams(-1,-1));
    }
    boolean landing(){return landing;}
    void mode(boolean value) {
        if(landing){landing=false;initialised=false;back.setVisibility(VISIBLE);}

        if(initialised&&browsing==value)return;initialised=true;browsing=value;
        detach(centre);detach(right);detach(details);detach(items);detach(scroll);
        listing.removeAllViews();workspace.removeAllViews();body.removeAllViews();
        if(value){
            listing.addView(items,new FrameLayout.LayoutParams(-1,-1));
            centre.removeView(listing);centre.addView(listing,new LayoutParams(-1,0,1));
            details.removeAllViews();details.addView(right,new ScrollView.LayoutParams(-1,-1));
            LinearLayout split=new LinearLayout(getContext());
            LayoutParams listSize=new LayoutParams(0,-1,.66f);listSize.rightMargin=dp(10);
            split.addView(centre,listSize);split.addView(details,new LayoutParams(0,-1,.34f));
            workspace.addView(split,new FrameLayout.LayoutParams(-1,-1));
        }else{
            listing.addView(items,new FrameLayout.LayoutParams(-1,-2));
            centre.removeView(listing);centre.addView(listing,new LayoutParams(-1,-2));
            body.setOrientation(HORIZONTAL);body.setBaselineAligned(false);
            LayoutParams fields=new LayoutParams(0,-2,.66f);fields.rightMargin=dp(18);
            body.addView(centre,fields);body.addView(right,new LayoutParams(0,-2,.34f));
            workspace.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
        }
        // Action scrolling belongs to the workspace on forms, and to details while browsing.
        View actionScroll=right.getChildAt(right.getChildCount()-1);
        if(actionScroll!=null)actionScroll.setLayoutParams(new LayoutParams(-1,-2));
    }
    int scrollPosition(){return scroll.getScrollY();}
    void restoreScroll(int position){scroll.post(()->scroll.scrollTo(0,position));}
    boolean browsing(){return browsing;}
    void reveal(View target){target.post(()->target.requestRectangleOnScreen(new android.graphics.Rect(0,0,target.getWidth(),target.getHeight()),false));}
    static void detach(View view){if(view.getParent() instanceof ViewGroup)((ViewGroup)view.getParent()).removeView(view);}
    static void surface(View view,int radius){
        GradientDrawable background=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0xe609111a,0xf00b121d});
        background.setCornerRadius(SharedThreePanel.dp(view.getContext(),radius));
        background.setStroke(SharedThreePanel.dp(view.getContext(),1),0x337f8996);view.setBackground(background);
    }
    private int dp(int n){return SharedThreePanel.dp(getContext(),n);}
}
