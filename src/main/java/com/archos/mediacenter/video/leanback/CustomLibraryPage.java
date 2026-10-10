package com.archos.mediacenter.video.leanback;
import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.json.*;
/** Exactly one persisted page. Draft changes never mutate media, watch state or the saved page. */
public final class CustomLibraryPage {
 public static final String KEY="supernova_custom_library_page";
 public String name="Library Page",icon="Library",type="both",view="grid",sort="title",watched="all",language="",studio="",collection="";
 public boolean allGenres,descending;public int year;public double rating;public final Set<String> genres=new TreeSet<>();
 public static CustomLibraryPage load(Context c){String data=PreferenceManager.getDefaultSharedPreferences(c).getString(KEY,null);CustomLibraryPage page=data==null?null:decode(data);return page!=null&&page.validation()==null?page:null;}
 public String encode(){try{return new JSONObject().put("name",name).put("icon",icon).put("type",type).put("view",view).put("sort",sort).put("watched",watched).put("allGenres",allGenres).put("descending",descending).put("year",year).put("rating",rating).put("language",language).put("studio",studio).put("collection",collection).put("genres",new JSONArray(genres)).toString();}catch(JSONException error){throw new IllegalStateException(error);}}
 public static CustomLibraryPage decode(String data){CustomLibraryPage page=new CustomLibraryPage();try{JSONObject value=new JSONObject(data);page.name=value.getString("name");page.icon=value.optString("icon","Library");page.type=value.optString("type","both");page.view=value.optString("view","grid");page.sort=value.optString("sort","title");page.watched=value.optString("watched","all");page.allGenres=value.optBoolean("allGenres");page.descending=value.optBoolean("descending");page.language=value.optString("language");page.studio=value.optString("studio");page.collection=value.optString("collection");page.year=value.optInt("year");page.rating=value.optDouble("rating",0);JSONArray genres=value.optJSONArray("genres");if(genres!=null)for(int i=0;i<genres.length();i++)page.genres.add(genres.getString(i));}catch(JSONException error){return null;}return page;}
 public String validation(){if(name.trim().isEmpty()||name.trim().length()>40)return "Enter a page name from 1 to 40 characters.";if(!Arrays.asList("movies","tv","both").contains(type))return "Choose Movies, TV Shows or Both.";if(!Arrays.asList("Library","Movies","TV Shows","Documentaries").contains(icon)||year<0||year>9999||!Arrays.asList("grid","list").contains(view)||!Arrays.asList("title","year","added").contains(sort)||!Arrays.asList("all","watched","unwatched","progress").contains(watched)||Double.isNaN(rating)||rating<0||rating>10)return "Choose valid page filters and display options.";return null;}
 public boolean save(Context c){if(validation()!=null)return false;name=name.trim();return PreferenceManager.getDefaultSharedPreferences(c).edit().putString(KEY,encode()).commit();}
 public static boolean delete(Context c){return PreferenceManager.getDefaultSharedPreferences(c).edit().remove(KEY).commit();}
 public List<Entry> entries(Snapshot snapshot){
  List<Entry> source=new ArrayList<>(),result=new ArrayList<>();if(!type.equals("tv"))source.addAll(snapshot.movies);if(!type.equals("movies"))source.addAll(snapshot.shows);
  for(Entry entry:source){Set<String> available=PreviewGenres.parse(entry.genres);boolean match=genres.isEmpty()||(allGenres?available.containsAll(genres):genres.stream().anyMatch(available::contains));if(!match||year>0&&entry.year()!=year||!language.isEmpty()&&!language.equals(entry.language)||!studio.isEmpty()&&!PreviewGenres.parse(entry.studio).contains(studio)||!collection.isEmpty()&&!collection.equals(entry.collection))continue;
   boolean seen=entry.media instanceof Video?((Video)entry.media).isWatched():snapshot.watched.stream().anyMatch(item->item.key().equals(entry.key()));boolean progress=entry.media instanceof Video&&((Video)entry.media).getResumeMs()>0||snapshot.continuingShows.stream().anyMatch(item->item.key().equals(entry.key()));
   if(watched.equals("watched")&&!seen||watched.equals("unwatched")&&seen||watched.equals("progress")&&!progress)continue;
   double actual=entry.media instanceof Movie?((Movie)entry.media).getRating():entry.media instanceof Tvshow?((Tvshow)entry.media).getRating():0;if(rating>0&&(actual<=0||actual<rating))continue;result.add(entry);
  }
  Comparator<Entry> order=sort.equals("year")?Comparator.comparingInt(Entry::year):sort.equals("added")?Comparator.comparingLong(e->e.added):Comparator.comparing(PreviewPages::displayName,String.CASE_INSENSITIVE_ORDER);
  if(descending)order=order.reversed();result.sort(order.thenComparing(Entry::key));return result;
 }
}
