package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import com.archos.mediaprovider.video.*;
import com.archos.mediacenter.utils.trakt.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PlaylistSyncPolicyTest {
    private Context app;private VideoProvider provider;
    @Before public void setup(){
        app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");
        // Exercise real provider query code; avoid unrelated receiver/network lifecycle startup.
        provider=spy(new VideoProvider());doReturn(true).when(provider).onCreate();
        android.content.pm.ProviderInfo info=new android.content.pm.ProviderInfo();info.authority=VideoStore.AUTHORITY;provider.attachInfo(app,info);
        ReflectionHelpers.setField(provider,"mDbHolder",com.archos.mediaprovider.VideoDb.getHolder(app));
        ReflectionHelpers.setField(provider,"mCr",app.getContentResolver());
        ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY,provider);
    }
    @After public void closeProviderStore(){com.archos.mediaprovider.VideoDb.getHolder(app).close();}
    @Test public void membershipUriReturnsAllAndOnlyRowsOwnedBySelectedList()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(77,'Selected',NULL,4),(88,'Other',NULL,4)");
            db.execSQL("INSERT INTO video_list_table(_id,list_id,m_online_id,e_online_id,sync_status) VALUES(100,77,123,-1,1),(101,77,-1,456,1),(102,88,789,-1,1)");
        }
        try(var rows=app.getContentResolver().query(VideoStore.List.getListUri(77),new String[]{"_id","list_id","m_online_id","e_online_id"},null,null,"_id ASC")){
            assertNotNull(rows);assertEquals(2,rows.getCount());assertTrue(rows.moveToFirst());assertEquals(100,rows.getInt(0));assertEquals(77,rows.getInt(1));assertEquals(123,rows.getInt(2));assertTrue(rows.moveToNext());assertEquals(101,rows.getInt(0));assertEquals(456,rows.getInt(3));
        }
    }
    @Test public void realTraktSyncCannotUploadDeleteOrQueryMembersOfLocalOnlyDefinitions()throws Exception{
        assertEquals(3,VideoStore.List.SyncStatus.STATUS_RENAMED);assertEquals(4,VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY);
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(77,'Restored local',NULL,4),(88,'Defensive existing association',99887766,4)");
            db.execSQL("INSERT INTO video_list_table(_id,list_id,m_online_id,e_online_id,sync_status) VALUES(100,77,123,-1,1),(101,88,-1,456,1)");
        }
        Trakt transport=mock(Trakt.class);when(transport.getLists(0)).thenReturn(new Trakt.Result(Trakt.Status.SUCCESS,Collections.emptyList(),Trakt.Result.ObjectType.LIST));
        TraktService service=Robolectric.buildService(TraktService.class).get();ReflectionHelpers.setField(service,"mTrakt",transport);ReflectionHelpers.callInstanceMethod(service,"syncLists");verify(transport).getLists(0);verifyNoMoreInteractions(transport);
        try(VideoOpenHelper helper=new VideoOpenHelper(app);var definitions=helper.getReadableDatabase().rawQuery("SELECT _id,trakt_id,sync_status FROM list_table ORDER BY _id",null)){
            assertEquals(2,definitions.getCount());assertTrue(definitions.moveToFirst());assertEquals(77,definitions.getInt(0));assertTrue(definitions.isNull(1));assertEquals(4,definitions.getInt(2));assertTrue(definitions.moveToNext());assertEquals(99887766,definitions.getInt(1));assertEquals(4,definitions.getInt(2));
            try(var members=helper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM video_list_table",null)){assertTrue(members.moveToFirst());assertEquals(2,members.getInt(0));}
        }
    }
}
