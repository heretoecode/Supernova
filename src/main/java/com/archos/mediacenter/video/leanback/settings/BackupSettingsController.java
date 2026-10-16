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

/** Three-panel Settings landing; deliberate archive operations use a separate, bounded dialog. */
public final class BackupSettingsController {
    private final PreferenceFragmentCompat fragment;
    private final BackupOperationModel model;
    private final ActivityResultLauncher<Intent> savePicker,openPicker;
    private FrameLayout frame;private View middle,help,opener;private SharedThreePanel split;
    private BackupWorkspace workspace;private boolean noticeActive,visible,exportSelection;
    private LinearLayout landing;private TextView description,exportAction,restoreAction,selectedAction;private android.app.Dialog operation;
    private final Map<View,Integer> hidden=new IdentityHashMap<>();
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
        if(this.split!=null)hide();this.split=split;this.middle=middle;this.help=help;
        frame=new FrameLayout(fragment.requireContext());frame.setBackground(MenuFoundation.surface(fragment.requireContext(),false));frame.setTag("settings.backup.workspace");
        landing=new LinearLayout(fragment.requireContext());landing.setOrientation(LinearLayout.VERTICAL);landing.setTag("settings.backup.landing");landing.setVisibility(View.GONE);
        exportAction=landingAction("Export Backup","backup.action.export",()->{exportSelection=true;render(model.current());},"Choose the configuration categories to export, then save a verified ZIP using Android Files. Media, artwork and credentials are excluded. Filenames and locations are Off by default. Backups are unencrypted.");
        restoreAction=landingAction("Restore Backup","backup.action.restore",this::chooseRestore,"Choose a backup ZIP using Android Files. Review the available categories and confirm replacement before any settings change. Unselected categories remain unchanged. Reconnect sources, accounts and Android permissions separately.");
        ((ViewGroup)middle).addView(landing,new LinearLayout.LayoutParams(-1,-1));
        description=SharedThreePanel.text(fragment.requireContext(),"",17);description.setTag("backup.action.description");description.setVisibility(View.GONE);
        ViewGroup helpBody=(ViewGroup)((ScrollView)help).getChildAt(0);helpBody.addView(description,new LinearLayout.LayoutParams(-1,-2));
        model.states().observe(fragment.getViewLifecycleOwner(),state->{if(isVisible())render(state);});
        fragment.getViewLifecycleOwner().getLifecycle().addObserver(new androidx.lifecycle.DefaultLifecycleObserver(){@Override public void onDestroy(androidx.lifecycle.LifecycleOwner owner){dismissOperation();}});
    }
    private TextView landingAction(String label,String tag,Runnable action,String explanation){
        TextView button=SharedThreePanel.text(fragment.requireContext(),label,17);button.setTag(tag);button.setPadding(dp(12),dp(12),dp(12),dp(12));button.setMinHeight(dp(54));button.setFocusable(true);button.setFocusableInTouchMode(true);button.setBackground(SharedThreePanel.accentFocus(fragment.requireContext()));button.setOnClickListener(v->action.run());
        button.setOnFocusChangeListener((v,yes)->{if(yes){selectedAction=button;description.setText(label+"\n\n"+explanation);((ScrollView)help).scrollTo(0,0);}});
        button.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_LEFT){sidebar();return true;}if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){(key==KeyEvent.KEYCODE_DPAD_UP?exportAction:restoreAction).requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT){help.requestFocus();return true;}return false;});
        landing.addView(button,new LinearLayout.LayoutParams(-1,-2));SharedThreePanel.divider(landing);return button;
    }
    private void hideChildren(ViewGroup parent,View retained){for(int i=0;i<parent.getChildCount();i++){View child=parent.getChildAt(i);if(child!=retained){hidden.putIfAbsent(child,child.getVisibility());child.setVisibility(View.GONE);}}}
    public void show(View opener){this.opener=opener;visible=true;
        hideChildren((ViewGroup)middle,landing);hideChildren((ViewGroup)((ScrollView)help).getChildAt(0),description);
        middle.setVisibility(View.VISIBLE);help.setVisibility(View.VISIBLE);split.widths(.28f,.46f,.26f);
        if(TvLayout.width(fragment.requireContext())<780&&split.getParent() instanceof HorizontalScrollView){
            HorizontalScrollView viewport=(HorizontalScrollView)split.getParent();if(viewportWidth==0)viewportWidth=split.getLayoutParams().width;
            split.getLayoutParams().width=Math.max(1,viewport.getWidth()-viewport.getPaddingLeft()-viewport.getPaddingRight());split.requestLayout();viewport.scrollTo(0,0);split.constrainViewport(true);
            LinearLayout.LayoutParams rail=(LinearLayout.LayoutParams)split.getChildAt(0).getLayoutParams();rail.width=TvLayout.px(fragment.requireContext(),132);rail.weight=0;split.getChildAt(0).setLayoutParams(rail);if(restoreRail.isEmpty())compactRail(split.getChildAt(0));
        }
        landing.setVisibility(View.VISIBLE);description.setVisibility(View.VISIBLE);
        if(description.length()==0)description.setText("Export Backup\n\nChoose which configuration categories to save in a verified ZIP. Media and credentials are excluded. Select Restore Backup to review an existing archive.");render(model.current());
    }
    public void hide(){visible=false;dismissOperation();for(Runnable restore:restoreRail)restore.run();restoreRail.clear();if(viewportWidth!=0){split.getLayoutParams().width=viewportWidth;viewportWidth=0;split.constrainViewport(false);split.requestLayout();LinearLayout.LayoutParams rail=(LinearLayout.LayoutParams)split.getChildAt(0).getLayoutParams();rail.width=0;rail.weight=.28f;split.getChildAt(0).setLayoutParams(rail);}if(landing!=null)landing.setVisibility(View.GONE);if(description!=null)description.setVisibility(View.GONE);for(Map.Entry<View,Integer> entry:hidden.entrySet())entry.getKey().setVisibility(entry.getValue());hidden.clear();}
    public boolean isVisible(){return visible;}
    private void dismissOperation(){if(operation!=null){operation.dismiss();operation=null;}}
    private void showOperation(){if(operation!=null)return;operation=new android.app.Dialog(fragment.requireContext());if(frame.getParent() instanceof ViewGroup)((ViewGroup)frame.getParent()).removeView(frame);operation.setContentView(frame);operation.setCanceledOnTouchOutside(false);operation.setOnKeyListener((dialog,key,event)->{if(key==KeyEvent.KEYCODE_BACK){if(event.getAction()==KeyEvent.ACTION_UP)back();return true;}return false;});operation.show();android.view.Window window=operation.getWindow();if(window!=null){window.setBackgroundDrawableResource(android.R.color.transparent);window.setLayout(TvLayout.safeWidth(fragment.requireContext()),Math.max(dp(280),TvLayout.px(fragment.requireContext(),TvLayout.height(fragment.requireContext())-32)));}focusFirst(frame);}
    public android.os.Bundle saveViewState(){
        android.os.Bundle saved=new android.os.Bundle();saved.putBoolean("visible",isVisible());saved.putBoolean("export_selection",exportSelection);
        if(workspace!=null){saved.putString("page",workspace.informationPage());if(model.current().phase==BackupOperationModel.Phase.REVIEW){ArrayList<String> selected=new ArrayList<>();for(BackupCategory category:workspace.selection())selected.add(category.name());saved.putStringArrayList("selection",selected);}}
        return saved;
    }
    public void restoreViewState(android.os.Bundle saved){
        if(saved==null||!saved.getBoolean("visible",false)||frame==null)return;
        ArrayList<String> selection=saved.getStringArrayList("selection");if(selection!=null&&model.current().phase==BackupOperationModel.Phase.REVIEW){selectedRestore=EnumSet.noneOf(BackupCategory.class);for(String name:selection)if(name!=null)try{BackupCategory category=BackupCategory.valueOf(name);if(model.current().categories.contains(category))selectedRestore.add(category);}catch(IllegalArgumentException invalid){/* Ignore obsolete view-state choices. */}}
        exportSelection=saved.getBoolean("export_selection",false);String page=saved.getString("page");View root=fragment.requireView();Runnable restore=()->{if(fragment.isAdded()&&fragment.getView()==root&&PreviewSettings.enterBackup(fragment)&&workspace!=null)workspace.restoreInformationPage(page);};
        if(androidx.core.view.ViewCompat.isLaidOut(root)&&root.getWidth()>0)restore.run();
        else root.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){public void onLayoutChange(View view,int left,int top,int right,int bottom,int oldLeft,int oldTop,int oldRight,int oldBottom){if(right>left){view.removeOnLayoutChangeListener(this);restore.run();}}});
    }
    public boolean hasFocus(){return isVisible()&&(landing.hasFocus()||help.hasFocus()||(operation!=null&&frame.hasFocus()));}
    public boolean navigation(KeyEvent event){
        if(!isVisible()||operation!=null||event.getAction()!=KeyEvent.ACTION_DOWN)return false;
        int key=event.getKeyCode();
        if(landing.hasFocus()){
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){(key==KeyEvent.KEYCODE_DPAD_UP?exportAction:restoreAction).requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){sidebar();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT){help.requestFocus();return true;}
        }else if(help.hasFocus()){
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){(selectedAction==null?exportAction:selectedAction).requestFocus();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){((ScrollView)help).smoothScrollBy(0,dp(key==KeyEvent.KEYCODE_DPAD_UP?-72:72));return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
        }
        return false;
    }
    public void enter(){if(isVisible()){if(operation!=null)focusFirst(frame);else exportAction.requestFocus();}}
    private void sidebar(){if(operation!=null)back();else if(opener!=null)opener.requestFocus();}
    public boolean back(){
        if(!isVisible())return false;if(noticeActive){render(model.current());return true;}if(workspace!=null&&workspace.back())return true;
        if(model.isBusy()){if(model.current().phase!=BackupOperationModel.Phase.RESTORING_LEGACY)model.cancel();return true;}
        if(model.current().phase!=BackupOperationModel.Phase.IDLE){selectedRestore=null;closeOperation();return true;}
        if(operation!=null){exportSelection=false;dismissOperation();exportAction.requestFocus();}else sidebar();return true;
    }
    private void closeOperation(){exportSelection=false;model.closeReview();}
    private void create(Set<BackupCategory> selected){
        List<String> unsupported=new ArrayList<>();for(BackupCategory category:selected)if(!LogicalBackupArchive.registered().contains(category))unsupported.add(category.label);
        if(!unsupported.isEmpty()){notice("Backup could not be created","The selected categories are unsupported by this build: "+android.text.TextUtils.join(", ",unsupported)+". No category was silently omitted and no backup was created.");return;}
        model.create(selected);
    }
    private void chooseSave(){model.destinationPending();try{savePicker.launch(new Intent(Intent.ACTION_CREATE_DOCUMENT).setType("application/zip").addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,"Supernova_Backup.zip"));}catch(RuntimeException unavailable){model.destinationCancelled();notice("File picker unavailable","The verified private backup is retained for retry.");}}
    private void chooseRestore(){try{openPicker.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("application/zip").addCategory(Intent.CATEGORY_OPENABLE));}catch(RuntimeException unavailable){notice("File picker unavailable","No state was changed. Connect an available document provider and try again.");}}
    private void render(BackupOperationModel.State state){
        if(!isVisible())return;
        if(state.phase==BackupOperationModel.Phase.IDLE&&!exportSelection){selectedRestore=null;workspace=null;noticeActive=false;boolean held=operation!=null;dismissOperation();if(held)exportAction.requestFocus();return;}
        boolean focused=holdFrameFocus();frame.removeAllViews();workspace=null;noticeActive=false;
        if(state.phase==BackupOperationModel.Phase.IDLE){selectedRestore=null;workspace=new BackupWorkspace(fragment.requireContext(),this::create,this::chooseRestore,this::sidebar,details,instructions);frame.addView(workspace,new FrameLayout.LayoutParams(-1,-1));}
        else if(state.phase==BackupOperationModel.Phase.EXPORT_READY){chooseSave();return;}
        else if(state.phase==BackupOperationModel.Phase.REVIEW){
            if(selectedRestore==null){selectedRestore=new LinkedHashSet<>();for(BackupCategory category:state.categories)if(category.defaultIncluded&&category!=BackupCategory.LOCATIONS)selectedRestore.add(category);}
            workspace=new BackupWorkspace(fragment.requireContext(),state.categories,selectedRestore,selected->{selectedRestore=new LinkedHashSet<>(selected);model.preview(selected);},this::closeOperation,this::sidebar,details,instructions);frame.addView(workspace,new FrameLayout.LayoutParams(-1,-1));
        }else if(state.phase==BackupOperationModel.Phase.RESTORE_PREVIEW){
            LinearLayout panel=panel("Confirm selected replacement",state.message+"\n\nSelected: "+labels(state.categories)+"\nUnresolved references: "+state.unresolved+(state.receivingChecks?"\nDisplay changes need receiving-device confirmation.":""));
            action(panel,"Cancel",this::closeOperation,"cancel");action(panel,"Restore selected categories",model::confirm,"history");
        }else if(state.phase==BackupOperationModel.Phase.LEGACY_PREVIEW){
            LinearLayout panel=panel("Confirm legacy whole-library replacement",state.message);action(panel,"Cancel",this::closeOperation,"cancel");action(panel,"Restore legacy backup",model::confirmLegacy,"history");
        }else if(state.phase==BackupOperationModel.Phase.RESTORING_LEGACY){
            panel("Restoring legacy backup",state.message);
        }else if(model.isBusy()||state.phase==BackupOperationModel.Phase.DESTINATION_PENDING){
            LinearLayout panel=panel("Backup & Restore",state.message);action(panel,"Cancel",model::cancel,"cancel");
        }else {
            LinearLayout panel=panel(state.phase==BackupOperationModel.Phase.RESTORED?"Restore result":state.phase==BackupOperationModel.Phase.EXPORTED?"Backup verified":"Backup & Restore",state.message+(state.unresolved>0?"\nUnresolved references: "+state.unresolved:""));
            action(panel,"Back to Backup & Restore",this::closeOperation,"back");if(state.retryExport&&!state.recoveryRequired)action(panel,"Save verified backup again",this::chooseSave,"save");
        }
        showOperation();releaseFrameFocus(focused);
    }
    private static String labels(Set<BackupCategory> categories){List<String> labels=new ArrayList<>();for(BackupCategory category:BackupCategory.ordered())if(categories.contains(category))labels.add(category.label);return android.text.TextUtils.join(", ",labels);}
    private LinearLayout panel(String title,String message){
        MenuFoundation.WindowPanel panel=new MenuFoundation.WindowPanel(fragment.requireContext());panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(18),dp(16),dp(18),dp(20));panel.setBackground(MenuFoundation.surface(fragment.requireContext(),false));panel.setTag("backup.operation.page");panel.addView(MenuFoundation.text(fragment.requireContext(),title,23));
        TextView copy=MenuFoundation.text(fragment.requireContext(),message,14);copy.setTextColor(MenuFoundation.SECONDARY);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-1,-2);size.topMargin=dp(16);size.bottomMargin=dp(20);panel.addView(copy,size);frame.addView(panel,new FrameLayout.LayoutParams(-1,-1));return panel;
    }
    private void action(LinearLayout panel,String label,Runnable action,String icon){TextView button=MenuFoundation.control(fragment.requireContext(),label,icon,action);button.setTag("backup.operation:"+label);panel.addView(button,new LinearLayout.LayoutParams(-1,-2));}
    private void notice(String title,String message){if(frame==null)return;holdFrameFocus();frame.removeAllViews();workspace=null;noticeActive=true;LinearLayout panel=panel(title,message);action(panel,"Back to Backup & Restore",()->render(model.current()),"back");showOperation();releaseFrameFocus(true);}
    private boolean holdFrameFocus(){boolean held=frame.hasFocus();if(held){frame.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);frame.setFocusableInTouchMode(true);frame.setFocusable(true);frame.requestFocus();}return held;}
    private void releaseFrameFocus(boolean held){frame.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);if(held)for(int i=0;i<frame.getChildCount();i++)if(focusFirst(frame.getChildAt(i)))break;frame.setFocusable(false);frame.setFocusableInTouchMode(false);}
    private int dp(float value){return MenuFoundation.dp(fragment.requireContext(),value);}
    private static boolean focusFirst(View view){if(view.isFocusable()&&view.isEnabled())return view.requestFocus();if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++)if(focusFirst(((android.view.ViewGroup)view).getChildAt(i)))return true;return false;}
}
