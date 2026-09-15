package com.archos.mediacenter.video.leanback;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import java.util.*;

/** Recycled native TV grids. Only visible artwork is decoded. Classic Browse remains untouched. */
public final class PreviewPages extends FrameLayout {
    public interface Click { void open(Presenter.ViewHolder holder,Object item); }
    private final RecyclerView list;
    private final GridLayoutManager layout;
    private final PageAdapter adapter=new PageAdapter();
    private final Click click;
    private Snapshot snapshot=new Snapshot();
    private List<Box> files=new ArrayList<>();
    private final List<Cell> cells=new ArrayList<>();
    private int tab;
    private final int[] sorts={0,0,0};
    private final String[] genres={"","",""};
    private final int[] years={0,0,0};
    private boolean loaded;
    private static final int HEADER=0, POSTER=1, RAIL=2, STORAGE=3;
    static class Cell {
        int type; String title; Object value;
        Cell(int type,String title,Object value) {this.type=type;this.title=title;this.value=value;}
    }
    public PreviewPages(Context c,Click click) {
        super(c); this.click=click; setBackgroundColor(0xff101f2e);
        list=new RecyclerView(c); list.setClipToPadding(false); list.setPadding(dp(28),dp(10),dp(28),dp(12));
        layout=new GridLayoutManager(c,6); layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){@Override public int getSpanSize(int p){return cells.get(p).type==POSTER?1:cells.get(p).type==STORAGE?2:6;}});
        list.setLayoutManager(layout); list.setAdapter(adapter); list.setItemAnimator(null);
        addView(list,new FrameLayout.LayoutParams(-1,-1)); render();
    }
    public boolean atTop() {
        View focused=list.findFocus(); if(focused==null)return true;
        View item=list.findContainingItemView(focused); return item!=null && list.getChildAdapterPosition(item)<=1;
    }
    public void setTab(int tab) { this.tab=tab; render(); list.scrollToPosition(0); }
    public void setSnapshot(Snapshot s) { if(s==null)return; snapshot=s; loaded=true; render(); }
    public void setFiles(List<Box> f) {files=f;if(tab==3)render();}
    private void header(String title,boolean controls) {cells.add(new Cell(HEADER,title,controls));}
    private void rail(String title,List<Entry> items) {if(items.isEmpty())return; header(title,false);cells.add(new Cell(RAIL,title,new ArrayList<>(items.subList(0,Math.min(30,items.size())))));}
    private void render() {
        Object state=layout.onSaveInstanceState(); cells.clear();
        if(tab==0) {
            rail("Recently played",snapshot.played); rail("Recently added",snapshot.recent);
            if(loaded&&cells.isEmpty()) header("Your library is empty — add media through Network & files",false);
        } else if(tab==1||tab==2) {
            rail("Continue watching",tab==1?snapshot.continuingMovies:snapshot.continuingShows);
            header(tab==1?"Movie library":"TV show library",true);
            List<Entry> entries=filtered(); for(Entry e:entries)cells.add(new Cell(POSTER,"",e));
            if(loaded&&entries.isEmpty())header("No matching titles",false);
        } else {
            header("Network & files",false);
            for(String section:new String[]{"Local storage","Network","Playlists"}) {
                List<Box> group=new ArrayList<>();for(Box box:files){String s=box.getBoxId()==Box.ID.NETWORK?"Network":box.getBoxId()==Box.ID.VIDEOS_BY_LISTS?"Playlists":"Local storage";if(section.equals(s))group.add(box);}
                if(!group.isEmpty()){header(section,false);for(Box box:group)cells.add(new Cell(STORAGE,"",box));}
            }
        }
        if(!loaded&&tab!=3)header("Loading library…",false);
        adapter.notifyDataSetChanged(); if(state!=null)layout.onRestoreInstanceState((android.os.Parcelable)state);
    }
    private List<Entry> source(){return tab==1?snapshot.movies:snapshot.shows;}
    private List<Entry> filtered(){
        List<Entry> entries=new ArrayList<>();for(Entry e:source())if((genres[tab].isEmpty()||Arrays.asList(e.genres.split("[|,;/]")).stream().anyMatch(g->g.trim().equals(genres[tab])))&&(years[tab]==0||e.year()==years[tab]))entries.add(e);
        Comparator<Entry> cmp=sorts[tab]==1?Comparator.comparing(e->e.media.getName(),String.CASE_INSENSITIVE_ORDER):sorts[tab]==2?Comparator.comparingInt(Entry::year).reversed():Comparator.comparingLong((Entry e)->e.added).reversed();
        entries.sort(cmp.thenComparing(e->e.media.getName(),String.CASE_INSENSITIVE_ORDER)); return entries;
    }
    private void sort(){new AlertDialog.Builder(getContext()).setTitle("Sort").setSingleChoiceItems(new String[]{"Recently added","Title A–Z","Year: newest first"},sorts[tab],(d,n)->{sorts[tab]=n;d.dismiss();render();}).setNegativeButton("Cancel",null).show();}
    private void filter(){new AlertDialog.Builder(getContext()).setTitle("Filter").setItems(new String[]{"Genre"+(genres[tab].isEmpty()?"":": "+genres[tab]),"Year"+(years[tab]==0?"":": "+years[tab]),"Clear filters"},(d,n)->{
        if(n==2){genres[tab]="";years[tab]=0;render();return;}
        TreeSet<String> values=new TreeSet<>(); for(Entry e:source())if(n==0){for(String g:e.genres.split("[|,;/]"))if(!g.trim().isEmpty())values.add(g.trim());}else if(e.year()>0)values.add(String.valueOf(e.year()));
        List<String> options=new ArrayList<>();options.add("All");options.addAll(values);
        new AlertDialog.Builder(getContext()).setTitle(n==0?"Genre":"Year").setItems(options.toArray(new String[0]),(dialog,i)->{if(n==0)genres[tab]=i==0?"":options.get(i);else years[tab]=i==0?0:Integer.parseInt(options.get(i));render();}).setNegativeButton("Cancel",null).show();
    }).setNegativeButton("Cancel",null).show();}
    private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    private GradientDrawable background(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0xff25445c:0xff192f45);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?0xff62bbf3:0xff304b60);return d;}
    private TextView button(String name,Runnable action){TextView b=text(name,13);b.setGravity(Gravity.CENTER);b.setPadding(dp(14),dp(9),dp(14),dp(9));b.setFocusable(true);b.setClickable(true);b.setBackground(background(false));b.setOnFocusChangeListener((v,f)->v.setBackground(background(f)));b.setOnClickListener(v->action.run());return b;}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    class Holder extends RecyclerView.ViewHolder {
        Presenter presenter; Presenter.ViewHolder card;
        Holder(View v){super(v);}
    }
    class PageAdapter extends RecyclerView.Adapter<Holder> {
        @Override public int getItemCount(){return cells.size();}
        @Override public int getItemViewType(int p){return cells.get(p).type;}
        @Override public Holder onCreateViewHolder(ViewGroup parent,int type){
            if(type==POSTER){PreviewCardPresenter pr=new PreviewCardPresenter(PreviewCardPresenter.Style.POSTER); Presenter.ViewHolder card=pr.onCreateViewHolder(parent);Holder h=new Holder(card.view);h.presenter=pr;h.card=card;RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,-2);lp.setMargins(0,0,dp(10),dp(15));h.itemView.setLayoutParams(lp);return h;}
            LinearLayout v=new LinearLayout(getContext());v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(0,dp(9),0,dp(10));v.setLayoutParams(new RecyclerView.LayoutParams(-1,-2));return new Holder(v);
        }
        @Override public void onBindViewHolder(Holder h,int p){Cell c=cells.get(p);
            if(c.type==POSTER){Entry e=(Entry)c.value;h.presenter.onBindViewHolder(h.card,e.media);h.itemView.setOnClickListener(v->click.open(h.card,e.media));return;}
            LinearLayout v=(LinearLayout)h.itemView;v.removeAllViews();v.setFocusable(false);v.setOnClickListener(null);v.setBackground(null);
            if(c.type==HEADER){v.addView(text(c.title,20),new LinearLayout.LayoutParams(0,-2,1));if(Boolean.TRUE.equals(c.value)){TextView sort=button("Sort: "+new String[]{"Recently added","Title A–Z","Year"}[sorts[tab]],()->sort());v.addView(sort);TextView filter=button(genres[tab].isEmpty()&&years[tab]==0?"Filter":"Filter •",()->filter());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(10);v.addView(filter,lp);}}
            else if(c.type==RAIL){RecyclerView rail=new RecyclerView(getContext());rail.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));rail.setItemAnimator(null);rail.setAdapter(new RailAdapter((List<Entry>)c.value));v.addView(rail,new LinearLayout.LayoutParams(-1,dp(164)));}
            else if(c.type==STORAGE){Box b=(Box)c.value;v.setPadding(dp(12),dp(12),dp(12),dp(12));RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,dp(88));lp.setMargins(0,0,dp(12),dp(12));v.setLayoutParams(lp);v.setBackground(background(false));v.setFocusable(true);v.setClickable(true);v.setOnFocusChangeListener((view,f)->view.setBackground(background(f)));
                ImageView icon=new ImageView(getContext());icon.setImageDrawable(new StorageIcon(b.getBoxId()));v.addView(icon,new LinearLayout.LayoutParams(dp(35),dp(35)));
                LinearLayout labels=new LinearLayout(getContext());labels.setOrientation(LinearLayout.VERTICAL);labels.setPadding(dp(12),0,0,0);
                String name=b.getName(),sub="";if(b.getBoxId()==Box.ID.NETWORK){name="Browse network";sub="Find shared folders";}else if(b.getBoxId()==Box.ID.FOLDERS){name="Internal storage";}else if(b.getBoxId()==Box.ID.VIDEOS_BY_LISTS){name="Playlists";sub="Browse saved playlists";}else{int at=name.indexOf('(');if(at>0){sub=name.substring(at+1).replace(")","").trim();name=name.substring(0,at).trim();}name=name.replaceFirst("^[^:]+:\\s*","");}
                TextView title=text(name,15);title.setMaxLines(2);labels.addView(title);if(!sub.isEmpty()){TextView detail=text(sub,12);detail.setTextColor(0xffb4cbe0);detail.setMaxLines(2);labels.addView(detail);}v.addView(labels,new LinearLayout.LayoutParams(0,-2,1));v.setContentDescription(name+" "+sub);v.setOnClickListener(view->click.open(new Presenter.ViewHolder(view),b));
            }
        }
        @Override public void onViewRecycled(Holder h){if(h.presenter!=null)h.presenter.onUnbindViewHolder(h.card);else if(h.itemView instanceof ViewGroup){ViewGroup group=(ViewGroup)h.itemView;for(int i=0;i<group.getChildCount();i++)if(group.getChildAt(i) instanceof RecyclerView)((RecyclerView)group.getChildAt(i)).setAdapter(null);}}
    }
    class RailAdapter extends RecyclerView.Adapter<Holder>{
        final List<Entry> entries;final PreviewCardPresenter pr=new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
        RailAdapter(List<Entry> e){entries=e;}public int getItemCount(){return entries.size();}
        public Holder onCreateViewHolder(ViewGroup p,int type){Presenter.ViewHolder card=pr.onCreateViewHolder(p);Holder h=new Holder(card.view);h.card=card;h.presenter=pr;RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(dp(280),dp(158));lp.rightMargin=dp(12);h.itemView.setLayoutParams(lp);return h;}
        public void onBindViewHolder(Holder h,int p){Entry e=entries.get(p);pr.onBindViewHolder(h.card,e.media);h.itemView.setOnClickListener(v->click.open(h.card,e.media));}
        public void onViewRecycled(Holder h){pr.onUnbindViewHolder(h.card);}
    }
    static final class StorageIcon extends android.graphics.drawable.Drawable {
        final Box.ID id;final android.graphics.Paint paint=new android.graphics.Paint(3);
        StorageIcon(Box.ID id){this.id=id;paint.setColor(0xffb7d7f5);paint.setStyle(android.graphics.Paint.Style.STROKE);paint.setStrokeWidth(1.7f);paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);}
        public void draw(android.graphics.Canvas c){c.save();c.translate(getBounds().left,getBounds().top);c.scale(getBounds().width()/32f,getBounds().height()/32f);
            if(id==Box.ID.VIDEOS_BY_LISTS){for(int y=7;y<29;y+=8){c.drawCircle(4,y,1,paint);c.drawLine(10,y,29,y,paint);}}
            else if(id==Box.ID.NETWORK){for(int y=3;y<25;y+=14){c.drawRoundRect(2,y,30,y+10,2,2,paint);c.drawCircle(7,y+5,1,paint);}}
            else {c.drawRoundRect(4,5,28,27,3,3,paint);c.drawLine(4,20,28,20,paint);c.drawCircle(23,24,1,paint);}
            c.restore();}
        public void setAlpha(int a){paint.setAlpha(a);}public void setColorFilter(android.graphics.ColorFilter f){paint.setColorFilter(f);}public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}
    }
}
