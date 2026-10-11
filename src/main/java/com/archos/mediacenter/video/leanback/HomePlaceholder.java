package com.archos.mediacenter.video.leanback;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.*;
import androidx.recyclerview.widget.*;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
/** UI-only surfaces: no media identity, database record, metadata or playback action. */
public final class HomePlaceholder {
 public enum Kind { HERO, RECENT }
 private HomePlaceholder(){}
 public static RecyclerView rail(Context c,Runnable explain){PreviewFocusRecycler rail=new PreviewFocusRecycler(c);rail.setLayoutManager(new LinearLayoutManager(c,RecyclerView.HORIZONTAL,false));rail.setClipToPadding(false);rail.setClipChildren(false);rail.setPadding(0,PreviewDialog.dp(c,10),0,PreviewDialog.dp(c,10));rail.setItemAnimator(null);rail.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>(){
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int type){PreviewCardPresenter.Card card=new PreviewCardPresenter.Card(c,PreviewCardPresenter.Style.CONTINUE);RecyclerView.LayoutParams size=new RecyclerView.LayoutParams(PreviewDialog.dp(c,172),PreviewDialog.dp(c,105));size.rightMargin=PreviewDialog.dp(c,12);card.setLayoutParams(size);card.image.setBackgroundColor(0xff0b2035);card.image.setForeground(new Skeleton());return new RecyclerView.ViewHolder(card){};}
  public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){holder.itemView.setTag("home:placeholder:"+position);holder.itemView.setContentDescription("Library placeholder. Add media folders to see details.");holder.itemView.setOnClickListener(v->explain.run());}
  public int getItemCount(){return 6;}
 });return rail;}
 public static final class Skeleton extends Drawable {private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);public void draw(Canvas canvas){Rect b=getBounds();paint.setColor(0x284d6984);float x=b.left+b.width()*.08f,y=b.top+b.height()*.32f,h=Math.max(2,b.height()*.06f);canvas.drawRoundRect(x,y,x+b.width()*.6f,y+h,h/2,h/2,paint);canvas.drawRoundRect(x,y+h*2.5f,x+b.width()*.4f,y+h*3.5f,h/2,h/2,paint);}public void setAlpha(int a){paint.setAlpha(a);}public void setColorFilter(ColorFilter f){paint.setColorFilter(f);}public int getOpacity(){return PixelFormat.TRANSLUCENT;}}
}
