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
 private static final String[] LABELS={"Continue Watching","Watch Next","Recently Added","Trending on Trakt · In Your Library","Popular on Trakt · In Your Library","Recently Watched","Because You Watched"};
 public static final class Row {public String id,name;public boolean visible=true,dynamic=false,movies=true,tv=true;public String genreRule="",sort="added",icon="";public boolean matchAll;public int maximum=0;public boolean descending=true;public final Set<String> members=new LinkedHashSet<>(),calculated=new LinkedHashSet<>();Row(String id,String name){this.id=id;this.name=name;maximum=id.startsWith("custom:")?15:0;}public boolean custom(){return id.startsWith("custom:");}}
 private final Context context;private final SharedPreferences prefs;public final List<Row> rows=new ArrayList<>();
 public PreviewHomeRows(Context c){context=c;prefs=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);try{JSONArray data=new JSONArray(prefs.getString("preview_home_rows41","[]"));for(int i=0;i<data.length();i++){JSONObject o=data.getJSONObject(i);Row r=new Row(o.getString("id"),o.getString("name"));r.visible=o.optBoolean("visible",true);r.dynamic=o.optBoolean("dynamic",false);r.movies=o.optBoolean("movies",true);r.tv=o.optBoolean("tv",true);if(!r.movies&&!r.tv)r.movies=true;r.genreRule=o.optString("genres","");r.sort=o.optString("sort","added");r.icon=o.optString("icon","");r.matchAll=o.optBoolean("matchAll",false);r.descending=o.optBoolean("descending",!r.sort.equals("title"));JSONArray calculated=o.optJSONArray("calculated");if(calculated!=null)for(int j=0;j<calculated.length();j++)r.calculated.add(calculated.getString(j));r.maximum=r.custom()?normaliseMaximum(o.optInt("maximum",0)):Math.max(0,o.optInt("maximum",0));JSONArray members=o.optJSONArray("members");if(members!=null)for(int j=0;j<members.length();j++)r.members.add(members.getString(j));rows.add(r);}}catch(JSONException ignored){}for(int i=0;i<SYSTEM.length;i++){final String id=SYSTEM[i];if(rows.stream().noneMatch(r->r.id.equals(id)))rows.add(new Row(id,LABELS[i]));}for(Row row:rows)for(int i=0;i<SYSTEM.length;i++)if(row.id.equals(SYSTEM[i]))row.name=LABELS[i];}
 String encode()throws JSONException{JSONArray data=new JSONArray();for(Row r:rows)data.put(new JSONObject().put("id",r.id).put("name",r.name).put("visible",r.visible).put("members",new JSONArray(r.members)).put("dynamic",r.dynamic).put("movies",r.movies).put("tv",r.tv).put("genres",r.genreRule).put("sort",r.sort).put("icon",r.icon).put("matchAll",r.matchAll).put("descending",r.descending).put("calculated",new JSONArray(r.calculated)).put("maximum",r.maximum));return data.toString();}
 void save(){try{prefs.edit().putString("preview_home_rows41",encode()).apply();}catch(JSONException error){throw new IllegalStateException(error);}}
 public List<Entry> members(Row row,Snapshot snapshot){
  Map<String,Entry> available=new LinkedHashMap<>();for(Entry entry:snapshot.movies)available.put(entry.key(),entry);for(Entry entry:snapshot.shows)available.put(entry.key(),entry);
  LinkedHashSet<String> keys=new LinkedHashSet<>(row.members);
  if(row.dynamic){Set<String> genres=PreviewGenres.parse(row.genreRule);Set<String> calculated=new LinkedHashSet<>();
   for(Entry entry:available.values())if((entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Tvshow?row.tv:row.movies)&&PreviewGenres.matches(entry.genres,genres,row.matchAll))calculated.add(entry.key());
   if(!row.calculated.equals(calculated)){row.calculated.clear();row.calculated.addAll(calculated);save();}keys.addAll(calculated);
  }
  List<Entry> result=new ArrayList<>();for(String key:keys)if(available.containsKey(key))result.add(available.get(key));
  Comparator<Entry> order=row.sort.equals("title")?Comparator.comparing((Entry e)->PreviewPages.titleForSort(context,e),String.CASE_INSENSITIVE_ORDER):row.sort.equals("year")?Comparator.comparingInt(Entry::year):Comparator.comparingLong((Entry e)->e.added);
  if(row.descending)order=order.reversed();result.sort(order.thenComparing(PreviewPages::displayName,String.CASE_INSENSITIVE_ORDER));
  return row.maximum>0&&result.size()>row.maximum?new ArrayList<>(result.subList(0,row.maximum)):result;
 }
 public void setDynamic(Row row,boolean dynamic,Snapshot snapshot){
  if(row.dynamic&&!dynamic){if(snapshot!=null)members(row,snapshot);row.members.addAll(row.calculated);}
  row.dynamic=dynamic;save();
 }
 public void supersedeWatchNext(Set<String> continuing){boolean changed=false;for(Row row:rows)if(row.id.equals("watchnext"))changed|=row.members.removeAll(continuing);if(changed)save();}
 public boolean dismissed(Entry e){return prefs.contains("preview_cw_dismiss:"+e.key())&&e.playedAt<=prefs.getLong("preview_cw_dismiss:"+e.key(),0);}
 public void dismiss(Entry e){prefs.edit().putLong("preview_cw_dismiss:"+e.key(),e.playedAt).apply();}
 public static void add(Context c, Entry entry, Runnable changed) {
  if (entry == null) return;
  PreviewHomeRows model = new PreviewHomeRows(c);
  model.membership(entry, changed);
 }
 /** Series correction keeps explicit Home memberships attached to the corrected local series. */
 public static void reconcileShowIdentity(Context context,long previous,long replacement){
  if(previous<=0||replacement<=0||previous==replacement)return;
  PreviewHomeRows model=new PreviewHomeRows(context);String oldKey="s"+previous,newKey="s"+replacement;boolean changed=false;
  for(Row row:model.rows)if(row.members.contains(oldKey)){
   Set<String> updated=new LinkedHashSet<>();for(String key:row.members)updated.add(key.equals(oldKey)?newKey:key);
   row.members.clear();row.members.addAll(updated);changed=true;
  }
  if(changed)model.save();
  String oldDismiss="preview_cw_dismiss:"+oldKey,newDismiss="preview_cw_dismiss:"+newKey;
  if(model.prefs.contains(oldDismiss))model.prefs.edit().putLong(newDismiss,Math.max(model.prefs.getLong(oldDismiss,0),model.prefs.getLong(newDismiss,0))).apply();
 }
 private void membership(Entry entry, Runnable changed) {
  membership(entry,changed,null);
 }
 private void membership(Entry entry,Runnable changed,String focusRow) {
  List<Row> choices = new ArrayList<>();
  for (Row row : rows) if (row.custom() || row.id.equals("watchnext")) choices.add(row);
  List<String> labels = new ArrayList<>(); Set<Integer> checks = new HashSet<>();
  for (Row row : choices) { if (row.members.contains(entry.key())) checks.add(labels.size()); labels.add(row.name); }
  labels.add("Create New Row…");int selected=-1;
  if(focusRow!=null)for(int i=0;i<choices.size();i++)if(choices.get(i).id.equals(focusRow))selected=i;
  Dialog[] menu = {null};
  menu[0] = PreviewDialog.choose(context,"Add to Row",labels.toArray(new String[0]),selected,checks,false,n -> {
   if (n == choices.size()) {
    name("Create New Row","",value -> {
     Row row = new Row("custom:" + UUID.randomUUID(), value);
     row.members.add(entry.key()); rows.add(row); save(); changed.run();
     menu[0].dismiss(); membership(entry,changed,row.id);
    });
   } else {
    Row row = choices.get(n);
    if (!row.members.add(entry.key())) row.members.remove(entry.key());
    if (row.members.contains(entry.key())) checks.add(n); else checks.remove(n);
    save(); PreviewDialog.updateMembership(menu[0],checks,choices.size()); changed.run();
   }
  });
  PreviewDialog.updateMembership(menu[0],checks,choices.size());
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
 private void editor(Runnable changed){new CustomiseHomeEditor(context,this,changed).show();}
 public static final int[] MAXIMUM_PRESETS={5,10,15,20,25,30};
 static int normaliseMaximum(int old){
  if(old==0)return 30; // Legacy0 meant unlimited: retain the largest permitted count.
  int nearest=5;for(int preset:MAXIMUM_PRESETS)if(Math.abs((long)old-preset)<Math.abs((long)old-nearest))nearest=preset;
  return nearest;
 }
 public static String iconName(Row row){
  if(row.custom()){
   if(PreviewGenres.known(row.icon))return row.icon;
   Set<String> genres=PreviewGenres.parse(row.genreRule);return genres.isEmpty()?"custom row":genres.iterator().next();
  }
  switch(row.id){case "continue":return "history";case "watchnext":return "list";case "recent":return "date added";
   case "trending":return "trending";case "popular":return "star";case "watched":return "history";case "similar":return "recommendation";default:return "custom row";}
 }
 static Integer parseMaximum(String value){
  if(value==null||!value.trim().matches("[0-9]{1,10}"))return null;
  try{return Integer.valueOf(value.trim());}catch(NumberFormatException invalid){return null;}
 }
 private TextView label(String text,int size){TextView v=new TextView(context);v.setText(text);v.setTextSize(size);v.setTextColor(0xffd6e5ef);return v;}
 private TextView control(String text,Runnable action){TextView v=label(text,14);v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(dp(12),0,dp(12),0);v.setFocusable(true);v.setFocusableInTouchMode(true);v.setBackground(PreviewDialog.focus(context));v.setOnClickListener(w->action.run());return v;}
 private int dp(int n){return PreviewDialog.dp(context,n);}
}
