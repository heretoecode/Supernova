# 04-search-keyboard — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-531d5dfeaa9d · PreviewKeyboard.class scope

[Source line 13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L13)

```java
public static final String[] ROWS = {"1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
```

## SITE-602d51af6181 · PreviewKeyboard.toggleCaps

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L21)

```java
private void toggleCaps(){caps=!caps;for(int r=1;r<ROWS.length;r++)for(TextView key:keys.get(r)){String value=key.getTag().toString().substring("semantic:keyboard:".length());key.setText(caps?value:value.toLowerCase(java.util.Locale.ROOT));}keys.get(4).get(0).setSelected(caps);keys.get(4).get(0).setContentDescription("Caps Lock "+(caps?"On":"Off"));}
```

## SITE-59cfe184f8ea · PreviewKeyboard.setLeaveDown

[Source line 43](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L43)

```java
String[] labels = r == 4 ? new String[]{"Caps Lock", "Space", "Backspace"} : r == 5 ? new String[]{"Clear", "Done", "Cancel"} : ROWS[r].split("");
```

## SITE-dd2f196e9ef7 · PreviewKeyboard.setLeaveDown

[Source line 50](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L50)

```java
else if(label.equals("Clear")) input.setText("");
```

## SITE-8f3de7bb6f2f · PreviewKeyboard.setLeaveDown

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L61)

```java
if(r>=4){key.setBackground(SharedThreePanel.control(context,10));key.setTextSize(12);key.setPadding(dp(6),0,dp(6),0);key.setGravity(Gravity.CENTER);PreviewIcon.apply(key,label,14);key.setCompoundDrawablePadding(dp(4));if(label.equals("Caps Lock")){key.setText("");key.setSelected(true);key.setContentDescription("Caps Lock On");}}
```

## SITE-5af07c16cb03 · PreviewKeyboard.setLeaveDown

[Source line 62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L62)

```java
LayoutParams keySize=new LayoutParams(0,-1,1);if(r>=4){keySize=new LayoutParams(0,-1,r==4&&label.equals("Space")?3f:1f);lp.leftMargin=0;lp.rightMargin=0;}if(label.equals("Backspace")){key.setText("");key.setContentDescription("Backspace");}keySize.leftMargin=dp(2);keySize.rightMargin=dp(2);
```

## SITE-bd8557ff7e1d · PreviewKeyboard.key

[Source line 117](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L117)

```java
key.setText(label); key.setTextColor(Color.WHITE); key.setTextSize(14);
```

## SITE-75da0dfbc0dc · PreviewKeyboard.key

[Source line 120](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L120)

```java
key.setBackground(SharedThreePanel.control(getContext(),6));
```

## SITE-1567ac9ae96c · PreviewMatchSearch.class scope

[Source line 24](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L24)

```java
addView(text("Find a Match",24));TextView help=text("Search by title, TMDB ID or IMDb ID",14);help.setPadding(0,dp(6),0,dp(14));addView(help);
```

## SITE-4c886f45b74c · PreviewMatchSearch.class scope

[Source line 27](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L27)

```java
input=new EditText(context);input.setTag("semantic:match.query");input.setSingleLine(true);input.setShowSoftInputOnFocus(false);input.setTextColor(Color.WHITE);input.setTextSize(17);input.setBackground(PreviewDialog.focus(context));input.setText(initial);input.setSelection(input.length());left.addView(input,new LayoutParams(-1,dp(44)));
```

## SITE-38ef6eec861a · PreviewMatchSearch.class scope

[Source line 30](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L30)

```java
status=text("Searching…",13);status.setPadding(0,0,0,dp(10));right.addView(status);
```

## SITE-77791f5c583a · PreviewMatchSearch.searching

[Source line 38](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L38)

```java
public void searching(){setResults(Collections.emptyList());status.setText("Searching…");}
```

## SITE-6848b0a69615 · PreviewMatchSearch.unavailable

[Source line 39](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L39)

```java
public void unavailable(){setResults(Collections.emptyList());status.setText("Search unavailable. Please try again.");}
```

