# 09-shared-focus — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-00396d084a4e · PreviewDialog.readInternal

[Source line 93](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L93)

```java
TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(24);heading.setTextColor(Color.WHITE);panel.addView(heading);
```

## SITE-5daf1e08e6b3 · PreviewDialog.readInternal

[Source line 94](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L94)

```java
ScrollView scroll=new ScrollView(c);TextView text=new TextView(c);text.setText(body);text.setTextColor(0xffd2e1ed);text.setTextSize(16);text.setPadding(0,dp(c,14),0,dp(c,14));View content=text;
```

## SITE-22dcb94dec96 · PreviewDialog.readInternal

[Source line 95](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L95)

```java
if(referenceImage!=null){LinearLayout reference=new LinearLayout(c);reference.setGravity(Gravity.CENTER_VERTICAL);reference.addView(text,new LinearLayout.LayoutParams(0,-2,1));ImageView image=new ImageView(c);image.setImageBitmap(referenceImage);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setContentDescription("QR containing only report reference, category and timestamp");LinearLayout.LayoutParams picture=new LinearLayout.LayoutParams(dp(c,180),dp(c,180));picture.setMargins(dp(c,16),dp(c,12),0,dp(c,12));reference.addView(image,picture);content=reference;}
```

## SITE-073ac4836f83 · PreviewDialog.readInternal

[Source line 97](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L97)

```java
LinearLayout actions=new LinearLayout(c);String[] actionLabels=confirmation==null?new String[]{"Close","Copy"}:new String[]{"Back",confirmation};
```

## SITE-f8a5d0e4cc7d · PreviewDialog.choose

[Source line 102](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L102)

```java
public static Dialog choose(Context c,String title,String[] labels,int selected,IntConsumer action){
```

## SITE-22e63eb878a8 · PreviewDialog.choose

[Source line 105](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L105)

```java
public static Dialog choose(Context c,String title,String[] labels,int selected,java.util.Set<Integer> checked,IntConsumer action){
```

## SITE-535a56f6017a · PreviewDialog.choose

[Source line 108](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L108)

```java
public static Dialog choose(Context c,String title,String[] labels,int selected,java.util.Set<Integer> checked,boolean dismissOnSelect,IntConsumer action){
```

## SITE-a04a6ebf086c · PreviewDialog.choose

[Source line 112](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L112)

```java
TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(17);heading.setTextColor(Color.WHITE);heading.setPadding(dp(c,6),dp(c,2),0,dp(c,12));panel.addView(heading);View divider=new View(c);divider.setBackgroundColor(0x50426a80);panel.addView(divider,new LinearLayout.LayoutParams(-1,dp(c,1)));
```

## SITE-e75bf62f8738 · PreviewDialog.dispatchDraw

[Source line 123](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L123)

```java
LinearLayout row=new LinearLayout(c);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(c,10),0,dp(c,10),0);row.setBackground(focus(c));row.setFocusable(enabled);row.setFocusableInTouchMode(enabled);row.setEnabled(enabled);row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);row.setAlpha(enabled?1f:group?1f:.4f);
```

## SITE-c4485fd64f4c · PreviewDialog.dispatchDraw

[Source line 126](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L126)

```java
TextView label=new TextView(c);label.setText(group?labels[i].substring(2):labels[i]);label.setTag("preview-label:"+i);label.setTextSize(group?11:14);label.setTextColor(group?0xff8aaec5:Color.WHITE);label.setSingleLine(true);label.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.leftMargin=group?0:dp(c,10);row.addView(label,lp);
```

## SITE-1d25026b0ad3 · PreviewDialog.dispatchDraw

[Source line 128](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L128)

```java
row.setTag(i);row.setContentDescription(labels[i]+(checked.contains(i)?", selected":""));row.setOnClickListener(v->{if(dismissOnSelect)d.dismiss();action.accept(index);});int rh=group?25:37;height+=rh;if(i==labels.length-1&&labels[i].equals("Done")){View doneDivider=new View(c);doneDivider.setBackgroundColor(0x50426a80);LinearLayout.LayoutParams dividerSize=new LinearLayout.LayoutParams(-1,dp(c,1));dividerSize.topMargin=dp(c,10);dividerSize.bottomMargin=dp(c,8);panel.addView(doneDivider,dividerSize);panel.addView(row,new LinearLayout.LayoutParams(-1,dp(c,rh)));footerHeight=dp(c,rh+19);height+=19;}else rows.addView(row,new LinearLayout.LayoutParams(-1,dp(c,rh)));if(enabled&&(initial==null||i==selected))initial=row;
```

## SITE-d07b47e267f9 · PreviewDialog.updateMembership

[Source line 146](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L146)

```java
TextView label=root.findViewWithTag("preview-label:"+i);row.setContentDescription(label.getText()+(checked.contains(i)?", selected":", not selected"));
```

## SITE-329daa811604 · PreviewDialog.updateChecks

[Source line 150](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L150)

```java
private static void updateChecks(View view,java.util.Set<Integer> checked){Object tag=view.getTag();if(tag instanceof String&&((String)tag).startsWith("preview-check:")){int index=Integer.parseInt(((String)tag).substring(14));view.setVisibility(checked.contains(index)?View.VISIBLE:View.INVISIBLE);View parent=(View)view.getParent();CharSequence description=parent.getContentDescription();if(description!=null)parent.setContentDescription(description.toString().replace(", selected","")+(checked.contains(index)?", selected":""));}if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)updateChecks(((ViewGroup)view).getChildAt(i),checked);}
```

