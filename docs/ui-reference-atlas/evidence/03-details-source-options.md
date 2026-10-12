# 03-details — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-d4c170dd59a2 · ArchosDetailsOverviewRowPresenter.onBindRowViewHolder

[Source line 71](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRowPresenter.java#L71)

```java
footer.setText("JustWatch via TMDb · " + com.archos.mediacenter.video.streaming.StreamingRepository.country(context)
```

## SITE-3212726a7dd3 · CastRowPresenter.onBindRowViewHolder

[Source line 114](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/CastRowPresenter.java#L114)

```java
vh.mCastTv.setText(cast);
```

## SITE-f2db554476cd · CastRowPresenter.onBindRowViewHolder

[Source line 117](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/CastRowPresenter.java#L117)

```java
vh.mDirectorsTv.setText(row.getDirectors());
```

## SITE-9cce873cc813 · FileDetailsRowPresenter.onBindRowViewHolder

[Source line 170](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java#L170)

```java
vh.mFileNameTv.setText(videoObject.getFilenameNonCryptic());
```

## SITE-3a3eae965a1d · FileDetailsRowPresenter.onBindRowViewHolder

[Source line 171](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java#L171)

```java
vh.mFilePathTv.setText(VideoInfoCommonClass.getParentPath(videoObject));
```

## SITE-604d88f02331 · FileDetailsRowPresenter.onBindRowViewHolder

[Source line 200](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java#L200)

```java
vh.mFileSizeAndDurationTv.setText(sb.toString());
```

## SITE-45d5816171d5 · FileDetailsRowPresenter.onBindRowViewHolder

[Source line 210](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java#L210)

```java
vh.mVideoTrackTv.setText(VideoInfoCommonClass.getVideoTrackString(videoMetadata, mR));
```

## SITE-bf6953d6b7f1 · FileDetailsRowPresenter.onBindRowViewHolder

[Source line 223](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java#L223)

```java
vh.mAudioTracksTv.setText(audioString);
```

## SITE-8b36ab5b8f2e · PlotAndGenresRowPresenter.onBindRowViewHolder

[Source line 111](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PlotAndGenresRowPresenter.java#L111)

```java
vh.mPlotTv.setText(row.getPlot());
```

## SITE-ff362366f465 · PlotAndGenresRowPresenter.onBindRowViewHolder

[Source line 114](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PlotAndGenresRowPresenter.java#L114)

```java
vh.mGenresTv.setText(row.getGenres());
```

## SITE-ae4cc24f863e · PreviewEpisodeRow.onBindViewHolder

[Source line 37](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeRow.java#L37)

```java
PreviewDialog.choose(activity,name,labels,0,index->StreamingRepository.IO.submit(()->{
```

## SITE-2ff3859addb5 · PreviewExtraDuration.request

[Source line 17](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewExtraDuration.java#L17)

```java
String current=label(cached(app,key));card.metadata.setText(current);card.metadata.setVisibility(current.isEmpty()?android.view.View.GONE:android.view.View.VISIBLE);
```

## SITE-ef2c2f2a1876 · PreviewExtraDuration.request

[Source line 27](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewExtraDuration.java#L27)

```java
String text=label(seconds);card.post(()->{if(card.isAttachedToWindow()&&("extra:"+key).equals(card.getTag())){card.metadata.setText(text);card.metadata.setVisibility(text.isEmpty()?android.view.View.GONE:android.view.View.VISIBLE);}});
```

## SITE-4adddcf13e14 · PreviewLandscapeCard.draw

[Source line 19](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewLandscapeCard.java#L19)

```java
title=text(13);metadata=text(11);title.setPadding(dp(4),dp(5),dp(4),0);metadata.setPadding(dp(4),0,dp(4),dp(4));addView(title);addView(metadata);
```

## SITE-a83a2e749688 · PreviewLandscapeCard.draw

[Source line 23](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewLandscapeCard.java#L23)

```java
void bind(String name,String detail,Uri art,boolean localIndicator,long media,String surface,String type){title.setText(name);metadata.setText(detail);metadata.setVisibility(detail==null||detail.isEmpty()?GONE:VISIBLE);availability.setTag(localIndicator);availability.setVisibility(localIndicator&&hasFocus()?VISIBLE:INVISIBLE);ArtworkRequest.cancel(image);ArtworkRequest.cancel(availability);availability.setImageDrawable(playGlyph);availability.clearColorFilter();availability.setImageAlpha(255);image.setImageDrawable(null);if(art!=null)ArtworkRequest.load(image,art,media,surface,type,Picasso.get().load(art).resize(dp(360),dp(204)).centerCrop().noFade());setContentDescription(name+(detail==null?"":" · "+detail));}
```

## SITE-4b408c88c44a · PreviewLandscapeCard.text

[Source line 25](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewLandscapeCard.java#L25)

```java
private TextView text(int size){TextView text=new TextView(getContext());text.setTextSize(size);text.setTextColor(0xffe1e9ef);text.setSingleLine(true);text.setEllipsize(android.text.TextUtils.TruncateAt.END);text.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));return text;}
```

## SITE-ca454580056f · PreviewMediaInfo.format

[Source line 15](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMediaInfo.java#L15)

```java
if(raw.startsWith("{")){try{JSONObject o=new JSONObject(raw);JSONArray tracks=o.optJSONArray("audiotracks");LinkedHashSet<String> labels=new LinkedHashSet<>();if(tracks!=null){for(int i=0;i<tracks.length();i++){JSONObject t=tracks.optJSONObject(i);if(t==null)continue;String codec=plain(t.optString("format","")),channels=plain(t.optString("channels",""));if(!codec.isEmpty())labels.add(codec+(channels.isEmpty()?"":" · "+channels));}}else {String codec=plain(o.optString("format",o.optString("codec","")));if(!codec.isEmpty())labels.add(codec);}return android.text.TextUtils.join(" · ",labels);}catch(JSONException ignored){return "";}}
```

## SITE-2be59db06c7e · PreviewMoviePage.onChanged

[Source line 59](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L59)

```java
title=text("",34);title.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));title.setMaxLines(2);title.setGravity(Gravity.CENTER_VERTICAL);title.setEllipsize(android.text.TextUtils.TruncateAt.END);cardCopy.addView(title,new LinearLayout.LayoutParams(dp(430),dp(96)));
```

## SITE-166fcea9ad94 · PreviewMoviePage.onChanged

[Source line 60](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L60)

```java
meta=text("",18);meta.setPadding(0,dp(10),0,dp(4));cardCopy.addView(meta);
```

## SITE-3cfa51dcab55 · PreviewMoviePage.onChanged

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L61)

```java
context=text("",13);context.setTextColor(0xffe1e9ef);context.setMaxLines(1);context.setEllipsize(android.text.TextUtils.TruncateAt.END);cardCopy.addView(context);
```

## SITE-b25e6cbfef80 · PreviewMoviePage.onChanged

[Source line 63](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L63)

```java
plot=text("",14);plot.setMaxLines(4);plot.setLineSpacing(dp(2),1);plot.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams plotSize=new LinearLayout.LayoutParams(Math.min(dp(440),getResources().getDisplayMetrics().widthPixels-dp(84)),dp(76));plotSize.topMargin=dp(8);cardCopy.addView(plot,plotSize);
```

## SITE-68f0e40620cd · PreviewMoviePage.onChanged

[Source line 64](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L64)

```java
matchMetadata=button("Match Metadata",()->{Action match=matchingAction();if(match!=null)action.accept(match);});matchMetadata.setVisibility(GONE);cardCopy.addView(matchMetadata,new LinearLayout.LayoutParams(-2,dp(38)));
```

## SITE-b5f13d891371 · PreviewMoviePage.onChanged

[Source line 66](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L66)

```java
trailer=button("Play Trailer",this::chooseTrailer);trailer.setVisibility(GONE);play=button("Play",this::play);buttons.addView(play);providerActions=new LinearLayout(c);providerActions.setClipChildren(false);buttons.addView(providerActions);moreButton=button("More",this::more);margin(buttons,moreButton);
```

## SITE-911b8842b570 · PreviewMoviePage.onChanged

[Source line 67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L67)

```java
versions=button("Versions",()->chooseVersions.run());versions.setVisibility(GONE);
```

## SITE-97c4ebc8eb85 · PreviewMoviePage.rebuildTabs

[Source line 123](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L123)

```java
if(show!=null||"tv".equals(remoteKind))labels.add("Seasons & Episodes");
```

## SITE-6a19afd75007 · PreviewMoviePage.rebuildTabs

[Source line 124](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L124)

```java
labels.add("Details");if(hasPlayableExtras())labels.add("Extras");if(!landscapeCards.isEmpty())labels.add("More Like This");
```

## SITE-3586d4bf98a6 · PreviewMoviePage.rebuildTabs

[Source line 126](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L126)

```java
TextView tab=button(label,()->showSection(label,true));tab.setBackground(null);tab.setCompoundDrawables(null,null,null,null);tab.setTag("section:"+label);
```

## SITE-3ac70fff81c0 · PreviewMoviePage.setVersions

[Source line 145](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L145)

```java
public void setVersions(int count,Runnable choose){chooseVersions=choose;versions.setText("Versions ("+count+")");versionCount=count;versions.setVisibility(count>1&&movie!=null?VISIBLE:GONE);}
```

## SITE-0833204e6a77 · PreviewMoviePage.text

[Source line 177](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L177)

```java
private TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setIncludeFontPadding(false);t.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));return t;}
```

## SITE-8bba3c29b450 · PreviewMoviePage.button

[Source line 179](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L179)

```java
private TextView button(String s,Runnable run){TextView t=TvButton.create(getContext(),s,run,false);t.setTag("action:"+s);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(t,"details.action."+s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","."));PreviewIcon.apply(t,s,16);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(9),dp(14),dp(9));t.setFocusable(true);t.setFocusableInTouchMode(true);t.setBackground(SharedThreePanel.control(getContext(),10));t.setOnClickListener(v->run.run());return t;}
```

## SITE-8a1f204728f6 · PreviewMoviePage.section

[Source line 181](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L181)

```java
private LinearLayout section(String name){LinearLayout section=new LinearLayout(getContext());section.setOrientation(LinearLayout.VERTICAL);section.setPadding(0,dp(10),0,dp(8));TextView label=text(name,16);label.setTextColor(0xff9ed4f7);section.addView(label);if(name.equals("More Like This")||name.equals("Details")||name.equals("Episodes")||name.equals("Extras"))label.setVisibility(GONE);LinearLayout content=new LinearLayout(getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,dp(6),0,0);section.addView(content);body.addView(section);return content;}
```

## SITE-93a17028d441 · PreviewMoviePage.bind

[Source line 182](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L182)

```java
public void bind(Video value){movie=value;com.archos.mediacenter.video.utils.SubtitleDiscovery.details(getContext(),value,snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache());title.setText(movie.getName());OfficialTitleArtwork.bind(title,value,false);plot.setText(movie.getDescriptionBody());
```

## SITE-3c5f621b38c1 · PreviewMoviePage.bind

[Source line 183](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L183)

```java
if(!movie.hasScraperData()){if(safe(movie.getName()).isEmpty())title.setText(movie.getFilenameNonCryptic());plot.setText("Not matched · Synopsis unavailable");}
```

## SITE-69dc58dea799 · PreviewMoviePage.bind

[Source line 184](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L184)

```java
play.setText(movie.getResumeMs()>0&&!PreviewSeriesJourney.completed(movie)?"Resume":"Play");
```

## SITE-48054347b100 · PreviewMoviePage.renderHumanMetadata

[Source line 189](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L189)

```java
if(movie instanceof Episode){Episode episode=(Episode)movie;title.setText(safe(episode.getShowName())+" — "+safe(episode.getEpisodeName()));
```

## SITE-d37feda176b9 · PreviewMoviePage.renderHumanMetadata

[Source line 201](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L201)

```java
meta.setText((movie instanceof Episode?safe(((Episode)movie).getEpisodeName())+"\n":"")+android.text.TextUtils.join("  ·  ",metadata));meta.setVisibility(metadata.isEmpty()?GONE:VISIBLE);
```

## SITE-67874e6cbe56 · PreviewMoviePage.setSeriesPlaybackTarget

[Source line 218](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L218)

```java
public void setSeriesPlaybackTarget(PreviewSeriesJourney.Selection selection){play.setText(PreviewSeriesJourney.playLabel(selection));}
```

## SITE-5396b93c3ff7 · PreviewMoviePage.renderDetails

[Source line 234](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L234)

```java
if(movie!=null&&!movie.hasScraperData())fact(key,"Metadata","Not matched");
```

## SITE-a23f5876bd8f · PreviewMoviePage.renderDetails

[Source line 238](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L238)

```java
context.setText(seasonNumber>=0?"Season "+seasonNumber:selectedEpisode==null?"":"S"+selectedEpisode.getSeasonNumber()+" E"+selectedEpisode.getEpisodeNumber());context.setVisibility(selectedEpisode==null&&seasonNumber<0?GONE:VISIBLE);
```

## SITE-a258bc99d403 · PreviewMoviePage.renderDetails

[Source line 242](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L242)

```java
if(year>0)fact(key,"Year",String.valueOf(year));
```

## SITE-ca40e1773908 · PreviewMoviePage.renderDetails

[Source line 246](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L246)

```java
if(runtime>0&&(movie!=null||"movie".equals(remoteKind)))fact(key,"Runtime",SharedMetadata.runtime(runtime));
```

## SITE-c5e15a275199 · PreviewMoviePage.renderDetails

[Source line 250](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L250)

```java
fact(key,"Age rating",certificate);fact(key,"Genres",genre);
```

## SITE-e8a59c4f1e82 · PreviewMoviePage.renderDetails

[Source line 253](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L253)

```java
fact(key,"Studio / Network",studios);
```

## SITE-e5d0988956b2 · PreviewMoviePage.renderDetails

[Source line 260](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L260)

```java
if(rating>0&&rating<=10)fact(reception,tmdbRating?"TMDB Rating":"Indexed Rating",String.format(Locale.UK,tmdbRating?"%.1f / 10":"%.1f",rating));
```

## SITE-5230830b48a4 · PreviewMoviePage.renderDetails

[Source line 261](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L261)

```java
if(selectedEpisode!=null)fact(key,"Air date",episodeDate>0?PreviewDetailsFacts.date(episodeDate):episodeFacts==null?"":episodeFacts.optString("air_date"));
```

## SITE-362816bdf270 · PreviewMoviePage.renderDetails

[Source line 263](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L263)

```java
if(selectedEpisode==null)fact(key,"Release / first air date",enriched.optString("release_date",enriched.optString("first_air_date")));
```

## SITE-eb55c5d8ab6f · PreviewMoviePage.renderDetails

[Source line 264](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L264)

```java
fact(key,selectedEpisode!=null?"Series original title":"Original title",enriched.optString("original_title",enriched.optString("original_name")));
```

## SITE-321dc34aba2d · PreviewMoviePage.renderDetails

[Source line 265](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L265)

```java
if(show!=null||"tv".equals(remoteKind)){fact(key,"Last air date",enriched.optString("last_air_date"));fact(key,"Series status",enriched.optString("status"));}
```

## SITE-bf4536d25646 · PreviewMoviePage.renderDetails

[Source line 266](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L266)

```java
fact(key,"Countries",jsonNames(enriched.optJSONArray("production_countries"),"name"));
```

## SITE-1ec69b2a06e2 · PreviewMoviePage.renderDetails

[Source line 267](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L267)

```java
fact(key,selectedEpisode!=null?"Series tagline":"Tagline",enriched.optString("tagline"));
```

## SITE-66a32c2da807 · PreviewMoviePage.renderDetails

[Source line 269](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L269)

```java
if(collection!=null)fact(key,"Collection",collection.optString("name"));
```

## SITE-05e5529be56b · PreviewMoviePage.renderDetails

[Source line 270](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L270)

```java
for(String field:new String[]{"budget","revenue"})if(enriched.optLong(field)>0)fact(key,field.equals("budget")?"Budget (TMDb)":"Box office (TMDb)",java.text.NumberFormat.getCurrencyInstance(Locale.US).format(enriched.optLong(field)));
```

## SITE-386690456048 · PreviewMoviePage.renderDetails

[Source line 271](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L271)

```java
if(enriched.optLong("vote_count")>0&&!(movie instanceof Episode))fact(reception,"TMDb votes",java.text.NumberFormat.getIntegerInstance().format(enriched.optLong("vote_count")));
```

## SITE-8a76e28d8048 · PreviewMoviePage.renderDetails

[Source line 272](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L272)

```java
if(episodeFacts!=null&&episodeFacts.optLong("vote_count")>0)fact(reception,"TMDb votes",java.text.NumberFormat.getIntegerInstance().format(episodeFacts.optLong("vote_count")));
```

## SITE-432894dbe6b1 · PreviewMoviePage.renderDetails

[Source line 275](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L275)

```java
if(movie.getMeasuredWidth()>0&&movie.getMeasuredHeight()>0)fact(technical,"Resolution",movie.getMeasuredWidth()+" × "+movie.getMeasuredHeight());
```

## SITE-2b7fe5153b61 · PreviewMoviePage.renderDetails

[Source line 276](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L276)

```java
fact(technical,"Video codec",PreviewMediaInfo.format(movie.getCalculatedVideoFormat()));
```

## SITE-86d3bd5a3dc3 · PreviewMoviePage.renderDetails

[Source line 277](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L277)

```java
fact(technical,"Audio",PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat()));
```

## SITE-b28da11602d8 · PreviewMoviePage.renderDetails

[Source line 278](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L278)

```java
fact(technical,"Dynamic range",PreviewVariants.dynamicRange(getContext(),movie));
```

## SITE-9ec69b725f57 · PreviewMoviePage.renderDetails

[Source line 279](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L279)

```java
fact(technical,"Source",source(movie.getFileUri()));
```

## SITE-931ad7b2d11b · PreviewMoviePage.renderDetails

[Source line 280](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L280)

```java
if(movie.getSize()>0)fact(technical,"File size",android.text.format.Formatter.formatShortFileSize(getContext(),movie.getSize()));
```

## SITE-e11abaad25fa · PreviewMoviePage.renderDetails

[Source line 281](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L281)

```java
fact(technical,"Selected file",movie.getFilenameNonCryptic());
```

## SITE-47523d86b22e · PreviewMoviePage.renderDetails

[Source line 282](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L282)

```java
fact(technical,"Location",PreviewVariants.safeLocation(movie.getFileUri()));
```

## SITE-0dd4a42ea188 · PreviewMoviePage.renderDetails

[Source line 284](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L284)

```java
if(extension>=0&&filename.length()-extension<=6)fact(technical,"Container",filename.substring(extension+1).toUpperCase(Locale.ROOT));
```

## SITE-853882cb9b09 · PreviewMoviePage.renderDetails

[Source line 289](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L289)

```java
if(video.bitRate>0)fact(technical,"Video bitrate",video.bitRate+" kb/s");
```

## SITE-5e0fd6f23632 · PreviewMoviePage.renderDetails

[Source line 290](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L290)

```java
if(video.fpsScale>0)fact(technical,"Frame rate",String.format(Locale.UK,"%.3f fps",video.fpsRate/(double)video.fpsScale));
```

## SITE-13e1fd269cd6 · PreviewMoviePage.renderDetails

[Source line 295](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L295)

```java
fact(technical,"Audio tracks",android.text.TextUtils.join("\n",audioTracks));
```

## SITE-1423e1a14c25 · PreviewMoviePage.renderDetails

[Source line 297](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L297)

```java
fact(technical,"Subtitles",android.text.TextUtils.join(", ",subtitles));
```

## SITE-5dc0a5184568 · PreviewMoviePage.renderDetails

[Source line 311](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L311)

```java
fact(technical,"Local episodes",String.valueOf(logical.size()));fact(technical,"Local seasons",String.valueOf(availableSeasons.size()));
```

## SITE-219e6b259562 · PreviewMoviePage.renderDetails

[Source line 312](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L312)

```java
if(bytes>0)fact(technical,"Library size",(knownSizes<physical.size()?"≥ ":"")+android.text.format.Formatter.formatShortFileSize(getContext(),bytes));
```

## SITE-fad403fd5d4a · PreviewMoviePage.renderDetails

[Source line 313](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L313)

```java
if(!physical.isEmpty()&&knownSizes==physical.size())fact(technical,"Average file size",android.text.format.Formatter.formatShortFileSize(getContext(),bytes/physical.size()));
```

## SITE-0b16eb03c2b3 · PreviewMoviePage.renderDetails

[Source line 314](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L314)

```java
if(specials>0)fact(technical,"Specials (files)",String.valueOf(specials));
```

## SITE-4b123bba97dd · PreviewMoviePage.renderDetails

[Source line 315](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L315)

```java
if(enriched!=null&&enriched.optInt("number_of_episodes")>0)fact(technical,"Series episodes (metadata)",String.valueOf(enriched.optInt("number_of_episodes")));
```

## SITE-dbb19b781a16 · PreviewMoviePage.renderDetails

[Source line 316](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L316)

```java
List<String> formatCounts=new ArrayList<>();for(Map.Entry<String,Integer> format:formats.entrySet())formatCounts.add(format.getKey()+": "+format.getValue()+" files");fact(technical,"File formats",android.text.TextUtils.join(", ",formatCounts));
```

## SITE-4d1c3b22098d · PreviewMoviePage.renderDetails

[Source line 317](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L317)

```java
fact(technical,"Storage locations",android.text.TextUtils.join("\n",locations));
```

## SITE-e4351114dccd · PreviewMoviePage.renderDetails

[Source line 320](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L320)

```java
fact(technical,"Region",com.archos.mediacenter.video.streaming.StreamingRepository.country(getContext()));
```

## SITE-fdb89dc61988 · PreviewMoviePage.renderDetails

[Source line 323](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L323)

```java
if(item instanceof Action&&com.archos.mediacenter.video.streaming.StreamingActions.isAvailableOffer((Action)item))fact(technical,"Available on",String.valueOf(((Action)item).getLabel1()));
```

## SITE-1cfaaf981eba · PreviewMoviePage.factPanel

[Source line 347](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L347)

```java
private LinearLayout factPanel(LinearLayout panels,String name){LinearLayout panel=new LinearLayout(getContext());panel.setTag("semantic:details.panel."+name.toLowerCase(Locale.ROOT).replace(' ','.'));panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(6),dp(6),dp(6),dp(6));panel.setBackground(PreviewDialog.surface(getContext(),false));panel.setFocusable(false);panel.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(0,-2,1);size.rightMargin=dp(8);panels.addView(panel,size);panel.addView(text(name,15));return panel;}
```

## SITE-1bb97349ec1f · PreviewMoviePage.fact

[Source line 348](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L348)

```java
private void fact(LinearLayout panel,String label,String value){
```

## SITE-e8f31e7b8378 · PreviewMoviePage.fact

[Source line 350](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L350)

```java
LinearLayout row=new LinearLayout(getContext());row.setGravity(Gravity.CENTER_VERTICAL);row.setContentDescription(label+": "+value);
```

## SITE-3c46336fdee7 · PreviewMoviePage.fact

[Source line 351](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L351)

```java
TextView name=text(label,12),content=text(value.replace("\n",", "),13);name.setTextColor(0xffb9c7d2);name.setSingleLine(true);content.setSingleLine(true);name.setEllipsize(android.text.TextUtils.TruncateAt.END);content.setEllipsize(android.text.TextUtils.TruncateAt.END);
```

## SITE-b1b84ad6361f · PreviewMoviePage.pill

[Source line 373](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L373)

```java
private void pill(String value){if(value==null||value.isEmpty())return;TextView label=text(value,10);label.setSingleLine(true);label.setPadding(dp(6),dp(3),dp(6),dp(3));GradientDrawable badge=new GradientDrawable();badge.setColor(0x50142634);badge.setCornerRadius(dp(3));badge.setStroke(dp(1),0xff648196);label.setBackground(badge);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.rightMargin=dp(6);pills.addView(label,lp);}
```

## SITE-2cebe52148c2 · PreviewMoviePage.person

[Source line 376](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L376)

```java
LinearLayout line=new LinearLayout(getContext());line.setPadding(0,dp(4),0,dp(4));line.setFocusable(false);line.setTag("person:"+name+":"+role);line.setContentDescription(name+", "+role);
```

## SITE-425bf1e42851 · PreviewMoviePage.person

[Source line 377](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L377)

```java
TextView person=SharedThreePanel.text(getContext(),"",14);android.text.SpannableStringBuilder copy=new android.text.SpannableStringBuilder(name);if(role!=null&&!role.isEmpty()){int start=copy.length();copy.append(" · ").append(role);copy.setSpan(new android.text.style.ForegroundColorSpan(0xffa4b6c7),start,copy.length(),android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);copy.setSpan(new android.text.style.RelativeSizeSpan(.86f),start,copy.length(),android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);}person.setText(copy);person.setSingleLine(true);person.setEllipsize(android.text.TextUtils.TruncateAt.END);line.addView(person,new LinearLayout.LayoutParams(-1,-2));row.addView(line,new LinearLayout.LayoutParams(-1,-2));
```

## SITE-76f6adf7592f · PreviewMoviePage.renderEnrichedPeople

[Source line 382](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L382)

```java
for(String section:new String[]{"cast","crew"}){
```

## SITE-c153d7f8d41c · PreviewMoviePage.bindShow

[Source line 402](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L402)

```java
public void bindShow(Tvshow value,Runnable playAction){show=value;com.archos.mediacenter.video.utils.SubtitleDiscovery.details(getContext(),value,snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache());showPlay=playAction;OfficialTitleArtwork.bind(title,value,false);title.setText(value.getName());plot.setText(value.getPlot());rebuildTabs();meta.setText((value.getYear()>0?value.getYear()+" · ":"")+value.getSeasonCount()+(value.getSeasonCount()==1?" season":" seasons")+" · "+value.getEpisodeCount()+(value.getEpisodeCount()==1?" episode":" episodes"));renderDetails();}
```

## SITE-c8931fa172ac · PreviewMoviePage.bindSeason

[Source line 403](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L403)

```java
public void bindSeason(Tvshow value,int number,List<Episode> local,Runnable playAction){seasonNumber=number;bindShow(value,playAction);plot.setText("");seasons.clear();setSeason(number,local);requestEnrichment();}
```

## SITE-ef2731b4bd4e · PreviewMoviePage.renderSeasons

[Source line 416](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L416)

```java
if(seasonNumber<0&&show!=null){TextView seasonInfo=button(season.getKey()==0?"Specials":"Season "+season.getKey(),()->getContext().startActivity(new android.content.Intent(getContext(),PreviewSeasonActivity.class).putExtra("show",show.getTvshowId()).putExtra("season",season.getKey())));seasonInfo.setTag("season:"+season.getKey());episodes.addView(seasonInfo);}else episodes.addView(text(season.getKey()==0?"Specials":"Season "+season.getKey(),18));
```

## SITE-322ef04db2a4 · PreviewMoviePage.info

[Source line 432](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L432)

```java
private void info(LinearLayout row,String name,String value){TextView t=text(value.replace("\n\n","    ·    ").replace("\n",": "),13);t.setMaxLines(3);t.setEllipsize(android.text.TextUtils.TruncateAt.END);t.setTextColor(0xffa8becf);t.setPadding(0,dp(3),0,dp(3));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(7);row.addView(t,lp);}
```

## SITE-810284cb2270 · PreviewMoviePage.renderRelated

[Source line 452](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L452)

```java
card.setTag("remote:"+id);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(card,"details.related.remote."+id);card.setContentDescription(name+" · "+recommendation.provider.name);
```

## SITE-1cfc314e50a0 · PreviewMoviePage.renderExtras

[Source line 484](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L484)

```java
for(String category:new String[]{"Trailers","Teasers","Behind the Scenes","Featurettes","Interviews","Clips","Other Videos"})categories.put(category,new ArrayList<>());
```

## SITE-83eb41ca1e95 · PreviewMoviePage.renderExtras

[Source line 496](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L496)

```java
TextView heading=text(category.getKey(),18);heading.setPadding(dp(5),dp(12),0,dp(4));trailers.addView(heading);
```

## SITE-6969e4fb3a67 · PreviewMoviePage.bindRemote

[Source line 511](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L511)

```java
public void bindRemote(org.json.JSONObject details,String kind,long id){remoteDetails=details;remoteKind=kind;remoteId=id;title.setText(details.optString("title",details.optString("name")));OfficialTitleArtwork.bindRemote(title,kind,id);plot.setText(details.optString("overview"));meta.setText(details.optString("release_date",details.optString("first_air_date")));String backdrop=details.optString("backdrop_path");if(backdrop.matches("/[A-Za-z0-9._-]+"))cardArtwork(Uri.parse("https://image.tmdb.org/t/p/w1280"+backdrop));play.setVisibility(GONE);renderDetails();requestEnrichment();}
```

## SITE-c6d41ce2283d · PreviewMoviePage.applyEnrichment

[Source line 539](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L539)

```java
enrichment=result;if(seasonNumber>=0&&result.details!=null)plot.setText(result.details.optString("overview"));renderEnrichedPeople();List<ScraperTrailer> extras=new ArrayList<>();
```

## SITE-f2dab3e0ba14 · PreviewMoviePage.moreInfo

[Source line 564](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L564)

```java
TextView heading=text(title.getText().toString(),28);page.addView(heading);TextView metadata=text(meta.getText()+"  "+context.getText(),13);metadata.setTextColor(0xffadc3d3);page.addView(metadata);
```

## SITE-ab1614ab734e · PreviewMoviePage.moreInfo

[Source line 569](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L569)

```java
if(show!=null)infoSection(facts,"Series",meta.getText()+"\n"+context.getText());TextView back=text("Press Back to return to Details",11);back.setPadding(0,dp(26),0,0);back.setTextColor(0xff8da9bb);page.addView(back);
```

## SITE-06571b751e68 · PreviewMoviePage.infoSection

[Source line 573](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L573)

```java
private void infoSection(LinearLayout page,String label,String value){if(value==null||value.trim().isEmpty())return;TextView heading=text(label,18);heading.setTextColor(PreviewAccent.color(getContext()));heading.setPadding(0,dp(22),0,dp(8));page.addView(heading);TextView content=text(value.trim(),14);content.setLineSpacing(dp(3),1f);page.addView(content);}
```

## SITE-30539f403b59 · PreviewMoviePage.technicalBadges

[Source line 576](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L576)

```java
LinearLayout line=null;for(int i=0;i<values.size();i++){if(i%2==0){line=new LinearLayout(getContext());parent.addView(line);}TextView badge=text(values.get(i),11);badge.setPadding(dp(7),dp(5),dp(7),dp(5));badge.setBackground(PreviewDialog.surface(getContext(),false));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(0,dp(4),dp(5),0);line.addView(badge,lp);}
```

## SITE-d856e134beee · PreviewMoviePage.more

[Source line 586](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L586)

```java
private void more(){if(seasonNumber>=0){PreviewDialog.choose(getContext(),"Season "+seasonNumber,new String[]{"Series Details","Episodes"},-1,n->{if(n==0)nativeDetails.run();else{showSection("Seasons & Episodes",true);}});return;}ObjectAdapter adapter=actions.get();if(adapter==null)return;
```

## SITE-001b6c1abf6f · PreviewMoviePage.more

[Source line 589](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L589)

```java
if(movie!=null&&getContext() instanceof Activity){labels.add(movie instanceof Episode?"Restart Episode":"Restart Movie");commands.add(()->com.archos.mediacenter.video.utils.PlayUtils.startVideo((Activity)getContext(),movie,com.archos.mediacenter.video.player.PlayerActivity.RESUME_NO,false,-1,null,-1));}
```

## SITE-73180c77da99 · PreviewMoviePage.more

[Source line 591](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L591)

```java
if(movie!=null&&movie.hasScraperData()){labels.add("Report Incorrect Match");commands.add(()->{getContext().getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0).edit().putBoolean("incorrect:"+movie.getId(),true).apply();getContext().startActivity(new android.content.Intent(getContext(),com.archos.mediacenter.video.leanback.LibraryHealthActivity.class));});}
```

## SITE-327e78ae749b · PreviewMoviePage.more

[Source line 592](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L592)

```java
String[] groups={"Playback & Navigation","Library","Streaming Services","Files & Media"};
```

## SITE-62e3451d3ad1 · PreviewMoviePage.more

[Source line 599](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L599)

```java
labels.add("— "+groups[group]);commands.add(null);
```

## SITE-a074b34f5888 · PreviewMoviePage.more

[Source line 600](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L600)

```java
for(Action a:section){moreRows.put(labels.size(),a);labels.add(titleStyle(String.valueOf(a.getLabel1())));commands.add(()->{Action current=PreviewMoreActions.current(actions.get(),a,show!=null);if(current!=null)action.accept(current);refreshMoreRows();});}
```

## SITE-a84500449b5a · PreviewMoviePage.more

[Source line 601](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L601)

```java
if(group==1){if(remoteDetails==null){labels.add("Add to Row");commands.add(()->PreviewHomeRows.add(getContext(),rowEntry(),()->{}));}}
```

## SITE-1ef341f0f738 · PreviewMoviePage.more

[Source line 602](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L602)

```java
if(group==3&&movie!=null){labels.add("Subtitles and Artwork");commands.add(nativeDetails);}
```

## SITE-0c25ac3e2b27 · PreviewMoviePage.more

[Source line 603](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L603)

```java
else if(group==3&&show!=null){labels.add("Artwork");commands.add(nativeDetails);}
```

## SITE-1435b32528f9 · PreviewMoviePage.more

[Source line 605](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L605)

```java
Dialog dialog=PreviewDialog.choose(getContext(),"Actions",labels.toArray(new String[0]),-1,Collections.emptySet(),false,n->{if(commands.get(n)!=null)commands.get(n).run();});moreDialog=dialog;dialog.setOnDismissListener(d->{if(moreDialog==dialog){moreDialog=null;moreRows.clear();}});
```

## SITE-7c22b19d7b71 · PreviewPeople.load

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeople.java#L20)

```java
for(String section:new String[]{"cast","crew"}){JSONArray values=credits.optJSONArray(section);if(values==null)continue;
```

## SITE-2fbaf39e73d4 · PreviewTrailer.show

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTrailer.java#L20)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.choose(activity,"In-app trailers are unavailable on this device.",new String[]{"Open YouTube","Cancel"},0,n->{if(n==0)external(activity,fallback);});return;
```

## SITE-000b1c4aacb0 · PreviewTrailer.shouldOverrideUrlLoading

[Source line 28](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTrailer.java#L28)

```java
TextView title=new TextView(activity);title.setText("Official trailer · "+trailer.mName);title.setTextSize(14);title.setTextColor(0xffffffff);title.setSingleLine(true);title.setEllipsize(android.text.TextUtils.TruncateAt.END);bar.addView(title,new LinearLayout.LayoutParams(0,-2,1));
```

## SITE-7a8c05f98957 · PreviewTrailer.shouldOverrideUrlLoading

[Source line 29](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTrailer.java#L29)

```java
for(String label:new String[]{"Open YouTube","Close"}){TextView button=new TextView(activity);button.setText(label);button.setTextColor(0xffffffff);button.setTextSize(12);button.setPadding(dp(activity,12),dp(activity,8),dp(activity,12),dp(activity,8));com.archos.mediacenter.video.leanback.PreviewIcon.apply(button,label,16);button.setFocusable(true);button.setBackground(com.archos.mediacenter.video.leanback.PreviewDialog.focus(activity));button.setOnClickListener(v->{dialog.dismiss();if(label.equals("Open YouTube"))external(activity,fallback);});bar.addView(button);}
```

## SITE-13833426f14c · SubtitlesDetailsRowPresenter.onClick

[Source line 180](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRowPresenter.java#L180)

```java
vh.mIntegratedSubsCol1Tv.setText(getFormattedSubList(c, internalSubsCol1Nb, 0,                  internalSubs));
```

## SITE-fc18e2a23f0e · SubtitlesDetailsRowPresenter.onClick

[Source line 181](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRowPresenter.java#L181)

```java
vh.mIntegratedSubsCol2Tv.setText(getFormattedSubList(c, internalSubsCol2Nb, internalSubsCol1Nb, internalSubs));
```

## SITE-b8d24148e537 · SubtitlesDetailsRowPresenter.onClick

[Source line 196](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRowPresenter.java#L196)

```java
vh.mExternalSubsCol1Tv.setText(getFormattedExternalSubList(c, externalSubsCol1Nb, 0,                  externalSubs));
```

## SITE-b74ad52f2b9d · SubtitlesDetailsRowPresenter.onClick

[Source line 197](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRowPresenter.java#L197)

```java
vh.mExternalSubsCol2Tv.setText(getFormattedExternalSubList(c, externalSubsCol2Nb, externalSubsCol1Nb, externalSubs));
```

## SITE-60ab2dd9cbbe · VideoActionAdapter.update

[Source line 92](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L92)

```java
set(ACTION_REMOTE_RESUME, new Action(ACTION_REMOTE_RESUME, mContext.getString(R.string.remote_resume), MediaUtils.formatTime(video.getRemoteResumeMs())));
```

Resolved English: `{"remote_resume": "Remote resume"}`

## SITE-842734a445a6 · VideoActionAdapter.update

[Source line 94](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L94)

```java
set(ACTION_RESUME, new Action(ACTION_LOCAL_RESUME, mContext.getString(R.string.resume), MediaUtils.formatTime(video.getResumeMs())));
```

Resolved English: `{"resume": "Resume"}`

## SITE-9c45d861acb3 · VideoActionAdapter.update

[Source line 102](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L102)

```java
set(ACTION_RESUME, new Action(ACTION_RESUME, mContext.getString(R.string.resume), MediaUtils.formatTime(video.getResumeMs())));
```

Resolved English: `{"resume": "Resume"}`

## SITE-f5c82af5c5c2 · VideoActionAdapter.update

[Source line 108](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L108)

```java
set(ACTION_PLAY_FROM_BEGIN, new Action(ACTION_PLAY_FROM_BEGIN, mContext.getString(R.string.play_from_beginning)));
```

Resolved English: `{"play_from_beginning": "Play from beginning"}`

## SITE-470c80457c9c · VideoActionAdapter.update

[Source line 111](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L111)

```java
set(ACTION_PLAY, new Action(ACTION_PLAY, mContext.getString(R.string.streaming_play_local)));
```

Resolved English: `{"streaming_play_local": "Play local file"}`

## SITE-6a904fd39546 · VideoActionAdapter.update

[Source line 116](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L116)

```java
set(ACTION_LIST_EPISODES, new Action(ACTION_LIST_EPISODES, mContext.getString(R.string.list_episodes)));
```

Resolved English: `{"list_episodes": "List episodes"}`

## SITE-c51a151f77a3 · VideoActionAdapter.update

[Source line 124](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L124)

```java
set(ACTION_MARK_AS_NOT_WATCHED, new Action(ACTION_MARK_AS_NOT_WATCHED, mContext.getString(R.string.mark_as_not_watched)));
```

Resolved English: `{"mark_as_not_watched": "Mark not watched"}`

## SITE-a86c2380f4bc · VideoActionAdapter.update

[Source line 130](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L130)

```java
set(ACTION_MARK_AS_WATCHED, new Action(ACTION_MARK_AS_WATCHED, mContext.getString(R.string.mark_as_watched)));
```

Resolved English: `{"mark_as_watched": "Mark watched"}`

## SITE-b913aa2e8417 · VideoActionAdapter.update

[Source line 139](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L139)

```java
set(ACTION_DELETE, new Action(ACTION_DELETE, mContext.getString(R.string.delete)));
```

Resolved English: `{"delete": "Delete"}`

## SITE-905f9b1d778a · VideoActionAdapter.update

[Source line 143](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L143)

```java
set(ACTION_CONFIRM_DELETE, new Action(ACTION_CONFIRM_DELETE, mContext.getString(R.string.confirm_delete_short)));
```

Resolved English: `{"confirm_delete_short": "Confirm deletion"}`

## SITE-e556ba4d3c5f · VideoActionAdapter.update

[Source line 155](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L155)

```java
set(ACTION_REMOVE_FROM_LIST, new Action(ACTION_REMOVE_FROM_LIST, mContext.getString(R.string.remove_from_list)));
```

Resolved English: `{"remove_from_list": "Remove from list"}`

## SITE-346257a69998 · VideoActionAdapter.update

[Source line 158](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L158)

```java
set(ACTION_ADD_TO_LIST, new Action(ACTION_ADD_TO_LIST, mContext.getString(R.string.add_to_list)));
```

Resolved English: `{"add_to_list": "Add to list"}`

## SITE-5995201da046 · VideoActionAdapter.update

[Source line 159](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L159)

```java
set(ACTION_UNSCRAP, new Action(ACTION_UNSCRAP, mContext.getString(R.string.leanback_unscrap)));
```

Resolved English: `{"leanback_unscrap": "@string/scrap_remove"}`

## SITE-843cfec189df · VideoActionAdapter.update

[Source line 164](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L164)

```java
set(ACTION_SCRAP, new Action(ACTION_SCRAP, mContext.getString(R.string.leanback_scrap)));
```

Resolved English: `{"leanback_scrap": "Get info"}`

## SITE-64055bba3909 · VideoActionAdapter.update

[Source line 174](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L174)

```java
set(ACTION_UNINDEX, new Action(ACTION_UNINDEX, mContext.getString(R.string.video_browser_unindex_file)));
```

Resolved English: `{"video_browser_unindex_file": "Remove from library"}`

## SITE-17f25ca2635c · VideoActionAdapter.update

[Source line 178](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L178)

```java
set(ACTION_INDEX, new Action(ACTION_INDEX, mContext.getString(R.string.video_browser_index_file)));
```

Resolved English: `{"video_browser_index_file": "Add to library"}`

## SITE-201b2e0a4e54 · VideoActionAdapter.setNextEpisodeStatus

[Source line 205](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L205)

```java
set(ACTION_NEXT_EPISODE, new Action(ACTION_NEXT_EPISODE, mContext.getString(R.string.next_episode)));
```

Resolved English: `{"next_episode": "Next episode"}`

## SITE-099cc2292c5d · VideoActionAdapter.setListEpisodesStatus

[Source line 214](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L214)

```java
set(ACTION_LIST_EPISODES, new Action(ACTION_LIST_EPISODES, mContext.getString(R.string.list_episodes)));
```

Resolved English: `{"list_episodes": "List episodes"}`

## SITE-c4d50e19ae5c · VideoActionAdapter.updateRemoteResume

[Source line 230](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L230)

```java
set(ACTION_REMOTE_RESUME, new Action(ACTION_REMOTE_RESUME, context.getString(R.string.remote_resume), MediaUtils.formatTime(video.getRemoteResumeMs())));
```

Resolved English: `{"remote_resume": "Remote resume"}`

## SITE-f869c82fdb84 · VideoActionAdapter.updateRemoteResume

[Source line 232](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L232)

```java
set(ACTION_RESUME, new Action(ACTION_LOCAL_RESUME, context.getString(R.string.resume), MediaUtils.formatTime(video.getResumeMs())));
```

Resolved English: `{"resume": "Resume"}`

## SITE-9c1f9c6c0b8f · VideoActionAdapter.updateRemoteResume

[Source line 239](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L239)

```java
set(ACTION_RESUME, new Action(ACTION_RESUME, context.getString(R.string.resume), MediaUtils.formatTime(video.getResumeMs())));
```

Resolved English: `{"resume": "Resume"}`

## SITE-829f3bdd2b69 · VideoActionAdapter.updateRemoteResume

[Source line 247](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L247)

```java
set(ACTION_PLAY_FROM_BEGIN, new Action(ACTION_PLAY_FROM_BEGIN, mContext.getString(R.string.play_from_beginning)));
```

Resolved English: `{"play_from_beginning": "Play from beginning"}`

## SITE-0eac3e2913c7 · VideoActionAdapter.updateRemoteResume

[Source line 250](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L250)

```java
set(ACTION_PLAY, new Action(ACTION_PLAY, mContext.getString(R.string.streaming_play_local)));
```

Resolved English: `{"streaming_play_local": "Play local file"}`

## SITE-d6e95548868e · VideoActionAdapter.updateToNonIndexed

[Source line 260](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L260)

```java
set(ACTION_PLAY, new Action(ACTION_PLAY, context.getString(R.string.streaming_play_local)));
```

Resolved English: `{"streaming_play_local": "Play local file"}`

## SITE-fa9dca447946 · VideoActionAdapter.updateToNonIndexed

[Source line 273](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L273)

```java
set(ACTION_INDEX, new Action(ACTION_INDEX, context.getString(R.string.video_browser_index_file)));
```

Resolved English: `{"video_browser_index_file": "Add to library"}`

## SITE-55a3a9736966 · VideoActionAdapter.updateToNonScraped

[Source line 281](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java#L281)

```java
set(ACTION_SCRAP, new Action(ACTION_SCRAP, context.getString(R.string.leanback_scrap)));
```

Resolved English: `{"leanback_scrap": "Get info"}`

## SITE-54aaf57dc4ab · VideoDetailsDescriptionPresenter.onBindViewHolder

[Source line 163](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L163)

```java
vh.mTitle.setText(((Episode) video).getShowName());
```

## SITE-3be32687995f · VideoDetailsDescriptionPresenter.onBindViewHolder

[Source line 164](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L164)

```java
vh.mEpisodeSXEX.setText(viewHolder.view.getContext().getString(R.string.leanback_episode_SXEX_code, episode.getSeasonNumber(), episode.getEpisodeNumber()));
```

Resolved English: `{"leanback_episode_SXEX_code": "\"S%1$02dE%2$02d\""}`

## SITE-32d87a1a345b · VideoDetailsDescriptionPresenter.onBindViewHolder

[Source line 165](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L165)

```java
vh.mEpisodeTitle.setText(episode.getName());
```

## SITE-ccd9ab10ae0c · VideoDetailsDescriptionPresenter.onBindViewHolder

[Source line 175](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L175)

```java
vh.mTitle.setText(movie.getName());
```

## SITE-94901a3ec3e5 · VideoDetailsDescriptionPresenter.onBindViewHolder

[Source line 185](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L185)

```java
vh.mTitle.setText(video.getFilenameNonCryptic());
```

## SITE-1b00a5eb12e0 · VideoDetailsDescriptionPresenter.onBindViewHolder

[Source line 200](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L200)

```java
vh.mBody.setText(video.getDescriptionBody());
```

## SITE-b04a8f243427 · VideoDetailsDescriptionPresenter.setTextOrSetInvisibleIfEmpty

[Source line 220](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L220)

```java
mTextView.setText(text);
```

## SITE-f428fbd1423c · VideoDetailsDescriptionPresenter.setTextOrSetGoneIfEmpty

[Source line 229](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L229)

```java
mTextView.setText(text);
```

## SITE-3dd08a3fb221 · VideoDetailsDescriptionPresenter.setTextOrSetGoneIfZero

[Source line 238](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java#L238)

```java
mTextView.setText( Float.toString(value));
```

## SITE-956a6df597a0 · VideoDetailsFragment.showPreviewTools

[Source line 231](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L231)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.choose(requireContext(),"Subtitles and artwork",new String[]{"Download subtitles","Choose subtitles","Posters","Backdrops","Subtitle Settings"},-1,java.util.Collections.emptySet(),false,n->{
```

## SITE-ffac6aa8c5f7 · VideoDetailsFragment.showPreviewArtwork

[Source line 237](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L237)

```java
if(row==null||row.getAdapter().size()==0){com.archos.mediacenter.video.leanback.PreviewDialog.read(requireContext(),"Artwork","No artwork available");return;}
```

## SITE-2dc729175863 · VideoDetailsFragment.run

[Source line 936](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L936)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.read(requireContext(),"Manage put.io file","This file is managed by put.io. Use put.io to delete it; Supernova will not send destructive operations through WebDAV.");return;
```

## SITE-5d212d7ac875 · VideoDetailsFragment.run

[Source line 939](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L939)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.choose(requireContext(),
```

## SITE-b1750acbf300 · VideoDetailsFragment.run

[Source line 940](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L940)

```java
getString(R.string.confirm_delete), new String[]{getString(android.R.string.cancel), getString(R.string.delete)},
```

Resolved English: `{"confirm_delete": "Do you really want to delete it?", "cancel": "[external/unresolved]", "delete": "Delete"}`

## SITE-1f866d2654c7 · VideoDetailsFragment.onCreateDialog

[Source line 2361](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L2361)

```java
npd.setMessage(getString(R.string.dialog_subloader_copying));
```

Resolved English: `{"dialog_subloader_copying": "Copying subtitles…"}`

## SITE-be3da14f1337 · VideoDetailsFragment.onVideoFileRemoved

[Source line 2384](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L2384)

```java
AlertDialog.Builder b = new AlertDialog.Builder(getActivity()).setTitle("");
```

## SITE-f365fc39d916 · VideoDetailsFragment.onVideoFileRemoved

[Source line 2386](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L2386)

```java
b.setMessage(R.string.confirm_delete_parent_folder);
```

Resolved English: `{"confirm_delete_parent_folder": "It seems that parent folder does not contain any other video, should we delete it?"}`

## SITE-951c593954ba · VideoDetailsFragment.deleteScraperInfo

[Source line 2470](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L2470)

```java
final String[] selectionArgs =new String[]{Long.toString(video.getId())};
```

## SITE-ce18f9990f00 · VideoDetailsFragment.unindexRemoteVideo

[Source line 2482](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java#L2482)

```java
new String[]{mVideo.getFilePath()});
```

## SITE-a6f45b72b588 · AllAnimeShowsIconBuilder.class scope

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/AllAnimeShowsIconBuilder.java#L42)

```java
final static String[] PROJECTION = {
```

## SITE-27feb311f496 · AllTvshowNoAmimeIconBuilder.class scope

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/AllTvshowNoAmimeIconBuilder.java#L42)

```java
final static String[] PROJECTION = {
```

## SITE-fc9058ee4cec · AllTvshowsIconBuilder.class scope

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/AllTvshowsIconBuilder.java#L42)

```java
final static String[] PROJECTION = {
```

## SITE-cba33e241041 · EpisodesByDateFragment.onClick

[Source line 71](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/EpisodesByDateFragment.java#L71)

```java
String[] names = { getString(R.string.date_view_week), getString(R.string.date_view_month), getString(R.string.date_view_year) };
```

Resolved English: `{"date_view_week": "Week", "date_view_month": "Month", "date_view_year": "Year"}`

## SITE-0efa55a0458b · SeasonFragment.onItemClicked

[Source line 223](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/SeasonFragment.java#L223)

```java
if(androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("try_new_ui",false))com.archos.mediacenter.video.leanback.PreviewDialog.confirmDelete(requireContext(),"Delete episodes?","This permanently deletes the selected episode files from their storage. This cannot be undone.",executeDelete);
```

## SITE-d6643a6e7d3e · SeasonFragment.onItemClicked

[Source line 267](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/SeasonFragment.java#L267)

```java
if(androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("try_new_ui",false))com.archos.mediacenter.video.leanback.PreviewDialog.confirmDelete(requireContext(),"Delete episodes?","This permanently deletes the selected episode files from their storage. This cannot be undone.",executeDelete);
```

## SITE-a36a7acd818b · SeasonFragment.onVideoFileRemoved

[Source line 433](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/SeasonFragment.java#L433)

```java
AlertDialog.Builder b = new AlertDialog.Builder(activity).setTitle("");
```

## SITE-43596736a9cc · SeasonFragment.onVideoFileRemoved

[Source line 435](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/SeasonFragment.java#L435)

```java
b.setMessage(R.string.confirm_delete_parent_folder);
```

Resolved English: `{"confirm_delete_parent_folder": "It seems that parent folder does not contain any other video, should we delete it?"}`

## SITE-4c04c0235e39 · TvshowActionAdapter.class scope

[Source line 50](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L50)

```java
set(ACTION_PLAY, new Action(ACTION_PLAY, context.getString(R.string.streaming_play_local)));
```

Resolved English: `{"streaming_play_local": "Play local file"}`

## SITE-a80108d09275 · TvshowActionAdapter.class scope

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L51)

```java
set(ACTION_MORE_DETAILS, new Action(ACTION_MORE_DETAILS, context.getString(R.string.leanback_action_more_details)));
```

Resolved English: `{"leanback_action_more_details": "More details"}`

## SITE-cc0efc69c6e5 · TvshowActionAdapter.class scope

[Source line 54](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L54)

```java
set(ACTION_MARK_SHOW_AS_NOT_WATCHED, new Action(ACTION_MARK_SHOW_AS_WATCHED, mContext.getString(R.string.mark_as_not_watched)));
```

Resolved English: `{"mark_as_not_watched": "Mark not watched"}`

## SITE-c93f40df28d4 · TvshowActionAdapter.class scope

[Source line 57](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L57)

```java
set(ACTION_MARK_SHOW_AS_WATCHED, new Action(ACTION_MARK_SHOW_AS_WATCHED, mContext.getString(R.string.mark_as_watched)));
```

Resolved English: `{"mark_as_watched": "Mark watched"}`

## SITE-bafb345febe4 · TvshowActionAdapter.class scope

[Source line 59](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L59)

```java
set(ACTION_UNINDEX, new Action(ACTION_UNINDEX, context.getString(R.string.video_browser_unindex_file)));
```

Resolved English: `{"video_browser_unindex_file": "Remove from library"}`

## SITE-27a8188d220f · TvshowActionAdapter.class scope

[Source line 60](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L60)

```java
set(ACTION_CHANGE_INFO, new Action(ACTION_CHANGE_INFO, context.getString(R.string.scrap_change)));
```

Resolved English: `{"scrap_change": "Change info of all episodes"}`

## SITE-3566296a817f · TvshowActionAdapter.class scope

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L61)

```java
set(ACTION_DELETE, new Action(ACTION_DELETE, context.getString(R.string.delete)));
```

Resolved English: `{"delete": "Delete"}`

## SITE-574f9f2e297a · TvshowActionAdapter.update

[Source line 69](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L69)

```java
set(ACTION_MARK_SHOW_AS_NOT_WATCHED, new Action(ACTION_MARK_SHOW_AS_WATCHED, mContext.getString(R.string.mark_as_not_watched)));
```

Resolved English: `{"mark_as_not_watched": "Mark not watched"}`

## SITE-f016f1adbb70 · TvshowActionAdapter.update

[Source line 72](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowActionAdapter.java#L72)

```java
set(ACTION_MARK_SHOW_AS_WATCHED, new Action(ACTION_MARK_SHOW_AS_WATCHED, mContext.getString(R.string.mark_as_watched)));
```

Resolved English: `{"mark_as_watched": "Mark watched"}`

## SITE-689c075058dd · TvshowDetailsDescriptionPresenter.onBindViewHolder

[Source line 120](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowDetailsDescriptionPresenter.java#L120)

```java
vh.mTitle.setText(tags.getTitle());
```

## SITE-cd508845da6b · TvshowDetailsDescriptionPresenter.onBindViewHolder

[Source line 121](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowDetailsDescriptionPresenter.java#L121)

```java
vh.mBody.setText(tags.getPlot());
```

## SITE-feba820e7cae · TvshowDetailsDescriptionPresenter.setTextOrSetGoneIfEmpty

[Source line 136](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowDetailsDescriptionPresenter.java#L136)

```java
mTextView.setText(text);
```

## SITE-6fffd5db8531 · TvshowDetailsDescriptionPresenter.setTextOrSetGoneIfZero

[Source line 145](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowDetailsDescriptionPresenter.java#L145)

```java
mTextView.setText( Float.toString(value));
```

## SITE-2a7ba20d2997 · TvshowFragment.showPreviewWatchedScope

[Source line 133](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowFragment.java#L133)

```java
if(seasons.isEmpty()){com.archos.mediacenter.video.leanback.PreviewDialog.read(requireContext(),"Watched state","No indexed seasons are available.");return;}
```

## SITE-4c66bbde98c6 · TvshowFragment.showPreviewWatchedScope

[Source line 142](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowFragment.java#L142)

```java
if(!success)com.archos.mediacenter.video.leanback.PreviewDialog.read(requireContext(),"Watched state","Some episodes could not be updated. Current library state has been refreshed.");
```

## SITE-ee6c4e9169c3 · TvshowFragment.showPreviewArtworkMenu

[Source line 151](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowFragment.java#L151)

```java
com.archos.mediacenter.video.leanback.PreviewDialog.choose(requireContext(),"Artwork",new String[]{"Posters","Backdrops"},-1,n->showPreviewArtwork(n==0));
```

## SITE-8b7c16a3d5fe · TvshowFragment.showPreviewArtwork

[Source line 164](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowFragment.java#L164)

```java
if(images==null||images.isEmpty()){com.archos.mediacenter.video.leanback.PreviewDialog.read(requireContext(),"Artwork","No artwork available");return;}
```

## SITE-bf5b57290a42 · TvshowMoreDetailsDescriptionPresenter.onBindViewHolder

[Source line 74](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowMoreDetailsDescriptionPresenter.java#L74)

```java
vh.mTitle.setText(tags.getTitle());
```

## SITE-6de2e9c9bc6c · TvshowMoreDetailsDescriptionPresenter.setTextOrSetGoneIfEmpty

[Source line 93](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowMoreDetailsDescriptionPresenter.java#L93)

```java
mTextView.setText(text);
```

## SITE-b73f66dd15e2 · TvshowMoreDetailsDescriptionPresenter.setTextOrSetGoneIfZero

[Source line 102](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowMoreDetailsDescriptionPresenter.java#L102)

```java
mTextView.setText( Float.toString(value));
```

## SITE-f8a621ff3858 · TvshowsByFragment.onViewCreated

[Source line 149](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowsByFragment.java#L149)

```java
mEmptyView.setText(R.string.you_have_no_tv_shows);
```

Resolved English: `{"you_have_no_tv_shows": "No TV shows"}`

## SITE-01b46f26bbc3 · TvshowsByFragment.onLoadFinished

[Source line 268](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowsByFragment.java#L268)

```java
mEmptyView.setText(R.string.you_have_no_tv_shows);
```

Resolved English: `{"you_have_no_tv_shows": "No TV shows"}`

## SITE-df0261a2aaef · TvshowsByFragment.showDeferredLoadingState

[Source line 399](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/tvshow/TvshowsByFragment.java#L399)

```java
mEmptyView.setText(R.string.not_available_during_media_scanning);
```

Resolved English: `{"not_available_during_media_scanning": "Not available during media scanning."}`

## SITE-9fea7033ffe1 · PreviewRemoteDetailsActivity.onCreate

[Source line 18](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/streaming/PreviewRemoteDetailsActivity.java#L18)

```java
work=StreamingRepository.IO.submit(()->{try{org.json.JSONObject details=StreamingRepository.metadata(getApplicationContext(),kind,id,"");StreamingRepository.Availability availability=StreamingRepository.load(getApplicationContext(),kind,id,StreamingRepository.country(this));java.util.List<StreamingRepository.Offer> offers=StreamingRepository.prefs(this).getBoolean(StreamingRepository.ENABLED,false)?StreamingRepository.filter(availability,StreamingRepository.selected(this),StreamingRepository.preferred(this)):java.util.Collections.emptyList();runOnUiThread(()->{if(isFinishing()||isDestroyed())return;for(StreamingRepository.Offer offer:offers)actions.add(new StreamingActionPresenter.LogoAction(offer.provider,()->open(kind,id,offer,availability)));page.bindRemote(details,kind,id);page.setSnapshot(PreviewLibraryLoader.memoryCache());page.focusPrimary();if(offers.isEmpty())PreviewNotice.show(this,"No availability on your configured services",false);});}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("remote_details_unavailable",error);runOnUiThread(()->{if(!isFinishing())PreviewDialog.read(this,"Details unavailable","Please try again when the metadata service is available.");});}});
```
