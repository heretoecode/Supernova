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
    public static final String LEVEL="supernova_diagnostic_level";
    private static final java.util.concurrent.atomic.AtomicLong DROPPED=new java.util.concurrent.atomic.AtomicLong(),WRITE_ERRORS=new java.util.concurrent.atomic.AtomicLong(),SEQUENCE=new java.util.concurrent.atomic.AtomicLong();
    private static final DiagnosticFlightRecorder FLIGHT=new DiagnosticFlightRecorder(252*1024,180000);
    private static volatile boolean qa;private static volatile long freezeUntil,mainAck=android.os.SystemClock.elapsedRealtime();private static volatile int foreground;private static final Set<String> COVERAGE=java.util.Collections.synchronizedSet(new TreeSet<>());
    private static final ScheduledExecutorService HEARTBEAT=Executors.newSingleThreadScheduledExecutor(r->{Thread t=new Thread(r,"SupernovaHeartbeat");t.setDaemon(true);return t;});
    private static boolean heartbeatInstalled;
    public static final String KEY="supernova_diagnostic_logging";
    static final int LIMIT=256*1024, ROTATIONS=4;
    private static final Object LOCK=new Object();
    private static final ThreadPoolExecutor WORK=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(256),r->{Thread t=new Thread(r,"SupernovaDiagnostics");t.setDaemon(true);return t;},
            (task,executor)->DROPPED.incrementAndGet());
    // Routine focus/artwork traffic cannot occupy the important-event queue.
    private static final ThreadPoolExecutor IMPORTANT=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(512),r->{Thread t=new Thread(r,"SupernovaImportantDiagnostics");t.setDaemon(true);return t;},
            (task,executor)->DROPPED.incrementAndGet());
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
        qa="qa".equals(PreferenceManager.getDefaultSharedPreferences(app).getString(LEVEL,"normal"));
        if(!heartbeatInstalled){heartbeatInstalled=true;HEARTBEAT.scheduleWithFixedDelay(Diagnostics::heartbeat,30,30,TimeUnit.SECONDS);}
        PreferenceManager.getDefaultSharedPreferences(app).registerOnSharedPreferenceChangeListener(CONFIG);
        sessionState=app.getSharedPreferences("supernova_diagnostic_session",Context.MODE_PRIVATE);
        if(enabled){
            String previousProcess=sessionState.getString("process","");
            if(!previousProcess.isEmpty()&&!sessionState.getBoolean("clean",true))event("PREVIOUS_SESSION_UNCLEAN_EXIT","previous_process",previousProcess,"previous_pid",sessionState.getInt("pid",0),"previous_operation",sessionState.getString("last_operation","unknown"));
            sessionState.edit().putString("process",PROCESS).putInt("pid",android.os.Process.myPid()).putBoolean("clean",false).apply();
        }
        Thread.UncaughtExceptionHandler previous=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread,error)->{
            try{if(enabled){String line=record("uncaught_exception","thread",thread.getName(),"trace",trace(error));FLIGHT.add(android.os.SystemClock.elapsedRealtime(),line);write(line,playback);freeze("uncaught_exception");}}
            finally{if(previous!=null)previous.uncaughtException(thread,error);
                else {android.os.Process.killProcess(android.os.Process.myPid());System.exit(10);}}
        });
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
            private void life(Activity a,String state){event("lifecycle","screen",a.getClass().getSimpleName(),"state",state);}
            public void onActivityCreated(Activity a,Bundle b){life(a,"created");}
            public void onActivityStarted(Activity a){foreground++;life(a,"started");}
            public void onActivityResumed(Activity a){mainAck=android.os.SystemClock.elapsedRealtime();
                life(a,"resumed");if(enabled&&sessionState!=null)sessionState.edit().putBoolean("clean",false).apply();
                android.view.ViewTreeObserver.OnGlobalFocusChangeListener listener=(old,next)->{
                    long now=android.os.SystemClock.elapsedRealtime();
                    if(!enabled||now-focusAt<100)return;focusAt=now;
                    event("focus","screen",a.getClass().getSimpleName(),"from",viewId(old),"to",viewId(next));
                };
                FOCUS.put(a,listener);a.getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(listener);
            }
            public void onActivityPaused(Activity a){life(a,"paused");android.view.ViewTreeObserver.OnGlobalFocusChangeListener l=FOCUS.remove(a);if(l!=null)a.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalFocusChangeListener(l);}
            public void onActivityStopped(Activity a){foreground=Math.max(0,foreground-1);life(a,"stopped");if(enabled&&foreground==0&&sessionState!=null){event("session_backgrounded");sessionState.edit().putBoolean("clean",true).apply();}}
            public void onActivitySaveInstanceState(Activity a,Bundle b){}
            public void onActivityDestroyed(Activity a){life(a,"destroyed");if(enabled&&a.isTaskRoot()&&a.isFinishing()&&sessionState!=null){event("session_clean_shutdown");sessionState.edit().putBoolean("clean",true).apply();}}
        });
        event("startup","version",com.archos.mediacenter.video.BuildConfig.VERSION_NAME,"sdk",android.os.Build.VERSION.SDK_INT);
    }
    private static String viewId(View view){
        if(view==null)return "none";
        Object semantic=view.getTag();
        if(semantic instanceof String&&((String)semantic).matches("semantic:[A-Za-z0-9 _:.-]{1,100}"))return (String)semantic;
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
    public static void navigation(View previous,View next,int key,boolean consumed){if(key>=android.view.KeyEvent.KEYCODE_DPAD_UP&&key<=android.view.KeyEvent.KEYCODE_DPAD_CENTER)event("focus_navigation","input",key,"from",viewId(previous),"to",viewId(next),"consumed",consumed,"edge_held",previous==next&&consumed);}
    public static void setEnabled(Context c,boolean value){
        context=c.getApplicationContext();
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(KEY,value).apply();
        enabled=value;mainAck=android.os.SystemClock.elapsedRealtime();if(!value)FLIGHT.clear();if(sessionState!=null)sessionState.edit().putString("process",PROCESS).putInt("pid",android.os.Process.myPid()).putBoolean("clean",!value).apply();if(value)event("logging_enabled","default","off");
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
        try{String line=record(event,fields),session=playback;long now=android.os.SystemClock.elapsedRealtime();FLIGHT.add(now,line);if(COVERAGE.size()<256)COVERAGE.add(event);
            boolean detail=event.startsWith("focus")||event.startsWith("artwork_")||event.equals("home_page_render");
            if(!detail&&!event.startsWith("checkpoint")&&!event.equals("scanner_state")){lastOperation=safe(event);if(sessionState!=null)sessionState.edit().putString("last_operation",lastOperation).apply();}
            boolean anomaly=event.contains("error")||event.contains("failed")||event.contains("exception")||event.equals("manual_problem_marker")||event.equals("main_thread_stall_suspected");
            if(important(event)||anomaly)IMPORTANT.execute(()->{if(!enabled)return;writeImportant(line);if(anomaly)freeze(event);});
            WORK.execute(()->{if(!enabled)return;if(qa||!detail)write(line,session);if(!anomaly&&android.os.SystemClock.elapsedRealtime()<freezeUntil)appendFlight(line);});
        }catch(RuntimeException ignored){DROPPED.incrementAndGet();}
    }
    static boolean important(String event){return event.contains("failed")||event.contains("error")||event.contains("exception")||event.contains("UNCLEAN")||event.equals("manual_problem_marker")||event.equals("main_thread_stall_suspected")||event.startsWith("session_")||event.equals("startup")||event.equals("playback_begin")||event.equals("playback_end")||event.equals("scan_started")||event.equals("scan_complete")||event.equals("scan_partial")||event.equals("incident_capture");}
    private static void writeImportant(String line){
        synchronized(LOCK){try{
            File dir=directory();if(!dir.isDirectory()&&!dir.mkdirs())return;
            java.text.SimpleDateFormat day=new java.text.SimpleDateFormat("yyyyMMdd",Locale.ROOT);day.setTimeZone(TimeZone.getTimeZone("UTC"));
            append(dir,"important-"+day.format(new Date())+".jsonl",3,line.getBytes(StandardCharsets.UTF_8));
            long oldest=System.currentTimeMillis()-7L*24*60*60*1000;
            for(File file:DiagnosticArchive.files(dir))if(file.getName().startsWith("important-")&&file.lastModified()<oldest&&!file.delete())WRITE_ERRORS.incrementAndGet();
        }catch(IOException failure){WRITE_ERRORS.incrementAndGet();}}
    }
    private static final SharedPreferences.OnSharedPreferenceChangeListener CONFIG=(prefs,key)->{if(LEVEL.equals(key))qa="qa".equals(prefs.getString(LEVEL,"normal"));if(key!=null&&Arrays.asList(LEVEL,"try_new_ui","remember_library_views","hide_watched","sort_ignore_articles","preview_accent41", "streaming_enabled").contains(key))event("configuration_changed","setting",key);};
    public static String operation(String kind){String id=PROCESS+":"+SEQUENCE.incrementAndGet();event("operation_begin","operation_id",id,"kind",kind);return id;}
    public static void finishOperation(String id,String kind,long started){event("operation_end","operation_id",id,"kind",kind,"latency_ms",android.os.SystemClock.elapsedRealtime()-started);}
    private static void heartbeat(){try{if(!enabled||context==null)return;long now=android.os.SystemClock.elapsedRealtime();if(foreground>0&&now-mainAck>65000)event("main_thread_stall_suspected","unresponsive_ms",now-mainAck);new android.os.Handler(android.os.Looper.getMainLooper()).post(()->mainAck=android.os.SystemClock.elapsedRealtime());Runtime runtime=Runtime.getRuntime();event("heartbeat","foreground",foreground,"heap_used",runtime.totalMemory()-runtime.freeMemory(),"heap_max",runtime.maxMemory(),"native_heap",android.os.Debug.getNativeHeapAllocatedSize(),"queue_depth",WORK.getQueue().size(),"dropped",DROPPED.get(),"write_errors",WRITE_ERRORS.get(),"flight_bytes",FLIGHT.bytes(),"level",qa?"QA_SOAK":"NORMAL");}catch(RuntimeException ignored){WRITE_ERRORS.incrementAndGet();}}
    private static void freeze(String reason){if(context==null)return;if(android.os.SystemClock.elapsedRealtime()<freezeUntil){appendFlight(record("incident_repeated","reason",reason));return;}writeImportant(record("incident_capture","reason",reason,"foreground",foreground,"heap_used",Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory(),"heap_max",Runtime.getRuntime().maxMemory(),"storage_free",context.getFilesDir().getUsableSpace(),"thread_count",Thread.activeCount()));String retained=FLIGHT.snapshot(android.os.SystemClock.elapsedRealtime());synchronized(LOCK){try{File dir=directory();if(!dir.isDirectory()&&!dir.mkdirs()){WRITE_ERRORS.incrementAndGet();return;}android.util.AtomicFile file=new android.util.AtomicFile(new File(dir,"flight.jsonl"));FileOutputStream out=null;try{out=file.startWrite();out.write(record("flight_snapshot","reason",reason,"completeness",retained.isEmpty()?"RECOVERY_DATA_MISSING":"PARTIAL","dropped",DROPPED.get(),"write_errors",WRITE_ERRORS.get()).getBytes(StandardCharsets.UTF_8));out.write(retained.getBytes(StandardCharsets.UTF_8));file.finishWrite(out);}catch(Exception error){if(out!=null)file.failWrite(out);WRITE_ERRORS.incrementAndGet();}freezeUntil=android.os.SystemClock.elapsedRealtime()+60000;}catch(RuntimeException error){WRITE_ERRORS.incrementAndGet();}}}
    private static void appendFlight(String line){synchronized(LOCK){try{append(directory(),"flight.jsonl",1,line.getBytes(StandardCharsets.UTF_8));}catch(IOException error){WRITE_ERRORS.incrementAndGet();}}}
    public static void reportProblem(Context c){
        if(!enabled){com.archos.mediacenter.video.leanback.PreviewDialog.read(c,"Diagnostic logging is off","Enable Diagnostic Logging in Advanced settings before recording a problem.");return;}
        String[] categories={"Playback","UI","Network","Other"};
        com.archos.mediacenter.video.leanback.PreviewDialog.choose(c,"Report a Problem",categories,-1,n->{
            String id=PROCESS.substring(0,8)+"-"+SEQUENCE.incrementAndGet();
            long time=System.currentTimeMillis();
            event("manual_problem_marker","category",categories[n],"marker_id",id,"reported_at",time);
            java.text.SimpleDateFormat format=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'",Locale.UK);format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String confirmation="Reference: "+id+"\nCategory: "+categories[n]+"\nTime: "+format.format(new Date(time));
            c.getSharedPreferences("supernova_diagnostic_session",Context.MODE_PRIVATE).edit().putString("latest_manual_reference",id).putString("latest_manual_category",categories[n]).putLong("latest_manual_time",time).apply();
            com.archos.mediacenter.video.leanback.PreviewDialog.read(c,"Problem recorded",confirmation+"\n\nUse this reference with your screenshot or video. Export Diagnostic Report to preserve the evidence.");
        });
    }
    public static void showDigest(Context c){WORK.execute(()->{String digest=summary();new android.os.Handler(android.os.Looper.getMainLooper()).post(()->{if(c instanceof Activity&&(((Activity)c).isFinishing()||((Activity)c).isDestroyed()))return;com.archos.mediacenter.video.leanback.PreviewDialog.read(c,"Issues Digest",digest);});});}
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
            json.put("sequence",SEQUENCE.incrementAndGet());json.put("pid",android.os.Process.myPid());json.put("last_operation",lastOperation);json.put("process",PROCESS);json.put("session",playback);json.put("event",safe(event));
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
        synchronized(LOCK){try{File dir=directory();if(!dir.isDirectory()&&!dir.mkdirs()){WRITE_ERRORS.incrementAndGet();return;}
            byte[] bytes=line.getBytes(StandardCharsets.UTF_8);append(dir,"events.jsonl",ROTATIONS,bytes);
            if(!session.isEmpty())append(dir,"playback.jsonl",1,bytes);
        }catch(Exception ignored){WRITE_ERRORS.incrementAndGet();/* Instrumentation must not affect playback or storage operations. */}}
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
            entry(zip,"README.txt",("Supernova opt-in diagnostic report\nStructured UTC and monotonic timestamps; process and playback session IDs.\nLogging is off by default. Disabling stops new events, not export of existing bounded logs.\nException messages, credentials, raw media paths, headers and preference dumps are excluded.\nRoutine streams are size-bounded. Important events use a separate queue and seven-day, daily size-bounded retention. Copies are deduplicated by process/sequence in the summaries.\nNo native decoder A/V clock precision or physical Shield playback success is implied.\n").getBytes(StandardCharsets.UTF_8));
            entry(zip,"summary.txt",summary().getBytes(StandardCharsets.UTF_8));
            entry(zip,"manifest.json",DiagnosticArchive.manifest(directory()).toString(2).getBytes(StandardCharsets.UTF_8));
            String coverage; synchronized(COVERAGE){coverage=COVERAGE.toString();}entry(zip,"coverage.txt",("Completeness: PARTIAL\nObserved event types: "+coverage+"\nNative decoder and every direct network transport are not fully observable. No app-quality score.\nDropped: "+DROPPED.get()+"; write errors: "+WRITE_ERRORS.get()+"; queued: "+WORK.getQueue().size()+"\n").getBytes(StandardCharsets.UTF_8));
            File dir=directory();for(File file:DiagnosticArchive.files(dir)){String name=file.getName();zip.putNextEntry(new ZipEntry(name));
                try(InputStream in=new FileInputStream(file)){byte[] buffer=new byte[8192];int read,total=0;while(total<LIMIT&&(read=in.read(buffer,0,Math.min(buffer.length,LIMIT-total)))!=-1){zip.write(buffer,0,read);total+=read;}}zip.closeEntry();
            }
        }catch(org.json.JSONException failure){throw new IOException(failure);}}
    }
    private static String summary(){
        Map<String,Integer> counts=new TreeMap<>();Set<String> sessions=new HashSet<>();ArrayDeque<String> timeline=new ArrayDeque<>();
        for(JSONObject record:DiagnosticArchive.records(directory())){
            String event=record.optString("event");counts.put(event,counts.getOrDefault(event,0)+1);
            String id=record.optString("session");if(!id.isEmpty())sessions.add(id);
            if(important(event)||event.equals("seek_complete")){
                timeline.add(record.optLong("utc_ms")+" "+safe(event)+" session="+id+" operation="+safe(record.optString("operation_id"))+" outcome="+safe(record.optString("reason"))+" reference="+safe(record.optString("marker_id")));
                while(timeline.size()>80)timeline.removeFirst();
            }
        }
        return "Supernova diagnostic summary\nCompleteness: PARTIAL (native/direct transport coverage is incomplete)\nLogger dropped: "+DROPPED.get()+"; write errors: "+WRITE_ERRORS.get()+"\nBuild: "+com.archos.mediacenter.video.BuildConfig.VERSION_NAME+"\nSource: "+com.archos.mediacenter.video.BuildConfig.PREVIEW_GIT_SHA+"\nPlayback sessions retained: "+sessions.size()+"\nEvent counts: "+counts+"\nRecent significant events (UTC milliseconds):\n"+android.text.TextUtils.join("\n",timeline)+"\nUnclean exit means no clean marker; it does not prove a crash. Native A/V offset is not exposed.\n";
    }
    private static void entry(ZipOutputStream zip,String name,byte[] bytes)throws IOException{zip.putNextEntry(new ZipEntry(name));zip.write(bytes);zip.closeEntry();}
    private Diagnostics(){}
}
