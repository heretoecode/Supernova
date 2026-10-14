package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediascraper.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class MediaMatchReconciliationTest {
    private final CanonicalMatchProviderTest fixture=new CanonicalMatchProviderTest();private Application app;private DbHolder holder;
    @Before public void setup(){fixture.setup();app=RuntimeEnvironment.getApplication();holder=ReflectionHelpers.getField(fixture,"holder");PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();com.archos.environment.ArchosUtils.setGlobalContext(app);}
    @After public void close(){fixture.close();}
    private PortablePhysicalReferences physical()throws Exception{return new PortablePhysicalReferences(app,LogicalBackupCatalog.capture(app,()->false).physical);}
    private JSONObject row(String reference,long provider)throws Exception{return new JSONObject().put("physical",reference).put("identity","tmdb:movie:"+provider).put("episodeProvider",JSONObject.NULL);}
    private JSONObject payload(String reference)throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(row(reference,789)).put(row(physical().reference(11),456)));}
    private void queue(JSONObject payload)throws Exception {var transaction=new PreferenceRestoreTransaction(app);var plan=transaction.prepareCanonicalMatches(Map.of(BackupCategory.MATCHES,payload),null,physical(),(context,request,file)->{throw new IOException("Provider fixture unavailable");},()->false);transaction.apply(plan,()->false);}
    private CanonicalMatchMaterial.Resolver resolver(AtomicInteger calls){return (context,request,file)->{calls.incrementAndGet();MovieTags tags=new MovieTags();tags.setOnlineId(Long.parseLong(request.identity.split(":")[2]));tags.setTitle("Restored canonical title");tags.setPlot("Restored canonical facts");return new ScrapeDetailResult(tags,true,null,ScrapeStatus.OKAY,null);};}
    private long provider(long video){try(var row=holder.get().rawQuery("SELECT m_online_id FROM MOVIE WHERE video_id=?",new String[]{Long.toString(video)})){assertTrue(row.moveToFirst());return row.getLong(0);}}
    @Test public void queuedCorrectionAppliesWhenProviderBecomesAvailableAndDoesNotRepeatAfterResolution()throws Exception {
        queue(payload(physical().reference(10)));assertEquals(123,provider(10));AtomicInteger calls=new AtomicInteger();var transaction=new PreferenceRestoreTransaction(app);assertTrue(transaction.reconcileMatches(resolver(calls),()->false));assertEquals(789,provider(10));assertEquals(1,calls.get());assertFalse(new JSONObject(PreferenceManager.getDefaultSharedPreferences(app).getString(MediaMatchIntents.key(10),null)).getBoolean("pending"));assertFalse(transaction.reconcileMatches(resolver(calls),()->false));assertEquals(1,calls.get());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));
    }
    @Test public void queuedManualCorrectionOverridesAutomaticIndexingButLaterExplicitReceivingChoiceWins()throws Exception {
        queue(payload(physical().reference(10)));holder.get().execSQL("UPDATE MOVIE SET m_online_id=555,name_movie='Automatic match' WHERE _id=100");AtomicInteger calls=new AtomicInteger();assertTrue(new PreferenceRestoreTransaction(app).reconcileMatches(resolver(calls),()->false));assertEquals(789,provider(10));
        queue(payload(physical().reference(10)).put("rows",new JSONArray().put(row(physical().reference(10),888)).put(row(physical().reference(11),456))));MediaMatchManualChanges.mark(app,10);holder.get().execSQL("UPDATE MOVIE SET m_online_id=999,name_movie='Receiving manual choice' WHERE _id=100");int before=calls.get();assertTrue(new PreferenceRestoreTransaction(app).reconcileMatches(resolver(calls),()->false));assertEquals(999,provider(10));assertEquals(before,calls.get());
    }
    @Test public void unresolvedPhysicalReferenceBindsAfterIndexingWithoutCreatingFakeMediaDuringImport()throws Exception {
        String reference=PortablePhysicalReferences.fingerprint("file:///later.mkv");queue(payload(reference));try(var row=holder.get().rawQuery("SELECT COUNT(*) FROM files WHERE _id=20",null)){assertTrue(row.moveToFirst());assertEquals(0,row.getLong(0));}
        holder.get().execSQL("INSERT INTO files(_id,remote_id,media_type,_data,bookmark) VALUES(20,20,3,'file:///later.mkv',7654)");AtomicInteger calls=new AtomicInteger();assertTrue(new PreferenceRestoreTransaction(app).reconcileMatches(resolver(calls),()->false));assertEquals(789,provider(20));try(var row=holder.get().rawQuery("SELECT bookmark FROM files WHERE _id=20",null)){assertTrue(row.moveToFirst());assertEquals(7654,row.getLong(0));}
        JSONObject pending=new JSONObject(PreferenceManager.getDefaultSharedPreferences(app).getString(PortableMediaMatches.PENDING,null));assertEquals(0,pending.getJSONArray("rows").length());
    }
    @Test public void unresolvedRetryRetainsOriginalManualBaselineUntilLocationCanBind()throws Exception {
        String reference=PortablePhysicalReferences.fingerprint("file:///later.mkv");queue(payload(reference));holder.get().execSQL("INSERT INTO files(_id,remote_id,media_type,_data) VALUES(20,20,3,'file:///temporary.mkv')");MediaMatchManualChanges.mark(app,20);holder.get().execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie,Nova_pinned) VALUES(250,20,999,'Receiving manual choice',7)");AtomicInteger calls=new AtomicInteger();new PreferenceRestoreTransaction(app).reconcileMatches(resolver(calls),()->false);
        JSONObject pending=new JSONObject(PreferenceManager.getDefaultSharedPreferences(app).getString(PortableMediaMatches.PENDING,null));assertEquals(0,pending.getLong("manualAt"));holder.get().execSQL("UPDATE files SET _data='file:///later.mkv' WHERE _id=20");new PreferenceRestoreTransaction(app).reconcileMatches(resolver(calls),()->false);assertEquals(999,provider(20));assertEquals(0,calls.get());
    }
    @Test public void manualChoiceAfterPreviewInvalidatesRestoreBeforeJournalOrNativeMutation()throws Exception {
        var transaction=new PreferenceRestoreTransaction(app);AtomicInteger calls=new AtomicInteger();var plan=transaction.prepareCanonicalMatches(Map.of(BackupCategory.MATCHES,payload(physical().reference(10))),null,physical(),resolver(calls),()->false);MediaMatchManualChanges.mark(app,10);
        try{transaction.apply(plan,()->false);fail("Later explicit choice ignored");}catch(IOException expected){}assertEquals(123,provider(10));assertFalse(new java.io.File(app.getFilesDir(),"supernova-restore/preferences-v1.json").exists());
    }
}
