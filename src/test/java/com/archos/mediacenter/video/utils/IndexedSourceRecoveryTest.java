package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class IndexedSourceRecoveryTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase(IndexedSourceStoreRecovery.DATABASE);PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();try(var db=SQLiteDatabase.openOrCreateDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE),null)){db.execSQL("CREATE TABLE shortcuts_table_video(_id INTEGER PRIMARY KEY AUTOINCREMENT,path TEXT NOT NULL,name TEXT NOT NULL,ippath TEXT,friendly_url TEXT,rescan INTEGER DEFAULT(1))");db.setVersion(5);db.execSQL("INSERT INTO shortcuts_table_video(_id,path,name,ippath,friendly_url,rescan) VALUES(1,'smb://nas.example/PRIVATE_FOLDER','PRIVATE_FOLDER','smb://10.0.0.1/PRIVATE_FOLDER','FRIENDLY_PATH_CANARY',0),(2,'putio:///ACCOUNT_CANARY','Private receiving account',NULL,NULL,NULL)");}}
    private JSONObject payload(String uri,boolean rescan)throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("location",PortableLocations.reference(uri)).put("name","Restored source").put("rescan",rescan)));}
    private JSONObject before()throws Exception{return IndexedSourceStoreRecovery.capture(app);}
    private void assertReceivingAccount(JSONObject state)throws Exception{JSONArray rows=state.getJSONArray("rows");boolean found=false;for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);if(row.getLong("id")==2){assertEquals("putio:///ACCOUNT_CANARY",row.getString("path"));assertTrue(row.isNull("rescan"));found=true;}}assertTrue(found);}
    @Test public void sourceExportRedactsDefaultFolderLabelIpCacheFriendlyLocatorAndExcludedAccount()throws Exception{
        JSONObject image=before(),payload=PortableIndexedSources.export(app,image);assertEquals(1,payload.getJSONArray("rows").length());JSONObject row=payload.getJSONArray("rows").getJSONObject(0);assertTrue(row.isNull("name"));assertFalse(row.getBoolean("rescan"));for(String excluded:List.of("PRIVATE_FOLDER","10.0.0.1","FRIENDLY_PATH_CANARY","ACCOUNT_CANARY","AUTH_CANARY","putio"))assertFalse(excluded,payload.toString().contains(excluded));assertEquals(Set.of("smb://nas.example/PRIVATE_FOLDER"),PortableIndexedSources.locations(image));
    }
    @Test public void configuredLocationRestoresRescanAndNameWithoutOptionalLocatorDisclosure()throws Exception{
        JSONObject before=before();var prepared=PortableIndexedSources.prepare(before,payload("smb://nas.example/PRIVATE_FOLDER",true),null);assertEquals(0,prepared.unresolved);try(var session=IndexedSourceStoreRecovery.begin(app,before,prepared.after)){session.write(prepared.after,()->false);session.commit();}assertEquals(prepared.after.toString(),before().toString());assertReceivingAccount(before());JSONObject row=before().getJSONArray("rows").getJSONObject(0);assertEquals("Restored source",row.getString("name"));assertEquals(1,row.getInt("rescan"));assertEquals("smb://10.0.0.1/PRIVATE_FOLDER",row.getString("ip"));assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));
    }
    @Test public void unknownLocationsStayPendingUnlessExplicitlySelectedAndNeverInventIpOrFriendlyBindings()throws Exception{
        JSONObject before=before(),incoming=payload("smb://other.example/NEW_PRIVATE",true);var pending=PortableIndexedSources.prepare(before,incoming,null);assertEquals(1,pending.unresolved);assertEquals(1,pending.after.getJSONArray("rows").length());assertReceivingAccount(pending.after);
        var known=PortableIndexedSources.prepare(before,incoming,PortableLocations.export(Set.of("smb://other.example/NEW_PRIVATE")));assertEquals(0,known.unresolved);JSONArray rows=known.after.getJSONArray("rows");JSONObject newRow=rows.getJSONObject(1);assertEquals("smb://other.example/NEW_PRIVATE",newRow.getString("path"));assertTrue(newRow.isNull("ip"));assertTrue(newRow.isNull("friendly"));assertEquals(3,newRow.getLong("id"));
        try(var session=IndexedSourceStoreRecovery.begin(app,before,known.after)){session.write(known.after,()->false);session.commit();}assertEquals(known.after.toString(),before().toString());
    }
    @Test public void nullableFieldsAndSequenceRecoverExactlyAfterRollbackOrNativeRedo()throws Exception{
        JSONObject before=before();var prepared=PortableIndexedSources.prepare(before,payload("smb://nas.example/PRIVATE_FOLDER",true),null);try(var session=IndexedSourceStoreRecovery.begin(app,before,prepared.after)){session.write(prepared.after,()->false);}assertEquals(before.toString(),before().toString());IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);assertEquals(prepared.after.toString(),before().toString());IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);IndexedSourceStoreRecovery.recover(app,before,prepared.after,false);assertEquals(before.toString(),before().toString());
    }
    @Test public void cancellationRollsBackAndLaterManualChangesAreRefusedByUnacknowledgedRecovery()throws Exception{
        JSONObject before=before();var prepared=PortableIndexedSources.prepare(before,payload("smb://nas.example/PRIVATE_FOLDER",true),null);try(var session=IndexedSourceStoreRecovery.begin(app,before,prepared.after)){try{session.write(prepared.after,()->true);fail();}catch(InterruptedIOException expected){}}assertEquals(before.toString(),before().toString());
        try(var db=SQLiteDatabase.openDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.execSQL("UPDATE shortcuts_table_video SET name='Later manual' WHERE _id=1");}String current=before().toString();try{IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);fail();}catch(IOException expected){}assertEquals(current,before().toString());try(var ignored=IndexedSourceStoreRecovery.begin(app,before,prepared.after)){fail();}catch(IOException expected){}assertEquals(current,before().toString());
    }
    @Test public void presentEmptyClearsSafeIndexedFoldersButPreservesExcludedCredentialAndPutioAssociations()throws Exception{
        try(var db=SQLiteDatabase.openDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.execSQL("INSERT INTO shortcuts_table_video(path,name,rescan) VALUES('smb://user:AUTH_CANARY@private/share','Receiving authentication',1)");}
        JSONObject before=before(),empty=new JSONObject().put("schema",1).put("rows",new JSONArray());var prepared=PortableIndexedSources.prepare(before,empty,null);assertEquals(2,prepared.after.getJSONArray("rows").length());IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);assertReceivingAccount(before());assertTrue(before().getJSONArray("rows").getJSONObject(1).getString("path").contains("AUTH_CANARY"));
    }
    @Test public void absentStoreStaysAbsentForPendingThenCanCreateExplicitSourceAndRemoveOnRollback()throws Exception{
        app.deleteDatabase(IndexedSourceStoreRecovery.DATABASE);JSONObject before=before(),incoming=payload("smb://new.example/Media",false);var pending=PortableIndexedSources.prepare(before,incoming,null);assertEquals(1,pending.unresolved);IndexedSourceStoreRecovery.recover(app,before,pending.after,true);assertFalse(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).exists());
        var prepared=PortableIndexedSources.prepare(before,incoming,PortableLocations.export(Set.of("smb://new.example/Media")));try(var session=IndexedSourceStoreRecovery.begin(app,before,prepared.after)){session.write(prepared.after,()->false);}assertTrue(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).exists());IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);assertEquals(prepared.after.toString(),before().toString());IndexedSourceStoreRecovery.recover(app,before,prepared.after,false);assertFalse(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).exists());IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);assertEquals(prepared.after.toString(),before().toString());
    }
    @Test public void excludedPairCorruptionUnknownColumnsAndFutureSchemasRejectBeforeMutation()throws Exception{
        JSONObject before=before(),corrupt=new JSONObject(before.toString());corrupt.getJSONArray("rows").getJSONObject(1).put("path","putio:///OTHER_ACCOUNT");try{IndexedSourceStoreRecovery.validatePair(before,corrupt);fail();}catch(JSONException expected){}assertEquals(before.toString(),before().toString());
        try(var db=SQLiteDatabase.openDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.execSQL("ALTER TABLE shortcuts_table_video ADD COLUMN unrelated_owner TEXT");}try{before();fail();}catch(IOException expected){}
        app.deleteDatabase(IndexedSourceStoreRecovery.DATABASE);setup();try(var db=SQLiteDatabase.openDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.setVersion(900);}try{before();fail();}catch(IOException expected){}
    }
    @Test public void duplicateLocationsUnsafeSourceLabelsAndAuthenticatedSourceExportFailClosed()throws Exception{
        try(var db=SQLiteDatabase.openDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.execSQL("INSERT INTO shortcuts_table_video(path,name,rescan) VALUES('smb://nas.example/PRIVATE_FOLDER','Duplicate',1)");}try{PortableIndexedSources.export(app,before());fail();}catch(JSONException expected){}try{PortableIndexedSources.prepare(before(),payload("smb://nas.example/PRIVATE_FOLDER",true),null);fail();}catch(JSONException expected){}
        setup();JSONObject incoming=payload("smb://nas.example/PRIVATE_FOLDER",true);incoming.getJSONArray("rows").getJSONObject(0).put("name","password=AUTH_CANARY");try{PortableIndexedSources.validate(incoming);fail();}catch(JSONException expected){assertFalse(expected.getMessage().contains("AUTH_CANARY"));}
        try(var db=SQLiteDatabase.openDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.execSQL("UPDATE shortcuts_table_video SET path='smb://user:AUTH_CANARY@nas/share' WHERE _id=1");}try{PortableIndexedSources.export(app,before());fail();}catch(JSONException expected){assertFalse(expected.getMessage().contains("AUTH_CANARY"));}
    }
    @Test public void indexedReplacementNeverOpensOrChangesBrowserFavouritesOrFtpCredentialStores()throws Exception{
        Map<String,byte[]> files=new HashMap<>();for(String db:List.of("shortcuts2_db","ftp_shortcuts_db")){File file=app.getDatabasePath(db);Files.writeString(file.toPath(),"PRIVATE_OTHER_STORE_"+db);files.put(db,Files.readAllBytes(file.toPath()));}JSONObject before=before();var prepared=PortableIndexedSources.prepare(before,payload("smb://nas.example/PRIVATE_FOLDER",true),null);IndexedSourceStoreRecovery.recover(app,before,prepared.after,true);for(var file:files.entrySet())assertArrayEquals(file.getValue(),Files.readAllBytes(app.getDatabasePath(file.getKey()).toPath()));
    }
}
