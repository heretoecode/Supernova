package com.archos.mediacenter.video.leanback;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.*;
import android.widget.*;
import java.util.function.IntConsumer;
/** Content-sized Nova menus. The caller still owns every real action and selection. */
public final class PreviewDialog {
 private static final java.util.Map<Context,java.util.List<java.lang.ref.WeakReference<Dialog>>> WINDOWS=new java.util.WeakHashMap<>();
 /** Shared child-dialog lifetime: Back restores the opener in the parent window. */
 public static Dialog create(Context context){
  Context owner=owner(context);View opener=anchor(context);
  java.lang.ref.WeakReference<View> previous=new java.lang.ref.WeakReference<>(opener);
  java.lang.ref.WeakReference<View> previousRoot=new java.lang.ref.WeakReference<>(opener==null?null:opener.getRootView());
  Object semantic=opener==null?null:opener.getTag();int id=opener==null?View.NO_ID:opener.getId();
  return new Dialog(context){
   @Override protected void onStart(){super.onStart();java.util.List<java.lang.ref.WeakReference<Dialog>> windows=WINDOWS.computeIfAbsent(owner,k->new java.util.ArrayList<>());windows.removeIf(reference->reference.get()==null||reference.get()==this);windows.add(new java.lang.ref.WeakReference<>(this));}
   @Override public void dismiss(){
    boolean showing=isShowing();super.dismiss();java.util.List<java.lang.ref.WeakReference<Dialog>> windows=WINDOWS.get(owner);if(windows!=null){windows.removeIf(reference->reference.get()==null||reference.get()==this);if(windows.isEmpty())WINDOWS.remove(owner);}
    if(!showing)return;View root=previousRoot.get(),requested=previous.get(),target=requested;boolean fallback=false;
    if(root==null||!root.isAttachedToWindow())return;
    Dialog parent=top(owner);if(parent!=null&&parent.getWindow()!=null&&parent.getWindow().getDecorView()!=root)return;
    if(target==null||!target.isAttachedToWindow()||!target.isShown()||!target.isFocusable()){
     fallback=true;target=semantic==null?null:root.findViewWithTag(semantic);if(target==null&&id!=View.NO_ID)target=root.findViewById(id);
     if(target==null||!target.isShown()||!target.isFocusable()){target=null;for(View candidate:root.getFocusables(View.FOCUS_FORWARD))if(candidate.isShown()&&candidate.isEnabled()){target=candidate;break;}}
    }
    boolean restored=target!=null&&target.requestFocus();com.archos.mediacenter.video.diagnostics.Diagnostics.focusRestored(requested,target,fallback,restored);
   }
  };
 }
 private static Context owner(Context context){while(context instanceof android.content.ContextWrapper&&!(context instanceof android.app.Activity)){Context base=((android.content.ContextWrapper)context).getBaseContext();if(base==context)break;context=base;}return context;}
 private static Dialog top(Context context){java.util.List<java.lang.ref.WeakReference<Dialog>> windows=WINDOWS.get(owner(context));if(windows!=null)for(int i=windows.size()-1;i>=0;i--){Dialog dialog=windows.get(i).get();if(dialog!=null&&dialog.isShowing())return dialog;}return null;}
 /** Presentation only for retained credential/artwork dialogs; original listeners and inputs remain. */
 public static void styleNative(Dialog dialog){
  Context c=dialog.getContext();if(!androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean("try_new_ui",false))return;
  Window window=dialog.getWindow();if(window==null)return;window.setBackgroundDrawable(surface(c,false));window.setDimAmount(.35f);
  window.setLayout(Math.min(dp(c,560),c.getResources().getDisplayMetrics().widthPixels-dp(c,64)),WindowManager.LayoutParams.WRAP_CONTENT);
  styleNativeChildren(window.getDecorView(),c);
 }
 private static void styleNativeChildren(View view,Context c){
  if(view instanceof TextView){TextView text=(TextView)view;if(!(view instanceof EditText)){text.setTextColor(0xffd6e5ef);text.setTextSize(Math.min(15,text.getTextSize()/c.getResources().getDisplayMetrics().scaledDensity));}if(view instanceof Button){view.setBackground(focus(c));view.setMinimumHeight(dp(c,36));}}
  if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)styleNativeChildren(((ViewGroup)view).getChildAt(i),c);
 }
 public static Dialog read(Context c,String title,String body){
  return read(c,title,body,null);
 }
 public static Dialog read(Context c,String title,String body,android.graphics.Bitmap referenceImage){
  return readInternal(c,title,body,referenceImage,null,null);
 }
 public static Dialog review(Context c,String title,String body,String confirmation,Runnable accepted){
  return readInternal(c,title,body,null,confirmation,accepted);
 }
 private static Dialog readInternal(Context c,String title,String body,android.graphics.Bitmap referenceImage,String confirmation,Runnable accepted){
  Dialog dialog=create(c);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);LinearLayout panel=new LinearLayout(c);panel.setOrientation(android.widget.LinearLayout.VERTICAL);panel.setPadding(dp(c,20),dp(c,16),dp(c,20),dp(c,16));panel.setBackground(surface(c,false));
  TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(20);heading.setTextColor(Color.WHITE);panel.addView(heading);
  ScrollView scroll=new ScrollView(c);TextView text=new TextView(c);text.setText(body);text.setTextColor(0xffd2e1ed);text.setTextSize(13);text.setPadding(0,dp(c,14),0,dp(c,14));View content=text;
  if(referenceImage!=null){LinearLayout reference=new LinearLayout(c);reference.setGravity(Gravity.CENTER_VERTICAL);reference.addView(text,new LinearLayout.LayoutParams(0,-2,1));ImageView image=new ImageView(c);image.setImageBitmap(referenceImage);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setContentDescription("QR containing only report reference, category and timestamp");LinearLayout.LayoutParams picture=new LinearLayout.LayoutParams(dp(c,180),dp(c,180));picture.setMargins(dp(c,16),dp(c,12),0,dp(c,12));reference.addView(image,picture);content=reference;}
  scroll.addView(content);scroll.setFocusable(true);panel.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  LinearLayout actions=new LinearLayout(c);String[] actionLabels=confirmation==null?new String[]{"Close","Copy"}:new String[]{"Back",confirmation};
  for(int index=0;index<actionLabels.length;index++){final int action=index;String label=actionLabels[index];TextView button=new TextView(c);button.setText(label);button.setTextSize(13);button.setTextColor(Color.WHITE);button.setGravity(Gravity.CENTER);button.setFocusable(true);button.setBackground(focus(c));button.setOnClickListener(v->{if(action==0)dialog.dismiss();else if(accepted!=null){if(dialog.isShowing()){dialog.dismiss();accepted.run();}}else{android.content.ClipboardManager clipboard=(android.content.ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);if(clipboard!=null)clipboard.setPrimaryClip(android.content.ClipData.newPlainText(title,body));Toast.makeText(c,"Copied",Toast.LENGTH_SHORT).show();}});actions.addView(button,new LinearLayout.LayoutParams(dp(c,confirmation==null?90:180),dp(c,38)));}panel.addView(actions);
  dialog.setContentView(panel);dialog.show();Window w=dialog.getWindow();w.setBackgroundDrawableResource(android.R.color.transparent);w.setDimAmount(.35f);
  int width=Math.min(dp(c,720),c.getResources().getDisplayMetrics().widthPixels-dp(c,56)),maximum=Math.min(dp(c,360),c.getResources().getDisplayMetrics().heightPixels-dp(c,64));int inner=View.MeasureSpec.makeMeasureSpec(width-dp(c,40),View.MeasureSpec.EXACTLY),natural=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);heading.measure(inner,natural);content.measure(inner,natural);actions.measure(inner,natural);int chrome=dp(c,32)+heading.getMeasuredHeight()+actions.getMeasuredHeight(),contentHeight=Math.min(content.getMeasuredHeight(),Math.max(dp(c,40),maximum-chrome));scroll.setLayoutParams(new LinearLayout.LayoutParams(-1,contentHeight));w.setLayout(width,chrome+contentHeight);scroll.requestFocus();return dialog;
 }
 public static Dialog choose(Context c,String title,String[] labels,int selected,IntConsumer action){
  return choose(c,title,labels,selected,selected<0?java.util.Collections.emptySet():java.util.Collections.singleton(selected),action);
 }
 public static Dialog choose(Context c,String title,String[] labels,int selected,java.util.Set<Integer> checked,IntConsumer action){
  return choose(c,title,labels,selected,checked,true,action);
 }
 public static Dialog choose(Context c,String title,String[] labels,int selected,java.util.Set<Integer> checked,boolean dismissOnSelect,IntConsumer action){
  View anchor=anchor(c);
  Dialog d=create(c);d.requestWindowFeature(Window.FEATURE_NO_TITLE);
  LinearLayout panel=new LinearLayout(c);panel.setOrientation(android.widget.LinearLayout.VERTICAL);int pad=dp(c,12);panel.setPadding(pad,pad,Math.round(pad*1.2f),pad);panel.setBackground(menuSurface(c));panel.setClipChildren(false);panel.setClipToPadding(false);
  TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(17);heading.setTextColor(Color.WHITE);heading.setPadding(dp(c,6),dp(c,2),0,dp(c,12));panel.addView(heading);View divider=new View(c);divider.setBackgroundColor(0x50426a80);panel.addView(divider,new LinearLayout.LayoutParams(-1,dp(c,1)));
  ScrollView scroll=new ScrollView(c);scroll.setVerticalScrollBarEnabled(false);LinearLayout rows=new LinearLayout(c);rows.setOrientation(android.widget.LinearLayout.VERTICAL);scroll.addView(rows);panel.addView(scroll,new LinearLayout.LayoutParams(-1,-2));
  View initial=null;int height=60;int footerHeight=0;
  for(int i=0;i<labels.length;i++){final int index=i;boolean group=labels[i].startsWith("— ");boolean enabled=!group&&!labels[i].endsWith(" (unavailable)")&&!labels[i].endsWith(" — unavailable")&&!labels[i].contains("Coming soon");
   LinearLayout row=new LinearLayout(c);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(c,10),0,dp(c,10),0);row.setBackground(focus(c));row.setFocusable(enabled);row.setFocusableInTouchMode(enabled);row.setEnabled(enabled);row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);row.setAlpha(enabled?1f:group?1f:.4f);
   if(!group){ImageView icon=new ImageView(c);String iconLabel=title.equals(c.getString(com.archos.mediacenter.video.R.string.menu_audio))?"Audio":title.equals(c.getString(com.archos.mediacenter.video.R.string.menu_subtitles))?"Subtitles":labels[i];icon.setImageDrawable(PreviewGenres.known(iconLabel)?new PreviewGenres.Icon(iconLabel):new PreviewIcon(iconLabel));row.addView(icon,new LinearLayout.LayoutParams(dp(c,18),dp(c,18)));}
   TextView label=new TextView(c);label.setText(group?labels[i].substring(2):labels[i]);label.setTag("preview-label:"+i);label.setTextSize(group?11:14);label.setTextColor(group?0xff8aaec5:Color.WHITE);label.setSingleLine(true);label.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.leftMargin=group?0:dp(c,10);row.addView(label,lp);
   if(!group){ImageView check=new ImageView(c);check.setTag("preview-check:"+i);check.setImageDrawable(new PreviewIcon("check"));check.setVisibility(checked.contains(i)?View.VISIBLE:View.INVISIBLE);row.addView(check,new LinearLayout.LayoutParams(dp(c,18),dp(c,18)));}
   row.setTag(i);row.setContentDescription(labels[i]+(checked.contains(i)?", selected":""));row.setOnClickListener(v->{if(dismissOnSelect)d.dismiss();action.accept(index);});int rh=group?25:37;height+=rh;if(i==labels.length-1&&labels[i].equals("Done")){panel.addView(row,new LinearLayout.LayoutParams(-1,dp(c,rh)));footerHeight=dp(c,rh);}else rows.addView(row,new LinearLayout.LayoutParams(-1,dp(c,rh)));if(enabled&&(initial==null||i==selected))initial=row;
  }
  d.setContentView(panel);d.show();Window w=d.getWindow();w.setBackgroundDrawableResource(android.R.color.transparent);w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);w.setDimAmount(.32f);int width=dp(c,240);android.graphics.Paint measure=new android.graphics.Paint();measure.setTextSize(14*c.getResources().getDisplayMetrics().scaledDensity);for(String label:labels)width=Math.max(width,(int)Math.ceil(measure.measureText(label))+dp(c,88));width=Math.min(width,Math.min(dp(c,520),c.getResources().getDisplayMetrics().widthPixels-dp(c,48)));
  heading.measure(View.MeasureSpec.makeMeasureSpec(width-pad*2,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));int chrome=pad*2+heading.getMeasuredHeight()+dp(c,1);
  int maxHeight=c.getResources().getDisplayMetrics().heightPixels-dp(c,48);int menuHeight=Math.min(dp(c,height-60)+chrome,maxHeight);
  if(anchor!=null){int[] pos=new int[2];anchor.getLocationOnScreen(pos);int[] fit=PreviewMenuPlacement.place(width,menuHeight,pos[0],pos[1],pos[0]+anchor.getWidth(),pos[1]+anchor.getHeight(),c.getResources().getDisplayMetrics().widthPixels,c.getResources().getDisplayMetrics().heightPixels,dp(c,24),dp(c,14));WindowManager.LayoutParams params=w.getAttributes();params.gravity=Gravity.TOP|Gravity.LEFT;params.x=fit[0];params.y=fit[1];w.setAttributes(params);width=fit[2];menuHeight=fit[3];}
  scroll.setLayoutParams(new LinearLayout.LayoutParams(-1,Math.max(dp(c,30),menuHeight-chrome-footerHeight)));w.setLayout(width,menuHeight);if(initial!=null)initial.requestFocus();return d;
 }
 private static View anchor(Context c){Dialog parent=top(c);if(parent!=null&&parent.getCurrentFocus()!=null)return parent.getCurrentFocus();Context owner=owner(c);return owner instanceof android.app.Activity?((android.app.Activity)owner).getCurrentFocus():null;}
 public static android.graphics.drawable.Drawable menuSurface(Context c){return surface(c,false);}
 /** Membership states use a plain white plus/check rather than a second generic row icon. */
 public static void updateMembership(Dialog dialog,java.util.Set<Integer> checked,int count){
  if(dialog==null||dialog.getWindow()==null)return;View root=dialog.getWindow().getDecorView();
  for(int i=0;i<count;i++){
   ImageView mark=root.findViewWithTag("preview-check:"+i);if(mark==null)continue;
   android.view.ViewGroup row=(android.view.ViewGroup)mark.getParent();
   if(row.getChildCount()>0&&row.getChildAt(0) instanceof ImageView)row.getChildAt(0).setVisibility(View.GONE);
   mark.setImageDrawable(new PreviewIcon(checked.contains(i)?"check":"plus"));mark.setColorFilter(Color.WHITE);mark.setVisibility(View.VISIBLE);
   TextView label=root.findViewWithTag("preview-label:"+i);row.setContentDescription(label.getText()+(checked.contains(i)?", selected":", not selected"));
  }
 }
 public static void updateChecks(Dialog dialog,java.util.Set<Integer> checked){if(dialog==null||dialog.getWindow()==null)return;updateChecks(dialog.getWindow().getDecorView(),checked);}
 private static void updateChecks(View view,java.util.Set<Integer> checked){Object tag=view.getTag();if(tag instanceof String&&((String)tag).startsWith("preview-check:")){int index=Integer.parseInt(((String)tag).substring(14));view.setVisibility(checked.contains(index)?View.VISIBLE:View.INVISIBLE);View parent=(View)view.getParent();CharSequence description=parent.getContentDescription();if(description!=null)parent.setContentDescription(description.toString().replace(", selected","")+(checked.contains(index)?", selected":""));}if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)updateChecks(((ViewGroup)view).getChildAt(i),checked);}
 public static void updateLabel(Dialog dialog,int index,String label){
  if(dialog==null||dialog.getWindow()==null)return;
  View view=dialog.getWindow().getDecorView().findViewWithTag("preview-label:"+index);
  if(view instanceof TextView){((TextView)view).setText(label);((View)view.getParent()).setContentDescription(label);}
 }
 public static Dialog confirmDelete(Context c,String title,String message,Runnable action){android.app.AlertDialog dialog=new android.app.AlertDialog.Builder(c).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->action.run()).create();dialog.setOnShowListener(d->{styleNative(dialog);dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(0xffffa5a5);dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).requestFocus();});dialog.show();return dialog;}
 public static int dp(Context c,int v){return Math.round(v*c.getResources().getDisplayMetrics().density);}
 public static StateListDrawable focus(Context c){return new PreviewContentFocus(c);}
 public static StateListDrawable buttonFocus(Context c){return focus(c);}
 public static GradientDrawable surface(Context c,boolean f){GradientDrawable g=new GradientDrawable();g.setColor(f?PreviewAccent.alpha(c,70):0xef0b1b29);g.setCornerRadius(dp(c,6));g.setStroke(dp(c,1),f?PreviewAccent.color(c):0x50426a80);return g;}
}
