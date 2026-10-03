package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DiagnosticsTest {
    @Test public void activityReturnRestoresItsOwnPageAndUnknownChildDoesNotInheritIt(){
        android.app.Activity library=Robolectric.buildActivity(android.app.Activity.class).setup().get(),child=Robolectric.buildActivity(android.app.Activity.class).setup().get();
        Diagnostics.libraryState("movies","list","SIZE:descending","Drama","2024","8",true);Diagnostics.pauseUi(library);
        Diagnostics.resumeUi(child);assertEquals("unknown",new Diagnostics.UiSnapshot().page);
        Diagnostics.uiState("playback","player","video","none","none",42);Diagnostics.pauseUi(child);Diagnostics.resumeUi(library);
        Diagnostics.UiSnapshot restored=new Diagnostics.UiSnapshot();assertEquals("movies",restored.page);assertEquals("list",restored.mode);assertEquals("SIZE:descending",restored.sort);assertTrue(restored.filterDetails.contains("2024"));
        Diagnostics.pauseUi(library);Diagnostics.resumeUi(child);assertEquals("playback",new Diagnostics.UiSnapshot().page);assertEquals(42,new Diagnostics.UiSnapshot().media);
        Diagnostics.uiState("unknown","none","unknown","unknown","none",0);library.finish();child.finish();
    }
    @Test public void detailsRebindReportsReasonAndPerViewCountWithoutTitle()throws Exception{
        android.app.Activity activity=Robolectric.buildActivity(android.app.Activity.class).setup().get();Diagnostics.setEnabled(activity,true);
        try{
            com.archos.mediacenter.video.leanback.details.PreviewMoviePage page=new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(activity,androidx.leanback.widget.ArrayObjectAdapter::new,a->{},()->{},uri->{});activity.setContentView(page);
            page.bindRemote(new org.json.JSONObject().put("title","private-fixture-title"),"movie",0);
            page.setSnapshot(new com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot());
            Object recorder=org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");java.lang.reflect.Method snapshot=recorder.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);
            String evidence=(String)snapshot.invoke(recorder,android.os.SystemClock.elapsedRealtime());assertFalse(evidence.contains("private-fixture-title"));
            int count=0;boolean indexed=false;for(String line:evidence.split("\n")){if(line.isEmpty())continue;org.json.JSONObject row=new org.json.JSONObject(line);
                if("ui_rebuild".equals(row.optString("event"))&&"details.information".equals(row.optString("surface"))){assertEquals(++count,row.getInt("rebuild_count"));assertFalse(row.getBoolean("adapter_recreated"));assertEquals(1,row.getInt("items"));indexed|="indexed_snapshot".equals(row.getString("reason"));}}
            assertTrue(count>=2);assertTrue(indexed);
        }finally{Diagnostics.setEnabled(activity,false);activity.finish();}
    }
    @Test public void nativeConfirmationTracksModalWithoutReplacingDismissOrDeleteCallbacks()throws Exception{
        android.app.Activity activity=Robolectric.buildActivity(android.app.Activity.class).setup().visible().get();
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("try_new_ui",true).commit();
        android.widget.Button opener=new android.widget.Button(activity);opener.setFocusableInTouchMode(true);activity.setContentView(opener);opener.requestFocus();Shadows.shadowOf(activity).setCurrentFocus(opener);
        Diagnostics.setEnabled(activity,true);int[] deleted={0},dismissed={0};
        android.app.Dialog dialog=com.archos.mediacenter.video.leanback.PreviewDialog.confirmDelete(activity,"Fixture","Private fixture text",()->deleted[0]++);
        try{
            dialog.setOnDismissListener(d->dismissed[0]++);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            String evidence=Diagnostics.incidentContext("operation_failed",new Diagnostics.UiSnapshot());assertEquals("native_dialog",new org.json.JSONObject(evidence).getString("modal_kinds"));assertFalse(evidence.contains("Private fixture text"));
            ((android.app.AlertDialog)dialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE).performClick();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertEquals(0,deleted[0]);assertEquals(1,dismissed[0]);assertTrue(opener.hasFocus());
            assertEquals(0,new org.json.JSONObject(Diagnostics.incidentContext("operation_failed",new Diagnostics.UiSnapshot())).getInt("modal_depth"));
        }finally{dialog.dismiss();Diagnostics.setEnabled(activity,false);activity.finish();}
    }
    @Test public void detailsReaderRetainsSemanticOpenerAndModalContext()throws Exception{
        android.app.Activity activity=Robolectric.buildActivity(android.app.Activity.class).setup().get();
        com.archos.mediacenter.video.leanback.details.PreviewMoviePage page=new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(activity,androidx.leanback.widget.ArrayObjectAdapter::new,a->{},()->{},uri->{});
        activity.setContentView(page);android.view.View opener=page.findViewWithTag("action:More");assertNotNull(opener);opener.requestFocus();
        assertEquals("semantic:details.action.more",opener.getTag(com.archos.mediacenter.video.R.id.preview_diagnostic_semantic));
        Diagnostics.setEnabled(activity,true);android.app.Dialog dialog=null;
        try{
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(page,"moreInfo");dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertNotNull(dialog);
            assertEquals("reader",new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed",new Diagnostics.UiSnapshot())).getString("modal_kinds"));
            dialog.dismiss();org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertTrue(opener.hasFocus());
        }finally{if(dialog!=null)dialog.dismiss();Diagnostics.setEnabled(activity,false);activity.finish();}
    }
    @Test public void trailerModalIsCapturedAndDismissalRestoresItsOpener()throws Exception{
        android.app.Activity activity=Robolectric.buildActivity(android.app.Activity.class).setup().get();Diagnostics.setEnabled(activity,true);
        android.widget.Button opener=new android.widget.Button(activity);opener.setFocusableInTouchMode(true);activity.setContentView(opener);opener.requestFocus();
        android.app.Dialog dialog=null;
        try{
            com.archos.mediacenter.video.leanback.details.PreviewTrailer.show(activity,new com.archos.mediascraper.ScraperTrailer(com.archos.mediascraper.ScraperTrailer.Type.SHOW_TRAILER,"Trailer","abcdefghijk","YouTube",""));
            dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertNotNull(dialog);
            assertEquals("trailer",new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed",new Diagnostics.UiSnapshot())).getString("modal_kinds"));
            dialog.dismiss();org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();assertTrue(opener.hasFocus());
        }finally{if(dialog!=null)dialog.dismiss();Diagnostics.setEnabled(activity,false);activity.finish();}
    }
    @Test public void nestedDialogSnapshotRetainsKindsAfterDismissal()throws Exception{
        android.app.Activity activity=Robolectric.buildActivity(android.app.Activity.class).setup().get();
        Diagnostics.setEnabled(activity,true);
        android.app.Dialog parent=com.archos.mediacenter.video.leanback.PreviewDialog.create(activity,"reader"),child=com.archos.mediacenter.video.leanback.PreviewDialog.create(activity,"choice");
        try{
            parent.show();child.show();Diagnostics.UiSnapshot captured=new Diagnostics.UiSnapshot();child.dismiss();parent.dismiss();
            org.json.JSONObject row=new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed",captured));
            assertEquals(2,row.getInt("modal_depth"));assertEquals("reader,choice",row.getString("modal_kinds"));
            row=new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed",new Diagnostics.UiSnapshot()));assertEquals(0,row.getInt("modal_depth"));assertEquals("none",row.getString("modal_kinds"));
        }finally{child.dismiss();parent.dismiss();Diagnostics.setEnabled(activity,false);activity.finish();}
    }
    @Test public void librarySnapshotRetainsSafeFilterValuesAndExactSort()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        try{
            Diagnostics.libraryState("movies","list","SIZE:descending","Drama|private-filename.mkv","2024|2025|secret","8|9|https://private.example",true);
            Diagnostics.UiSnapshot captured=new Diagnostics.UiSnapshot();
            Diagnostics.uiState("settings","playback","workspace","none","none",0);
            String text=Diagnostics.incidentContext("artwork_failed",captured);org.json.JSONObject row=new org.json.JSONObject(text);
            assertEquals("SIZE:descending",row.getString("sort"));assertFalse(text.contains("private"));assertFalse(text.contains("secret"));assertFalse(text.contains("Drama"));
            org.json.JSONObject filters=new org.json.JSONObject(row.getString("filter_state"));
            assertEquals(2,filters.getInt("genre_count"));assertEquals(64,filters.getString("genre_selection_id").length());
            assertEquals("[2024,2025]",filters.getJSONArray("years").toString());assertEquals("[8,9]",filters.getJSONArray("provider_ids").toString());assertTrue(filters.getBoolean("unmatched"));
            assertEquals("{}",new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed",new Diagnostics.UiSnapshot())).getString("filter_state"));
        }finally{Diagnostics.uiState("unknown","none","unknown","unknown","none",0);Diagnostics.setEnabled(c,false);}
    }
    @Test public void delayedIncidentKeepsFailureTimeState()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        try{
            Diagnostics.uiState("movies","library","list","2","genre,year",42);Diagnostics.modalDepth(2);
            Diagnostics.UiSnapshot captured=new Diagnostics.UiSnapshot();
            Diagnostics.uiState("settings","playback","workspace","none","none",0);Diagnostics.modalDepth(0);
            org.json.JSONObject value=new org.json.JSONObject(Diagnostics.incidentContext("artwork_failed",captured));
            assertEquals("movies",value.getString("page"));assertEquals("list",value.getString("view_mode"));assertEquals(42,value.getLong("media_id"));assertEquals(2,value.getInt("modal_depth"));assertEquals(captured.utc,value.getLong("failure_utc_ms"));
        }finally{Diagnostics.uiState("unknown","none","unknown","unknown","none",0);Diagnostics.modalDepth(0);Diagnostics.setEnabled(c,false);}
    }
    @Test public void delayedIncidentDoesNotAttachToALaterPlaybackSession()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        try{
            Diagnostics.beginPlayback(android.net.Uri.parse("file:///private/first.mkv"));
            Diagnostics.UiSnapshot captured=new Diagnostics.UiSnapshot();
            Diagnostics.endPlayback("user_back");Diagnostics.beginPlayback(android.net.Uri.parse("file:///private/second.mkv"));
            assertNotEquals(captured.playbackSession,new Diagnostics.UiSnapshot().playbackSession);
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(40));
            String evidence=Diagnostics.incidentContext("playback_failed",captured);org.json.JSONObject value=new org.json.JSONObject(evidence);
            assertEquals(captured.playbackSession,value.getString("session"));assertEquals(captured.foregroundSession,value.getString("app_session"));assertEquals(captured.operation,value.getString("last_operation"));
            assertEquals(captured.utc,value.getLong("failure_utc_ms"));assertTrue(value.getLong("resource_sample_delay_ms")>=40);assertTrue(value.has("resource_sample_utc_ms"));assertFalse(evidence.contains("private"));
        }finally{Diagnostics.endPlayback("fixture_complete");Diagnostics.setEnabled(c,false);}
    }
    @Test public void incidentSnapshotContainsStructuralUiStateWithoutRawPaths()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        try{
            Diagnostics.uiState("movies","library","list","2","genre,year",42);Diagnostics.modalDepth(2);
            java.lang.reflect.Method snapshot=Diagnostics.class.getDeclaredMethod("incidentContext",String.class);snapshot.setAccessible(true);
            org.json.JSONObject value=new org.json.JSONObject((String)snapshot.invoke(null,"artwork_failed"));
            assertEquals("movies",value.getString("page"));assertEquals("list",value.getString("view_mode"));assertEquals("genre,year",value.getString("active_filters"));assertEquals(42,value.getLong("media_id"));assertEquals(2,value.getInt("modal_depth"));
            Diagnostics.uiState("https://private.example/token=secret","/storage/private.mkv","grid","none","none",-1);Diagnostics.modalDepth(-1);
            String safe=(String)snapshot.invoke(null,"artwork_failed");assertFalse(safe.contains("private"));assertFalse(safe.contains("secret"));
            value=new org.json.JSONObject(safe);assertEquals(0,value.getLong("media_id"));assertEquals(0,value.getInt("modal_depth"));
        }finally{Diagnostics.uiState("unknown","none","unknown","unknown","none",0);Diagnostics.modalDepth(0);Diagnostics.setEnabled(c,false);}
    }
    @Test public void repeatedFailuresKeepRawEventsAndAddCorrelatedCumulativeSummaries()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        try{
            java.lang.reflect.Method freeze=Diagnostics.class.getDeclaredMethod("freeze",String.class);freeze.setAccessible(true);
            freeze.invoke(null,"manual_problem_marker");freeze.invoke(null,"artwork_failed");freeze.invoke(null,"artwork_failed");freeze.invoke(null,"artwork_failed");
            java.util.List<org.json.JSONObject> records=DiagnosticArchive.records(new java.io.File(c.getFilesDir(),"supernova-diagnostics"));
            int repeats=0;boolean summary=false;String incident=null;
            for(org.json.JSONObject row:records){
                if("incident_repeated".equals(row.optString("event"))&&"artwork_failed".equals(row.optString("reason"))){repeats++;if(incident==null)incident=row.getString("incident_id");assertEquals(incident,row.getString("incident_id"));}
                if("incident_burst_summary".equals(row.optString("event"))&&row.optLong("occurrences")==2)summary=true;
            }
            assertEquals(3,repeats);assertTrue(summary);
        }finally{Diagnostics.setEnabled(c,false);}
    }
    @Test public void generatedFocusControlsAreDistinctWithoutText()throws Exception{
        Application c=RuntimeEnvironment.getApplication();android.widget.LinearLayout parent=new android.widget.LinearLayout(c);android.widget.TextView a=new android.widget.TextView(c),b=new android.widget.TextView(c);a.setText("private title");b.setText("private query");parent.addView(a);parent.addView(b);
        java.lang.reflect.Method id=Diagnostics.class.getDeclaredMethod("viewId",android.view.View.class);id.setAccessible(true);String first=(String)id.invoke(null,a),second=(String)id.invoke(null,b);assertNotEquals(first,second);assertFalse(first.contains("private"));assertFalse(second.contains("private"));
    }
    @Test public void rotatedFilesStayBoundedAndSessionsRemainCorrelated()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        java.lang.reflect.Method write=Diagnostics.class.getDeclaredMethod("write",String.class,String.class);write.setAccessible(true);
        String payload=new String(new char[4000]).replace('\0','x');
        for(int i=0;i<500;i++)write.invoke(null,"{\"session\":\"fixture-session\",\"event\":\"bounded_test\",\"detail\":\""+payload+"\"}\n","fixture-session");
        java.io.File[] files=new java.io.File(c.getFilesDir(),"supernova-diagnostics").listFiles();assertNotNull(files);
        // Protected daily/incident streams have a separate budget and may already exist.
        long routine=java.util.Arrays.stream(files).filter(file->file.getName().matches("(?:events|playback)\\.jsonl(?:\\.[1-4])?")).count();assertTrue(routine<=7);
        for(java.io.File file:files)assertTrue(file.length()<=Diagnostics.LIMIT);
        java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();Diagnostics.export(c,bytes);boolean playback=false;
        try(java.util.zip.ZipInputStream zip=new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))){java.util.zip.ZipEntry entry;while((entry=zip.getNextEntry())!=null){if(entry.getName().equals("playback.jsonl")){java.io.ByteArrayOutputStream content=new java.io.ByteArrayOutputStream();byte[] buffer=new byte[8192];int count;while((count=zip.read(buffer))!=-1)content.write(buffer,0,count);assertTrue(content.toString("UTF-8").contains("fixture-session"));playback=true;}}}
        assertTrue(playback);Diagnostics.setEnabled(c,false);
    }
    @Test public void locationsAndInlineSecretsAreRemoved(){String value=Diagnostics.safe("https://user:password@example/private?token=secret /storage/private.mkv token=abc Bearer xyz");assertFalse(value.contains("example"));assertFalse(value.contains("private"));assertFalse(value.contains("abc"));assertFalse(value.contains("xyz"));}
    @Test public void exceptionMessagesNeverEnterReport(){RuntimeException error=new RuntimeException("password=extremely-private");String trace=Diagnostics.trace(error);assertTrue(trace.contains("RuntimeException"));assertFalse(trace.contains("extremely-private"));}
    @Test public void offByDefaultAndExportWorksWhileOff()throws Exception{Application c=RuntimeEnvironment.getApplication();assertFalse(androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean(Diagnostics.KEY,false));Diagnostics.setEnabled(c,false);java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();Diagnostics.export(c,bytes);assertTrue(bytes.size()>0);java.util.Set<String> names=new java.util.HashSet<>();try(java.util.zip.ZipInputStream zip=new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))){java.util.zip.ZipEntry entry;while((entry=zip.getNextEntry())!=null)names.add(entry.getName());}assertTrue(names.contains("build-device.json"));assertTrue(names.contains("decoders.txt"));assertTrue(names.contains("README.txt"));}
}
