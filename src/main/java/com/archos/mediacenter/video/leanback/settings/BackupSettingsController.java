package com.archos.mediacenter.video.leanback.settings;

import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import java.util.*;

/** Registered in Fragment.onCreate; connects the approved right workspace without replacing the Settings rail. */
public final class BackupSettingsController {
    private final PreferenceFragmentCompat fragment;
    private final BackupOperationModel model;
    private final ActivityResultLauncher<Intent> savePicker,openPicker;
    private FrameLayout frame;private View middle,help,opener;private SharedThreePanel split;
    private BackupWorkspace workspace;private boolean noticeActive;
    private Set<BackupCategory> selectedRestore;
    private final List<BackupWorkspace.Paragraph> details=List.of(
        new BackupWorkspace.Paragraph("Your configuration","Choose each category independently. Media, artwork, subtitle files, credentials, accounts, access grants, Put.io associations and legacy Collections are excluded."),
        new BackupWorkspace.Paragraph("Privacy and replacement","History can reveal viewing interests. Filenames and locations is Off by default and requires explicit consent. Selected restore categories replace their owned state; selected empty categories clear it. Missing and unselected categories remain unchanged."),
        new BackupWorkspace.Paragraph("Reconnect and verify","Backups are unencrypted. Reconnect sources, services and Android permissions separately. Exact receiving identities can resolve; unknown references remain pending. Portability and offline metadata are not guaranteed."));
    private final List<BackupWorkspace.Paragraph> instructions=List.of(new BackupWorkspace.Paragraph("Restoring a logical backup",LogicalBackupArchive.INSTRUCTIONS));
    public BackupSettingsController(PreferenceFragmentCompat fragment){
        this.fragment=fragment;model=new ViewModelProvider(fragment.requireActivity()).get(BackupOperationModel.class);
        savePicker=fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
            if(result.getResultCode()!=Activity.RESULT_OK||result.getData()==null||result.getData().getData()==null){model.destinationCancelled();return;}
            try{model.publish(result.getData().getData());}catch(IllegalStateException lost){notice("Backup unavailable","Prepare and verify another backup before saving. The application did not write to the selected document.");}
        });
        openPicker=fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{
            if(result.getResultCode()==Activity.RESULT_OK&&result.getData()!=null&&result.getData().getData()!=null)model.open(result.getData().getData());
        });
    }
    public void attach(SharedThreePanel split,View middle,View help){
        this.split=split;this.middle=middle;this.help=help;frame=new FrameLayout(fragment.requireContext());frame.setTag("settings.backup.workspace");frame.setVisibility(View.GONE);
        split.addView(frame,new LinearLayout.LayoutParams(0,-1,.72f));model.states().observe(fragment.getViewLifecycleOwner(),state->{if(isVisible())render(state);});
    }
    public void show(View opener){this.opener=opener;middle.setVisibility(View.GONE);help.setVisibility(View.GONE);split.widths(.28f,0,0);frame.setVisibility(View.VISIBLE);render(model.current());}
    public void hide(){if(frame!=null)frame.setVisibility(View.GONE);}
    public boolean isVisible(){return frame!=null&&frame.getVisibility()==View.VISIBLE;}
    public boolean hasFocus(){return isVisible()&&frame.hasFocus();}
    public void enter(){if(isVisible())focusFirst(frame);}
    private void sidebar(){if(opener!=null)opener.requestFocus();}
    public boolean back(){
        if(!isVisible())return false;if(noticeActive){render(model.current());return true;}if(workspace!=null&&workspace.back())return true;
        if(model.isBusy()){model.cancel();return true;}
        if(model.current().phase!=BackupOperationModel.Phase.IDLE){selectedRestore=null;model.closeReview();return true;}
        sidebar();return true;
    }
    private void create(Set<BackupCategory> selected){
        List<String> unsupported=new ArrayList<>();for(BackupCategory category:selected)if(!LogicalBackupArchive.registered().contains(category))unsupported.add(category.label);
        if(!unsupported.isEmpty()){notice("Backup could not be created","The selected categories are unsupported by this build: "+android.text.TextUtils.join(", ",unsupported)+". No category was silently omitted and no backup was created.");return;}
        model.create(selected);
    }
    private void chooseSave(){model.destinationPending();try{savePicker.launch(new Intent(Intent.ACTION_CREATE_DOCUMENT).setType("application/zip").addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,"Supernova_Backup.zip"));}catch(RuntimeException unavailable){model.destinationCancelled();notice("File picker unavailable","The verified private backup is retained for retry.");}}
    private void chooseRestore(){try{openPicker.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("application/zip").addCategory(Intent.CATEGORY_OPENABLE));}catch(RuntimeException unavailable){notice("File picker unavailable","No state was changed. Connect an available document provider and try again.");}}
    private void render(BackupOperationModel.State state){
        if(!isVisible())return;boolean focused=frame.hasFocus();frame.removeAllViews();workspace=null;noticeActive=false;
        if(state.phase==BackupOperationModel.Phase.IDLE){selectedRestore=null;workspace=new BackupWorkspace(fragment.requireContext(),this::create,this::chooseRestore,this::sidebar,details,instructions);frame.addView(workspace,new FrameLayout.LayoutParams(-1,-1));}
        else if(state.phase==BackupOperationModel.Phase.EXPORT_READY){chooseSave();return;}
        else if(state.phase==BackupOperationModel.Phase.REVIEW){
            if(selectedRestore==null){selectedRestore=new LinkedHashSet<>();for(BackupCategory category:state.categories)if(category.defaultIncluded&&category!=BackupCategory.LOCATIONS)selectedRestore.add(category);}
            workspace=new BackupWorkspace(fragment.requireContext(),state.categories,selectedRestore,selected->{selectedRestore=new LinkedHashSet<>(selected);model.preview(selected);},model::closeReview,this::sidebar,details,instructions);frame.addView(workspace,new FrameLayout.LayoutParams(-1,-1));
        }else if(state.phase==BackupOperationModel.Phase.RESTORE_PREVIEW){
            LinearLayout panel=panel("Confirm selected replacement",state.message+"\n\nSelected: "+labels(state.categories)+"\nUnresolved references: "+state.unresolved+(state.receivingChecks?"\nDisplay changes need receiving-device confirmation.":""));
            action(panel,"Cancel",model::closeReview,"cancel");action(panel,"Restore selected categories",model::confirm,"history");
        }else if(model.isBusy()||state.phase==BackupOperationModel.Phase.DESTINATION_PENDING){
            LinearLayout panel=panel("Backup & Restore",state.message);action(panel,"Cancel",model::cancel,"cancel");
        }else {
            LinearLayout panel=panel(state.phase==BackupOperationModel.Phase.RESTORED?"Restore result":state.phase==BackupOperationModel.Phase.EXPORTED?"Backup verified":"Backup & Restore",state.message+(state.unresolved>0?"\nUnresolved references: "+state.unresolved:""));
            action(panel,"Back to Backup & Restore",model::closeReview,"back");if(state.retryExport&&!state.recoveryRequired)action(panel,"Save verified backup again",this::chooseSave,"save");
        }
        if(focused)focusFirst(frame);
    }
    private static String labels(Set<BackupCategory> categories){List<String> labels=new ArrayList<>();for(BackupCategory category:BackupCategory.ordered())if(categories.contains(category))labels.add(category.label);return android.text.TextUtils.join(", ",labels);}
    private LinearLayout panel(String title,String message){
        MenuFoundation.WindowPanel panel=new MenuFoundation.WindowPanel(fragment.requireContext());panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(18),dp(16),dp(18),dp(20));panel.setBackground(MenuFoundation.surface(fragment.requireContext(),false));panel.setTag("backup.operation.page");panel.addView(MenuFoundation.text(fragment.requireContext(),title,23));
        TextView copy=MenuFoundation.text(fragment.requireContext(),message,14);copy.setTextColor(MenuFoundation.SECONDARY);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-1,-2);size.topMargin=dp(16);size.bottomMargin=dp(20);panel.addView(copy,size);frame.addView(panel,new FrameLayout.LayoutParams(-1,-1));return panel;
    }
    private void action(LinearLayout panel,String label,Runnable action,String icon){TextView button=MenuFoundation.control(fragment.requireContext(),label,icon,action);button.setTag("backup.operation:"+label);panel.addView(button,new LinearLayout.LayoutParams(-1,-2));}
    private void notice(String title,String message){if(frame==null)return;frame.removeAllViews();workspace=null;noticeActive=true;LinearLayout panel=panel(title,message);action(panel,"Back to Backup & Restore",()->render(model.current()),"back");focusFirst(frame);}
    private int dp(float value){return MenuFoundation.dp(fragment.requireContext(),value);}
    private static boolean focusFirst(View view){if(view.isFocusable()&&view.isEnabled())return view.requestFocus();if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++)if(focusFirst(((android.view.ViewGroup)view).getChildAt(i)))return true;return false;}
}
