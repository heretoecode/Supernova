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
    private int viewportWidth;
    private final List<Runnable> restoreRail=new ArrayList<>();
    private void compactRail(View view){
        int l=view.getPaddingLeft(),t=view.getPaddingTop(),r=view.getPaddingRight(),b=view.getPaddingBottom();
        restoreRail.add(()->view.setPadding(l,t,r,b));
        view.setPadding(dp(4),t,dp(4),b);
        if(view instanceof TextView){
            TextView label=(TextView)view;float size=label.getTextSize();int lines=label.getMaxLines();android.text.TextUtils.TruncateAt ellipsis=label.getEllipsize();
            restoreRail.add(()->{label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,size);label.setSingleLine(lines==1);label.setMaxLines(lines);label.setEllipsize(ellipsis);});
            label.setTextSize(12);label.setSingleLine(false);label.setMaxLines(2);label.setEllipsize(null);
        }
        if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)compactRail(((ViewGroup)view).getChildAt(i));
    }
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
        if(this.split!=null)hide();this.split=split;this.middle=middle;this.help=help;frame=new FrameLayout(fragment.requireContext());frame.setTag("settings.backup.workspace");frame.setVisibility(View.GONE);
        split.addView(frame,new LinearLayout.LayoutParams(0,-1,.72f));model.states().observe(fragment.getViewLifecycleOwner(),state->{if(isVisible())render(state);});
    }
    public void show(View opener){this.opener=opener;int railWidth=split.getChildAt(0).getWidth();
        if(TvLayout.width(fragment.requireContext())<780&&split.getParent() instanceof HorizontalScrollView){
            HorizontalScrollView viewport=(HorizontalScrollView)split.getParent();if(viewportWidth==0)viewportWidth=split.getLayoutParams().width;
            split.getLayoutParams().width=Math.max(1,viewport.getWidth()-viewport.getPaddingLeft()-viewport.getPaddingRight());split.requestLayout();viewport.scrollTo(0,0);split.constrainViewport(true);
            railWidth=TvLayout.px(fragment.requireContext(),132);
            if(restoreRail.isEmpty())compactRail(split.getChildAt(0));
        }
        middle.setVisibility(View.GONE);help.setVisibility(View.GONE);split.widths(.28f,0,0);LinearLayout.LayoutParams rail=(LinearLayout.LayoutParams)split.getChildAt(0).getLayoutParams();rail.width=railWidth;rail.weight=0;split.getChildAt(0).setLayoutParams(rail);frame.setVisibility(View.VISIBLE);render(model.current());}
    public void hide(){if(frame!=null){frame.setVisibility(View.GONE);for(Runnable restore:restoreRail)restore.run();restoreRail.clear();if(viewportWidth!=0){split.getLayoutParams().width=viewportWidth;viewportWidth=0;split.constrainViewport(false);split.requestLayout();}LinearLayout.LayoutParams rail=(LinearLayout.LayoutParams)split.getChildAt(0).getLayoutParams();rail.width=0;rail.weight=.28f;split.getChildAt(0).setLayoutParams(rail);}}
    public boolean isVisible(){return frame!=null&&frame.getVisibility()==View.VISIBLE;}
    public android.os.Bundle saveViewState(){
        android.os.Bundle saved=new android.os.Bundle();saved.putBoolean("visible",isVisible());
        if(workspace!=null){saved.putString("page",workspace.informationPage());if(model.current().phase==BackupOperationModel.Phase.REVIEW){ArrayList<String> selected=new ArrayList<>();for(BackupCategory category:workspace.selection())selected.add(category.name());saved.putStringArrayList("selection",selected);}}
        return saved;
    }
    public void restoreViewState(android.os.Bundle saved){
        if(saved==null||!saved.getBoolean("visible",false)||frame==null)return;
        ArrayList<String> selection=saved.getStringArrayList("selection");if(selection!=null&&model.current().phase==BackupOperationModel.Phase.REVIEW){selectedRestore=EnumSet.noneOf(BackupCategory.class);for(String name:selection)if(name!=null)try{BackupCategory category=BackupCategory.valueOf(name);if(model.current().categories.contains(category))selectedRestore.add(category);}catch(IllegalArgumentException invalid){/* Ignore obsolete view-state choices. */}}
        String page=saved.getString("page");View root=fragment.requireView();Runnable restore=()->{if(fragment.isAdded()&&fragment.getView()==root&&PreviewSettings.enterBackup(fragment)&&workspace!=null)workspace.restoreInformationPage(page);};
        if(androidx.core.view.ViewCompat.isLaidOut(root)&&root.getWidth()>0)restore.run();
        else root.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){public void onLayoutChange(View view,int left,int top,int right,int bottom,int oldLeft,int oldTop,int oldRight,int oldBottom){if(right>left){view.removeOnLayoutChangeListener(this);restore.run();}}});
    }
    public boolean hasFocus(){return isVisible()&&frame.hasFocus();}
    public void enter(){if(isVisible())focusFirst(frame);}
    private void sidebar(){if(opener!=null)opener.requestFocus();}
    public boolean back(){
        if(!isVisible())return false;if(noticeActive){render(model.current());return true;}if(workspace!=null&&workspace.back())return true;
        if(model.isBusy()){if(model.current().phase!=BackupOperationModel.Phase.RESTORING_LEGACY)model.cancel();return true;}
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
        if(!isVisible())return;boolean focused=holdFrameFocus();frame.removeAllViews();workspace=null;noticeActive=false;
        if(state.phase==BackupOperationModel.Phase.IDLE){selectedRestore=null;workspace=new BackupWorkspace(fragment.requireContext(),this::create,this::chooseRestore,this::sidebar,details,instructions);frame.addView(workspace,new FrameLayout.LayoutParams(-1,-1));}
        else if(state.phase==BackupOperationModel.Phase.EXPORT_READY){chooseSave();return;}
        else if(state.phase==BackupOperationModel.Phase.REVIEW){
            if(selectedRestore==null){selectedRestore=new LinkedHashSet<>();for(BackupCategory category:state.categories)if(category.defaultIncluded&&category!=BackupCategory.LOCATIONS)selectedRestore.add(category);}
            workspace=new BackupWorkspace(fragment.requireContext(),state.categories,selectedRestore,selected->{selectedRestore=new LinkedHashSet<>(selected);model.preview(selected);},model::closeReview,this::sidebar,details,instructions);frame.addView(workspace,new FrameLayout.LayoutParams(-1,-1));
        }else if(state.phase==BackupOperationModel.Phase.RESTORE_PREVIEW){
            LinearLayout panel=panel("Confirm selected replacement",state.message+"\n\nSelected: "+labels(state.categories)+"\nUnresolved references: "+state.unresolved+(state.receivingChecks?"\nDisplay changes need receiving-device confirmation.":""));
            action(panel,"Cancel",model::closeReview,"cancel");action(panel,"Restore selected categories",model::confirm,"history");
        }else if(state.phase==BackupOperationModel.Phase.LEGACY_PREVIEW){
            LinearLayout panel=panel("Confirm legacy whole-library replacement",state.message);action(panel,"Cancel",model::closeReview,"cancel");action(panel,"Restore legacy backup",model::confirmLegacy,"history");
        }else if(state.phase==BackupOperationModel.Phase.RESTORING_LEGACY){
            panel("Restoring legacy backup",state.message);
        }else if(model.isBusy()||state.phase==BackupOperationModel.Phase.DESTINATION_PENDING){
            LinearLayout panel=panel("Backup & Restore",state.message);action(panel,"Cancel",model::cancel,"cancel");
        }else {
            LinearLayout panel=panel(state.phase==BackupOperationModel.Phase.RESTORED?"Restore result":state.phase==BackupOperationModel.Phase.EXPORTED?"Backup verified":"Backup & Restore",state.message+(state.unresolved>0?"\nUnresolved references: "+state.unresolved:""));
            action(panel,"Back to Backup & Restore",model::closeReview,"back");if(state.retryExport&&!state.recoveryRequired)action(panel,"Save verified backup again",this::chooseSave,"save");
        }
        releaseFrameFocus(focused);
    }
    private static String labels(Set<BackupCategory> categories){List<String> labels=new ArrayList<>();for(BackupCategory category:BackupCategory.ordered())if(categories.contains(category))labels.add(category.label);return android.text.TextUtils.join(", ",labels);}
    private LinearLayout panel(String title,String message){
        MenuFoundation.WindowPanel panel=new MenuFoundation.WindowPanel(fragment.requireContext());panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(18),dp(16),dp(18),dp(20));panel.setBackground(MenuFoundation.surface(fragment.requireContext(),false));panel.setTag("backup.operation.page");panel.addView(MenuFoundation.text(fragment.requireContext(),title,23));
        TextView copy=MenuFoundation.text(fragment.requireContext(),message,14);copy.setTextColor(MenuFoundation.SECONDARY);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-1,-2);size.topMargin=dp(16);size.bottomMargin=dp(20);panel.addView(copy,size);frame.addView(panel,new FrameLayout.LayoutParams(-1,-1));return panel;
    }
    private void action(LinearLayout panel,String label,Runnable action,String icon){TextView button=MenuFoundation.control(fragment.requireContext(),label,icon,action);button.setTag("backup.operation:"+label);panel.addView(button,new LinearLayout.LayoutParams(-1,-2));}
    private void notice(String title,String message){if(frame==null)return;holdFrameFocus();frame.removeAllViews();workspace=null;noticeActive=true;LinearLayout panel=panel(title,message);action(panel,"Back to Backup & Restore",()->render(model.current()),"back");releaseFrameFocus(true);}
    private boolean holdFrameFocus(){boolean held=frame.hasFocus();if(held){frame.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);frame.setFocusableInTouchMode(true);frame.setFocusable(true);frame.requestFocus();}return held;}
    private void releaseFrameFocus(boolean held){frame.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);if(held)for(int i=0;i<frame.getChildCount();i++)if(focusFirst(frame.getChildAt(i)))break;frame.setFocusable(false);frame.setFocusableInTouchMode(false);}
    private int dp(float value){return MenuFoundation.dp(fragment.requireContext(),value);}
    private static boolean focusFirst(View view){if(view.isFocusable()&&view.isEnabled())return view.requestFocus();if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++)if(focusFirst(((android.view.ViewGroup)view).getChildAt(i)))return true;return false;}
}
