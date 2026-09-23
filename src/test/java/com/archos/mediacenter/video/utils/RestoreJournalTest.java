package com.archos.mediacenter.video.utils;
import android.app.Application;
import android.content.*;
import androidx.preference.PreferenceManager;
import java.io.*;
import java.nio.file.Files;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class RestoreJournalTest {
    @Test public void interruptedSwapRollsBackFilesAndPreferencesBeforeReopening()throws Exception{exercise(false);}
    @Test public void committedSwapSurvivesInterruptedCleanup()throws Exception{exercise(true);}
    @Test public void recoveryWaitsForActiveRestoreGuard()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        java.util.concurrent.ExecutorService worker=java.util.concurrent.Executors.newSingleThreadExecutor();
        java.util.concurrent.CountDownLatch entered=new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.Future<?> recovery;
        try{
            try(RestoreJournal.Guard guard=RestoreJournal.acquire(context)){
                recovery=worker.submit(()->{entered.countDown();try{RestoreJournal.recover(context);}catch(Exception e){throw new RuntimeException(e);}});
                assertTrue(entered.await(2,java.util.concurrent.TimeUnit.SECONDS));assertFalse(recovery.isDone());
            }
            recovery.get(2,java.util.concurrent.TimeUnit.SECONDS);
        }finally{worker.shutdownNow();}
    }
    private void exercise(boolean commit)throws Exception{
        Context context=RuntimeEnvironment.getApplication();File dir=new File(context.getFilesDir(),"restore-probe");dir.mkdirs();
        File live=new File(dir,"db"),old=new File(dir,"db.old"),fresh=new File(dir,"db.new");
        Files.write(live.toPath(),new byte[]{1});Files.write(fresh.toPath(),new byte[]{2});
        File second=new File(dir,"credentials"),secondOld=new File(dir,"credentials.old"),secondFresh=new File(dir,"credentials.new");
        Files.write(second.toPath(),new byte[]{3});Files.write(secondFresh.toPath(),new byte[]{4});
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);prefs.edit().clear().putString("generation","old").commit();
        JSONArray files=new JSONArray().put(new JSONObject().put("live",live.getPath()).put("old",old.getPath()).put("fresh",fresh.getPath()).put("existed",true));
        files.put(new JSONObject().put("live",second.getPath()).put("old",secondOld.getPath()).put("fresh",secondFresh.getPath()).put("existed",true));
        JSONObject state=RestoreJournal.prepare(context,files,SettingsBackup.encode(prefs),new JSONObject());
        assertTrue(live.renameTo(old));assertTrue(fresh.renameTo(live));prefs.edit().putString("generation","new").putBoolean("newOnly",true).commit();
        if(commit){assertTrue(second.renameTo(secondOld));assertTrue(secondFresh.renameTo(second));RestoreJournal.commit(context,state);}
        // Simulate a new process using only persisted intent and files, with no Swap object.
        RestoreJournal.recover(context);RestoreJournal.recover(context);
        assertArrayEquals(new byte[]{(byte)(commit?2:1)},Files.readAllBytes(live.toPath()));
        assertArrayEquals(new byte[]{(byte)(commit?4:3)},Files.readAllBytes(second.toPath()));
        assertEquals(commit?"new":"old",prefs.getString("generation",null));assertEquals(commit,prefs.contains("newOnly"));assertFalse(old.exists());
    }
}