## SITE-c4e30e7aa96d · PreviewMatchSearch.setResults

[Source line 45](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L45)

```java
TextView title=text(label(tags),16);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);row.addView(title);
```

## SITE-447c20cc04bb · PreviewMatchSearch.setResults

[Source line 46](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L46)

```java
String plot=tags.getPlot();if(plot!=null&&!plot.trim().isEmpty()){TextView detail=text(plot,12);detail.setMaxLines(2);detail.setEllipsize(android.text.TextUtils.TruncateAt.END);detail.setPadding(0,dp(5),0,0);row.addView(detail);}
```

## SITE-550a6f564299 · PreviewMatchSearch.setResults

[Source line 47](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L47)

```java
row.setContentDescription(label(tags));row.setOnClickListener(v->choose.accept(tags));
```

## SITE-fa457f15eb3c · PreviewMatchSearch.setResults

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L51)

```java
status.setText(results.isEmpty()?"No matches":"Choose a match to review");
```

## SITE-7a60c55dd62a · PreviewMatchSearch.parentSeries

[Source line 57](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L57)

```java
TextView label=text("Episodes in "+name+" · enter S1 E1, an episode title or ID",13);parent.addView(label,new LayoutParams(0,-2,1));
```

## SITE-e980c5d33e73 · PreviewMatchSearch.parentSeries

[Source line 58](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L58)

```java
TextView action=text("Change Series Match",14);action.setPadding(dp(10),dp(6),dp(10),dp(6));action.setFocusable(true);action.setFocusableInTouchMode(true);action.setBackground(PreviewDialog.focus(getContext()));action.setOnClickListener(view->change.run());action.setTag("semantic:match.change-series");parent.addView(action);
```

## SITE-5d8974b82294 · PreviewMatchSearch.text

