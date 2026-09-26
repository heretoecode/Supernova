package com.archos.mediacenter.video.leanback;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.json.*;

/** Home presentation and explicit row membership. Never changes playback or watched state. */
public final class PreviewHomeRows {
 public static final String[] SYSTEM={"continue","watchnext","recent","trending","popular","watched","similar"};
 private static final String[] LABELS={"Continue Watching","Watch Next","Recently Added","Trending on Trakt — In Your Library","Popular on Trakt — In Your Library","Recently Watched","Because You Watched"};
 public static final class Row {public String id,name;public boolean visible=true,dynamic=false,movies=true,tv=true;public String genreRule="",sort="added";public int maximum=0;public final Set<String> members=new LinkedHashSet<>();Row(String id,String name){this.id=id;this.name=name;}public boolean custom(){return id.startsWith("custom:");}}
 private final Context context;private final SharedPreferences prefs;public final List<Row> rows=new ArrayList<>();
 public PreviewHomeRows(Context c){context=c;prefs=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);try{JSONArray data=new JSONArray(prefs.getString("preview_home_rows41","[]"));for(int i=0;i<data.length();i++){JSONObject o=data.getJSONObject(i);Row r=new Row(o.getString("id"),o.getString("name"));r.visible=o.optBoolean("visible",true);r.dynamic=o.optBoolean("dynamic",false);r.movies=o.optBoolean("movies",true);r.tv=o.optBoolean("tv",true);if(!r.movies&&!r.tv)r.movies=true;r.genreRule=o.optString("genres","");r.sort=o.optString("sort","added");r.maximum=Math.max(0,o.optInt("maximum",0));JSONArray members=o.optJSONArray("members");if(members!=null)for(int j=0;j<members.length();j++)r.members.add(members.getString(j));rows.add(r);}}catch(JSONException ignored){}for(int i=0;i<SYSTEM.length;i++){final String id=SYSTEM[i];if(rows.stream().noneMatch(r->r.id.equals(id)))rows.add(new Row(id,LABELS[i]));}}
 private void save(){JSONArray data=new JSONArray();try{for(Row r:rows)data.put(new JSONObject().put("id",r.id).put("name",r.name).put("visible",r.visible).put("members",new JSONArray(r.members)).put("dynamic",r.dynamic).put("movies",r.movies).put("tv",r.tv).put("genres",r.genreRule).put("sort",r.sort).put("maximum",r.maximum));prefs.edit().putString("preview_home_rows41",data.toString()).apply();}catch(JSONException error){throw new IllegalStateException(error);}}
 public List<Entry> members(Row row,Snapshot snapshot){List<Entry> result=new ArrayList<>();if(row.dynamic){List<Entry> source=new ArrayList<>();if(row.movies)source.addAll(snapshot.movies);if(row.tv)source.addAll(snapshot.shows);Set<String> genres=PreviewGenres.parse(row.genreRule);for(Entry e:source)if(PreviewGenres.matches(e.genres,genres))result.add(e);Comparator<Entry> order=row.sort.equals("title")?Comparator.comparing((Entry e)->PreviewPages.titleForSort(context,e),String.CASE_INSENSITIVE_ORDER):row.sort.equals("year")?Comparator.comparingInt(Entry::year).reversed():Comparator.comparingLong((Entry e)->e.added).reversed();result.sort(order.thenComparing(PreviewPages::displayName,String.CASE_INSENSITIVE_ORDER));if(row.maximum>0&&result.size()>row.maximum)return new ArrayList<>(result.subList(0,row.maximum));return result;}for(String key:row.members){for(Entry e:snapshot.movies)if(e.key().equals(key)){result.add(e);break;}for(Entry e:snapshot.shows)if(e.key().equals(key)){result.add(e);break;}}return result;}
 public void supersedeWatchNext(Set<String> continuing){boolean changed=false;for(Row row:rows)if(row.id.equals("watchnext"))changed|=row.members.removeAll(continuing);if(changed)save();}
 public boolean dismissed(Entry e){return prefs.contains("preview_cw_dismiss:"+e.key())&&e.playedAt<=prefs.getLong("preview_cw_dismiss:"+e.key(),0);}
 public void dismiss(Entry e){prefs.edit().putLong("preview_cw_dismiss:"+e.key(),e.playedAt).apply();}
 public static void add(Context c, Entry entry, Runnable changed) {
  if (entry == null) return;
  PreviewHomeRows model = new PreviewHomeRows(c);
  model.membership(entry, changed);
 }
 private void membership(Entry entry, Runnable changed) {
  List<Row> choices = new ArrayList<>();
  for (Row row : rows) if (row.custom() && !row.dynamic || row.id.equals("watchnext")) choices.add(row);
  List<String> labels = new ArrayList<>(); Set<Integer> checks = new HashSet<>();
  for (Row row : choices) { if (row.members.contains(entry.key())) checks.add(labels.size()); labels.add(row.name); }
  labels.add("Create new row…");
  Dialog[] menu = {null};
  menu[0] = PreviewDialog.choose(context,"Add to Row",labels.toArray(new String[0]),-1,checks,false,n -> {
   if (n == choices.size()) {
    name("Create new row","",value -> {
     Row row = new Row("custom:" + UUID.randomUUID(), value);
     row.members.add(entry.key()); rows.add(row); save(); changed.run();
     menu[0].dismiss(); membership(entry,changed);
    });
   } else {
    Row row = choices.get(n);
    if (!row.members.add(entry.key())) row.members.remove(entry.key());
    if (row.members.contains(entry.key())) checks.add(n); else checks.remove(n);
    save(); PreviewDialog.updateChecks(menu[0],checks); changed.run();
   }
  });
 }
 private void name(String title,String previous,java.util.function.Consumer<String> accept){PreviewTextInput.show(context,title,previous,40,accept);}

 public static void clearWatchNext(Context c){PreviewDialog.choose(c,"Remove all titles from Watch Next?",new String[]{"Cancel","Clear"},0,n->{if(n==1){PreviewHomeRows model=new PreviewHomeRows(c);for(Row row:model.rows)if(row.id.equals("watchnext"))row.members.clear();model.save();PreviewNotice.show(c,"Watch Next cleared",false);}});}
 public static void customise(Context c,Runnable changed){new PreviewHomeRows(c).editor(changed);}
 public static void rowControls(Context c,String id,Runnable changed){
  PreviewHomeRows model=new PreviewHomeRows(c);Row selected=null;
  for(Row row:model.rows)if(row.id.equals(id)){selected=row;break;}
  if(selected==null)return;final Row row=selected;
  PreviewDialog.choose(c,row.name,new String[]{"Move","Hide"},0,choice->{
   if(choice==1){row.visible=false;model.save();changed.run();return;}
   String[] positions=new String[model.rows.size()];for(int n=0;n<positions.length;n++)positions[n]=(n+1)+" · "+model.rows.get(n).name;
   PreviewDialog.choose(c,"Move row to",positions,model.rows.indexOf(row),position->{model.rows.remove(row);model.rows.add(Math.min(position,model.rows.size()),row);model.save();changed.run();});
  });
 }
 private void editor(Runnable changed){Dialog dialog=new Dialog(context);LinearLayout screen=new LinearLayout(context);screen.setOrientation(LinearLayout.VERTICAL);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);screen.setPadding(dp(16),dp(12),dp(16),dp(12));screen.setBackground(PreviewDialog.surface(context,false));TextView heading=label("Customise Home",21);screen.addView(heading);TextView help=label("Select to edit · Up/Down to reorder",13);help.setPadding(0,dp(8),0,dp(16));screen.addView(help);ScrollView scroll=new ScrollView(context);LinearLayout list=new LinearLayout(context);list.setOrientation(LinearLayout.VERTICAL);scroll.addView(list);screen.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));final String[] moving={null};final Runnable[] render={null};render[0]=()->{list.removeAllViews();for(Row row:rows){LinearLayout line=new LinearLayout(context);TextView move=control((row.id.equals(moving[0])?"↕  ":"")+row.name,()->{moving[0]=row.id.equals(moving[0])?null:row.id;save();render[0].run();View target=list.findViewWithTag(row.id);if(target!=null)target.requestFocus();});move.setTag(row.id);move.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN||!row.id.equals(moving[0])||key!=KeyEvent.KEYCODE_DPAD_UP&&key!=KeyEvent.KEYCODE_DPAD_DOWN)return false;int index=rows.indexOf(row),next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<rows.size()){Collections.swap(rows,index,next);save();render[0].run();list.findViewWithTag(row.id).requestFocus();}return true;});line.addView(move,new LinearLayout.LayoutParams(0,dp(36),1));TextView visible=control(row.visible?"Shown":"Hidden",()->{row.visible=!row.visible;save();TextView control=list.findViewWithTag(row.id+":visibility");if(control!=null)control.setText(row.visible?"Shown":"Hidden");});visible.setTag(row.id+":visibility");line.addView(visible,new LinearLayout.LayoutParams(dp(95),dp(36)));if(row.custom()){TextView more=control("More",()->PreviewDialog.choose(context,row.name,new String[]{"Rename","Genre rule & contents","Delete row"},-1,n->{if(n==0)name("Rename row",row.name,value->{row.name=value;save();render[0].run();});else if(n==1)rule(row,()->{save();render[0].run();});else PreviewDialog.confirmDelete(context,"Delete row?","Titles remain in your library. Only this Home row is removed.",()->{rows.remove(row);save();render[0].run();});}));line.addView(more,new LinearLayout.LayoutParams(dp(82),dp(36)));}LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(3);list.addView(line,lp);}list.addView(control("Create new row…",()->name("Create new row","",name->{Row row=new Row("custom:"+UUID.randomUUID(),name);row.dynamic=true;rows.add(row);save();rule(row,()->{save();render[0].run();});render[0].run();})),new LinearLayout.LayoutParams(-1,dp(36)));};render[0].run();dialog.setContentView(screen);dialog.setOnDismissListener(d->changed.run());dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);dialog.getWindow().setDimAmount(.4f);dialog.getWindow().setLayout(Math.min(dp(430),context.getResources().getDisplayMetrics().widthPixels-dp(60)),Math.min(dp(Math.min(350,100+39*(rows.size()+1))),context.getResources().getDisplayMetrics().heightPixels-dp(100)));if(!rows.isEmpty())list.findViewWithTag(rows.get(0).id).requestFocus();}
 private void rule(Row row,Runnable changed) {
  Dialog[] menu={null};
  java.util.function.IntFunction<String> label = n -> new String[]{
   "Dynamic genre rule: "+(row.dynamic?"On":"Off"),
   "Genres: "+(row.genreRule.isEmpty()?"All":row.genreRule.replace("|",", ")),
   "Movies: "+(row.movies?"On":"Off"),"TV Shows: "+(row.tv?"On":"Off"),
   "Sort: "+row.sort,"Maximum items: "+(row.maximum==0?"No Limit":row.maximum),"Done"}[n];
  String[] labels=new String[7];for(int i=0;i<labels.length;i++)labels[i]=label.apply(i);
  Runnable refresh=()->{save();for(int i=0;i<6;i++)PreviewDialog.updateLabel(menu[0],i,label.apply(i));};
  menu[0]=PreviewDialog.choose(context,row.name,labels,-1,Collections.emptySet(),false,n->{
   if(n==6){menu[0].dismiss();changed.run();return;}
   if(n==0)row.dynamic=!row.dynamic;
   if(n==1){Set<String> values=new TreeSet<>();Snapshot snapshot=PreviewLibraryLoader.memoryCache();if(snapshot!=null){for(Entry e:snapshot.movies)values.addAll(PreviewGenres.parse(e.genres));for(Entry e:snapshot.shows)values.addAll(PreviewGenres.parse(e.genres));}PreviewGenres.choose(context,values,PreviewGenres.parse(row.genreRule),selected->{row.genreRule=android.text.TextUtils.join("|",selected);row.dynamic=true;refresh.run();});return;}
   if(n==2&&(!row.movies||row.tv))row.movies=!row.movies;
   if(n==3&&(!row.tv||row.movies))row.tv=!row.tv;
   if(n==4){PreviewDialog.choose(context,"Row sort",new String[]{"Date Added","Title","Year"},Arrays.asList("added","title","year").indexOf(row.sort),i->{row.sort=new String[]{"added","title","year"}[i];refresh.run();});return;}
   if(n==5){int[] limits={0,10,20,30,40,50,75,100};String[] options={"No Limit","10","20","30","40","50","75","100","Select…"};int selected=-1;for(int i=0;i<limits.length;i++)if(limits[i]==row.maximum)selected=i;PreviewDialog.choose(context,"Maximum items",options,selected,i->{
    if(i<limits.length){row.maximum=limits[i];refresh.run();return;}
    PreviewTextInput.showValidated(context,"Maximum items",row.maximum==0?"":String.valueOf(row.maximum),10,
      value->parseMaximum(value)==null?"Enter a whole number from 0 to 2147483647. Zero means No Limit.":null,value->{
     row.maximum=parseMaximum(value);refresh.run();
    });
   });return;}
   refresh.run();
  });
 }
 static Integer parseMaximum(String value){
  if(value==null||!value.trim().matches("[0-9]{1,10}"))return null;
  try{return Integer.valueOf(value.trim());}catch(NumberFormatException invalid){return null;}
 }
 private TextView label(String text,int size){TextView v=new TextView(context);v.setText(text);v.setTextSize(size);v.setTextColor(0xffd6e5ef);return v;}
 private TextView control(String text,Runnable action){TextView v=label(text,14);v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(dp(12),0,dp(12),0);v.setFocusable(true);v.setFocusableInTouchMode(true);v.setBackground(PreviewDialog.focus(context));v.setOnClickListener(w->action.run());return v;}
 private int dp(int n){return PreviewDialog.dp(context,n);}
}
