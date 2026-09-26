package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import com.archos.mediascraper.ScraperImage;
import com.squareup.picasso.Picasso;
import java.util.*;
import java.util.function.*;

/** Shared grid. Selection reflects a completed native save, never a failed download. */
public final class PreviewArtworkPicker {
    public static Dialog show(Context context,String heading,List<ScraperImage> images,ScraperImage selected,boolean posters,
                              BiConsumer<ScraperImage,Consumer<Boolean>> apply) {
        Dialog dialog=PreviewDialog.create(context);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel=new LinearLayout(context);panel.setOrientation(LinearLayout.VERTICAL);
        int gap=PreviewDialog.dp(context,12);panel.setPadding(gap,gap,gap,gap);panel.setBackground(PreviewDialog.menuSurface(context));
        TextView title=new TextView(context);title.setText(heading);title.setTextColor(Color.WHITE);title.setTextSize(19);panel.addView(title);
        TextView status=new TextView(context);status.setTextColor(Color.WHITE);status.setTextSize(12);status.setMinHeight(PreviewDialog.dp(context,24));panel.addView(status);
        ScrollView scroll=new ScrollView(context);scroll.setClipChildren(false);scroll.setClipToPadding(false);scroll.setPadding(gap,gap,gap,gap);
        GridLayout grid=new GridLayout(context);grid.setColumnCount(posters?5:3);grid.setClipChildren(false);grid.setClipToPadding(false);scroll.addView(grid);
        panel.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        int width=Math.min(PreviewDialog.dp(context,800),context.getResources().getDisplayMetrics().widthPixels-gap*4);
        int columns=posters?5:3,cardWidth=(width-gap*4)/columns-gap*2,cardHeight=posters?cardWidth*3/2:cardWidth*9/16;
        List<FrameLayout> cards=new ArrayList<>();List<ImageView> pictures=new ArrayList<>();List<TextView> checks=new ArrayList<>();
        boolean[] busy={false};int initial=0;
        for(int i=0;i<images.size();i++) {
            final int index=i;ScraperImage image=images.get(i);
            FrameLayout card=new FrameLayout(context);card.setFocusable(true);card.setFocusableInTouchMode(true);card.setTag("artwork:"+i);
            card.setBackgroundColor(0xff142431);card.setForeground(PreviewDialog.focus(context));card.setContentDescription(heading+" "+(i+1));
            ImageView picture=new ImageView(context);picture.setScaleType(ImageView.ScaleType.CENTER_CROP);card.addView(picture,new FrameLayout.LayoutParams(-1,-1));pictures.add(picture);
            java.io.File file=image.getLargeFileF();com.squareup.picasso.RequestCreator request=file!=null&&file.isFile()?Picasso.get().load(file):Picasso.get().load(image.getLargeUrl());
            request.resize(cardWidth,cardHeight).centerCrop().noFade().into(picture);
            TextView check=new TextView(context);check.setTag("artwork-check:"+i);check.setText("✓");check.setTextColor(Color.WHITE);check.setTextSize(22);check.setGravity(Gravity.CENTER);
            boolean current=same(image,selected);check.setVisibility(current?View.VISIBLE:View.INVISIBLE);if(current)initial=i;
            if(current)card.setContentDescription(heading+" "+(i+1)+", selected");
            FrameLayout.LayoutParams mark=new FrameLayout.LayoutParams(PreviewDialog.dp(context,30),PreviewDialog.dp(context,30),Gravity.TOP|Gravity.RIGHT);mark.setMargins(gap/2,gap/2,gap/2,0);card.addView(check,mark);checks.add(check);
            card.setOnFocusChangeListener((v,focused)->card.animate().scaleX(focused?1.08f:1).scaleY(focused?1.08f:1).setDuration(140).start());
            card.setOnClickListener(v->{
                if(busy[0])return;busy[0]=true;status.setText("Applying artwork…");
                apply.accept(image,success->panel.post(()->{
                    busy[0]=false;if(!dialog.isShowing())return;
                    if(Boolean.TRUE.equals(success)){for(int n=0;n<checks.size();n++){checks.get(n).setVisibility(n==index?View.VISIBLE:View.INVISIBLE);cards.get(n).setContentDescription(heading+" "+(n+1)+(n==index?", selected":""));}status.setText("");}
                    else status.setText("Artwork could not be applied. Try again.");
                }));
            });
            card.setOnKeyListener((v,key,event)->{
                if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
                int next=index;
                if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(index%columns>0)next--;}
                else if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(index%columns<columns-1&&index+1<images.size())next++;}
                else if(key==KeyEvent.KEYCODE_DPAD_UP){if(index>=columns)next-=columns;}
                else if(key==KeyEvent.KEYCODE_DPAD_DOWN){if(index+columns<images.size())next+=columns;}
                else return false;
                cards.get(next).requestFocus();return true;
            });
            GridLayout.LayoutParams cell=new GridLayout.LayoutParams();cell.width=cardWidth;cell.height=cardHeight;cell.setMargins(gap,gap,gap,gap);grid.addView(card,cell);cards.add(card);
        }
        dialog.setContentView(panel);dialog.setOnDismissListener(d->{for(ImageView picture:pictures){Picasso.get().cancelRequest(picture);picture.setImageDrawable(null);}});
        dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);dialog.getWindow().setDimAmount(.32f);
        int height=PreviewDialog.dp(context,74)+(cardHeight+gap*2)*((images.size()+columns-1)/columns)+gap*2;
        dialog.getWindow().setLayout(width,Math.min(height,context.getResources().getDisplayMetrics().heightPixels-gap*4));
        if(!cards.isEmpty())cards.get(initial).requestFocus();return dialog;
    }
    static boolean same(ScraperImage a,ScraperImage b){return a!=null&&b!=null&&(a==b||a.getLargeUrl()!=null&&a.getLargeUrl().equals(b.getLargeUrl()));}
    private PreviewArtworkPicker(){}
}