[Source line 69](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java#L69)

```java
private TextView text(String value,int size){TextView view=new TextView(getContext());view.setText(value);view.setTextSize(size);view.setTextColor(Color.WHITE);return view;}
```

## SITE-180a0b676d82 · PreviewTextInput.showInput

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java#L21)

```java
TextView heading=new TextView(c);heading.setText(title.equalsIgnoreCase("Page name")?"Page Name":title);heading.setGravity(Gravity.CENTER);heading.setTextColor(-1);heading.setTextSize(20);panel.addView(heading);
```

## SITE-3c5a1d1ba7b7 · PreviewTextInput.showInput

[Source line 22](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java#L22)

```java
EditText input=new KeyboardInput(c);input.setSingleLine(true);input.setShowSoftInputOnFocus(false);input.setText(previous);if(secret){input.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(input,"private.credential");}input.setTextColor(-1);input.setTextSize(16);input.setBackground(SharedThreePanel.control(c,10));input.setPadding(dp(c,12),0,dp(c,12),0);if(title.equalsIgnoreCase("Page name")){input.setHint("Name your library page");input.setHintTextColor(0xff8197a8);}input.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(limit)});panel.addView(input,new LinearLayout.LayoutParams(-1,dp(c,44)));input.setSelection(input.length());
```

## SITE-45d9b0a5de9d · PreviewTextInput.button

[Source line 30](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java#L30)

```java
private static TextView button(Context c,String label,Runnable action){TextView v=new TextView(c);v.setText(label);v.setTextColor(-1);v.setTextSize(14);v.setGravity(Gravity.CENTER);v.setFocusable(true);v.setBackground(PreviewDialog.focus(c));v.setOnClickListener(w->action.run());return v;}
```

## SITE-ba26fd8aecc0 · PreviewSearch.class scope

[Source line 50](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L50)

```java
status=text("",12);status.setGravity(Gravity.END);addView(status,new LayoutParams(-1,dp(22)));
```

## SITE-0a5c2e06cace · PreviewSearch.class scope

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L51)

```java
LinearLayout columns=new LinearLayout(activity);columns.setOrientation(HORIZONTAL);addView(columns,new LayoutParams(-1,0,1));LinearLayout keyboardColumn=new LinearLayout(activity);keyboardColumn.setOrientation(VERTICAL);TextView heading=text("Search",27);heading.setTextColor(-1);keyboardColumn.addView(heading);heading.setPadding(0,0,0,dp(10));LayoutParams keyboardSize=new LayoutParams(0,-1,.40f);keyboardSize.rightMargin=dp(24);columns.addView(keyboardColumn,keyboardSize);
```

## SITE-6e8639100bbd · PreviewSearch.class scope

[Source line 52](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L52)

```java
LinearLayout search=new LinearLayout(activity);search.setGravity(Gravity.CENTER_VERTICAL);search.setPadding(dp(12),0,dp(8),0);search.setBackground(SharedThreePanel.control(activity,10));ImageView icon=new ImageView(activity);icon.setImageResource(com.archos.mediacenter.video.R.drawable.preview_search);search.addView(icon,new LayoutParams(dp(20),dp(20)));
```

## SITE-c0c499d2e5c2 · PreviewSearch.class scope

[Source line 53](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L53)

```java
query=new KeyboardInput(activity);query.setId(View.generateViewId());query.setHint("Search Movies and TV Shows");query.setTextColor(-1);query.setHintTextColor(0xff8da7b9);query.setTextSize(16);query.setSingleLine(true);query.setMaxLines(1);query.setGravity(Gravity.CENTER_VERTICAL);query.setIncludeFontPadding(false);query.setBackgroundColor(android.graphics.Color.TRANSPARENT);query.setCursorVisible(true);query.setLongClickable(false);query.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH | android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI);query.setPadding(dp(12),0,dp(12),0);search.addView(query,new LayoutParams(0,dp(38),1));keyboardColumn.addView(search);query.setShowSoftInputOnFocus(false);
```

## SITE-384d1bbd7424 · PreviewSearch.class scope

[Source line 57](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L57)

```java
String[][] features={{"movie","Your Library","Search movies, TV shows, episodes and filenames."},
```

## SITE-26c5f2e97c9e · PreviewSearch.class scope

[Source line 62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L62)

```java
for(String[] feature:features){TextView title=text(feature[1],18);PreviewIcon.apply(title,feature[0],20);title.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);title.setPadding(0,dp(12),0,dp(4));guidance.addView(title);TextView explanation=text(feature[2],15);explanation.setGravity(Gravity.START);explanation.setPadding(dp(29),0,0,dp(8));guidance.addView(explanation);View divider=new View(activity);divider.setBackgroundColor(0x337f8996);guidance.addView(divider,new LayoutParams(-1,dp(1)));}
```

## SITE-1a2bc1567697 · PreviewSearch.beforeTextChanged

[Source line 68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L68)

```java
if(state!=null){selectedId=state.getLong("preview_search_selected",-1);scrollState=state.getParcelable("preview_search_scroll");restoring=state.getBoolean("preview_search_results_focused",selectedId!=-1);keyboard.restoreKey(state.getString("preview_search_key","T"));query.setText(state.getString("preview_search_query",""));} if(!restoring)keyboard.focusLastKey();
```

## SITE-b6d54211527e · PreviewSearch.acceptVoice

[Source line 86](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L86)

```java
public void acceptVoice(String text){query.setText(text);query.setSelection(query.length());}
```

## SITE-561f943477e6 · PreviewSearch.schedule

[Source line 89](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L89)

```java
private void schedule(String value){if(pending!=null)main.removeCallbacks(pending);if(cancellation!=null)cancellation.cancel();int request=++generation;String search=value.trim();if(search.isEmpty()){int previous=items.size();items.clear();results.setVisibility(GONE);guidancePanel.setVisibility(VISIBLE);adapter.notifyDataSetChanged();Diagnostics.uiRebuild(results,"search.results","query_cleared",previous,0,false);status.setText("");return;}pending=()->load(search,request);main.postDelayed(pending,250);}
```

## SITE-05ec715d1c3f · PreviewSearch.load

[Source line 93](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L93)

```java
android.net.Uri uri=loader.getUri();String[] extra={VideoStore.Video.VideoColumns.SCRAPER_M_GENRES,VideoStore.Video.VideoColumns.SCRAPER_S_GENRES,VideoStore.Video.VideoColumns.SCRAPER_ACTORS,VideoStore.Video.VideoColumns.SCRAPER_DIRECTORS,VideoStore.Video.VideoColumns.SCRAPER_WRITERS,VideoStore.Video.VideoColumns.SCRAPER_STUDIOS,VideoStore.Video.VideoColumns.SCRAPER_E_ACTORS,VideoStore.Video.VideoColumns.SCRAPER_E_DIRECTORS,VideoStore.Video.VideoColumns.SCRAPER_E_WRITERS,VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID};String[] projection=Arrays.copyOf(loader.getProjection(),loader.getProjection().length+extra.length);System.arraycopy(extra,0,projection,loader.getProjection().length,extra.length);String selection=loader.getSelection(),sort=loader.getSortOrder();String[] args=loader.getSelectionArgs();CancellationSignal signal=new CancellationSignal();cancellation=signal;status.setText("Searching library…");Context app=activity.getApplicationContext();AllTvshowsLoader showLoader=new AllTvshowsLoader(activity);android.net.Uri showUri=showLoader.getUri();String[] showProjection=showLoader.getProjection(),showArgs=showLoader.getSelectionArgs();String showSelection=showLoader.getSelection(),showSort=showLoader.getSortOrder();
```

## SITE-c5912fa2230e · PreviewSearch.load

[Source line 94](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L94)

```java
worker.execute(()->{Map<Long,String[]> movieNames=metadata(app,false,signal),showNames=metadata(app,true,signal);List<Result> found=new ArrayList<>();String error=null;try(Cursor cursor=app.getContentResolver().query(uri,projection,selection,args,sort,signal)){if(cursor!=null){VideoCursorMapper mapper=new VideoCursorMapper();mapper.bindColumns(cursor);while(cursor.moveToNext()){signal.throwIfCanceled();String name=cursor.getString(cursor.getColumnIndexOrThrow(VideoLoader.COLUMN_NAME));String episode=cursor.getString(cursor.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_E_NAME));String path=cursor.getString(cursor.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.DATA));Video video=(Video)mapper.bind(cursor);List<String> searchable=new ArrayList<>(Arrays.asList(name,episode,path));for(String column:extra)if(!column.endsWith("genres")&&!column.equals(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID))searchable.add(cell(cursor,column));String[] local=video instanceof Movie?movieNames.get(((Movie)video).getMovieId()):video instanceof Episode?showNames.get(longCell(cursor,VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID)):null;if(local!=null)Collections.addAll(searchable,local);int rank=PreviewSearchText.rank(value,searchable.toArray(new String[0]));if(rank==0)continue;String genres=cursor.getString(cursor.getColumnIndexOrThrow(video instanceof Episode?VideoStore.Video.VideoColumns.SCRAPER_S_GENRES:VideoStore.Video.VideoColumns.SCRAPER_M_GENRES));Result result=new Result(video,genres);result.rank=rank;result.showId=longCell(cursor,VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);found.add(result);}}}catch(OperationCanceledException cancelled){Diagnostics.event("search_cancelled","operation_id",operation);Diagnostics.finishOperation(operation,"search",started);return;}catch(RuntimeException failure){Diagnostics.error("search_failed",failure);error="Library search is temporarily unavailable";}if(mode!=VideoSearchActivity.SEARCH_MODE_MOVIE&&mode!=VideoSearchActivity.SEARCH_MODE_NON_SCRAPED){List<Result> parents=new ArrayList<>();try(Cursor shows=app.getContentResolver().query(showUri,showProjection,showSelection,showArgs,showSort,signal)){if(shows!=null){com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper mapper=new com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper();mapper.bindColumns(shows);while(shows.moveToNext()){signal.throwIfCanceled();Tvshow show=(Tvshow)mapper.bind(shows);List<String> names=new ArrayList<>();names.add(show.getName());names.add(show.getActors());names.add(show.getStudio());String[] local=showNames.get(show.getTvshowId());if(local!=null)Collections.addAll(names,local);int rank=PreviewSearchText.rank(value,names.toArray(new String[0]));for(Result result:found)if(result.showId==show.getTvshowId())rank=Math.max(rank,result.rank);if(rank>0){Result result=new Result(show,"");result.rank=rank;parents.add(result);}}}}catch(OperationCanceledException cancelled){Diagnostics.event("search_cancelled","operation_id",operation);Diagnostics.finishOperation(operation,"search",started);return;}catch(RuntimeException failure){Diagnostics.error("search_series_failed",failure);}found.addAll(0,parents);}found.sort(Comparator.comparingInt((Result item)->item.rank).reversed().thenComparing(item->item.show==null?item.video.getName():item.show.getName(),String.CASE_INSENSITIVE_ORDER));String message=error;com.archos.mediacenter.video.diagnostics.Diagnostics.finishOperation(operation,"search",started);com.archos.mediacenter.video.diagnostics.Diagnostics.event("search_results","operation_id",operation,"count",found.size());main.post(()->{if(request!=generation||!isAttachedToWindow())return;boolean focusResults=results.hasFocus();int position=0;if(selectedId!=-1)for(int i=0;i<found.size();i++)if(found.get(i).id()==selectedId){position=i;break;}int previous=items.size();items.clear();items.addAll(found);results.setVisibility(items.isEmpty()?GONE:VISIBLE);guidancePanel.setVisibility(items.isEmpty()?VISIBLE:GONE);adapter.notifyDataSetChanged();Diagnostics.uiRebuild(results,"search.results","query_result",previous,items.size(),false);status.setText(message!=null?message:found.isEmpty()?"No matching library titles":found.size()+" results · In your library");if(scrollState!=null){layout.onRestoreInstanceState(scrollState);scrollState=null;}if((restoring||focusResults)&&!items.isEmpty()){final int target=position;results.scrollToPosition(target);results.post(()->{RecyclerView.ViewHolder h=results.findViewHolderForAdapterPosition(target);if(h!=null)h.itemView.requestFocus();});}restoring=false;});});
```

## SITE-a51b394f31b9 · PreviewSearch.cell

[Source line 100](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L100)

```java
private static Map<Long,String[]> metadata(Context context,boolean shows,CancellationSignal cancellation){
```

## SITE-6cf7e187e14d · PreviewSearch.cell

[Source line 101](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L101)

```java
Map<Long,String[]> result=new HashMap<>();String[] columns=shows?new String[]{"_id","original_title_show"}:new String[]{"_id","original_title_movie"};
```

## SITE-fb8c4593d8e0 · PreviewSearch.text

[Source line 107](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L107)

```java
private TextView text(String s,int size){TextView t=new TextView(activity);t.setText(s);t.setTextSize(size);t.setTextColor(0xffc0d3df);t.setSingleLine(true);t.setEllipsize(TextUtils.TruncateAt.END);return t;}
```

## SITE-6ae7ab7f1af4 · PreviewSearch.text

[Source line 108](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L108)

```java
private class Holder extends RecyclerView.ViewHolder {ImageView image;TextView title,metadata,genres,badge,action;Holder(LinearLayout row){super(row);image=new ImageView(activity);image.setScaleType(ImageView.ScaleType.FIT_CENTER);row.addView(image,new LayoutParams(dp(92),dp(65)));LinearLayout labels=new LinearLayout(activity);labels.setOrientation(VERTICAL);labels.setPadding(dp(12),0,dp(12),0);badge=text("Top Result",11);badge.setTextColor(0xff9eb2c0);labels.addView(badge);title=text("",18);title.setTextColor(-1);metadata=text("",12);genres=text("",12);labels.addView(title);labels.addView(metadata);labels.addView(genres);row.addView(labels,new LayoutParams(0,-2,1));action=text("View Show",13);action.setGravity(Gravity.CENTER);action.setDuplicateParentStateEnabled(true);action.setBackgroundColor(android.graphics.Color.TRANSPARENT);action.setPadding(dp(8),dp(7),dp(8),dp(7));row.addView(action,new LayoutParams(dp(104),dp(38)));}}
```

## SITE-a136f4da0a63 · PreviewSearch.onBindViewHolder

[Source line 113](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/PreviewSearch.java#L113)

```java
public void onBindViewHolder(Holder h,int position){Result r=items.get(position);Diagnostics.semantic(h.itemView,"search.result."+(r.show==null?"video.":"series.")+(r.show==null?r.video.getId():r.show.getTvshowId()));if(r.show!=null){Tvshow show=r.show;h.title.setText(show.getName());h.metadata.setText((show.getYear()>0?show.getYear()+" · ":"")+"TV Series");h.genres.setText(show.getPlot());h.genres.setVisibility(TextUtils.isEmpty(show.getPlot())?GONE:VISIBLE);ArtworkRequest.cancel(h.image);h.image.setImageDrawable(null);if(show.getPosterUri()!=null)ArtworkRequest.load(h.image,show.getPosterUri(),show.getTvshowId(),"search.results","poster",Picasso.get().load(show.getPosterUri()).resize(dp(98),dp(140)).centerInside().noFade());h.itemView.setOnFocusChangeListener((v,focused)->{if(focused){selectedId=r.id();Diagnostics.focusedMedia(show.getTvshowId());}});h.itemView.setOnClickListener(v->{selectedId=r.id();hideKeyboard();recordDetailsEntry(v);VideoViewClickedListener.showTvshowDetails(activity,show,new Presenter.ViewHolder(v));});return;}Video video=r.video;h.title.setText(video instanceof Episode?((Episode)video).getShowName()+" · "+((Episode)video).getEpisodeName():video.getName());List<String> meta=new ArrayList<>();if(video instanceof Movie){Movie m=(Movie)video;if(m.getYear()>0)meta.add(String.valueOf(m.getYear()));if(m.getContentRating()!=null&&!m.getContentRating().isEmpty())meta.add(m.getContentRating());}else if(video instanceof Episode){Episode e=(Episode)video;meta.add("S"+e.getSeasonNumber()+" E"+e.getEpisodeNumber());}meta.add(com.archos.mediacenter.video.leanback.PreviewVariants.label(video));if(video.getDurationMs()>0)meta.add(video.getDurationMs()/60000+" min");h.metadata.setText(TextUtils.join("  ·  ",meta));h.genres.setText(r.genres==null?"":r.genres);h.genres.setVisibility(TextUtils.isEmpty(r.genres)?GONE:VISIBLE);ArtworkRequest.cancel(h.image);h.image.setImageDrawable(null);android.net.Uri art=video instanceof Episode?((Episode)video).getPictureUri():video.getPosterUri();if(art!=null)ArtworkRequest.load(h.image,art,video.getId(),"search.results",video instanceof Episode?"episode_still":"poster",Picasso.get().load(art).resize(dp(92),dp(65)).centerInside().noFade());h.itemView.setOnFocusChangeListener((v,focused)->{if(focused){selectedId=video.getId();Diagnostics.focusedMedia(video.getId());}});h.itemView.setOnClickListener(v->{selectedId=video.getId();hideKeyboard();recordDetailsEntry(v);VideoViewClickedListener.showVideoDetails(activity,video,new Presenter.ViewHolder(v),true,-1);});}
```

## SITE-855275175d6c · VideoSearchProvider.query

[Source line 81](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/VideoSearchProvider.java#L81)

```java
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
```

## SITE-eb2290abf708 · VideoSearchProvider.query

[Source line 85](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/VideoSearchProvider.java#L85)

```java
String[] extendedSelectionArgs = {
```

## SITE-d45d33611c44 · VideoSearchProvider.query

[Source line 108](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/VideoSearchProvider.java#L108)

```java
new String[]{VideoStore.Video.Thumbnails._ID},
```

## SITE-ddd3aac32f79 · VideoSearchProvider.delete

[Source line 135](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/VideoSearchProvider.java#L135)

```java
public int delete(Uri uri, String selection, String[] selectionArgs) {
```

## SITE-3efafd5175e0 · VideoSearchProvider.update

[Source line 140](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/search/VideoSearchProvider.java#L140)

```java
public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
```
