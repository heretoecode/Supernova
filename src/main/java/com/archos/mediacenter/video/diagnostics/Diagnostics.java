package com.archos.mediacenter.video.diagnostics;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import androidx.preference.PreferenceManager;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

/** Opt-in structured instrumentation. Never pass user text, headers or credentials here. */
public final class Diagnostics {
    public static final String KEY="supernova_diagnostic_logging";
    static final int LIMIT=256*1024, ROTATIONS=4;
    private static final Object LOCK=new Object();
    private static final ThreadPoolExecutor WORK=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(256),r->{Thread t=new Thread(r,"SupernovaDiagnostics");t.setDaemon(true);return t;},
            new ThreadPoolExecutor.DiscardPolicy());
    private static volatile Context context;
    private static volatile boolean enabled;
    private static volatile String playback="";
    private static final String PROCESS=UUID.randomUUID().toString();
    private static long focusAt;
    private static volatile String lastOperation="startup";
    private static SharedPreferences sessionState;
    private static final Map<Activity,android.view.ViewTreeObserver.OnGlobalFocusChangeListener> FOCUS=new WeakHashMap<>();

    public static void install(Application app){
        context=app.getApplicationContext();
        enabled=PreferenceManager.getDefaultSharedPreferences(app).getBoolean(KEY,false);
        sessionState=app.getSharedPreferences("supernova_diagnostic_session",Context.MODE_PRIVATE);
        if(enabled){
            String previousProcess=sessionState.getString("process","");
            if(!previousProcess.isEmpty()&&!sessionState.getBoolean("clean",true))event("PREVIOUS_SESSION_UNCLEAN_EXIT","previous_process",previousProcess,"previous_pid",sessionState.getInt("pid",0),"previous_operation",sessionState.getString("last_operation","unknown"));
            sessionState.edit().putString("process",PROCESS).putInt("pid",android.os.Process.myPid()).putBoolean("clean",false).apply();
        }
        Thread.UncaughtExceptionHandler previous=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread,error)->{
            try{if(enabled)write(record("uncaught_exception","thread",thread.getName(),"trace",trace(error)),playback);}
            finally{if(previous!=null)previous.uncaughtException(thread,error);
                else {android.os.Process.killProcess(android.os.Process.myPid());System.exit(10);}}
        });
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
            private void life(Activity a,String state){event("lifecycle","screen",a.getClass().getSimpleName(),"state",state);}
            public void onActivityCreated(Activity a,Bundle b){life(a,"created");}
            public void onActivityStarted(Activity a){life(a,"started");}
            public void onActivityResumed(Activity a){
                life(a,"resumed");if(enabled&&sessionState!=null)sessionState.edit().putBoolean("clean",false).apply();
                android.view.ViewTreeObserver.OnGlobalFocusChangeListener listener=(old,next)->{
                    long now=android.os.SystemClock.elapsedRealtime();
                    if(!enabled||now-focusAt<100)return;focusAt=now;
                    event("focus","screen",a.getClass().getSimpleName(),"from",viewId(old),"to",viewId(next));
                };
                FOCUS.put(a,listener);a.getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(listener);
            }
            public void onActivityPaused(Activity a){life(a,"paused");android.view.ViewTreeObserver.OnGlobalFocusChangeListener l=FOCUS.remove(a);if(l!=null)a.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalFocusChangeListener(l);}
            public void onActivityStopped(Activity a){life(a,"stopped");}
            public void onActivitySaveInstanceState(Activity a,Bundle b){}
            public void onActivityDestroyed(Activity a){life(a,"destroyed");if(enabled&&a.isTaskRoot()&&a.isFinishing()&&sessionState!=null){event("session_clean_shutdown");sessionState.edit().putBoolean("clean",true).apply();}}
        });
        event("startup","version",com.archos.mediacenter.video.BuildConfig.VERSION_NAME,"sdk",android.os.Build.VERSION.SDK_INT);
    }
    private static String viewId(View view){
        if(view==null)return "none";
        String id="generated";
        if(view.getId()!=View.NO_ID)try{id=view.getResources().getResourceEntryName(view.getId());}catch(android.content.res.Resources.NotFoundException ignored){}
        // Programmatic TV controls often have no resource ID. Structural child
        // positions distinguish them without recording titles or user-entered text.
        StringBuilder position=new StringBuilder();View cursor=view;
        for(int depth=0;depth<4&&cursor.getParent() instanceof android.view.ViewGroup;depth++){
            android.view.ViewGroup parent=(android.view.ViewGroup)cursor.getParent();
            position.insert(0,"/"+parent.indexOfChild(cursor));cursor=parent;
        }
        return view.getClass().getSimpleName()+":"+id+position;
    }
    public static boolean enabled(){return enabled;}
    public static void setEnabled(Context c,boolean value){
        context=c.getApplicationContext();
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(KEY,value).apply();
        enabled=value;if(sessionState!=null)sessionState.edit().putString("process",PROCESS).putInt("pid",android.os.Process.myPid()).putBoolean("clean",!value).apply();if(value)event("logging_enabled","default","off");
    }
    public static void beginPlayback(android.net.Uri source){
        playback=UUID.randomUUID().toString();event("playback_begin","source",sourceType(source));
    }
    public static void endPlayback(String reason){event("playback_end","reason",reason);playback="";}
    public static String sourceType(android.net.Uri uri){
        if(uri==null)return "unknown";String scheme=uri.getScheme();
        if(scheme==null||"file".equalsIgnoreCase(scheme))return "local";
        String lower=scheme.toLowerCase(Locale.ROOT);
        return Arrays.asList("content","smb","webdav","webdavs","dav","davs","http","https","ftp","ftps","upnp").contains(lower)?lower:"other";
    }
    public static void event(String event,Object... fields){
        if(!enabled||context==null)return;
        String line=record(event,fields),session=playback;
        if(!event.equals("focus")&&!event.startsWith("checkpoint")&&!event.equals("scanner_state")){lastOperation=safe(event);if(sessionState!=null)sessionState.edit().putString("last_operation",lastOperation).apply();}
        WORK.execute(()->{if(enabled)write(line,session);});
    }
    public static void error(String event,Throwable error){event(event,"trace",trace(error));}
    /** Stack locations are useful; exception messages may contain secrets and are NEVER retained. */
    static String trace(Throwable error){
        StringBuilder out=new StringBuilder();Set<Throwable> seen=Collections.newSetFromMap(new IdentityHashMap<>());
        for(int causes=0;error!=null&&causes<4&&seen.add(error);causes++,error=error.getCause()){
            out.append(error.getClass().getName()).append('\n');StackTraceElement[] frames=error.getStackTrace();
            for(int i=0;i<Math.min(frames.length,20);i++)out.append(frames[i].getClassName()).append('.').append(frames[i].getMethodName()).append(':').append(frames[i].getLineNumber()).append('\n');
        }return out.toString();
    }
    static String safe(String value){
        if(value==null)return "";
        // Remove complete locations, not merely their user-info/query components.
        String clean=value.replaceAll("(?i)[a-z][a-z0-9+.-]*://[^\\s]+","[location]")
            .replaceAll("(?:^|\\s)/[^\\s]+"," [path]")
            .replaceAll("(?i)(bearer\\s+|(?:password|passwd|token|secret|api[_-]?key|authorization|cookie)\\s*[:=]\\s*)[^\\s,;]+","[redacted]");
        return clean.length()>4096?clean.substring(0,4096):clean;
    }
    private static String record(String event,Object... fields){
        try{
            JSONObject json=new JSONObject();json.put("utc_ms",System.currentTimeMillis());json.put("elapsed_ms",android.os.SystemClock.elapsedRealtime());
            json.put("pid",android.os.Process.myPid());json.put("last_operation",lastOperation);json.put("process",PROCESS);json.put("session",playback);json.put("event",safe(event));
            for(int i=0;i+1<fields.length;i+=2){String key=String.valueOf(fields[i]);
                if(!key.matches("[a-z_]{1,48}")||key.matches(".*(password|credential|token|secret|header|cookie|path|url|uri|api_key).*"))continue;
                Object value=fields[i+1];json.put(key,value instanceof Number||value instanceof Boolean?value:safe(String.valueOf(value)));
            }return json.toString()+"\n";
        }catch(Exception ignored){return "";}
    }
    private static File directory(){return new File(context.getFilesDir(),"supernova-diagnostics");}
    private static void rotate(File dir,String name,int copies,int incoming)throws IOException{
        File active=new File(dir,name);if(active.length()+incoming<=LIMIT)return;
        File oldest=new File(dir,name+"."+copies);if(oldest.exists()&&!oldest.delete())throw new IOException("rotation unavailable");
        for(int i=copies-1;i>=0;i--){File from=new File(dir,name+(i==0?"":"."+i));if(from.exists()&&!from.renameTo(new File(dir,name+"."+(i+1))))throw new IOException("rotation unavailable");}
    }
    private static void append(File dir,String name,int copies,byte[] bytes)throws IOException{
        rotate(dir,name,copies,bytes.length);try(FileOutputStream stream=new FileOutputStream(new File(dir,name),true)){stream.write(bytes);}
    }
    private static void write(String line,String session){
        if(context==null||line.isEmpty())return;
        synchronized(LOCK){try{File dir=directory();if(!dir.isDirectory()&&!dir.mkdirs())return;
            byte[] bytes=line.getBytes(StandardCharsets.UTF_8);append(dir,"events.jsonl",ROTATIONS,bytes);
            if(!session.isEmpty())append(dir,"playback.jsonl",1,bytes);
        }catch(Exception ignored){/* Instrumentation must not affect playback or storage operations. */}}
    }
    /** Called on a worker. Only our bounded, sanitised files and allow-listed device fields are exported. */
    public static void export(Context c,OutputStream destination)throws IOException{
        context=c.getApplicationContext();
        synchronized(LOCK){try(ZipOutputStream zip=new ZipOutputStream(destination)){
            JSONObject info=new JSONObject();try{
                info.put("package",c.getPackageName());info.put("version_name",com.archos.mediacenter.video.BuildConfig.VERSION_NAME);
                info.put("version_code",com.archos.mediacenter.video.BuildConfig.VERSION_CODE);
                info.put("source_commit",com.archos.mediacenter.video.BuildConfig.PREVIEW_GIT_SHA);
                info.put("build_utc",com.archos.mediacenter.video.BuildConfig.PREVIEW_BUILD_UTC);
                info.put("manufacturer",safe(android.os.Build.MANUFACTURER));info.put("model",safe(android.os.Build.MODEL));
                info.put("android_release",safe(android.os.Build.VERSION.RELEASE));info.put("sdk",android.os.Build.VERSION.SDK_INT);
                info.put("abis",Arrays.toString(android.os.Build.SUPPORTED_ABIS));info.put("logging_enabled",enabled);
                info.put("utc_ms",System.currentTimeMillis());
            }catch(org.json.JSONException failure){throw new IOException(failure);}
            entry(zip,"build-device.json",info.toString(2).getBytes(StandardCharsets.UTF_8));
            StringBuilder decoders=new StringBuilder();
            try{for(android.media.MediaCodecInfo codec:new android.media.MediaCodecList(android.media.MediaCodecList.ALL_CODECS).getCodecInfos())if(!codec.isEncoder())decoders.append(safe(codec.getName())).append(" ").append(Arrays.toString(codec.getSupportedTypes())).append('\n');}catch(RuntimeException failure){decoders.append("Capability enumeration unavailable: ").append(failure.getClass().getSimpleName());}
            entry(zip,"decoders.txt",decoders.toString().getBytes(StandardCharsets.UTF_8));
            entry(zip,"README.txt",("Supernova opt-in diagnostic report\nStructured UTC and monotonic timestamps; process and playback session IDs.\nLogging is off by default. Disabling stops new events, not export of existing bounded logs.\nException messages, credentials, raw media paths, headers and preference dumps are excluded.\nUp to five 256 KiB event files and two 256 KiB playback files; a bounded queue may drop events under load.\nNo native decoder A/V clock precision or physical Shield playback success is implied.\n").getBytes(StandardCharsets.UTF_8));
            entry(zip,"summary.txt",summary().getBytes(StandardCharsets.UTF_8));
            File dir=directory();for(String name:new String[]{"events.jsonl","events.jsonl.1","events.jsonl.2","events.jsonl.3","events.jsonl.4","playback.jsonl","playback.jsonl.1"}){
                File file=new File(dir,name);if(!file.isFile())continue;zip.putNextEntry(new ZipEntry(name));
                try(InputStream in=new FileInputStream(file)){byte[] buffer=new byte[8192];int read,total=0;while(total<LIMIT&&(read=in.read(buffer,0,Math.min(buffer.length,LIMIT-total)))!=-1){zip.write(buffer,0,read);total+=read;}}zip.closeEntry();
            }
        }catch(org.json.JSONException failure){throw new IOException(failure);}}
    }
    private static String summary(){
        Map<String,Integer> counts=new TreeMap<>();Set<String> sessions=new HashSet<>();ArrayDeque<String> timeline=new ArrayDeque<>();
        for(int i=ROTATIONS;i>=0;i--){File file=new File(directory(),"events.jsonl"+(i==0?"":"."+i));if(!file.isFile())continue;
            try(BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8))){String line;while((line=reader.readLine())!=null){
                try{JSONObject record=new JSONObject(line);String event=record.optString("event");counts.put(event,counts.getOrDefault(event,0)+1);String id=record.optString("session");if(!id.isEmpty())sessions.add(id);
                    if(event.contains("error")||event.contains("exception")||event.contains("UNCLEAN")||event.startsWith("playback_")||event.equals("seek_complete")){timeline.add(record.optLong("utc_ms")+" "+safe(event)+" session="+id+" outcome="+safe(record.optString("reason")));while(timeline.size()>80)timeline.removeFirst();}
                }catch(org.json.JSONException ignored){}
            }}catch(IOException ignored){}
        }
        return "Supernova diagnostic summary\nBuild: "+com.archos.mediacenter.video.BuildConfig.VERSION_NAME+"\nSource: "+com.archos.mediacenter.video.BuildConfig.PREVIEW_GIT_SHA+"\nPlayback sessions retained: "+sessions.size()+"\nEvent counts: "+counts+"\nRecent significant events (UTC milliseconds):\n"+android.text.TextUtils.join("\n",timeline)+"\nUnclean exit means no clean marker; it does not prove a crash. Native A/V offset is not exposed.\n";
    }
    private static void entry(ZipOutputStream zip,String name,byte[] bytes)throws IOException{zip.putNextEntry(new ZipEntry(name));zip.write(bytes);zip.closeEntry();}
    private Diagnostics(){}
}
