package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.Bundle;
import android.view.*;
import androidx.preference.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.settings.PreviewSettings;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextQaReliabilityTest {
 @Before public void offline(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drain() throws Exception {PreviewAsyncFixtures.drain();}
 public static class Settings extends PreferenceFragmentCompat {
  @Override public void onCreatePreferences(Bundle state,String key){setPreferencesFromResource(R.xml.preferences_video,key);PreviewSettings.organise(this);}
  @Override public androidx.recyclerview.widget.RecyclerView onCreateRecyclerView(LayoutInflater i,ViewGroup p,Bundle s){return PreviewSettings.grid(requireContext());}
  @Override protected androidx.recyclerview.widget.RecyclerView.Adapter onCreateAdapter(PreferenceScreen root){return PreviewSettings.adapter(root);}
  @Override public void onViewCreated(View v,Bundle s){super.onViewCreated(v,s);PreviewSettings.sidebar(this);}
 }
 @Test public void everyRealSettingsCategoryAndDiagnosticsRemainReachable() throws Exception {
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try {
   host.get().setTheme(R.style.MyLeanbackTheme);
   Settings settings=new Settings();host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,settings).commitNow();
   View root=settings.requireView();PreviewPagesTest.layout(root);assertNotNull(settings.findPreference(com.archos.mediacenter.video.diagnostics.Diagnostics.LEVEL));assertTrue(com.archos.mediacenter.video.diagnostics.Diagnostics.defaultEnabled());
   for(String label:new String[]{"Playback","Video","Audio","Subtitles","Library & Metadata","Home","Appearance","Streaming","Network","Integrations","Advanced","About"}){
    View row=root.findViewWithTag("semantic:settings:category:"+label);assertNotNull(label,row);if(label.equals("Playback"))assertTrue(row.requestFocus());else root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));PreviewPagesTest.layout(root);assertSame(label,row,root.findFocus());
   }
   View advanced=root.findViewWithTag("semantic:settings:category:Advanced");advanced.requestFocus();advanced.performClick();PreviewPagesTest.layout(root);
   View diagnostics=root.findViewWithTag("semantic:settings:section:preview_diagnostics_group");assertNotNull(diagnostics);diagnostics.requestFocus();PreviewPagesTest.layout(root);
   assertTrue(settings.findPreference("export_diagnostic_report").isVisible());
  } finally {host.pause().stop().destroy();}
 }
 @Test public void allFilesUsesOneFilterForBothFilesAndHiddenDirectories(){
  class Filter extends com.archos.filecorelibrary.ListingEngine {
   Filter(){super(RuntimeEnvironment.getApplication());}public void start(){}public void abort(){}
   boolean file(String name){return keepFile(name);}boolean directory(String name){return keepDirectory(name);}
  }
  Filter filter=new Filter();filter.setFilter(new String[]{"video/","application/x-bittorrent"},null);filter.setKeepHiddenFiles(false);
  assertTrue(filter.file("Film.MKV"));assertFalse(filter.file("notes.txt"));assertFalse(filter.file(".private.mkv"));assertFalse(filter.directory(".hidden"));assertTrue(filter.directory("Movies"));
  filter.setFilter(null,null);filter.setKeepHiddenFiles(true);assertTrue(filter.file("notes.txt"));assertTrue(filter.file(".private.mkv"));assertTrue(filter.directory(".hidden"));
  filter.setFilter(new String[]{"video/","application/x-bittorrent"},null);filter.setKeepHiddenFiles(false);assertFalse(filter.file("notes.txt"));assertTrue(filter.file("Film.MKV"));
 }
 @Test public void historyBroadcastFollowsCommittedEpisodeProgress() throws Exception {
  android.content.Context c=RuntimeEnvironment.getApplication();c.deleteDatabase(ViewingHistory.DATABASE);PreferenceManager.getDefaultSharedPreferences(c).edit().clear().commit();
  final int[] observed={0};android.content.BroadcastReceiver receiver=new android.content.BroadcastReceiver(){public void onReceive(android.content.Context context,android.content.Intent intent){assertEquals(5000,ViewingHistory.read(context).get("tmdb:tv:123:2:1").position);observed[0]++;}};
  c.registerReceiver(receiver,new android.content.IntentFilter(c.getPackageName()+".VIEWING_STATE_CHANGED"));
  try{com.archos.mediacenter.utils.videodb.VideoDbInfo info=new com.archos.mediacenter.utils.videodb.VideoDbInfo(android.net.Uri.parse("file:///storage/TV/Vigil/S02E01.mkv"));info.isShow=true;info.scraperShowId="123";info.scraperSeasonNr=2;info.scraperEpisodeNr=1;info.duration=100000;info.resume=5000;info.lastTimePlayed=100;ViewingHistory.record(c,info,false);ViewingHistory.awaitWrites();org.robolectric.shadows.ShadowLooper.idleMainLooper();assertEquals(1,observed[0]);}finally{c.unregisterReceiver(receiver);}
 }
 @Test public void keyboardDoneEditsFieldAndCancelDoesNotCommitPage(){
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{android.widget.EditText input=new KeyboardInput(host.get());input.setText("Name");input.setSelection(4);PreviewKeyboard keyboard=new PreviewKeyboard(host.get(),input,null);host.get().setContentView(keyboard);final int[] committed={0};keyboard.setActions(()->committed[0]++,()->{});PreviewPagesTest.layout(keyboard);keyboard.focusLastKey();assertEquals("T",keyboard.focusedKey());assertNotNull(keyboard.findViewWithTag("semantic:keyboard:Space"));keyboard.findViewWithTag("semantic:keyboard:Done").performClick();assertEquals(1,committed[0]);keyboard.findViewWithTag("semantic:keyboard:Clear").performClick();assertEquals("",input.getText().toString());assertEquals(1,committed[0]);assertTrue(input.getFocusables(View.FOCUS_DOWN).isEmpty());}finally{host.pause().stop().destroy();}
 }
 @Test public void responsiveTokensRespectLogicalViewportAndBoundDialogs(){
  var c=RuntimeEnvironment.getApplication();assertEquals(6,TvLayout.gridColumns(c));assertEquals(1f,TvLayout.scale(c),.001f);assertTrue(TvLayout.dialogWidth(c)<TvLayout.safeWidth(c));assertEquals(44,PreviewDialog.dp(c,44));
 }
 @Test(timeout=20000) public void excludedFullPageCannotStarveLaterDriveRows() throws Exception {
  android.content.Context c=RuntimeEnvironment.getApplication();PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("supernova_onboarding_complete",true).putBoolean("supernova_library_policy_initialized",true).putStringSet("supernova_library_roots",java.util.Set.of("file:///storage/drive-b")).commit();
  android.content.ContentResolver resolver=org.mockito.Mockito.mock(android.content.ContentResolver.class);final java.util.List<Long> pages=new java.util.ArrayList<>();
  org.mockito.Mockito.when(resolver.query(org.mockito.ArgumentMatchers.any(android.net.Uri.class),org.mockito.ArgumentMatchers.any(String[].class),org.mockito.ArgumentMatchers.any(android.os.Bundle.class),org.mockito.ArgumentMatchers.isNull())).thenAnswer(call->{String selection=((android.os.Bundle)call.getArgument(2)).getString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION);java.util.regex.Matcher match=java.util.regex.Pattern.compile("_id > (-?[0-9]+)").matcher(selection);long after=match.find()?Long.parseLong(match.group(1)):-1;pages.add(after);if(pages.size()>4)throw new AssertionError("Unscanned excluded page was revisited");android.database.MatrixCursor rows=new android.database.MatrixCursor(new String[]{"_id","_data","scraper_id"});for(long id=Math.max(1,after+1);id<=Math.min(2502,Math.max(1,after+1)+2499);id++)rows.addRow(new Object[]{id,"file:///storage/drive-"+(id<=2500?"a":"b")+(id<=2500?"/excluded.mkv":"/sentinel.txt"),0});return rows;});
  com.archos.mediaprovider.video.VideoStoreImportImpl importer=org.mockito.Mockito.mock(com.archos.mediaprovider.video.VideoStoreImportImpl.class,org.mockito.Mockito.CALLS_REAL_METHODS);java.lang.reflect.Method scan=com.archos.mediaprovider.video.VideoStoreImportImpl.class.getDeclaredMethod("doScan",android.content.ContentResolver.class,android.content.Context.class,com.archos.mediaprovider.video.Blacklist.class);scan.setAccessible(true);scan.invoke(importer,resolver,c,org.mockito.Mockito.mock(com.archos.mediaprovider.video.Blacklist.class));
  org.mockito.ArgumentCaptor<java.util.ArrayList<android.content.ContentProviderOperation>> batches=org.mockito.ArgumentCaptor.forClass(java.util.ArrayList.class);org.mockito.Mockito.verify(resolver,org.mockito.Mockito.times(2)).applyBatch(org.mockito.ArgumentMatchers.anyString(),batches.capture());assertEquals(0,batches.getAllValues().get(0).size());assertEquals(2,batches.getAllValues().get(1).size());
  assertEquals(java.util.List.of(-1L,2500L),pages);org.mockito.Mockito.verify(resolver,org.mockito.Mockito.never()).delete(org.mockito.ArgumentMatchers.any(android.net.Uri.class),org.mockito.ArgumentMatchers.any(),org.mockito.ArgumentMatchers.any());
 }
}
