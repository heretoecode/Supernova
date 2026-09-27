package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import androidx.fragment.app.FragmentActivity;
import com.archos.mediacenter.video.utils.SubtitlesWizardCommon;
import java.util.*;
import java.util.concurrent.*;

/** Shared file choices over the retained subtitle discovery/association implementation. */
public final class PreviewSubtitleChooser {
    public static final String ALLOW_SELECTION="preview_subtitle_selection";
    public static final String ACTIVE_PATH="preview_subtitle_active_path";
    public static final String SELECTED_PATH="preview_subtitle_selected_path";
    static final class Entry {
        final String path,name,source,size;final int index;final boolean associated,writable;
        Entry(String path,String name,String source,String size,int index,boolean associated,boolean writable){this.path=path;this.name=name;this.source=source;this.size=size;this.index=index;this.associated=associated;this.writable=writable;}
    }
    interface Backend {
        List<Entry> load() throws Exception;
        String associate(Entry entry) throws Exception;
        boolean delete(Entry entry) throws Exception;
    }
    private final Activity host;private final Backend backend;private final Executor worker;
    private final Handler main=new Handler(Looper.getMainLooper());
    private Dialog choices,child,progress;private boolean closed,busy;
    private final List<Dialog> children=new ArrayList<>();
    private List<Entry> entries=Collections.emptyList();private String focusPath;
    public static PreviewSubtitleChooser create(FragmentActivity host){
        SubtitlesWizardCommon common=new SubtitlesWizardCommon(host);common.prepare();
        Backend backend=new Backend(){
            public List<Entry> load(){
                common.loadFiles();if(common.hasLoadFailure())throw new IllegalStateException("Subtitle listing unavailable");
                List<Entry> items=new ArrayList<>();
                for(boolean associated:new boolean[]{true,false})for(int i=0;i<(associated?common.getCurrentFilesCount():common.getAvailableFilesCount());i++){
                    String path=associated?common.getCurrentFile(i):common.getAvailableFile(i);boolean writable=false;
                    try{writable=com.archos.filecorelibrary.MetaFile2Factory.getMetaFileForUrl(Uri.parse(path)).canWrite();}catch(Exception ignored){}
                    items.add(new Entry(path,common.getFileName(path),common.isCacheFile(path)?"Saved subtitle cache":"Media folder",common.getFileSize(path),i,associated,writable));
                }return items;
            }
            public String associate(Entry entry){if(!common.renameFile(entry.path,entry.index))return null;return common.getCurrentFile(common.getCurrentFilesCount()-1);}
            public boolean delete(Entry entry){return common.deleteFile(entry.path,entry.index,entry.associated);}
        };
        return new PreviewSubtitleChooser(host,backend,Executors.newSingleThreadExecutor());
    }
    PreviewSubtitleChooser(Activity host,Backend backend,Executor worker){this.host=host;this.backend=backend;this.worker=worker;}
    private boolean alive(){return !closed&&!host.isFinishing()&&!host.isDestroyed();}
    public void start(){load();}
    private void load(){
        if(!alive()||busy)return;busy=true;
        progress=PreviewOperationDialog.show(host,"Choose Subtitles","Finding local and saved subtitle files…",host::finish);
        worker.execute(()->{
            List<Entry> result;try{result=backend.load();}catch(Exception error){result=null;}
            final List<Entry> loaded=result;
            main.post(()->{busy=false;dismissProgress();if(!alive())return;if(loaded==null){notice("Subtitle files could not be listed. Check that the source is available.",host::finish);return;}entries=loaded;render();});
        });
    }
    private void render(){
        if(!alive())return;if(choices!=null)choices.dismiss();
        if(entries.isEmpty()){notice("No local or saved subtitle files were found for this title.",host::finish);return;}
        List<String> labels=new ArrayList<>();List<Entry> rows=new ArrayList<>();Set<Integer> checked=new HashSet<>();int focus=-1;
        String active=host.getIntent().getStringExtra(ACTIVE_PATH);
        for(boolean associated:new boolean[]{true,false}){
            boolean heading=false;
            for(Entry entry:entries)if(entry.associated==associated){
                if(!heading){labels.add(associated?"— Associated with this title":"— Other subtitle files");rows.add(null);heading=true;}
                if(sameFile(active,entry.path))checked.add(labels.size());
                if(entry.path.equals(focusPath))focus=labels.size();
                labels.add(entry.name+" · "+entry.source+(sameFile(active,entry.path)?" · Active":""));rows.add(entry);
            }
        }
        choices=PreviewDialog.choose(host,"Choose Subtitles",labels.toArray(new String[0]),focus,checked,false,index->{Entry entry=rows.get(index);if(entry!=null){focusPath=entry.path;review(entry);}});
        choices.setOnCancelListener(dialog->host.finish());
    }
    private void review(Entry entry){
        boolean playback=host.getIntent().getBooleanExtra(ALLOW_SELECTION,false);
        List<String> labels=new ArrayList<>();List<Runnable> actions=new ArrayList<>();
        if(entry.associated&&playback){labels.add("Use Subtitle");actions.add(()->selected(entry.path));}
        if(!entry.associated){labels.add("Associate with This Title"+(entry.writable?"":" — unavailable"));actions.add(()->{
            if(!entry.writable)return;
            child=trackChild(PreviewDialog.review(host,"Associate Subtitle",entry.name+"\n\nThis renames the subtitle file to associate it with this title. The media file is not changed.","Associate",()->mutate(entry,false)));
        });}
        labels.add("File Information");actions.add(()->{child=trackChild(PreviewOperationDialog.notice(host,"Subtitle File",entry.name+"\n"+entry.source+"\n"+entry.size+"\n"+(entry.associated?"Associated with this title":"Not associated with this title")+(sameFile(host.getIntent().getStringExtra(ACTIVE_PATH),entry.path)?"\nActive in playback":""),()->{}));});
        labels.add("Delete Subtitle File"+(entry.writable?"":" — unavailable"));actions.add(()->{
            if(entry.writable)child=trackChild(PreviewDialog.confirmDelete(host,"Delete Subtitle File",entry.name+"\nThis deletes the subtitle file, not the video.",()->mutate(entry,true)));
        });
        child=trackChild(PreviewDialog.choose(host,"Subtitle Actions",labels.toArray(new String[0]),-1,Collections.emptySet(),false,index->actions.get(index).run()));
    }
    private void mutate(Entry entry,boolean delete){
        if(!alive()||busy)return;busy=true;
        progress=PreviewOperationDialog.waiting(host,"Choose Subtitles",delete?"Deleting subtitle file…":"Associating subtitle file…");
        worker.execute(()->{
            String selected=null;boolean success=false;
            try{if(delete)success=backend.delete(entry);else{selected=backend.associate(entry);success=selected!=null;}}catch(Exception ignored){}
            final String path=selected;final boolean changed=success;
            main.post(()->{busy=false;dismissProgress();if(!alive())return;if(!changed){notice("The subtitle file could not be changed. Check source permissions and availability.",()->{});return;}
                host.setResult(Activity.RESULT_OK);if(path!=null&&host.getIntent().getBooleanExtra(ALLOW_SELECTION,false)){selected(path);return;}
                closeChildren();if(choices!=null)choices.dismiss();load();
            });
        });
    }
    private void selected(String path){host.setResult(Activity.RESULT_OK,new Intent().putExtra(SELECTED_PATH,path));host.finish();}
    private Dialog trackChild(Dialog dialog){children.removeIf(item->!item.isShowing());children.add(dialog);return dialog;}
    private void closeChildren(){for(int i=children.size()-1;i>=0;i--){Dialog dialog=children.get(i);dialog.setOnDismissListener(null);dialog.dismiss();}children.clear();child=null;}
    private void notice(String text,Runnable closed){child=trackChild(PreviewOperationDialog.notice(host,"Choose Subtitles",text,closed));}
    private void dismissProgress(){if(progress!=null){progress.dismiss();progress=null;}}
    public void close(){closed=true;main.removeCallbacksAndMessages(null);dismissProgress();closeChildren();if(choices!=null)choices.dismiss();if(worker instanceof ExecutorService)((ExecutorService)worker).shutdownNow();}
    public static boolean sameFile(String first,String second){
        if(first==null||second==null||first.isEmpty()||second.isEmpty())return false;
        Uri a=Uri.parse(first),b=Uri.parse(second);
        boolean localA=a.getScheme()==null||"file".equals(a.getScheme()),localB=b.getScheme()==null||"file".equals(b.getScheme());
        return localA&&localB?Objects.equals(a.getPath(),b.getPath()):first.equals(second);
    }
}
