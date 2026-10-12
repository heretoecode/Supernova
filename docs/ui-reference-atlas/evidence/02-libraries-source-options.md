# 02-libraries — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-f74853233534 · UserCollectionStore.getVisibleCollections

[Source line 31](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java#L31)

```java
String[] projection = { ScraperStore.UserCollections.ID, ScraperStore.UserCollections.NAME };
```

## SITE-31ca7be32288 · UserCollectionStore.contains

[Source line 58](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java#L58)

```java
String[] args = { String.valueOf(mediaType), String.valueOf(mediaId) };
```

## SITE-771dc0b4bc66 · UserCollectionStore.contains

[Source line 62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java#L62)

```java
new String[] { ScraperStore.UserCollectionItems.ID }, selection, args, null)) {
```

## SITE-c4894c83bdb6 · UserCollectionStore.contains

[Source line 73](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java#L73)

```java
String[] args = { String.valueOf(collectionId), String.valueOf(mediaType), String.valueOf(mediaId) };
```

## SITE-f9681888e28d · CustomLibraryActivity.onDestroy

[Source line 26](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L26)

```java
final String[] steps={"Page Details","Content Type","Filters","Display Options","Review"};CustomLibraryPage draft;String original;boolean editing;int step;Snapshot snapshot=new Snapshot();LinearLayout options,libraryBody;LinearLayout footer;boolean choice;TextView preview;RecyclerView grid;PreviewLibraryColumns columns;String restoreFocus="";android.os.Parcelable restoreScroll;String pendingFocus;PreviewPages sharedLibrary;String editorOpener="edit";LinearLayout wizardRail;ScrollView wizardPreview,wizardOptions;View wizardCentreAnchor;
```

## SITE-3e6be96cf44f · CustomLibraryActivity.onDestroy

[Source line 34](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L34)

```java
TextView action(String label,Runnable run){return SharedThreePanel.accentAction(getContext(),label,run);}
```

## SITE-2e1b22bd76e0 · CustomLibraryActivity.onDestroy

[Source line 35](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L35)

```java
void footerAction(String label,Runnable run){TextView button=action(label,run);button.setMinHeight(dp(40));button.setBackground(SharedThreePanel.control(getContext(),10));LayoutParams size=new LayoutParams(-2,dp(40));size.topMargin=dp(6);footer.addView(button,size);}
```

## SITE-029144de75c8 · CustomLibraryActivity.onDestroy

[Source line 36](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L36)

```java
void row(String label,Runnable run){TextView row=action(label,run);row.setTag("wizard.option."+step+"."+options.getChildCount());options.addView(row,new LayoutParams(-1,dp(42)));}
```

## SITE-effff7362187 · CustomLibraryActivity.onDestroy

[Source line 40](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L40)

```java
boolean saved=CustomLibraryPage.load(getContext())!=null;if(saved){CustomLibraryPage edited=draft;draft=CustomLibraryPage.load(getContext());library();draft=edited;libraryBody.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);libraryBody.setAlpha(.35f);if(android.os.Build.VERSION.SDK_INT>=31)libraryBody.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(dp(6),dp(6),android.graphics.Shader.TileMode.CLAMP));TextView label=SharedThreePanel.text(getContext(),"Edit Library",22);label.setGravity(Gravity.CENTER);FrameLayout.LayoutParams titleSize=new FrameLayout.LayoutParams(-1,dp(36),Gravity.TOP);addView(label,titleSize);}
```

## SITE-5798b8ab5791 · CustomLibraryActivity.onDestroy

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L42)

```java
SharedThreePanel panels=new SharedThreePanel(getContext());LinearLayout left=column();options=column();LinearLayout right=column();ScrollView centreScroll=new ScrollView(getContext()),rightScroll=new ScrollView(getContext());wizardOptions=centreScroll;centreScroll.addView(options);rightScroll.addView(right);LinearLayout centre=column();footer=column();SharedThreePanel.heading(centre,steps[step]);centre.addView(centreScroll,new LinearLayout.LayoutParams(-1,0,1));centre.addView(footer,new LinearLayout.LayoutParams(-1,-2));wizardRail=left;wizardPreview=rightScroll;rightScroll.setFocusable(true);rightScroll.setFocusableInTouchMode(true);rightScroll.setForeground(SharedThreePanel.accentFocus(getContext()));panels.panels(left,centre,rightScroll);FrameLayout.LayoutParams panelSize=new FrameLayout.LayoutParams(-1,-1);if(saved){panelSize.topMargin=dp(48);panelSize.bottomMargin=dp(24);panelSize.leftMargin=dp(24);panelSize.rightMargin=dp(24);}else{TextView introduction=SharedThreePanel.text(getContext(),"Create one personalised library page using your Movies and TV Shows. Choose filters and a layout, then save your page.",15);FrameLayout.LayoutParams introSize=new FrameLayout.LayoutParams(-1,dp(48),Gravity.TOP);addView(introduction,introSize);panelSize.topMargin=dp(56);}addView(SharedThreePanel.viewport(panels),panelSize);
```

## SITE-73c639c5eca8 · CustomLibraryActivity.onDestroy

[Source line 43](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L43)

```java
SharedThreePanel.heading(left,"Library Page");for(int n=0;n<steps.length;n++){final int position=n;TextView entry=action((n+1)+". "+steps[n],()->{step=position;pendingFocus="wizard.step."+position;render();});entry.setTag("wizard.step."+n);SharedThreePanel.rowSeparator(entry);entry.setOnFocusChangeListener((v,focused)->{if(focused&&step!=position){step=position;pendingFocus="wizard.step."+position;post(this::render);}});left.addView(entry,new LayoutParams(-1,dp(42)));}
```

## SITE-f82e783bdba3 · CustomLibraryActivity.onDestroy

[Source line 45](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L45)

```java
switch(step){case 0:row("Name: "+draft.name,()->PreviewTextInput.show(getContext(),"Page Name",draft.name,40,value->{draft.name=value;render();}));if(saved)row("Delete Page",()->PreviewDialog.confirmPageDelete(getContext(),"Delete this page?","Your library media, watched state and playback progress are kept.",()->{if(CustomLibraryPage.delete(getContext()))finish();else PreviewDialog.read(getContext(),"Page not deleted","Retry after returning to the page.");}));break;
```

## SITE-f1235e89529d · CustomLibraryActivity.onDestroy

[Source line 46](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L46)

```java
case 1:for(int type=0;type<3;type++){final int selected=type;String value=new String[]{"movies","tv","both"}[type];row((draft.type.equals(value)?"✓  ":"○  ")+new String[]{"Movies","TV Shows","Both"}[type],()->{draft.type=new String[]{"movies","tv","both"}[selected];render();});}break;
```

## SITE-235e5c807459 · CustomLibraryActivity.onDestroy

[Source line 47](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L47)

```java
case 2:row("Genres: "+(draft.genres.isEmpty()?"All":android.text.TextUtils.join(", ",draft.genres)),()->{Set<String> available=new TreeSet<>();for(Entry entry:snapshot.movies)available.addAll(PreviewGenres.parse(entry.genres));for(Entry entry:snapshot.shows)available.addAll(PreviewGenres.parse(entry.genres));PreviewGenres.choose(getContext(),available,draft.genres,chosen->{draft.genres.clear();draft.genres.addAll(chosen);render();});});row("Genre matching: "+(draft.allGenres?"All":"Any"),()->{draft.allGenres=!draft.allGenres;render();});
```

## SITE-b3055ab2ff8e · CustomLibraryActivity.onDestroy

[Source line 48](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L48)

```java
row("Year: "+(draft.year==0?"All":draft.year),()->{TreeSet<Integer> years=new TreeSet<>(Comparator.reverseOrder());for(Entry entry:snapshot.movies)if(entry.year()>0)years.add(entry.year());for(Entry entry:snapshot.shows)if(entry.year()>0)years.add(entry.year());List<Integer> values=new ArrayList<>();values.add(0);values.addAll(years);String[] labels=values.stream().map(value->value==0?"All":String.valueOf(value)).toArray(String[]::new);choose("Year",labels,n->draft.year=values.get(n));});
```

## SITE-1310ba4aaa6f · CustomLibraryActivity.onDestroy

[Source line 49](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L49)

```java
row("Watched state: "+draft.watched,()->choose("Watched state",new String[]{"All","Watched","Unwatched","In Progress"},n->draft.watched=new String[]{"all","watched","unwatched","progress"}[n]));row("Minimum rating: "+(draft.rating==0?"Any":draft.rating),()->choose("Minimum rating",new String[]{"Any","5","6","7","8","9"},n->draft.rating=n==0?0:n+4));
```

## SITE-9477bccaf60c · CustomLibraryActivity.onDestroy

[Source line 50](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L50)

```java
facet("Original Language",draft.language,0,value->draft.language=value);facet("Studio / Network",draft.studio,1,value->draft.studio=value);break;
```

## SITE-302a2013d109 · CustomLibraryActivity.onDestroy

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L51)

```java
case 3:row("View: "+draft.view,()->choose("Display",new String[]{"Grid","List"},n->draft.view=n==0?"grid":"list"));row("Sort: "+draft.sort,()->choose("Sort",new String[]{"Title","Year","Date Added"},n->draft.sort=new String[]{"title","year","added"}[n]));row("Direction: "+(draft.descending?"Descending":"Ascending"),()->{draft.descending=!draft.descending;render();});break;
```

## SITE-b3c7f40ce680 · CustomLibraryActivity.onDestroy

[Source line 54](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L54)

```java
if(step>0)footerAction("Previous",()->{step--;pendingFocus="wizard.step."+step;render();});
```

## SITE-9df6315a4f1d · CustomLibraryActivity.onDestroy

[Source line 55](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L55)

```java
if(step<4)footerAction("Next",()->{step++;pendingFocus="wizard.step."+step;render();});else footerAction("Save Page",this::save);
```

## SITE-b217065f83e8 · CustomLibraryActivity.onDestroy

[Source line 56](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L56)

```java
footerAction(saved?"Close Editor":"Cancel",()->requestExit(saved?this::closeEditor:CustomLibraryActivity.this::finish));
```

## SITE-9d9e2c22073d · CustomLibraryActivity.onDestroy

[Source line 57](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L57)

```java
SharedThreePanel.heading(right,"Live Preview");preview=SharedThreePanel.text(getContext(),"",16);right.addView(preview);preview();
```

## SITE-952bae8eec73 · CustomLibraryActivity.onDestroy

[Source line 60](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L60)

```java
void choose(String title,String[] labels,java.util.function.IntConsumer chosen){choice=true;options.removeAllViews();SharedThreePanel.heading(options,title);for(int n=0;n<labels.length;n++){final int selected=n;row(labels[n],()->{chosen.accept(selected);choice=false;render();});}options.post(()->{if(options.getChildCount()>2)options.getChildAt(2).requestFocus();});}
```

## SITE-340c95cd13c6 · CustomLibraryActivity.onDestroy

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L61)

```java
void facet(String title,String selected,int kind,java.util.function.Consumer<String> changed){
```

## SITE-9abbd1409db1 · CustomLibraryActivity.onDestroy

[Source line 65](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L65)

```java
row(title+": "+(selected.isEmpty()?"All":selected),()->{choose(title,labels,n->changed.accept(choices.get(n)));if(kind==0)for(int n=0;n<choices.size();n++){TextView row=(TextView)options.getChildAt(n+2);PreviewLanguageIcon icon=new PreviewLanguageIcon(choices.get(n));icon.setBounds(0,0,dp(22),dp(22));row.setCompoundDrawables(icon,null,null,null);row.setCompoundDrawablePadding(dp(8));}});
```

## SITE-45664042e8a3 · CustomLibraryActivity.onDestroy

[Source line 67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L67)

```java
void review(LinearLayout target){String[] values={"Name: "+draft.name,"Content: "+(draft.type.equals("tv")?"TV Shows":draft.type.equals("movies")?"Movies":"Both"),"Genres: "+(draft.genres.isEmpty()?"All Genres":android.text.TextUtils.join(", ",draft.genres)),"Genre Match: "+(draft.allGenres?"All":"Any"),"Year: "+(draft.year==0?"All":draft.year),"Watched State: "+draft.watched,"Minimum Rating: "+(draft.rating==0?"Any":draft.rating),"Original Language: "+(draft.language.isEmpty()?"All":new Locale(draft.language).getDisplayLanguage()),"Studio / Network: "+(draft.studio.isEmpty()?"All":draft.studio),"Display: "+draft.view,"Sort: "+draft.sort+" · "+(draft.descending?"Descending":"Ascending")};for(String value:values){target.addView(SharedThreePanel.text(getContext(),value,15));SharedThreePanel.divider(target);}}
```

## SITE-6e242bfe6675 · CustomLibraryActivity.onDestroy

[Source line 68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L68)

```java
void preview(){if(preview==null)return;List<Entry> entries=draft.entries(snapshot);StringBuilder text=new StringBuilder(draft.name).append("\n\n").append(PreviewLibrarySummary.describe(getContext(),snapshot,entries)).append("\n");for(int i=0;i<Math.min(20,entries.size());i++)text.append("\n").append(PreviewPages.displayName(entries.get(i)));preview.setText(text);}
```

## SITE-3c28200e8fd7 · CustomLibraryActivity.onDestroy

[Source line 69](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L69)

```java
void save(){String validation=draft.validation();if(validation!=null){PreviewDialog.read(getContext(),"Review page settings",validation);return;}if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Keep editing and retry.");return;}original=draft.encode();editing=false;pendingFocus="custom.control."+editorOpener;sharedLibrary=null;navigation.updateCustomPage();render();}
```

## SITE-ffd450e6169d · CustomLibraryActivity.requestExit

[Source line 70](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L70)

```java
@Override public void requestExit(Runnable leave){if(!editing||draft.encode().equals(original)){leave.run();return;}PreviewDialog.message(getContext(),"Unsaved Changes","You've made changes to your custom library page that haven't been saved. What would you like to do?",false,new String[]{"Continue Editing","Save Page","Discard Changes"},0,n->{if(n==1)save();else if(n==2){navigate(0);}});}
```

## SITE-1bd0eae59cb2 · CustomLibraryActivity.requestExit

[Source line 74](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L74)

```java
void persistDisplay(){if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Retry after returning to the page.");return;}render();}
```

## SITE-d9c4bdb48eae · CustomLibraryActivity.requestExit

[Source line 75](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java#L75)

```java
void control(LinearLayout controls,String key,String label,Runnable action){TextView button=TvButton.create(getContext(),label,action,true);button.setTag("custom.control."+key);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-2,-2);size.leftMargin=dp(8);controls.addView(button,size);}
```

## SITE-b38e91b7a4d2 · LibraryPageHeader.add

[Source line 13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/LibraryPageHeader.java#L13)

```java
target.setOrientation(LinearLayout.VERTICAL);TextView title=SharedThreePanel.text(target.getContext(),name,30);title.setTypeface(null,Typeface.BOLD);target.addView(title);
```

## SITE-8d69012797f7 · LibraryPageHeader.add

[Source line 14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/LibraryPageHeader.java#L14)

```java
String[] lines=summary.split("\n"),icons={kind,"Total Size","Local Storage","Network"};for(int i=0;i<lines.length;i++){TextView row=SharedThreePanel.text(target.getContext(),lines[i],13);row.setTextColor(0xffe1e9ef);row.setPadding(0,SharedThreePanel.dp(target.getContext(),2),0,SharedThreePanel.dp(target.getContext(),2));if(i<icons.length)PreviewIcon.apply(row,icons[i],17);target.addView(row);}
```

## SITE-f75f43ea8f21 · PreviewGenres.class scope

[Source line 12](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewGenres.java#L12)

```java
public static final String[] NAMES={"Action","Adventure","Animation","Comedy","Crime","Documentary","Drama","Family","Fantasy","History","Horror","Kids","Music","Mystery","News","Reality","Romance","Science Fiction","Soap","Talk","Thriller","TV Movie","War","Western","Action & Adventure","Sci-Fi & Fantasy","War & Politics"};
```

## SITE-fb8861709e7b · PreviewGenres.choose

[Source line 18](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewGenres.java#L18)

```java
List<String> labels=new ArrayList<>();labels.add("All Genres");labels.addAll(names);
```

## SITE-5c7d5970bd6a · PreviewGenres.choose

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewGenres.java#L20)

```java
Dialog[] menu={null};menu[0]=PreviewDialog.choose(c,"Genres · Match Any Selected",labels.toArray(new String[0]),-1,checks,false,n->{
```

## SITE-2c11e4eb3e11 · PreviewLibraryColumns.text

[Source line 69](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L69)

```java
private TextView text(String value,int size){TextView t=new TextView(context);t.setText(value);t.setTextSize(size);t.setTextColor(0xffd5e4ee);t.setGravity(Gravity.CENTER_VERTICAL);t.setPadding(dp(6),0,dp(6),0);t.setSingleLine(true);t.setEllipsize(TextUtils.TruncateAt.END);t.setIncludeFontPadding(false);return t;}
```

## SITE-7914e9714804 · PreviewLibraryColumns.header

[Source line 71](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L71)

```java
for(Column c:visible()){TextView label=text(c.label+(sortColumn==c?(ascending?" ↑":" ↓"):""),11);label.setFocusable(true);label.setTag("column:"+c);label.setContentDescription(c.label+", sort"+(sortColumn==c?(ascending?", ascending":", descending"):""));label.setBackground(PreviewDialog.focus(context));label.setOnClickListener(v->{setSort(c,sortColumn==c?!ascending:c==Column.TITLE||c==Column.YEAR);changed.run();});row.addView(label,new LinearLayout.LayoutParams(0,dp(30),c.width));}
```

## SITE-a8006ffbf09f · PreviewLibraryColumns.bind

[Source line 87](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L87)

```java
TextView name=text(value(entry,c),12);name.setTextColor(Color.WHITE);title.addView(name,new LinearLayout.LayoutParams(0,-1,1));row.addView(title,new LinearLayout.LayoutParams(0,dp(42),c.width));
```

## SITE-bdc28feb7a52 · PreviewLibraryColumns.bind

[Source line 88](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L88)

```java
}else{TextView cell=text(value(entry,c),11);row.addView(cell,new LinearLayout.LayoutParams(0,dp(42),c.width));}
```

## SITE-07e3ba5f15c6 · PreviewLibraryColumns.bind

[Source line 92](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L92)

```java
row.setContentDescription(description.toString());
```

## SITE-22e7df053cf2 · PreviewLibraryColumns.choose

[Source line 96](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L96)

```java
private void choose(Runnable changed,int focus){List<String> labels=new ArrayList<>();Set<Integer> checked=new HashSet<>();for(int i=0;i<order.size();i++){Column c=order.get(i);labels.add(c.label+(c==Column.TITLE?" (always shown)":"")+"   ≡");if(shown.contains(c))checked.add(i);}labels.add("Reset to default");labels.add("Done");final Dialog[] menu={null};
```

## SITE-db8fa5e6e0f3 · PreviewLibraryColumns.choose

[Source line 97](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java#L97)

```java
menu[0]=PreviewDialog.choose(context,"Columns · ◀ ▶ to reorder",labels.toArray(new String[0]),focus,checked,false,n->{
```

## SITE-c3db3a923a79 · PreviewLibraryLoader.getProjection

[Source line 26](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java#L26)

```java
@Override public String[] getProjection() {
```

## SITE-cef651c8d2bc · PreviewLibraryLoader.getProjection

[Source line 27](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java#L27)

```java
return concatTwoStringArrays(mProjection, new String[]{
```

## SITE-771908d8cba5 · PreviewLibraryScan.onReceive

[Source line 47](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryScan.java#L47)

```java
filter.addAction(app.getPackageName()+".LOCAL_IMPORT_LIFECYCLE");
```

## SITE-e5140d8a2892 · PreviewPages.run

[Source line 115](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L115)

```java
private final String[] genres={"","",""};
```

## SITE-4ca3c956fda6 · PreviewPages.run

[Source line 117](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L117)

```java
private final String[] providers={"","",""};
```

## SITE-247ba46ce7b2 · PreviewPages.run

[Source line 118](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L118)

```java
private final String[] selectedYears={"","",""};
```

## SITE-e14c7d41da33 · PreviewPages.featuredCandidates

[Source line 158](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L158)

```java
String[] reasons={"Recently Added","TRENDING ON TRAKT · IN YOUR LIBRARY","POPULAR ON TRAKT · IN YOUR LIBRARY"};
```

## SITE-b875d0ab22b6 · PreviewPages.sortLabels

[Source line 479](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L479)

```java
private String[] sortLabels(){return new String[]{"Date Added","Title",customLibrary!=null?"Release / Air Date":tab==2?"Air Date":"Release Date","Trakt Trending","Trakt Popular"};}
```

## SITE-a6400f7a1074 · PreviewPages.sort

[Source line 485](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L485)

```java
for(PreviewLibraryColumns.Column column:metadata)labels.add(column.label);
```

## SITE-25765bbd8803 · PreviewPages.sort

[Source line 488](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L488)

```java
PreviewDialog.choose(getContext(),"Sort",labels.toArray(new String[0]),selected,n->{
```

## SITE-76cbe5b38710 · PreviewPages.filter

[Source line 496](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L496)

```java
private void filter(){PreviewDialog.choose(getContext(),"Filters",new String[]{"Genre"+(genres[tab].isEmpty()?"":": "+genres[tab]),"Year"+(years[tab]==0?"":": "+years[tab]),"Streaming Service"+(providers[tab].isEmpty()?"":": selected"),"Clear Filters"},-1,n->{
```

## SITE-c60b7a2adb34 · PreviewPages.chooseMany

[Source line 504](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L504)

```java
Set<String> selected=PreviewGenres.parse(saved);List<String> labels=new ArrayList<>(names);labels.add("Clear selection");labels.add("Done");Set<Integer> checks=new HashSet<>();for(int i=0;i<values.size();i++)if(selected.contains(values.get(i)))checks.add(i);
```

## SITE-1b5a4910a60c · PreviewPages.chooseMany

[Source line 505](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L505)

```java
android.app.Dialog[] dialog={null};dialog[0]=PreviewDialog.choose(getContext(),title,labels.toArray(new String[0]),-1,checks,false,n->{if(n==values.size()+1){dialog[0].dismiss();accept.accept(android.text.TextUtils.join("|",selected));return;}if(n==values.size())selected.clear();else if(!selected.add(values.get(n)))selected.remove(values.get(n));checks.clear();for(int i=0;i<values.size();i++)if(selected.contains(values.get(i)))checks.add(i);PreviewDialog.updateChecks(dialog[0],checks);});
```

## SITE-85c9f0cba28b · PreviewPages.text

[Source line 508](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L508)

```java
private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
```

## SITE-32e31b545a74 · PreviewPages.placeholderInfo

[Source line 524](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L524)

```java
private void placeholderInfo(){boolean firstRun=!com.archos.mediaprovider.video.SupernovaLibraryPolicy.configured(getContext())||emptyHome();PreviewDialog.message(getContext(),firstRun?"No Media Details Yet":"No Details for This Placeholder",firstRun?"Your movies and TV shows will appear here once you've added folders to your library.":"No media is attached to this placeholder. Browse your library or add more folders in Network & Files.",false,new String[]{"Network & Files","Continue Browsing"},0,n->{if(n==0)openNetwork();});}
```

## SITE-913f35e3bb4e · PreviewPages.showWelcome

[Source line 525](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L525)

```java
private void showWelcome(){if(tab!=0||!isAttachedToWindow()||preferences.getBoolean("supernova_welcome_dismissed",false)||com.archos.mediaprovider.video.SupernovaLibraryPolicy.configured(getContext()))return;android.app.Dialog dialog=PreviewDialog.message(getContext(),"Welcome to Supernova","Add movies and TV shows from local storage, external drives or network shares. Supernova will only begin scanning once you've selected and confirmed your folders.",true,new String[]{"Network & Files","Explore Supernova"},0,n->{preferences.edit().putBoolean("supernova_welcome_dismissed",true).apply();if(n==0)openNetwork();});dialog.setOnCancelListener(d->preferences.edit().putBoolean("supernova_welcome_dismissed",true).apply());}
```

## SITE-bef05db6c411 · PreviewPages.button

[Source line 527](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L527)

```java
private TextView button(String name,Runnable action){return TvButton.create(getContext(),name,()->{quietOrder.clear();action.run();},false);}
```

## SITE-5dad5f5eb7ba · PreviewPages.onBindViewHolder

[Source line 560](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L560)

```java
else if(c.type==CUSTOMISE){v.setGravity(Gravity.CENTER);TextView custom=button("Customise Home",()->PreviewHomeRows.customise(getContext(),()->{render();}));custom.setTag("home:customise");v.addView(custom);}
```

## SITE-a9ea2b2db7e3 · PreviewPages.run

[Source line 563](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L563)

```java
else if(c.type==SCAN){v.setOrientation(LinearLayout.VERTICAL);TextView scan=button("Scan Library",()->PreviewLibraryScan.request(getContext()));scan.setTag("scan:library");v.addView(scan,new LinearLayout.LayoutParams(dp(158),dp(40)));TextView description=text("Scan local storage and indexed network folders. Full Library Scan in Settings also retries unmatched descriptions.",12);description.setPadding(0,dp(8),0,dp(8));description.setTextColor(0xffaac1d1);v.addView(description);TextView progress=text("",13);progress.setTextColor(PreviewAccent.color(getContext()));progress.setMinHeight(dp(36));v.addView(progress,new LinearLayout.LayoutParams(-1,-2));progress.post(new Runnable(){public void run(){if(!progress.isAttachedToWindow())return;String status=PreviewLibraryScan.libraryStatus(getContext());progress.setText(status);progress.setVisibility(VISIBLE);progress.postDelayed(this,1000);}});}
```

## SITE-a8e466f402f4 · PreviewPages.run

[Source line 564](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L564)

```java
else if(c.type==NOTICE){v.setOrientation(LinearLayout.VERTICAL);TextView title=text(c.title,18);title.setTextColor(0xff8298aa);v.addView(title);TextView status=text((String)c.value,12);status.setTextColor(0xff8298aa);v.addView(status);v.setPadding(0,dp(12),0,dp(12));}
```

## SITE-526183671003 · PreviewPages.run

[Source line 568](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L568)

```java
controls.addView(button("Filters"+(genres[tab].isEmpty()?"":": "+genres[tab])+(years[tab]==0?"":" · "+years[tab])+"  ▾",()->filter()));
```

## SITE-ad39b0effa8d · PreviewPages.run

[Source line 570](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L570)

```java
for(TextView control:new TextView[]{button("Sort: "+(columns[tab].sortColumn!=null?columns[tab].sortColumn.label:sortLabels()[sorts[tab]])+"  ▾",()->sort()),button(order,()->{quietOrder.clear();boolean value=!(columns[tab].sortColumn==null?ascending[tab]:columns[tab].ascending);ascending[tab]=value;columns[tab].setAscending(value);render();}),button(listMode[tab]?"Grid View":"List View",()->{listMode[tab]=!listMode[tab];render();})}){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(8);controls.addView(control,lp);}
```

## SITE-adcd72fca96c · PreviewPages.run

[Source line 571](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L571)

```java
if(listMode[tab]){TextView chooser=button("Columns",()->columns[tab].choose(this::refreshColumns));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(8);controls.addView(chooser,lp);}
```

## SITE-bd99f22ee59c · PreviewPages.run

[Source line 572](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L572)

```java
String[] controlIds={"filters","sort","order","view","columns"};
```

## SITE-9203c34a5983 · PreviewPages.run

[Source line 573](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L573)

```java
for(int i=0;i<controls.getChildCount();i++){View control=controls.getChildAt(i);control.setTag(customLibrary==null?"control:"+i:"custom.control."+controlIds[i]);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(control,"library.toolbar."+controlIds[i]);}if(customLibrary!=null){controls.addView(new View(getContext()),new LinearLayout.LayoutParams(0,1,1));TextView edit=button("Edit Library",()->customEdit.run());edit.setTag("custom.control.edit");controls.addView(edit);}
```

## SITE-0d0e9f2a78ef · PreviewPages.run

[Source line 577](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L577)

```java
}else{TextView title=text(c.title,tab==3&&c.title.equals("Network & files")?30:19);title.setTextColor(0xff9ed4f7);v.addView(title);}
```

## SITE-f77b6acd798e · PreviewPages.onScrolled

[Source line 584](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L584)

```java
TextView title=text(name,13);title.setMaxLines(1);title.setEllipsize(android.text.TextUtils.TruncateAt.END);labels.addView(title);if(!sub.isEmpty()){TextView detail=text(sub,10);detail.setTextColor(0xffb4cbe0);detail.setMaxLines(2);labels.addView(detail);}v.addView(labels,new LinearLayout.LayoutParams(0,-2,1));v.setContentDescription(name+" "+sub);v.setOnClickListener(view->click.open(new Presenter.ViewHolder(view),b));
```

## SITE-d80af4294f46 · PreviewPages.bindNetworkPanel

[Source line 590](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L590)

```java
TextView heading=text(c.title,16);PreviewIcon.apply(heading,c.title,24);heading.setPadding(0,0,0,dp(8));v.addView(heading);
```

## SITE-c1565af6029d · PreviewPages.bindNetworkPanel

[Source line 592](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L592)

```java
v.addView(text("Check local storage and library sources for new or changed media.",12));
```

## SITE-2b15328559df · PreviewPages.bindNetworkPanel

[Source line 593](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L593)

```java
v.addView(button("Scan Library",()->PreviewLibraryScan.request(getContext())));
```

## SITE-c31ceaef1201 · PreviewPages.bindNetworkPanel

[Source line 594](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L594)

```java
TextView status=text(PreviewNetworkScanning.lastResult(getContext()),12);v.addView(status,new LinearLayout.LayoutParams(-1,dp(70)));
```

## SITE-87a7d0d33843 · PreviewPages.run

[Source line 595](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L595)

```java
status.post(new Runnable(){public void run(){if(!status.isAttachedToWindow())return;status.setText(com.archos.mediaprovider.video.NetworkScannerReceiver.isScannerWorking()?"Scanning network sources · "+com.archos.mediaprovider.video.NetworkScannerServiceVideo.getFilesFoundCount()+" files found":PreviewNetworkScanning.lastResult(getContext()));status.postDelayed(this,1000);}});
```

## SITE-0c89eb3059c2 · PreviewPages.run

[Source line 597](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L597)

```java
v.addView(text("Automatic scanning for your network sources.",12));
```

## SITE-1964b4d521a1 · PreviewPages.run

[Source line 598](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L598)

```java
int period=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(getContext());TextView schedule=text("Automatic: "+(period>0?"On":"Off")+"\nFrequency: "+(period>0?period/60000+" minutes":"Not scheduled")+"\nSources: "+librarySources.size()+"\n"+PreviewNetworkScanning.lastResult(getContext()),12);schedule.setLineSpacing(dp(4),1);schedule.setPadding(0,dp(12),0,dp(8));v.addView(schedule);v.addView(button("Configure Network Scanning",()->PreviewNetworkScanning.show(getContext())));
```

## SITE-01faa5a7a0b9 · PreviewPages.run

[Source line 600](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L600)

```java
for(Box box:files)if(box.getBoxId()==Box.ID.FOLDERS||box.getBoxId()==Box.ID.USB||box.getBoxId()==Box.ID.SDCARD||box.getBoxId()==Box.ID.OTHER)v.addView(button(box.getBoxId()==Box.ID.FOLDERS?"Internal Storage":box.getName(),()->click.open(new Presenter.ViewHolder(v),box)));
```

## SITE-a380052f382e · PreviewPages.run

[Source line 602](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L602)

```java
v.addView(button("Network Computers & NAS",()->networkSection("smb")));v.addView(button("Media Servers · DLNA/UPnP",()->networkSection("upnp")));
```

## SITE-dcbacea48239 · PreviewPages.run

[Source line 604](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L604)

```java
v.addView(text(c.title.equals("Library Sources")?"Indexed network sources appearing in Movies and TV Shows.":"Saved browsing locations.",12));
```

## SITE-3d6c231269ba · PreviewPages.run

[Source line 606](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L606)

```java
for(int i=0;i<Math.min(2,sourceList.size());i++){com.archos.mediacenter.video.leanback.adapter.object.Shortcut source=sourceList.get(i);TextView item=button(source.getName()+" · "+source.getUri().getScheme(),()->openSource(source));item.setSingleLine(true);item.setEllipsize(android.text.TextUtils.TruncateAt.END);v.addView(item,new LinearLayout.LayoutParams(-1,dp(34)));}
```

## SITE-79d538b65112 · PreviewPages.run

[Source line 607](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L607)

```java
v.addView(button("Open "+c.title,()->networkSection(c.title.equals("Library Sources")?"library":"saved")));
```

## SITE-02451cc60dfb · PreviewPages.run

[Source line 608](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L608)

```java
if(c.title.equals("Library Sources"))v.addView(button("Add Network Source",()->networkSection("add")));
```

## SITE-9351599c986d · PreviewPages.contextMenu

[Source line 635](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L635)

```java
List<String> labels=new ArrayList<>(Arrays.asList("More Info","Add to Row"));if(continuing){labels.add("Remove from Continue Watching Row");labels.add(entry.media instanceof Episode?"Restart Episode":"Restart Movie");}
```

## SITE-25b6ce8175e7 · PreviewPages.contextMenu

[Source line 636](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L636)

```java
PreviewDialog.choose(getContext(),displayName(entry),labels.toArray(new String[0]),-1,index->{if(index==0)open(entry,view);else if(index==1)PreviewHomeRows.add(getContext(),entry,this::render);else if(index==2){new PreviewHomeRows(getContext()).dismiss(entry);render();}else if(entry.media instanceof Video&&getContext() instanceof android.app.Activity)com.archos.mediacenter.video.utils.PlayUtils.startVideo(getContext(),(Video)entry.media,com.archos.mediacenter.video.player.PlayerService.RESUME_NO,false,-1,null,-1);});
```

## SITE-cd0c7a8e8827 · AnimeCollectionsIconBuilder.class scope

[Source line 46](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/AnimeCollectionsIconBuilder.java#L46)

```java
final static String[] PROJECTION = {
```

## SITE-a491243d0cea · CollectionActionAdapter.class scope

[Source line 44](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionActionAdapter.java#L44)

```java
set(ACTION_PLAY, new Action(ACTION_PLAY, context.getString(R.string.play_selection)));
```

Resolved English: `{"play_selection": "Play"}`

## SITE-b98cf85302ac · CollectionActionAdapter.update

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionActionAdapter.java#L51)

```java
set(ACTION_DELETE, new Action(ACTION_DELETE, mContext.getString(R.string.delete)));
```

Resolved English: `{"delete": "Delete"}`

## SITE-2db195ee0d4d · CollectionActionAdapter.update

[Source line 54](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionActionAdapter.java#L54)

```java
set(ACTION_CONFIRM_DELETE, new Action(ACTION_CONFIRM_DELETE, mContext.getString(R.string.confirm_delete_short)));
```

Resolved English: `{"confirm_delete_short": "Confirm deletion"}`

## SITE-10d85646b873 · CollectionActionAdapter.update

[Source line 58](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionActionAdapter.java#L58)

```java
set(ACTION_MARK_COLLECTION_AS_NOT_WATCHED, new Action(ACTION_MARK_COLLECTION_AS_NOT_WATCHED, mContext.getString(R.string.mark_as_not_watched)));
```

Resolved English: `{"mark_as_not_watched": "Mark not watched"}`

## SITE-dd92d43ab2d3 · CollectionActionAdapter.update

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionActionAdapter.java#L61)

```java
set(ACTION_MARK_COLLECTION_AS_WATCHED, new Action(ACTION_MARK_COLLECTION_AS_WATCHED, mContext.getString(R.string.mark_as_watched)));
```

Resolved English: `{"mark_as_watched": "Mark watched"}`

## SITE-a936f7bdd55b · CollectionDetailsDescriptionPresenter.onBindViewHolder

[Source line 116](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionDetailsDescriptionPresenter.java#L116)

```java
vh.mTitle.setText(collection.getName());
```

## SITE-fe7a9c368c59 · CollectionDetailsDescriptionPresenter.onBindViewHolder

[Source line 117](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionDetailsDescriptionPresenter.java#L117)

```java
vh.mBody.setText(collection.getPlot());
```

## SITE-be1623f01086 · CollectionDetailsDescriptionPresenter.setTextOrSetGoneIfEmpty

[Source line 126](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionDetailsDescriptionPresenter.java#L126)

```java
mTextView.setText(text);
```

## SITE-b07599c74cae · CollectionDetailsDescriptionPresenter.setTextOrSetGoneIfZero

[Source line 135](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionDetailsDescriptionPresenter.java#L135)

```java
mTextView.setText( Float.toString(value));
```

## SITE-10c6ca5cf4f0 · CollectionFragment.onVideoFileRemoved

[Source line 328](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionFragment.java#L328)

```java
AlertDialog.Builder b = new AlertDialog.Builder(activity).setTitle("");
```

## SITE-11bc5226458d · CollectionFragment.onVideoFileRemoved

[Source line 330](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionFragment.java#L330)

```java
b.setMessage(R.string.confirm_delete_parent_folder);
```

Resolved English: `{"confirm_delete_parent_folder": "It seems that parent folder does not contain any other video, should we delete it?"}`

## SITE-763ebe5e949a · CollectionsIconBuilder.class scope

[Source line 43](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionsIconBuilder.java#L43)

```java
final static String[] PROJECTION = {
```

## SITE-eb899189e9cb · CollectionsMoviesIconBuilder.class scope

[Source line 43](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionsMoviesIconBuilder.java#L43)

```java
final static String[] PROJECTION = {
```
