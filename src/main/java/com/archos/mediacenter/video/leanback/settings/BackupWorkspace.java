package com.archos.mediacenter.video.leanback.settings;

import android.content.*;
import android.view.*;
import android.widget.*;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.BackupCategory;
import java.util.*;
import java.util.function.Consumer;

/** Right-hand Settings workspace only. The caller retains the permanent sidebar and backdrop. */
public final class BackupWorkspace extends MenuFoundation.WindowPanel {
    public static final int COLUMNS=5;
    public static final class Paragraph {
        public final String heading,text;
        public Paragraph(String heading,String text){this.heading=heading;this.text=text;}
    }
    private final SharedPreferences preferences;
    private final Consumer<Set<BackupCategory>> create;
    private final Runnable restore,sidebar;
    private final List<Paragraph> details,instructions;
    private final Map<BackupCategory,TextView> toggles=new EnumMap<>(BackupCategory.class);
    private final List<BackupCategory> ordered=BackupCategory.ordered();
    private TextView createButton,restoreButton,detailsButton,instructionsButton;
    private View previousFocus;
    private ViewGroup information;
    private int lastGrid;

    public BackupWorkspace(Context context,Consumer<Set<BackupCategory>> create,Runnable restore,Runnable sidebar,List<Paragraph> details,List<Paragraph> instructions){
        super(context);preferences=PreferenceManager.getDefaultSharedPreferences(context);this.create=create;this.restore=restore;this.sidebar=sidebar;this.details=new ArrayList<>(details);this.instructions=new ArrayList<>(instructions);
        setOrientation(VERTICAL);setClipChildren(false);setClipToPadding(false);showMain();
    }
    private int dp(float value){return MenuFoundation.dp(getContext(),value);}
    public boolean included(BackupCategory category){String key=category.preference();if(!preferences.contains(key)&&category==BackupCategory.HISTORY)return preferences.getBoolean("supernova_backup_history",true);if(!preferences.contains(key)&&category==BackupCategory.POSITIONS)return preferences.getBoolean("supernova_backup_resume",true);return preferences.getBoolean(key,category.defaultIncluded);}
    public Set<BackupCategory> selection(){Set<BackupCategory> result=EnumSet.noneOf(BackupCategory.class);for(BackupCategory category:ordered)if(included(category))result.add(category);return Collections.unmodifiableSet(result);}
    private void update(BackupCategory category,boolean included){
        SharedPreferences.Editor edit=preferences.edit().putBoolean(category.preference(),included);
        if(category==BackupCategory.HISTORY)edit.putBoolean("supernova_backup_history",included);
        if(category==BackupCategory.POSITIONS)edit.putBoolean("supernova_backup_resume",included);
        if(!edit.commit()){Toast.makeText(getContext(),"Could not save backup choice.",Toast.LENGTH_SHORT).show();return;}decorate(toggles.get(category),category);
    }
    private void toggle(BackupCategory category){
        boolean next=!included(category);
        if(category==BackupCategory.LOCATIONS&&next){privacy("Include filenames and locations?","These can reveal media filenames, folders, hosts and shares across included categories. The archive is unencrypted. Media and credentials remain excluded.","Include locations",()->update(category,true));return;}
        if(category==BackupCategory.HISTORY&&!next){privacy("Exclude Watch history?","Watched states and related personalised behaviour may not be restored. Playback positions are selected independently.","Exclude history",()->update(category,false));return;}
        if((category==BackupCategory.SOURCES||category==BackupCategory.BOOKMARKS)&&next&&!included(BackupCategory.LOCATIONS)){privacy("Locations remain excluded","These definitions can retain unresolved intent, but physical locations cannot be recreated while Filenames and locations is Off. Reconnect and select locations separately.","Keep locations Off",()->update(category,true));return;}
        update(category,next);
    }
    private void decorate(TextView view,BackupCategory category){
        android.graphics.drawable.Drawable meaning=new PreviewIcon(category.icon),check=new PreviewIcon(included(category)?"checkbox checked":"checkbox");meaning.setBounds(0,0,dp(17),dp(17));check.setBounds(0,0,dp(17),dp(17));
        view.setCompoundDrawables(meaning,null,check,null);view.setCompoundDrawablePadding(dp(4));view.setContentDescription(category.label+". "+(included(category)?"Included. ":"Excluded. ")+category.description);
    }
    private TextView option(BackupCategory category){
        TextView view=MenuFoundation.control(getContext(),category.label,null,()->toggle(category));view.setTextSize(13*TvLayout.scale(getContext()));view.setPadding(dp(4),dp(2),dp(4),dp(2));view.setSingleLine(false);view.setEllipsize(null);view.setGravity(Gravity.CENTER_VERTICAL);view.setTag("backup.category."+category.name());toggles.put(category,view);decorate(view,category);
        view.setOnFocusChangeListener((v,yes)->{if(yes&&category!=BackupCategory.LOCATIONS)lastGrid=ordered.indexOf(category);});
        view.setOnKeyListener((v,key,event)->{
            if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
            int index=ordered.indexOf(category),last=ordered.size()-2;
            if((key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER)&&event.getRepeatCount()>0)return true;
            if(category==BackupCategory.LOCATIONS){if(key==KeyEvent.KEYCODE_DPAD_UP){focus(ordered.get(Math.min(last,Math.max(last/COLUMNS*COLUMNS,lastGrid))));return true;}if(key==KeyEvent.KEYCODE_DPAD_DOWN){detailsButton.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT){sidebar.run();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;}
            if(key==KeyEvent.KEYCODE_DPAD_UP){if(index<COLUMNS)(index<COLUMNS/2?createButton:restoreButton).requestFocus();else focus(ordered.get(index-COLUMNS));return true;}
            if(key==KeyEvent.KEYCODE_DPAD_DOWN){focus(ordered.get(index+COLUMNS>last?ordered.size()-1:index+COLUMNS));return true;}
            if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(index%COLUMNS==0)sidebar.run();else focus(ordered.get(index-1));return true;}
            if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(index%COLUMNS<COLUMNS-1&&index<last)focus(ordered.get(index+1));return true;}
            return false;
        });return view;
    }
    private void focus(BackupCategory category){TextView view=toggles.get(category);if(view!=null)view.requestFocus();}
    private void divider(LinearLayout parent){View line=new View(getContext());line.setBackgroundColor(MenuFoundation.DIVIDER);line.setTag("backup.divider");parent.addView(line,new LayoutParams(-1,dp(1)));}
    private LinearLayout cell(BackupCategory category){LinearLayout cell=new LinearLayout(getContext());cell.setOrientation(VERTICAL);cell.setClipChildren(false);cell.setClipToPadding(false);cell.addView(option(category),new LayoutParams(-1,-2));divider(cell);return cell;}
    private void showMain(){
        Object restoreTag=previousFocus==null?null:previousFocus.getTag();removeAllViews();toggles.clear();information=null;
        LinearLayout actions=new LinearLayout(getContext());actions.setGravity(Gravity.CENTER_VERTICAL);actions.setClipChildren(false);
        TextView title=MenuFoundation.text(getContext(),"Backup & Restore",22);actions.addView(title,new LayoutParams(0,-2,1));
        createButton=MenuFoundation.control(getContext(),"Create Backup","save",()->create.accept(selection()));createButton.setTag("backup.create");createButton.setTextSize(13*TvLayout.scale(getContext()));createButton.setPadding(dp(8),dp(4),dp(8),dp(4));actions.addView(createButton,new LayoutParams(-2,dp(44)));
        restoreButton=MenuFoundation.control(getContext(),"Restore Backup","history",restore);restoreButton.setTag("backup.restore");restoreButton.setTextSize(13*TvLayout.scale(getContext()));restoreButton.setPadding(dp(8),dp(4),dp(8),dp(4));LayoutParams restoreSize=new LayoutParams(-2,dp(44));restoreSize.leftMargin=dp(8);actions.addView(restoreButton,restoreSize);addView(actions,new LayoutParams(-1,dp(44)));
        for(TextView button:new TextView[]{createButton,restoreButton})button.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_DOWN){focus(ordered.get(v==createButton?0:COLUMNS-1));return true;}if(key==KeyEvent.KEYCODE_DPAD_UP)return true;if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(v==createButton)sidebar.run();else createButton.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT){restoreButton.requestFocus();return true;}return false;});
        LinearLayout options=new LinearLayout(getContext());options.setTag("backup.options");options.setOrientation(VERTICAL);options.setGravity(Gravity.CENTER_VERTICAL);options.setClipChildren(false);options.setClipToPadding(false);options.setPadding(dp(8),dp(8),dp(8),dp(20));options.setBackground(MenuFoundation.surface(getContext(),false));
        for(int index=0;index<ordered.size()-1;index+=COLUMNS){LinearLayout row=new LinearLayout(getContext());row.setClipChildren(false);row.setClipToPadding(false);
            for(int column=0;column<COLUMNS;column++){int position=index+column;View cell=position<ordered.size()-1?cell(ordered.get(position)):new View(getContext());LayoutParams size=new LayoutParams(0,-2,1);if(column>0)size.leftMargin=dp(4);row.addView(cell,size);}
            LayoutParams size=new LayoutParams(-1,-2);if(index>0)size.topMargin=dp(4);options.addView(row,size);
        }
        LayoutParams locationsSize=new LayoutParams(-1,-2);locationsSize.topMargin=dp(4);options.addView(cell(BackupCategory.LOCATIONS),locationsSize);
        LayoutParams optionsSize=new LayoutParams(-1,0,1);optionsSize.topMargin=dp(8);optionsSize.bottomMargin=dp(8);addView(options,optionsSize);
        LinearLayout important=new LinearLayout(getContext());important.setTag("backup.important");important.setGravity(Gravity.BOTTOM);important.setClipChildren(false);important.setPadding(dp(8),dp(8),dp(8),dp(8));important.setBackground(MenuFoundation.surface(getContext(),false));
        LinearLayout copy=new LinearLayout(getContext());copy.setOrientation(VERTICAL);copy.addView(MenuFoundation.text(getContext(),"Important Information",15));TextView warning=MenuFoundation.text(getContext(),"No media, artwork or credentials.\nHistory can reveal viewing interests.",12);warning.setTextColor(MenuFoundation.SECONDARY);copy.addView(warning);important.addView(copy,new LayoutParams(0,-2,1));
        detailsButton=MenuFoundation.control(getContext(),"See full details","info",()->showInformation(false));detailsButton.setTag("backup.details");detailsButton.setTextSize(12*TvLayout.scale(getContext()));detailsButton.setPadding(dp(6),dp(3),dp(6),dp(3));LayoutParams detailsSize=new LayoutParams(-2,dp(44));detailsSize.leftMargin=dp(8);important.addView(detailsButton,detailsSize);
        instructionsButton=MenuFoundation.control(getContext(),"Restore instructions","list",()->showInformation(true));instructionsButton.setTag("backup.instructions");instructionsButton.setTextSize(12*TvLayout.scale(getContext()));instructionsButton.setPadding(dp(6),dp(3),dp(6),dp(3));LayoutParams instructionsSize=new LayoutParams(-2,dp(44));instructionsSize.leftMargin=dp(8);important.addView(instructionsButton,instructionsSize);
        for(TextView button:new TextView[]{detailsButton,instructionsButton})button.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;if(key==KeyEvent.KEYCODE_DPAD_UP){focus(BackupCategory.LOCATIONS);return true;}if(key==KeyEvent.KEYCODE_DPAD_DOWN)return true;if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(v==detailsButton)sidebar.run();else detailsButton.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT){instructionsButton.requestFocus();return true;}return false;});
        addView(important,new LayoutParams(-1,dp(76)));
        if(restoreTag!=null){View target=findViewWithTag(restoreTag);if(target!=null)target.requestFocus();}previousFocus=null;
    }
    private void showInformation(boolean instructionsPage){
        previousFocus=findFocus();removeAllViews();information=instructionsPage?new ScrollView(getContext()):new FrameLayout(getContext());information.setBackground(MenuFoundation.surface(getContext(),false));information.setTag(instructionsPage?"backup.instructions.page":"backup.details.page");if(information instanceof ScrollView)((ScrollView)information).setFillViewport(true);information.setFocusable(true);information.setFocusableInTouchMode(true);information.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        LinearLayout content=new LinearLayout(getContext());content.setOrientation(VERTICAL);content.setPadding(dp(16),dp(14),dp(16),dp(20));TextView title=MenuFoundation.text(getContext(),instructionsPage?"Restore Instructions":"Backup & Restore Details",23);content.addView(title);
        for(Paragraph paragraph:instructionsPage?instructions:details){TextView heading=MenuFoundation.text(getContext(),paragraph.heading,16);LayoutParams headingSize=new LayoutParams(-1,-2);headingSize.topMargin=dp(12);content.addView(heading,headingSize);TextView copy=MenuFoundation.text(getContext(),paragraph.text,14);copy.setTextColor(MenuFoundation.SECONDARY);content.addView(copy,new LayoutParams(-1,-2));}
        information.addView(content,new FrameLayout.LayoutParams(-1,-2));addView(information,new LayoutParams(-1,-1));information.requestFocus();
    }
    public boolean back(){if(information!=null){showMain();return true;}return false;}
    @Override public boolean dispatchKeyEvent(KeyEvent event){if(event.getAction()==KeyEvent.ACTION_DOWN){int key=event.getKeyCode();if(key==KeyEvent.KEYCODE_BACK&&back())return true;if(information!=null){if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){if(information instanceof ScrollView)information.scrollBy(0,dp(key==KeyEvent.KEYCODE_DPAD_UP?-60:60));return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT||key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER)return true;}}return super.dispatchKeyEvent(event);}
    private void privacy(String title,String message,String accept,Runnable confirm){
        View opener=findFocus();android.app.Dialog dialog=new android.app.Dialog(getContext());MenuFoundation.WindowPanel panel=new MenuFoundation.WindowPanel(getContext());panel.setOrientation(VERTICAL);panel.setPadding(dp(18),dp(18),dp(18),dp(18));panel.setBackground(MenuFoundation.surface(getContext(),false));panel.addView(MenuFoundation.text(getContext(),title,22));TextView copy=MenuFoundation.text(getContext(),message,16);LayoutParams copySize=new LayoutParams(-1,-2);copySize.topMargin=dp(12);copySize.bottomMargin=dp(16);panel.addView(copy,copySize);
        LinearLayout actions=new LinearLayout(getContext());actions.setClipChildren(false);TextView cancel=MenuFoundation.control(getContext(),"Cancel","cancel",dialog::dismiss),positive=MenuFoundation.control(getContext(),accept,"checkbox checked",()->{confirm.run();dialog.dismiss();});actions.addView(cancel,new LayoutParams(0,dp(44),1));LayoutParams positiveSize=new LayoutParams(0,dp(44),1);positiveSize.leftMargin=dp(12);actions.addView(positive,positiveSize);panel.addView(actions);dialog.setContentView(panel);android.view.Window window=dialog.getWindow();if(window!=null){window.setBackgroundDrawableResource(android.R.color.transparent);window.setLayout(Math.min(TvLayout.safeWidth(getContext()),dp(600)),-2);}dialog.setOnDismissListener(d->{if(opener!=null&&opener.isAttachedToWindow())opener.requestFocus();});dialog.show();if(window!=null)window.setLayout(Math.min(TvLayout.safeWidth(getContext()),dp(600)),-2);cancel.requestFocus();

    }
}
