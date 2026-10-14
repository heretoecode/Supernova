package com.archos.mediacenter.video.utils;

import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import com.archos.mediacenter.video.leanback.settings.BackupOperationModel;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import java.util.*;

/** Historical Settings uses the same immutable import and journal as the TV workspace. */
public final class BackupReview implements DefaultLifecycleObserver {
    /** Temporary review choices survive recreation; they are never application preferences. */
    public static final class Selection extends ViewModel {
        Set<BackupCategory> categories;
    }
    private final PreferenceFragmentCompat fragment;
    private final BackupOperationModel model;
    private final Selection selection;
    private AlertDialog dialog,privacy;
    public BackupReview(PreferenceFragmentCompat fragment) {
        this.fragment=fragment;
        ViewModelProvider provider=new ViewModelProvider(fragment.requireActivity());
        model=provider.get(BackupOperationModel.class);selection=provider.get(Selection.class);
        fragment.getLifecycle().addObserver(this);
        model.states().observe(fragment,this::render);
    }
    public void open(Uri source) {
        if(model.isBusy())return;
        selection.categories=null;model.open(source);
    }
    @Override public void onStop(LifecycleOwner owner){dismiss();}
    @Override public void onStart(LifecycleOwner owner){render(model.current());}
    private void dismiss(){if(privacy!=null){privacy.dismiss();privacy=null;}if(dialog!=null){dialog.dismiss();dialog=null;}}
    private void render(BackupOperationModel.State state) {
        if(!fragment.isAdded()||!fragment.getLifecycle().getCurrentState().isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED))return;
        dismiss();if(state.phase==BackupOperationModel.Phase.IDLE)return;
        AlertDialog.Builder builder=new AlertDialog.Builder(fragment.requireContext());
        if(state.phase==BackupOperationModel.Phase.REVIEW){
            List<BackupCategory> present=new ArrayList<>();for(BackupCategory category:BackupCategory.ordered())if(state.categories.contains(category))present.add(category);
            if(selection.categories==null){selection.categories=EnumSet.noneOf(BackupCategory.class);for(BackupCategory category:present)if(category.defaultIncluded&&category!=BackupCategory.LOCATIONS)selection.categories.add(category);}
            selection.categories.retainAll(state.categories);
            String[] labels=new String[present.size()];boolean[] checked=new boolean[present.size()];for(int i=0;i<present.size();i++){labels[i]=present.get(i).label;checked[i]=selection.categories.contains(present.get(i));}
            builder.setTitle("Select categories to replace").setMultiChoiceItems(labels,checked,(choice,index,included)->{
                BackupCategory category=present.get(index);
                if(included&&category==BackupCategory.LOCATIONS){
                    ((AlertDialog)choice).getListView().setItemChecked(index,false);
                    privacy=new AlertDialog.Builder(fragment.requireContext()).setTitle("Include filenames and locations?")
                        .setMessage("This may expose filenames, folders, hosts and shares. Reconnect credentials and Android permissions separately. Unknown locations are not automatically trusted.")
                        .setNegativeButton(android.R.string.cancel,(warning,which)->{selection.categories.remove(category);if(dialog!=null)dialog.getListView().setItemChecked(index,false);updateContinue();}).setPositiveButton("Include",(warning,which)->{selection.categories.add(category);if(dialog!=null)dialog.getListView().setItemChecked(index,true);updateContinue();}).create();privacy.setOnCancelListener(warning->{selection.categories.remove(category);if(dialog!=null)dialog.getListView().setItemChecked(index,false);updateContinue();});privacy.show();
                }else if(included)selection.categories.add(category);else selection.categories.remove(category);
                updateContinue();
            }).setNegativeButton(android.R.string.cancel,(d,w)->model.closeReview()).setPositiveButton("Review replacement",(d,w)->model.preview(new LinkedHashSet<>(selection.categories)));
        }else if(state.phase==BackupOperationModel.Phase.RESTORE_PREVIEW){
            builder.setTitle("Confirm selected replacement").setMessage(state.message+"\n\nSelected: "+labels(state.categories)+"\nUnresolved references: "+state.unresolved+(state.receivingChecks?"\nConfirm display choices on this device after restoring.":""))
                .setNegativeButton(android.R.string.cancel,(d,w)->model.closeReview()).setPositiveButton("Restore selected categories",(d,w)->model.confirm());
        }else if(state.phase==BackupOperationModel.Phase.LEGACY_PREVIEW){
            builder.setTitle("Confirm legacy whole-library replacement").setMessage(state.message)
                .setNegativeButton(android.R.string.cancel,(d,w)->model.closeReview()).setPositiveButton("Restore legacy backup",(d,w)->model.confirmLegacy());
        }else if(model.isBusy()){
            builder.setTitle("Backup & Restore").setMessage(state.message).setCancelable(false);
            if(state.phase!=BackupOperationModel.Phase.RESTORING_LEGACY)builder.setNegativeButton(android.R.string.cancel,(d,w)->model.cancel());
        }else{
            builder.setTitle(state.phase==BackupOperationModel.Phase.RESTORED?"Restore result":"Backup & Restore").setMessage(state.message+(state.unresolved>0?"\nUnresolved references: "+state.unresolved:""))
                .setPositiveButton(android.R.string.ok,(d,w)->model.closeReview());
        }
        dialog=builder.create();dialog.setOnCancelListener(d->{if(model.isBusy())model.cancel();else model.closeReview();});dialog.show();updateContinue();
    }
    private void updateContinue(){if(dialog!=null&&model.current().phase==BackupOperationModel.Phase.REVIEW)dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(selection.categories!=null&&!selection.categories.isEmpty());}
    private static String labels(Set<BackupCategory> categories){List<String> labels=new ArrayList<>();for(BackupCategory category:BackupCategory.ordered())if(categories.contains(category))labels.add(category.label);return android.text.TextUtils.join(", ",labels);}
}
