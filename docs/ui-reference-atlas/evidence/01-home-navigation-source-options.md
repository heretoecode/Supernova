# 01-home-navigation — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-d83b3e16583c · HomePlaceholder.onBindViewHolder

[Source line 14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/HomePlaceholder.java#L14)

```java
public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){holder.itemView.setTag("home:placeholder:"+position);holder.itemView.setContentDescription("Library placeholder. Add media folders to see details.");holder.itemView.setOnClickListener(v->explain.run());}
```

## SITE-8a0d97181c3f · MainFragment.onResume

[Source line 613](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/MainFragment.java#L613)

```java
intentFilter.addAction(ExtStorageReceiver.ACTION_MEDIA_UNMOUNTED);
```

## SITE-bf972a2ae21a · MainFragment.onResume

[Source line 614](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/MainFragment.java#L614)

```java
intentFilter.addAction(ExtStorageReceiver.ACTION_MEDIA_CHANGED);
```

## SITE-0a6b1ec964d1 · PreviewFeaturedCard.class scope

[Source line 28](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java#L28)

```java
TextView title=text(entry==null?"":PreviewPages.displayName(entry),30);title.setTag("semantic:featured.title");title.setMaxLines(2);title.setTypeface(null,android.graphics.Typeface.BOLD);copy.addView(title,new LinearLayout.LayoutParams(-1,dp(86)));if(entry!=null)OfficialTitleArtwork.bindStable(title,entry.media);else title.setForeground(new HomePlaceholder.Skeleton());
```

## SITE-bb5f814d4129 · PreviewFeaturedCard.class scope

[Source line 29](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java#L29)

```java
TextView metadata=text("",18);metadata.setMaxLines(2);metadata.setTag("semantic:featured.metadata");metadata.setPadding(0,dp(4),0,dp(4));LinearLayout.LayoutParams metadataSize=new LinearLayout.LayoutParams(-1,-2);metadataSize.topMargin=dp(18);copy.addView(metadata,metadataSize);
```

## SITE-db8d308e4e5e · PreviewFeaturedCard.class scope

[Source line 35](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java#L35)

```java
TextView synopsis=text(plot,16);synopsis.setSingleLine(true);if(plot.trim().isEmpty())synopsis.setVisibility(GONE);if(tagline!=null&&!tagline.trim().isEmpty())synopsis.setTypeface(android.graphics.Typeface.create("casual",android.graphics.Typeface.NORMAL));synopsis.setTag("semantic:featured.synopsis");LinearLayout.LayoutParams synopsisSize=new LinearLayout.LayoutParams(-1,dp(26));synopsisSize.topMargin=dp(12);copy.addView(synopsis,synopsisSize);copy.addView(new View(c),new LinearLayout.LayoutParams(1,0,1));
```

## SITE-b031b9697143 · PreviewFeaturedCard.class scope

[Source line 37](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java#L37)

```java
TextView more=action("More Info",info);more.setTag("hero:info");actions.addView(more,new LinearLayout.LayoutParams(-2,-1));
```

## SITE-b7cda30f9076 · PreviewFeaturedCard.text

[Source line 56](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java#L56)

```java
private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextSize(size);t.setTextColor(Color.WHITE);t.setIncludeFontPadding(false);t.setShadowLayer(dp(3),0,dp(1),Color.BLACK);t.setEllipsize(android.text.TextUtils.TruncateAt.END);return t;}
```

## SITE-e4a4c792b399 · PreviewFeaturedCard.action

[Source line 57](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java#L57)

```java
private TextView action(String label,Runnable run){return TvButton.create(getContext(),label,run,false);}
```

## SITE-bf4bb8c42e8c · PreviewHomeRows.class scope

[Source line 12](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L12)

```java
public static final String[] SYSTEM={"continue","watchnext","recent","trending","popular","watched","similar"};
```

## SITE-77c8ecec388a · PreviewHomeRows.class scope

[Source line 13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L13)

```java
private static final String[] LABELS={"Continue Watching","Watch Next","Recently Added","Trending on Trakt · In Your Library","Popular on Trakt · In Your Library","Recently Watched","Because You Watched"};
```

## SITE-1a95dc155fb8 · PreviewHomeRows.membership

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L61)

```java
for (Row row : choices) { if (row.members.contains(entry.key())) checks.add(labels.size()); labels.add(row.name); }
```

## SITE-3a5db468b6c8 · PreviewHomeRows.membership

[Source line 62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L62)

```java
labels.add("Create New Row…");int selected=-1;
```

## SITE-f391885a2e57 · PreviewHomeRows.membership

[Source line 65](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L65)

```java
menu[0] = PreviewDialog.choose(context,"Add to Row",labels.toArray(new String[0]),selected,checks,false,n -> {
```

## SITE-0f3485cbe788 · PreviewHomeRows.clearWatchNext

[Source line 83](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L83)

```java
public static void clearWatchNext(Context c){PreviewDialog.choose(c,"Remove all titles from Watch Next?",new String[]{"Cancel","Clear"},0,n->{if(n==1){PreviewHomeRows model=new PreviewHomeRows(c);for(Row row:model.rows)if(row.id.equals("watchnext"))row.members.clear();model.save();PreviewNotice.show(c,"Watch Next cleared",false);}});}
```

## SITE-8593f08fc5c6 · PreviewHomeRows.rowControls

[Source line 89](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L89)

```java
PreviewDialog.choose(c,row.name,new String[]{"Move","Hide"},0,choice->{
```

## SITE-c1865978fd2e · PreviewHomeRows.rowControls

[Source line 92](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L92)

```java
PreviewDialog.choose(c,"Move row to",positions,model.rows.indexOf(row),position->{model.rows.remove(row);model.rows.add(Math.min(position,model.rows.size()),row);model.save();changed.run();});
```

## SITE-99f1695ed694 · PreviewHomeRows.editor

[Source line 95](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L95)

```java
private void editor(Runnable changed){Dialog dialog=PreviewDialog.create(context);LinearLayout screen=new LinearLayout(context);screen.setOrientation(LinearLayout.VERTICAL);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);screen.setPadding(dp(16),dp(12),dp(16),dp(12));screen.setBackground(PreviewDialog.surface(context,false));SharedThreePanel.heading(screen,"Customise Home");TextView help=label("Select to edit · Up/Down to reorder",13);help.setPadding(0,dp(8),0,dp(16));screen.addView(help);ScrollView scroll=new ScrollView(context);LinearLayout list=new LinearLayout(context);list.setOrientation(LinearLayout.VERTICAL);scroll.addView(list);screen.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));final String[] moving={null};final Runnable[] render={null};render[0]=()->{list.removeAllViews();for(Row row:rows){LinearLayout line=new LinearLayout(context);TextView move=control((row.id.equals(moving[0])?"↕  ":"")+row.name,()->{moving[0]=row.id.equals(moving[0])?null:row.id;save();render[0].run();View target=list.findViewWithTag(row.id);if(target!=null)target.requestFocus();});move.setTag(row.id);move.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN||!row.id.equals(moving[0])||key!=KeyEvent.KEYCODE_DPAD_UP&&key!=KeyEvent.KEYCODE_DPAD_DOWN)return false;int index=rows.indexOf(row),next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=0&&next<rows.size()){Collections.swap(rows,index,next);save();render[0].run();list.findViewWithTag(row.id).requestFocus();}return true;});line.addView(move,new LinearLayout.LayoutParams(0,dp(36),1));TextView visible=control(row.visible?"Shown":"Hidden",()->{row.visible=!row.visible;save();TextView control=list.findViewWithTag(row.id+":visibility");if(control!=null)control.setText(row.visible?"Shown":"Hidden");});visible.setTag(row.id+":visibility");line.addView(visible,new LinearLayout.LayoutParams(dp(95),dp(36)));if(row.custom()){TextView rename=control("Rename",()->name("Rename Row",row.name,value->{row.name=value;save();render[0].run();}));TextView genres=control("Genre Rule",()->rule(row,()->{save();render[0].run();}));TextView delete=control("Delete",()->PreviewDialog.confirmDelete(context,"Delete Row?","Titles remain in your library. Only this Home row is removed.",()->{rows.remove(row);save();render[0].run();}));for(TextView button:new TextView[]{rename,genres,delete}){LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(dp(88),dp(36));size.leftMargin=dp(6);line.addView(button,size);}}LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(3);list.addView(line,lp);}list.addView(control("Create New Row",()->name("Create New Row","",name->{Row row=new Row("custom:"+UUID.randomUUID(),name);row.dynamic=true;rows.add(row);save();rule(row,()->{save();render[0].run();});render[0].run();})),0,new LinearLayout.LayoutParams(-1,dp(36)));};render[0].run();dialog.setContentView(screen);dialog.setOnDismissListener(d->changed.run());dialog.show();dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);dialog.getWindow().setDimAmount(.4f);dialog.getWindow().setLayout(Math.min(dp(700),context.getResources().getDisplayMetrics().widthPixels-dp(60)),Math.min(dp(Math.min(350,100+39*(rows.size()+1))),context.getResources().getDisplayMetrics().heightPixels-dp(100)));if(!rows.isEmpty())list.findViewWithTag(rows.get(0).id).requestFocus();}
```

## SITE-9bc47c4ac87a · PreviewHomeRows.rule

[Source line 98](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L98)

```java
java.util.function.IntFunction<String> label = n -> new String[]{
```

## SITE-2e8a977e40b5 · PreviewHomeRows.rule

[Source line 105](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L105)

```java
menu[0]=PreviewDialog.choose(context,row.name,labels,-1,Collections.emptySet(),false,n->{
```

## SITE-bf08bf9d098f · PreviewHomeRows.rule

[Source line 111](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L111)

```java
if(n==4){PreviewDialog.choose(context,"Row sort",new String[]{"Date Added","Title","Year"},Arrays.asList("added","title","year").indexOf(row.sort),i->{row.sort=new String[]{"added","title","year"}[i];row.descending=!row.sort.equals("title");refresh.run();});return;}
```

## SITE-292ceec1a96a · PreviewHomeRows.rule

[Source line 112](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L112)

```java
if(n==5){int[] limits={0,10,20,30,40,50,75,100};String[] options={"No Limit","10","20","30","40","50","75","100","Select…"};int selected=-1;for(int i=0;i<limits.length;i++)if(limits[i]==row.maximum)selected=i;PreviewDialog.choose(context,"Maximum items",options,selected,i->{
```

## SITE-1b0a6a425ad9 · PreviewHomeRows.label

[Source line 126](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L126)

```java
private TextView label(String text,int size){TextView v=new TextView(context);v.setText(text);v.setTextSize(size);v.setTextColor(0xffd6e5ef);return v;}
```

## SITE-e51cc69ee5aa · PreviewHomeRows.control

[Source line 127](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java#L127)

```java
private TextView control(String text,Runnable action){TextView v=label(text,14);v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(dp(12),0,dp(12),0);v.setFocusable(true);v.setFocusableInTouchMode(true);v.setBackground(PreviewDialog.focus(context));v.setOnClickListener(w->action.run());return v;}
```

## SITE-b2728ea4716d · TopNavigation.getOpacity

[Source line 56](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L56)

```java
brand.setText("SUPERNOVA"); brand.setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL)); brand.setTextSize(19); brand.setTextColor(Color.WHITE);
```

## SITE-3f33859cee21 · TopNavigation.getOpacity

[Source line 60](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L60)

```java
String[] labels = {"Home", "Movies", "TV Shows", "Network & Files", "Settings", "Search", customLabel(c)};
```

## SITE-1a4e00677f04 · TopNavigation.getOpacity

[Source line 64](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L64)

```java
tab.setText(labels[i]); tab.setContentDescription(labels[i]); tab.setTag("semantic:topnav." + new String[]{"home","movies","tv","network","settings","search","custom"}[index]);
```

## SITE-62326d160f76 · TopNavigation.getOpacity

[Source line 94](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L94)

```java
tab.setText("");
```

## SITE-be7b29f15960 · TopNavigation.updateCustomPage

[Source line 186](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java#L186)

```java
public void updateCustomPage(){CustomLibraryPage page=CustomLibraryPage.load(getContext());PreviewIcon.apply(tabs[6],page==null?"plus":"",22);if(page!=null)tabs[6].setCompoundDrawables(null,null,null,null);tabs[6].setText(page==null?"":page.name);tabs[6].setContentDescription(CustomLibraryPage.load(getContext())==null?"Create a New Page":customLabel(getContext()));}
```