## SITE-5448825ae8ae · PreviewDialog.updateLabel

[Source line 154](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L154)

```java
if(view instanceof TextView){((TextView)view).setText(label);((View)view.getParent()).setContentDescription(label);}
```

## SITE-96775387320e · PreviewDialog.confirmPageDelete

[Source line 157](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L157)

```java
Dialog dialog=message(c,title,message,false,new String[]{"Cancel","Delete"},0,index->{if(index==1)action.run();});
```

## SITE-6b279a7eeed9 · PreviewDialog.confirmDelete

[Source line 160](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L160)

```java
public static Dialog confirmDelete(Context c,String title,String message,Runnable action){android.app.AlertDialog dialog=new android.app.AlertDialog.Builder(c).setTitle(title).setMessage(message).setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->action.run()).create();dialog.setOnShowListener(d->{styleNative(dialog);dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(0xffffa5a5);dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).requestFocus();});dialog.show();return dialog;}
```

## SITE-ee7d59764376 · PreviewDialog.message

[Source line 162](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L162)

```java
public static Dialog message(Context c,String title,String body,boolean welcome,String[] labels,int initial,IntConsumer action){
```

## SITE-c6d8590c9755 · PreviewDialog.message

[Source line 168](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L168)

```java
if(welcome){ImageView icon=new ImageView(c);icon.setImageResource(com.archos.mediacenter.video.R.drawable.foundation_icon);icon.setContentDescription("Supernova");LinearLayout.LayoutParams image=new LinearLayout.LayoutParams(dp(c,56),dp(c,56));image.gravity=Gravity.CENTER;content.addView(icon,image);TextView brand=SharedThreePanel.text(c,"SUPERNOVA",14);brand.setLetterSpacing(.06f);brand.setGravity(Gravity.CENTER);brand.setPadding(0,dp(c,12),0,dp(c,8));content.addView(brand);}
```

## SITE-616f985ef57b · PreviewDialog.message

[Source line 169](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L169)

```java
TextView heading=SharedThreePanel.text(c,title,24);heading.setTypeface(null,android.graphics.Typeface.BOLD);heading.setGravity(Gravity.CENTER);content.addView(heading);
```

## SITE-dc20fe1df7dd · PreviewDialog.message

[Source line 170](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L170)

```java
TextView copy=SharedThreePanel.text(c,body,16);copy.setTextColor(0xffc7d8e5);copy.setGravity(Gravity.CENTER);copy.setPadding(0,dp(c,16),0,dp(c,20));copy.setLineSpacing(dp(c,4),1);content.addView(copy);
```

## SITE-80f02f1a60aa · SharedThreePanel.heading

[Source line 38](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L38)

```java
public static void heading(LinearLayout column,String title) { column.addView(text(column.getContext(),title,21)); divider(column); }
```

## SITE-a7f188be0267 · SharedThreePanel.text

[Source line 62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L62)

```java
public static TextView text(Context context, String value, int size) {
```

## SITE-28a21c88f6ff · SharedThreePanel.text

[Source line 64](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L64)

```java
text.setText(value);
```

## SITE-b30f6bd50969 · SharedThreePanel.action

[Source line 72](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L72)

```java
public static TextView action(Context context, String label, Runnable action) {
```

## SITE-b29ce8630155 · SharedThreePanel.action

[Source line 73](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L73)

```java
TextView row = text(context, label, 18);
```

## SITE-ec3b57e88d46 · SharedThreePanel.accentAction

[Source line 86](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L86)

```java
TextView row=action(context,label,action);row.setBackground(accentFocus(context));return row;
```

## SITE-721952ddaadc · SharedThreePanel.control

[Source line 98](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L98)

```java
public static StateListDrawable control(Context context, int radius) {
```

## SITE-989690550733 · TvButton.create

[Source line 10](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TvButton.java#L10)

```java
public static TextView create(Context c,String label,Runnable action,boolean functional){TextView view=SharedThreePanel.text(c,label,13);style(view,functional);view.setFocusable(true);view.setFocusableInTouchMode(true);view.setClickable(true);view.setOnClickListener(v->action.run());return view;}
```

## SITE-e4fd2a18da12 · TvButton.style

[Source line 11](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TvButton.java#L11)

```java
public static void style(TextView view,boolean functional){Context c=view.getContext();float ratio=functional?1.12f:1f;view.setAllCaps(false);view.setTextSize(13*ratio*TvLayout.scale(c));view.setGravity(Gravity.CENTER);view.setMinimumHeight(TvLayout.dimension(c,44*ratio));int padding=TvLayout.dimension(c,16*ratio);view.setPadding(padding,TvLayout.dimension(c,8*ratio),padding,TvLayout.dimension(c,8*ratio));view.setBackground(SharedThreePanel.control(c,functional?11:10));PreviewIcon.apply(view,view.getText().toString(),Math.round(18*ratio*TvLayout.scale(c)));view.setCompoundDrawablePadding(TvLayout.dimension(c,8*ratio));view.setTextColor(android.graphics.Color.WHITE);view.setSingleLine(true);view.setEllipsize(android.text.TextUtils.TruncateAt.END);}
```
