package com.archos.mediaprovider.video;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediacenter.video.streaming.putio.PutioReconciliation;
import com.archos.mediacenter.video.streaming.putio.PutioAssociationStore;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import static org.junit.Assert.*;

/** Native schema/trigger integration; only ContentProvider transport is an in-process fixture. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewPutioNativeImportTest {
    DbHolder database;Object bridge;Class<?> type;boolean failAfterMove;Uri source=Uri.parse("https://webdav.put.io/Films");
    @Before public void setup()throws Exception{
        Application app=RuntimeEnvironment.getApplication();app.deleteDatabase("putio-native.db");app.deleteDatabase("putio-associations.db");app.getSharedPreferences("provider-discovery-ownership-v1",0).edit().clear().commit();database=new DbHolder(new VideoOpenHelper(app,"putio-native.db",VideoOpenHelper.getDatabaseVersion()));
        ContentProvider provider=new ContentProvider(){
            public boolean onCreate(){return true;}public String getType(Uri uri){return null;}
            String table(Uri uri){String name=uri.getLastPathSegment();if(!name.equals("files")&&!name.equals("files_scanned"))throw new IllegalArgumentException();return name;}
            public Cursor query(Uri uri,String[] projection,String selection,String[] args,String order){return database.get().query(table(uri),projection,selection,args,null,null,order);}
            public Uri insert(Uri uri,ContentValues values){return ContentUris.withAppendedId(uri,database.get().insertOrThrow(table(uri),null,values));}
            public int update(Uri uri,ContentValues values,String selection,String[] args){int changed=database.get().update(table(uri),values,selection,args);if(failAfterMove){failAfterMove=false;throw new IllegalStateException("Interrupted after native URI commit");}return changed;}
            public int delete(Uri uri,String selection,String[] args){throw new AssertionError("Native sync must not delete library records");}
        };
        provider.attachInfo(app,null);ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY,provider);
        type=Class.forName("com.archos.mediacenter.video.streaming.putio.PutioLibraryBridge");java.lang.reflect.Constructor<?> constructor=type.getDeclaredConstructor(Context.class);constructor.setAccessible(true);bridge=constructor.newInstance(app);
    }
    @After public void close(){if(database!=null)database.close();}
    Object call(String name,Class<?>[] parameters,Object...args)throws Exception{java.lang.reflect.Method method=type.getDeclaredMethod(name,parameters);method.setAccessible(true);return method.invoke(bridge,args);}
    @Test public void importedFileUsesNativeIdAndRenameKeepsHistoryAndMetadata()throws Exception{
        PutioReconciliation.File file=new PutioReconciliation.File(30,20,"Original.mkv",100);
        long id=(Long)call("insert",new Class[]{Uri.class,PutioReconciliation.File.class},source,file);
        ContentValues state=new ContentValues();state.put(VideoStore.Video.VideoColumns.BOOKMARK,12345);state.put(VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED,98765L);database.get().update("files",state,"_id=?",new String[]{""+id});
        ScraperProvider.hookUriMatcher(new UriMatcher(UriMatcher.NO_MATCH));ScraperProvider scraper=new ScraperProvider(RuntimeEnvironment.getApplication(),database);
        ContentValues tags=new ContentValues();tags.put(ScraperStore.Movie.VIDEO_ID,id);tags.put(ScraperStore.Movie.NAME,"Preserved movie match");assertNotNull(scraper.insert(ScraperStore.Movie.URI.BASE,tags));
        call("relocate",new Class[]{Uri.class,PutioReconciliation.Existing.class,PutioReconciliation.File.class},source,new PutioReconciliation.Existing(id,"Original.mkv",100,30),new PutioReconciliation.File(30,20,"Moved/Renamed.mkv",100));
        try(Cursor c=database.get().query("files",new String[]{"_id","_data",VideoStore.Video.VideoColumns.BOOKMARK,VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED},null,null,null,null,null)){
            assertEquals(1,c.getCount());assertTrue(c.moveToFirst());assertEquals(id,c.getLong(0));assertEquals("https://webdav.put.io/Films/Moved/Renamed.mkv",c.getString(1));assertEquals(12345,c.getLong(2));assertEquals(98765,c.getLong(3));
        }
        try(Cursor c=database.get().query(ScraperTables.MOVIE_TABLE_NAME,new String[]{ScraperStore.Movie.NAME},ScraperStore.Movie.VIDEO_ID+"=?",new String[]{""+id},null,null,null)){assertTrue(c.moveToFirst());assertEquals("Preserved movie match",c.getString(0));}
    }
    @Test public void existingHiddenOrVisiblePathCannotBeInsertedAgain()throws Exception{
        PutioReconciliation.File file=new PutioReconciliation.File(30,20,"Original.mkv",100);call("insert",new Class[]{Uri.class,PutioReconciliation.File.class},source,file);
        try{call("insert",new Class[]{Uri.class,PutioReconciliation.File.class},source,file);fail();}catch(java.lang.reflect.InvocationTargetException expected){assertTrue(expected.getCause() instanceof IllegalStateException);}
        try(Cursor c=database.get().rawQuery("SELECT COUNT(*) FROM files",null)){assertTrue(c.moveToFirst());assertEquals(1,c.getInt(0));}
    }
    private PutioReconciliation.Snapshot snapshot(long folder,boolean complete,PutioReconciliation.File...files){PutioReconciliation.Snapshot s=new PutioReconciliation.Snapshot("10:"+folder,"first");s.page("first",java.util.Arrays.asList(files),complete?java.util.Collections.emptyList():java.util.Collections.singletonList("more"));if(complete)s.finish();return s;}
    private long linkedFile()throws Exception{
        PutioReconciliation.File file=new PutioReconciliation.File(30,20,"Original.mkv",100);long id=(Long)call("insert",new Class[]{Uri.class,PutioReconciliation.File.class},source,file);
        try(PutioAssociationStore store=new PutioAssociationStore(RuntimeEnvironment.getApplication())){store.prepare(10,20,source);PutioAssociationStore.Session session=store.begin(10,20);assertTrue(store.commit(session,snapshot(20,true,file),java.util.Collections.singletonList(new PutioReconciliation.Existing(id,"Original.mkv",100,0))));assertTrue(store.activate(session));}return id;
    }
    private void reassign(long folder,Uri target,PutioReconciliation.Snapshot snapshot)throws Exception{
        Class<?> implementation=Class.forName("com.archos.mediacenter.video.streaming.putio.PutioReassignment");java.lang.reflect.Method method=implementation.getDeclaredMethod("apply",Context.class,long.class,long.class,long.class,Uri.class,PutioAssociationStore.DisconnectChoice.class,PutioReconciliation.Snapshot.class);method.setAccessible(true);
        method.invoke(null,RuntimeEnvironment.getApplication(),10L,20L,folder,target,PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE,snapshot);
    }
    @Test public void reassignmentResumesAfterNativeMoveWithoutDuplicatingOrChangingId()throws Exception{
        long id=linkedFile();Uri target=Uri.parse("https://webdav.put.io/NewFilms");PutioReconciliation.Snapshot full=snapshot(21,true,new PutioReconciliation.File(30,21,"Moved.mkv",100));failAfterMove=true;
        try{reassign(21,target,full);fail();}catch(java.lang.reflect.InvocationTargetException expected){assertTrue(expected.getCause() instanceof IllegalStateException);}
        try(PutioAssociationStore store=new PutioAssociationStore(RuntimeEnvironment.getApplication())){assertEquals(PutioAssociationStore.Ownership.REASSIGNING,store.ownership(10,21));}
        reassign(21,target,full);
        try(PutioAssociationStore store=new PutioAssociationStore(RuntimeEnvironment.getApplication())){assertEquals(id,store.links(10,21).get(0).mediaId);assertTrue(store.links(10,20).isEmpty());assertEquals(PutioAssociationStore.Ownership.INACTIVE,store.ownership(10,20));}
        try(Cursor c=database.get().rawQuery("SELECT _id,_data FROM files",null)){assertEquals(1,c.getCount());assertTrue(c.moveToFirst());assertEquals(id,c.getLong(0));assertEquals("https://webdav.put.io/NewFilms/Moved.mkv",c.getString(1));}
    }
    @Test public void incompleteReassignmentCannotRetireTheOriginalSource()throws Exception{
        long id=linkedFile();try{reassign(21,Uri.parse("https://webdav.put.io/NewFilms"),snapshot(21,false));fail();}catch(java.lang.reflect.InvocationTargetException expected){assertTrue(expected.getCause() instanceof IllegalArgumentException);}
        try(PutioAssociationStore store=new PutioAssociationStore(RuntimeEnvironment.getApplication())){assertEquals(PutioAssociationStore.Ownership.API,store.ownership(10,20));assertEquals(id,store.links(10,20).get(0).mediaId);}
    }
    @Test public void sameFolderTransportChangeRetainsMissingIdentityAndExplicitRetiredSourceChoice()throws Exception{
        long id=linkedFile();reassign(20,Uri.parse("https://webdav.put.io/MovedRoot"),snapshot(20,true));
        try(PutioAssociationStore store=new PutioAssociationStore(RuntimeEnvironment.getApplication())){assertEquals(id,store.links(10,20).get(0).mediaId);assertTrue(store.links(10,20).get(0).missing);assertTrue(ProviderDiscoveryGate.excludes(RuntimeEnvironment.getApplication(),source));store.disconnectAccount(10,PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC);assertFalse(ProviderDiscoveryGate.excludes(RuntimeEnvironment.getApplication(),source));}
    }
}
