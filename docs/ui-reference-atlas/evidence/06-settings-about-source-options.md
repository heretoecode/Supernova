# 06-settings-about — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-4cae887028ef · BundledNoticePager.class scope

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/BundledNoticePager.java#L20)

```java
counter=SharedThreePanel.text(context,"",14);counter.setGravity(Gravity.CENTER);commands.addView(previous,new LayoutParams(-2,-2));commands.addView(counter,new LayoutParams(0,-2,1));commands.addView(next,new LayoutParams(-2,-2));addView(commands);
```

## SITE-acdf6827097c · BundledNoticePager.class scope

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/BundledNoticePager.java#L21)

```java
scroll=new ScrollView(context);scroll.setFocusable(true);scroll.setFocusableInTouchMode(true);scroll.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);text=SharedThreePanel.text(context,"",14);text.setTextIsSelectable(false);text.setPadding(0,PreviewDialog.dp(context,10),0,PreviewDialog.dp(context,16));scroll.addView(text,new ScrollView.LayoutParams(-1,-2));addView(scroll,new LayoutParams(-1,Math.max(PreviewDialog.dp(context,120),TvLayout.safeHeight(context)/2)));
```

## SITE-8c85e092de13 · BundledNoticePager.render

[Source line 36](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/BundledNoticePager.java#L36)

```java
private void render(){text.setText(pages.get(page));counter.setText((page+1)+" / "+pages.size());previous.setEnabled(page>0);next.setEnabled(page+1<pages.size());scroll.scrollTo(0,0);}
```

## SITE-96fbaff487cc · FoundationAboutActivity.onCreateView

[Source line 35](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutActivity.java#L35)

```java
TextView about=new TextView(requireContext());about.setText("About");about.setTextSize(20);about.setTextColor(android.graphics.Color.WHITE);about.setGravity(Gravity.CENTER_VERTICAL);about.setFocusable(true);about.setBackground(PreviewDialog.focus(requireContext()));rail.addView(about,new LinearLayout.LayoutParams(-1,dp(48)));
```

## SITE-2352d7eaa7f5 · FoundationAboutEntry.install

[Source line 13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutEntry.java#L13)

```java
for(String key:new String[]{"preferences_video_os","preferences_video_tmdb","preferences_video_trakt"})link(fragment,key,3);
```

## SITE-39f25990c595 · FoundationAboutWorkspace.class scope

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L42)

```java
public static final String[] SECTIONS = {"Release Notes", "Open-source Licences", "Credits & Acknowledgements", "Technical Information"};
```

## SITE-f4c6191c45ca · FoundationAboutWorkspace.text

[Source line 76](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L76)

```java
private TextView text(String value,int size){TextView v=com.archos.mediacenter.video.leanback.SharedThreePanel.text(getContext(),value,size);v.setLineSpacing(dp(3),1);return v;}
```

## SITE-18ee8f654fd0 · FoundationAboutWorkspace.show

[Source line 82](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L82)

```java
TextView child=text(name,16); child.setSingleLine(true);child.setEllipsize(android.text.TextUtils.TruncateAt.END);child.setPadding(dp(24),0,dp(6),0);child.setGravity(Gravity.CENTER_VERTICAL);
```

## SITE-4622e2bb342f · FoundationAboutWorkspace.row

[Source line 140](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L140)

```java
private TextView row(String label,Runnable select,Runnable activate){
```

## SITE-7398d35b985f · FoundationAboutWorkspace.row

[Source line 141](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L141)

```java
final int index=rows.size();TextView row=text(label,14);row.setPadding(dp(10),dp(10),dp(8),dp(10));
```

## SITE-ab33eb2cad1d · FoundationAboutWorkspace.appInformation

[Source line 160](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L160)

```java
ImageView logo=new ImageView(getContext());logo.setImageResource(R.drawable.foundation_ring);logo.setContentDescription("Supernova double-ring logo");
```

## SITE-e957716c3bab · FoundationAboutWorkspace.appInformation

[Source line 162](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L162)

```java
TextView wordmark=text("SUPERNOVA",28);wordmark.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));wordmark.setGravity(Gravity.CENTER);
```

## SITE-a846f2f88a42 · FoundationAboutWorkspace.appInformation

[Source line 164](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L164)

```java
TextView info=text("Version "+BuildConfig.VERSION_NAME+"\n\nBased on Nova Video Player 6.4.64\n\nApplication: "+BuildConfig.APPLICATION_ID+"\nBuilt (UTC): "+BuildConfig.PREVIEW_BUILD_UTC,16);
```

## SITE-b2cca61611b2 · FoundationAboutWorkspace.releaseRow

[Source line 182](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L182)

```java
TextView row=row(label+"\n"+date,()->showDetails(version,details,null,null),null);
```

## SITE-821f4235809b · FoundationAboutWorkspace.releaseRow

[Source line 184](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L184)

```java
if(current){expanded.add(version);row.setText(heading);}
```

## SITE-f32e979fb3dc · FoundationAboutWorkspace.releaseRow

[Source line 186](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L186)

```java
row.setText("▸ "+heading);
```

## SITE-4a29b5a8a1e6 · FoundationAboutWorkspace.releaseRow

[Source line 189](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L189)

```java
row.setText((expanded.contains(version)?"▾ ":"▸ ")+heading);});
```

## SITE-9aa26f7b001c · FoundationAboutWorkspace.releaseRow

[Source line 190](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L190)

```java
if(expanded.contains(version))row.setText("▾ "+heading);
```

## SITE-fd6cc7234824 · FoundationAboutWorkspace.loadLicences

[Source line 207](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L207)

```java
catalogueBody.removeAllViews();com.archos.mediacenter.video.leanback.SharedThreePanel.heading(catalogueBody,"Components");rows.clear();rowSelections.clear();
```

## SITE-6bbe5b154bb7 · FoundationAboutWorkspace.loadLicences

[Source line 211](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L211)

```java
row(name+"\n"+component.optString("role","Software component"),()->loadLegal(component,name+" · "+version),null);
```

## SITE-9f3a4f675251 · FoundationAboutWorkspace.credits

[Source line 233](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L233)

```java
if(!group.equals(previous)){TextView heading=text(group,15);heading.setPadding(dp(10),dp(14),0,dp(4));heading.setFocusable(false);catalogueBody.addView(heading);previous=group;}
```

## SITE-2b6cbc1e9536 · FoundationAboutWorkspace.credits

[Source line 234](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L234)

```java
row(entry.getString("name")+"\n"+entry.getString("role"),()->showDetails(entry.optString("name"),entry.optString("text"),entry.optString("url"),entry.optString("logo")),null);
```

## SITE-b6798bbf629e · FoundationAboutWorkspace.technical

[Source line 238](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L238)

```java
row("Application & Build",()->showDetails("Application & Build","Version: "+BuildConfig.VERSION_NAME+"\nVersion code: "+BuildConfig.VERSION_CODE+"\nApplication ID: "+BuildConfig.APPLICATION_ID+"\nBuild type: "+BuildConfig.BUILD_TYPE+"\nSource: "+BuildConfig.PREVIEW_GIT_SHA+"\nBuilt (UTC): "+BuildConfig.PREVIEW_BUILD_UTC+"\nUpstream: Nova Video Player 6.4.64",null,null),null);
```

## SITE-ef99fd6c5bd7 · FoundationAboutWorkspace.technical

[Source line 239](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L239)

```java
row("Device & Android TV",()->showDetails("Device & Android TV","Manufacturer: "+Build.MANUFACTURER+"\nModel: "+Build.MODEL+"\nAndroid: "+Build.VERSION.RELEASE+"\nAPI: "+Build.VERSION.SDK_INT+"\nDevice ABIs: "+android.text.TextUtils.join(", ",Build.SUPPORTED_ABIS),null,null),null);
```

## SITE-6f39bc8c239b · FoundationAboutWorkspace.technical

[Source line 240](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L240)

```java
row("Playback & Media Capabilities",()->{
```

## SITE-e4fa8f90f27e · FoundationAboutWorkspace.technical

[Source line 244](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L244)

```java
row("Library & Storage",()->{
```

## SITE-782b35cfa3dc · FoundationAboutWorkspace.technical

[Source line 251](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L251)

```java
row("Diagnostics",()->showDetails("Diagnostics","Optional diagnostic logging: "+(com.archos.mediacenter.video.diagnostics.Diagnostics.enabled()?"On":"Off")+"\n\nUse the existing Settings controls for logging and export. No new diagnostic viewer or upload is added.",null,null),null);
```

## SITE-8829e3c30c86 · FoundationAboutWorkspace.showDetails

[Source line 255](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L255)

```java
boolean restore=details.hasFocus();detailsBody.removeAllViews();noticePager=null;TextView title=text(heading,20);title.setFocusable(true);detailsBody.addView(title);
```

## SITE-1224aedd4a63 · FoundationAboutWorkspace.showDetails

[Source line 258](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L258)

```java
if(id!=0){ImageView image=new ImageView(getContext());image.setImageResource(id);image.setContentDescription("TMDB");detailsBody.addView(image,new LayoutParams(dp(160),dp(48)));}
```

## SITE-a31249e74c00 · FoundationAboutWorkspace.showDetails

[Source line 261](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L261)

```java
ImageView image=new ImageView(getContext());image.setContentDescription("QR code for the official "+heading+" website");detailsBody.addView(image,new LayoutParams(dp(124),dp(124)));
```

## SITE-a0a4e0740f15 · FoundationAboutWorkspace.showDetails

[Source line 265](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java#L265)

```java
TextView text=text(section.equals(SECTIONS[1])&&licencesOpened&&body.length()>BundledNoticePager.PAGE_CHARS?"":body,14);if(section.equals(SECTIONS[1]))com.archos.mediacenter.video.leanback.PreviewIcon.apply(text,"info",16);text.setFocusable(true);text.setPadding(0,dp(12),0,0);detailsBody.addView(text);if(section.equals(SECTIONS[1])&&licencesOpened&&body.length()>BundledNoticePager.PAGE_CHARS){text.setVisibility(GONE);text.setFocusable(false);noticePager=new BundledNoticePager(getContext(),body,this::focusSelectedRow);detailsBody.addView(noticePager);}
```

## SITE-ef6e79f9c4f5 · PreviewAccent.choose

[Source line 10](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewAccent.java#L10)

```java
public static void choose(Context c,Runnable changed){String[] names={"Slate Blue","Cyan","Emerald","Purple","Amber","Red","Custom…"};int[] colours={0xff62bbf3,0xff51d8e8,0xff58cfaa,0xffb49af4,0xffedbd67,0xffef8282};PreviewDialog.choose(c,"Accent & Colour",names,-1,n->{if(n<colours.length){save(c,colours[n]);changed.run();}else custom(c,changed);});}
```

## SITE-6e70c21ecdae · PreviewAccent.custom

[Source line 12](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewAccent.java#L12)

```java
private static void custom(Context c,Runnable changed){android.app.Dialog d=new android.app.Dialog(c);android.widget.LinearLayout box=new android.widget.LinearLayout(c);box.setOrientation(android.widget.LinearLayout.VERTICAL);box.setPadding(28,24,28,24);box.setBackground(utility(c));android.widget.TextView title=new android.widget.TextView(c);title.setText("Custom accent — Left / Right adjusts each colour");title.setTextColor(Color.WHITE);box.addView(title);int initial=color(c);int[] rgb={Color.red(initial),Color.green(initial),Color.blue(initial)};for(int i=0;i<3;i++){final int channel=i;android.widget.TextView name=new android.widget.TextView(c);name.setText(new String[]{"Red","Green","Blue"}[i]);name.setTextColor(Color.WHITE);box.addView(name);android.widget.SeekBar slider=new android.widget.SeekBar(c);slider.setMax(255);slider.setProgress(rgb[i]);slider.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(android.widget.SeekBar b,int value,boolean user){rgb[channel]=value;title.setTextColor(Color.rgb(rgb[0],rgb[1],rgb[2]));}public void onStartTrackingTouch(android.widget.SeekBar b){}public void onStopTrackingTouch(android.widget.SeekBar b){}});box.addView(slider);}android.widget.Button apply=new android.widget.Button(c);apply.setText("Apply");apply.setOnClickListener(v->{save(c,Color.rgb(rgb[0],rgb[1],rgb[2]));d.dismiss();changed.run();});box.addView(apply);d.setContentView(box);d.show();d.getWindow().setLayout(PreviewDialog.dp(c,500),-2);}
```

## SITE-c0ab9d14e5ce · InlineOpenSubtitles.row

[Source line 16](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/InlineOpenSubtitles.java#L16)

```java
private void row(String label,String tag,Runnable run){TextView view=SharedThreePanel.accentAction(context,label,run);view.setTag(tag);target.addView(view);SharedThreePanel.divider(target);}
```

## SITE-ab4a4979ed78 · InlineOpenSubtitles.row

[Source line 17](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/InlineOpenSubtitles.java#L17)

```java
void render(){target.removeAllViews();SharedThreePanel.heading(target,"OpenSubtitles");row("Username: "+username,"credential.username",()->PreviewTextInput.showValidated(context,"Username",username,128,value->value.isEmpty()?"Enter a username":null,value->{username=value;render();focus("credential.username");}));row("Password: "+(reveal?password:password.isEmpty()?"":"••••••"),"credential.password",()->PreviewTextInput.showSecret(context,"Password",password,value->{password=value;render();focus("credential.password");}));row("Show Password: "+(reveal?"On":"Off"),"credential.reveal",()->{reveal=!reveal;render();focus("credential.reveal");});row("Save & Sign In","credential.save",this::save);information.setText("Use your OpenSubtitles account to download available subtitles. Passwords are stored privately on this device and excluded from backups and diagnostics.");}
```

## SITE-bd2560c130b3 · InlineOpenSubtitles.save

[Source line 19](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/InlineOpenSubtitles.java#L19)

```java
private void save(){if(username.isEmpty()||password.isEmpty()){information.setText("Enter your username and password before signing in.");return;}int token=++generation;String user=username,secret=password;information.setText("Signing in…");if(work!=null)work.cancel(true);work=IO.submit(()->{boolean valid=false;try{valid=OpenSubtitlesApiHelper.login(context.getString(com.archos.mediacenter.video.R.string.opensubtitles_api_key),user,secret);}catch(java.io.IOException failure){/* Never retain exception bodies or credential-bearing URLs. */}final boolean accepted=valid;target.post(()->{if(token!=generation||!target.isAttachedToWindow()||!target.isShown())return;if(accepted){context.getSharedPreferences("opensubtitles_credentials",0).edit().putString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME,user).putString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD,secret).apply();OpenSubtitlesApiHelper.persistStatus(context,OpenSubtitlesApiHelper.OS_STATUS_OK);information.setText("Signed in to OpenSubtitles.");}else information.setText("Sign-in failed. Check the account details and connection. Previously saved credentials have been kept.");});});}
```

Resolved English: `{"opensubtitles_api_key": "[non-UI configuration value omitted]"}`

## SITE-5f8b816c8598 · InlinePreferenceChoices.multi

[Source line 27](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java#L27)

```java
if(providers)SharedThreePanel.heading(target,chosen?"Selected Providers":"Other Providers");LinearLayout rows=column(target);int count=0;
```

## SITE-86d8a4f8c02d · InlinePreferenceChoices.multi

[Source line 30](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java#L30)

```java
TextView label=SharedThreePanel.text(target.getContext(),(selected.contains(id)?"✓  ":"○  ")+labels[n],14);label.setPadding(SharedThreePanel.dp(target.getContext(),8),0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));
```

## SITE-d143621e886b · InlinePreferenceChoices.multi

[Source line 33](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java#L33)

```java
if(count==0)rows.addView(SharedThreePanel.text(target.getContext(),chosen?"No providers selected":"No other providers",14));ScrollView group=scroll(target,rows,providers&&chosen?140:230);if(providers&&!chosen)group.setLayoutParams(new LinearLayout.LayoutParams(-1,0,1));
```

## SITE-be54d7c4b2f1 · PreviewBuildInfo.install

[Source line 9](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java#L9)

```java
Preference build=about.findPreference("preferences_version");if(build!=null){build.setSummary(BuildConfig.VERSION_NAME);build.setOnPreferenceClickListener(p->{PreviewDialog.read(fragment.requireContext(),"SUPERNOVA Preview · Build information",describe());return true;});}
```

## SITE-5e9dd374c9ce · PreviewBuildInfo.install

[Source line 11](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java#L11)

```java
Preference attribution=new Preference(fragment.requireContext());attribution.setKey("preview_metadata_attribution");attribution.setTitle("Metadata & availability");attribution.setSummary("This product uses the TMDB API but is not endorsed or certified by TMDB. Streaming availability by JustWatch; availability does not guarantee account entitlement.");attribution.setSelectable(false);about.addPreference(attribution);
```

## SITE-18e6851d19ca · PreviewBuildInfo.install

[Source line 12](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java#L12)

```java
Preference identity=new Preference(fragment.requireContext());identity.setKey("preview_build_info");identity.setTitle("Build information");identity.setSummary(BuildConfig.VERSION_NAME+" · "+BuildConfig.BUILD_TYPE);
```

## SITE-8030dd6aa28d · PreviewBuildInfo.install

[Source line 13](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java#L13)

```java
identity.setOnPreferenceClickListener(p->{PreviewDialog.read(fragment.requireContext(),"SUPERNOVA Preview · Build information",describe());return true;});about.addPreference(identity);
```

## SITE-6df81e5d9fa0 · PreviewBuildInfo.install

[Source line 14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java#L14)

```java
Preference notes=new Preference(fragment.requireContext());notes.setKey("preview_whats_new");notes.setTitle("What's New");notes.setSummary("Release notes bundled with this build");notes.setOnPreferenceClickListener(p->{String body;try(java.io.InputStream stream=fragment.requireContext().getAssets().open("preview-whats-new.txt")){java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();byte[] chunk=new byte[4096];int n;while((n=stream.read(chunk))>0)bytes.write(chunk,0,n);body=bytes.toString("UTF-8");}catch(java.io.IOException missing){body="Release notes are unavailable in this build.";}PreviewDialog.read(fragment.requireContext(),"What's New",body);return true;});about.addPreference(notes);
```

## SITE-5e5f89afbd3b · PreviewPreferenceDialogs.show

[Source line 11](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L11)

```java
if(p instanceof ListPreference){ListPreference list=(ListPreference)p;CharSequence[] entries=list.getEntries(),values=list.getEntryValues();if(entries==null||values==null)return true;String[] labels=new String[entries.length];for(int i=0;i<labels.length;i++)labels[i]=entries[i].toString();Dialog dialog=PreviewDialog.choose(f.requireContext(),String.valueOf(p.getTitle()),labels,list.findIndexOfValue(list.getValue()),i->{String value=values[i].toString();if(list.callChangeListener(value))list.setValue(value);});languageIcons(dialog,p,values);return true;}
```

## SITE-b9c1425051b0 · PreviewPreferenceDialogs.providers

[Source line 19](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L19)

```java
for(boolean chosen:new boolean[]{true,false}){labels.add(chosen?"— Selected Providers":"— Other Providers");ids.add(null);List<Integer> group=new ArrayList<>();for(int i=0;i<values.length;i++)if(selected.contains(values[i].toString())==chosen)group.add(i);group.sort(Comparator.comparing(i->entries[i].toString().toLowerCase(Locale.ROOT)));for(int i:group){if(chosen)checked.add(labels.size());labels.add(entries[i].toString());ids.add(values[i].toString());}}
```

## SITE-ff43508c1c12 · PreviewPreferenceDialogs.providers

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L20)

```java
labels.add("Done");ids.add(null);Dialog[] dialog={null};
```

## SITE-1e3897211e65 · PreviewPreferenceDialogs.providers

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L21)

```java
dialog[0]=PreviewDialog.choose(f.requireContext(),String.valueOf(p.getTitle()),labels.toArray(new String[0]),-1,checked,false,i->{
```

## SITE-7967fff7e0df · PreviewPreferenceDialogs.multi

[Source line 29](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L29)

```java
String[] labels=new String[entries.length+1];Set<Integer> checked=new HashSet<>();for(int i=0;i<entries.length;i++){labels[i]=entries[i].toString();if(selected.contains(values[i].toString()))checked.add(i);}labels[entries.length]="Done";
```

## SITE-e375ebd72ddc · PreviewPreferenceDialogs.multi

[Source line 30](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L30)

```java
Dialog[] d={null};d[0]=PreviewDialog.choose(f.requireContext(),String.valueOf(p.getTitle()),labels,focus,checked,false,i->{
```

## SITE-321bfce61188 · PreviewPreferenceDialogs.edit

[Source line 41](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java#L41)

```java
private static void edit(PreferenceFragmentCompat f,EditTextPreference p){com.archos.mediacenter.video.leanback.PreviewTextInput.showValidated(f.requireContext(),String.valueOf(p.getTitle()),p.getText()==null?"":p.getText(),512,value->null,value->{if(p.callChangeListener(value))p.setText(value);});}
```

## SITE-d2671f97da57 · PreviewSettings.class scope

[Source line 14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L14)

```java
private static final String[] ORIGINAL_NAMES={"General","Home & Discovery","Playback","Video & Audio","Subtitles","Library","Sources & Storage","Appearance","Trakt","Streaming","Integrations","Advanced","About","Legacy"};
```

## SITE-ae914dde16c6 · PreviewSettings.organise

[Source line 18](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L18)

```java
String[] keys={"preferences_about","category_leanback_user_interface","preview_playback","preferences_category_video","preview_subtitles","scraper_category","netshare_category","category_user_interface","trakt_category","streaming_category","preview_integrations","preferences_category_advanced_video","about_category","preview_legacy"};
```

## SITE-565467d48c66 · PreviewSettings.organise

[Source line 19](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L19)

```java
for(int i=0;i<keys.length;i++){Preference p=root.findPreference(keys[i]);PreferenceCategory c=p instanceof PreferenceCategory?(PreferenceCategory)p:new PreferenceCategory(fragment.requireContext());if(p==null){c.setKey(keys[i]);root.addPreference(c);}c.setTitle(ORIGINAL_NAMES[i]);c.setOrder(i*100);categories.put(ORIGINAL_NAMES[i],c);}
```

## SITE-9d48045661e4 · PreviewSettings.organise

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L21)

```java
for(PreferenceCategory c:old){String title=String.valueOf(c.getTitle()).toLowerCase(Locale.ROOT);if(!categories.containsValue(c)){PreferenceCategory dest=categories.get(title.contains("language")?"Subtitles":title.contains("torrent")?"Advanced":"Sources & Storage");while(c.getPreferenceCount()>0){Preference p=c.getPreference(0);c.removePreference(p);dest.addPreference(p);}c.setVisible(false);}}
```

## SITE-7648dda55ac4 · PreviewSettings.organise

[Source line 23](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L23)

```java
Preference trust=new Preference(fragment.requireContext());trust.setKey("preview_sftp_trust");trust.setTitle("SFTP server identities");
```

## SITE-8842ecc6df25 · PreviewSettings.organise

[Source line 24](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L24)

```java
trust.setSummary("First connection stores a server identity. Changed identities are rejected. Only reset after verifying a legitimate server key change.");
```

## SITE-d7739f000c93 · PreviewSettings.organise

[Source line 26](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L26)

```java
if(hosts.length==0){PreviewDialog.read(fragment.requireContext(),"SFTP server identities","No identities recorded yet. First-use trust does not protect an already compromised first connection.");return true;}
```

## SITE-2aa5a1f86383 · PreviewSettings.organise

[Source line 27](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L27)

```java
PreviewDialog.choose(fragment.requireContext(),"Recorded SFTP servers",hosts,-1,n->PreviewDialog.choose(fragment.requireContext(),"Reset this server identity only after independently verifying its new key",new String[]{"Cancel","Reset identity for "+hosts[n]},0,i->{if(i==1)com.archos.filecorelibrary.sftp.SftpHostTrust.forget(hosts[n]);}));return true;});
```

## SITE-a0fe22c2a471 · PreviewSettings.organise

[Source line 28](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L28)

```java
categories.get("Sources & Storage").addPreference(trust);
```

## SITE-31d3f3c094b4 · PreviewSettings.organise

[Source line 31](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L31)

```java
for(String key:new String[]{"playback_speed","audio_speed_audiotrack","resume","repeat_mode","playback_repeat_mode"})move(root,categories.get("Playback"),key);
```

## SITE-045878559f87 · PreviewSettings.organise

[Source line 32](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L32)

```java
if(root.findPreference("remember_library_views")==null){SwitchPreferenceCompat remember=new SwitchPreferenceCompat(fragment.requireContext());remember.setKey("remember_library_views");remember.setTitle("Remember library view preferences");remember.setSummary("Keep Movies and TV Shows views, filters, sort and order separately");remember.setDefaultValue(true);categories.get("Library").addPreference(remember);}
```

## SITE-a2fdb838e44a · PreviewSettings.organise

[Source line 33](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L33)

```java
for(String key:new String[]{"subtitles_credentials","subtitles_hide_default","favSubLang","languages_list","codepage"})move(root,categories.get("Subtitles"),key);
```

## SITE-2cb4be0f0a2b · PreviewSettings.organise

[Source line 34](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L34)

```java
if(root.findPreference(com.archos.mediacenter.video.utils.SubtitleDiscovery.ENABLED)==null){SwitchPreferenceCompat discovery=new SwitchPreferenceCompat(fragment.requireContext());discovery.setKey(com.archos.mediacenter.video.utils.SubtitleDiscovery.ENABLED);discovery.setTitle("Prepare Subtitle Searches");discovery.setSummary("Check your chosen languages when viewing titles or starting playback. Cache results privately; downloads remain your choice. No provider searches during library import.");discovery.setDefaultValue(false);categories.get("Subtitles").addPreference(discovery);SwitchPreferenceCompat season=new SwitchPreferenceCompat(fragment.requireContext());season.setKey(com.archos.mediacenter.video.utils.SubtitleDiscovery.PREFETCH);season.setTitle("Prepare Current Season");season.setSummary("Pace searches for remaining local episodes after this season starts playing. Prepare only the next episode at a season boundary.");season.setDefaultValue(true);categories.get("Subtitles").addPreference(season);}
```

## SITE-c3b78ff70478 · PreviewSettings.organise

[Source line 35](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L35)

```java
for(String key:new String[]{"display_resume_box","hide_controls_on_pause","player_projector_mode_key"})move(root,categories.get("Playback"),key);
```

## SITE-5143d5e4d122 · PreviewSettings.organise

[Source line 37](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L37)

```java
Preference fullScan=root.findPreference("rescan_storage");if(fullScan!=null){fullScan.setTitle("Full Library Scan");fullScan.setSummary("Scan local storage and indexed network folders; retry unmatched descriptions");}
```

## SITE-ba185afff9cd · PreviewSettings.organise

[Source line 38](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L38)

```java
SwitchPreferenceCompat automatic=new SwitchPreferenceCompat(fragment.requireContext());automatic.setKey("auto_rescan_on_app_restart");automatic.setTitle("Scan network library on return");automatic.setSummary("Check indexed sources, including WebDAV, when Supernova opens or returns to the foreground");automatic.setDefaultValue(true);categories.get("Library").addPreference(automatic);
```

## SITE-76ef2bbb05a7 · PreviewSettings.organise

[Source line 39](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L39)

```java
ListPreference interval=new ListPreference(fragment.requireContext());interval.setKey("preview_scan_interval_control");interval.setPersistent(false);interval.setTitle("Automatic network scan interval");interval.setEntries(new String[]{"Off","15 minutes","30 minutes","1 hour","6 hours","24 hours"});interval.setEntryValues(new String[]{"0","900000","1800000","3600000","21600000","86400000"});int savedPeriod=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(fragment.requireContext());interval.setValue(String.valueOf(savedPeriod));interval.setSummaryProvider(p->{int period=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(fragment.requireContext());return period<=0?"Off":period/60000+" minutes · Android may defer background jobs";});interval.setOnPreferenceChangeListener((p,value)->{com.archos.mediaprovider.video.NetworkScannerUtil.scheduleNewRescan(fragment.requireContext(),0,Integer.parseInt(value.toString()),true);return true;});categories.get("Library").addPreference(interval);
```

## SITE-8cebd347cfb5 · PreviewSettings.organise

[Source line 43](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L43)

```java
for(String key:new String[]{"preferences_video_os","preferences_video_tmdb","preferences_video_trakt"}){Preference attribution=root.findPreference(key);if(attribution!=null){attribution.setEnabled(false);attribution.setSelectable(false);}}
```

## SITE-c2e307980eaa · PreviewSettings.organise

[Source line 44](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L44)

```java
for(String key:new String[]{"always_leanback_on_tv_key","reset_last_played_row","reset_last_played_section","preferences_movie_sort_order","preferences_tv_show_sort_order","preferences_animes_sort_order","try_new_ui","smart_recently_rows","separate_anime_movie_show","show_last_added_row","show_last_played_row","show_watching_up_next_row","show_all_movies_row","show_all_tv_shows_row","show_all_animes_row","show_documentaries","hide_trailer_row","show_by_rating","app_theme"}){
```

## SITE-9bfe70fd0254 · PreviewSettings.organise

[Source line 45](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L45)

```java
Preference p=root.findPreference(key);if(p!=null){move(root,categories.get("Legacy"),key);p.setEnabled(false);p.setSelectable(false);p.setSummary(key.equals("player_projector_mode_key")?"Legacy: aligns video and effects to the top of the screen. Existing value is preserved; presentation control retired pending device QA.":key.equals("app_theme")?"Legacy theme retained for classic surfaces. Use Accent & Colour in Appearance.":"Deprecated in this interface — underlying behaviour and saved value preserved");}
```

## SITE-1ed60f43a1a6 · PreviewSettings.organise

[Source line 47](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L47)

```java
Preference accent=new Preference(fragment.requireContext());accent.setKey("preview_accent_picker");accent.setTitle("Accent & Colour");accent.setSummary("Focus, selected controls and subtle utility gradients");accent.setOnPreferenceClickListener(p->{PreviewAccent.choose(fragment.requireContext(),()->fragment.requireActivity().recreate());return true;});categories.get("Appearance").addPreference(accent);
```

## SITE-a1d7f9a1090a · PreviewSettings.organise

[Source line 48](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L48)

```java
Preference rows=new Preference(fragment.requireContext());rows.setKey("preview_home_editor");rows.setTitle("Home rows");rows.setSummary("Create, rename, hide and reorder rows; manage Watch Next");rows.setOnPreferenceClickListener(p->{PreviewHomeRows.customise(fragment.requireContext(),()->{});return true;});categories.get("Home & Discovery").addPreference(rows);
```

## SITE-75bef4ec1a8f · PreviewSettings.organise

[Source line 49](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L49)

```java
Preference clear=new Preference(fragment.requireContext());clear.setKey("preview_clear_watch_next");clear.setTitle("Clear Watch Next");clear.setSummary("Remove all Watch Next titles after confirmation. Library and playback history are kept.");clear.setOnPreferenceClickListener(p->{PreviewHomeRows.clearWatchNext(fragment.requireContext());return true;});categories.get("Home & Discovery").addPreference(clear);
```

## SITE-de0619f6ef44 · PreviewSettings.organise

[Source line 50](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L50)

```java
String[] sources={"recent","trending","popular"},sourceNames={"Featured: Recently Added","Featured: Trakt Trending","Featured: Trakt Popular"};for(int n=0;n<sources.length;n++){SwitchPreferenceCompat enabled=new SwitchPreferenceCompat(fragment.requireContext());enabled.setKey("preview_featured_"+sources[n]);enabled.setTitle(sourceNames[n]);enabled.setSummary("Select from matching local-library titles; offline local fallback");enabled.setDefaultValue(true);categories.get("Home & Discovery").addPreference(enabled);}
```

## SITE-ac0ef24221d3 · PreviewSettings.organise

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L51)

```java
SwitchPreferenceCompat diagnostics=new SwitchPreferenceCompat(fragment.requireContext());diagnostics.setKey(com.archos.mediacenter.video.diagnostics.Diagnostics.KEY);diagnostics.setTitle("Diagnostic Logging");diagnostics.setSummary("Detailed diagnostic logging starts enabled. Bounded local reports exclude credentials.");diagnostics.setDefaultValue(com.archos.mediacenter.video.diagnostics.Diagnostics.defaultEnabled());diagnostics.setOnPreferenceChangeListener((p,value)->{if((Boolean)value){com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(fragment.requireContext(),true);return true;}com.archos.mediacenter.video.diagnostics.Diagnostics.chooseDisable(fragment.requireContext(),checked->diagnostics.setChecked(checked));return false;});categories.get("Advanced").addPreference(diagnostics);
```

## SITE-46a8d11fb3de · PreviewSettings.organise

[Source line 52](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L52)

```java
for(String label:new String[]{"Issues Digest","Report a Problem","Show Latest Reference"}){Preference item=new Preference(fragment.requireContext());item.setKey("preview_diagnostic_"+label);item.setTitle(label);item.setOnPreferenceClickListener(p->{if(label.equals("Issues Digest"))com.archos.mediacenter.video.diagnostics.Diagnostics.showDigest(fragment.requireContext());else if(label.equals("Show Latest Reference"))com.archos.mediacenter.video.diagnostics.Diagnostics.showLatestReference(fragment.requireContext());else com.archos.mediacenter.video.diagnostics.Diagnostics.reportProblem(fragment.requireContext());return true;});categories.get("Advanced").addPreference(item);}
```

## SITE-51eb4f0b59f2 · PreviewSettings.organise

[Source line 53](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L53)

```java
Preference export=new Preference(fragment.requireContext());export.setKey("export_diagnostic_report");export.setTitle("Export Diagnostic Report");export.setSummary("Save recent logs and safe device/build information as a ZIP. Choose a destination using Android Files.");export.setOnPreferenceClickListener(p->{fragment.startActivity(new android.content.Intent(fragment.requireContext(),com.archos.mediacenter.video.diagnostics.DiagnosticExportActivity.class));return true;});categories.get("Advanced").addPreference(export);
```

## SITE-fd0eb862ed34 · PreviewSettings.organise

[Source line 55](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L55)

```java
for(String[] choice:new String[][]{{com.archos.mediacenter.video.leanback.ViewingHistory.TRACK,"Track Watch History"},{com.archos.mediacenter.video.leanback.ViewingHistory.RESUME,"Remember Playback Position"}}){SwitchPreferenceCompat enabled=new SwitchPreferenceCompat(fragment.requireContext());enabled.setKey(choice[0]);enabled.setTitle(choice[1]);enabled.setDefaultValue(true);enabled.setSummary(choice[0].equals(com.archos.mediacenter.video.leanback.ViewingHistory.TRACK)?"Keep viewing history until explicitly cleared. Turning this off keeps existing history.":"Save unfinished playback positions independently of viewing-history tracking.");categories.get("Library").addPreference(enabled);}
```

## SITE-62ca9ea0f0b4 · PreviewSettings.organise

[Source line 56](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L56)

```java
for(String[] backup:new String[][]{{"supernova_backup_history","Include Watch History in Backups"},{"supernova_backup_resume","Include Playback Positions in Backups"}}){CheckBoxPreference selected=new CheckBoxPreference(fragment.requireContext());selected.setKey(backup[0]);selected.setTitle(backup[1]);selected.setDefaultValue(true);selected.setSummary("Controls backup inclusion independently of tracking. Backups can reveal viewing habits.");categories.get("Advanced").addPreference(selected);}
```

## SITE-2c28b1567f9b · PreviewSettings.organise

[Source line 57](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L57)

```java
Preference clearHistory=new Preference(fragment.requireContext());clearHistory.setTitle("Clear Viewing History");clearHistory.setKey("supernova_clear_history");clearHistory.setOnPreferenceClickListener(p->{PreviewDialog.confirmDelete(fragment.requireContext(),"Clear Viewing History?","Saved watch history will be removed. Unfinished playback positions, media files and sources are kept.",()->com.archos.mediacenter.video.leanback.ViewingHistory.clear(fragment.requireContext(),()->android.widget.Toast.makeText(fragment.requireContext(),"Watch history cleared. Unfinished playback positions kept.",android.widget.Toast.LENGTH_LONG).show()));return true;});categories.get("Library").addPreference(clearHistory);
```

## SITE-89d2fa4c88c1 · PreviewSettings.organise

[Source line 63](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L63)

```java
private static final String[] NAMES={"Playback","Video","Audio","Subtitles","Library & Metadata","Home","Appearance","Streaming","Network","Integrations","Advanced","About"};
```

## SITE-344af5e886b2 · PreviewSettings.consolidate

[Source line 65](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L65)

```java
PreferenceCategory video=categories.get("Video & Audio");video.setTitle("Video");
```

## SITE-3352bfad2614 · PreviewSettings.consolidate

[Source line 66](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L66)

```java
categories.get("Sources & Storage").setTitle("Network");
```

## SITE-31af8e37b56a · PreviewSettings.consolidate

[Source line 67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L67)

```java
categories.get("Library").setTitle("Library & Metadata");
```

## SITE-cc463633fcba · PreviewSettings.consolidate

[Source line 68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L68)

```java
categories.get("Home & Discovery").setTitle("Home");
```

## SITE-bf47b44e6b83 · PreviewSettings.consolidate

[Source line 69](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L69)

```java
PreferenceCategory audio=new PreferenceCategory(fragment.requireContext());audio.setKey("preview_audio");audio.setTitle("Audio");root.addPreference(audio);
```

## SITE-dd0ff2728f13 · PreviewSettings.consolidate

[Source line 72](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L72)

```java
while(general.getPreferenceCount()>0){Preference p=general.getPreference(0);general.removePreference(p);categories.get("About").addPreference(p);}general.setVisible(false);
```

## SITE-b217bdae275c · PreviewSettings.consolidate

[Source line 73](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L73)

```java
for(String key:new String[]{"favAudioLang","prefer_original_audio_track","force_audio_passthrough_multiple","player_spatialization_enabled"})move(root,audio,key);
```

## SITE-e9f556b23dc8 · PreviewSettings.consolidate

[Source line 74](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L74)

```java
for(String key:new String[]{"allow_3rd_party_player","network_bookmarks",fragment.getString(R.string.reset_brightness_on_start_key)})move(root,categories.get("Playback"),key);
```

Resolved English: `{"reset_brightness_on_start_key": "reset_brightness_on_start"}`

## SITE-b2ea0bf0ee3a · PreviewSettings.consolidate

[Source line 75](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L75)

```java
for(String key:new String[]{"hide_watched","sort_ignore_articles","preference_display_all_files","pref_create_remote_thumbs"})move(root,categories.get("Library"),key);
```

## SITE-c33b365b5218 · PreviewSettings.consolidate

[Source line 77](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L77)

```java
String[][] compatibility={
```

## SITE-9b5cedbdcfab · PreviewSettings.consolidate

[Source line 82](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L82)

```java
for(String[] group:compatibility){PreferenceCategory section=new PreferenceCategory(fragment.requireContext());section.setKey("preview_compat_"+group[0]);section.setTitle(group[0]);advanced.addPreference(section);for(int i=1;i<group.length;i++)move(root,section,group[i]);}
```

## SITE-8c2cc5e0912b · PreviewSettings.consolidate

[Source line 83](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L83)

```java
PreferenceCategory backupGroup=new PreferenceCategory(fragment.requireContext());backupGroup.setKey("preview_backup_restore");backupGroup.setTitle("Backup & Restore");advanced.addPreference(backupGroup);
```

## SITE-593746d166cf · PreviewSettings.consolidate

[Source line 84](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L84)

```java
for(String key:new String[]{"media_library_export_prefkey","media_library_import_prefkey","supernova_backup_history","supernova_backup_resume"})move(root,backupGroup,key);
```

## SITE-444746134887 · PreviewSettings.consolidate

[Source line 85](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L85)

```java
PreferenceCategory diagnosticGroup=new PreferenceCategory(fragment.requireContext());diagnosticGroup.setKey("preview_diagnostics_group");diagnosticGroup.setTitle("Diagnostics");advanced.addPreference(diagnosticGroup);
```

## SITE-76631badf6f9 · PreviewSettings.consolidate

[Source line 86](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L86)

```java
for(String key:new String[]{com.archos.mediacenter.video.diagnostics.Diagnostics.KEY,"preview_diagnostic_Issues Digest","preview_diagnostic_Report a Problem","preview_diagnostic_Show Latest Reference","export_diagnostic_report"})move(root,diagnosticGroup,key);
```

## SITE-f9bf87976740 · PreviewSettings.consolidate

[Source line 87](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L87)

```java
PreferenceCategory integrations=categories.get("Integrations"),trakt=categories.get("Trakt");root.removePreference(trakt);integrations.addPreference(trakt);integrationToggle(fragment,trakt,"supernova_integration_trakt","Enable Trakt");
```

## SITE-61f48d2152dc · PreviewSettings.consolidate

[Source line 88](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L88)

```java
PreferenceCategory subtitles=new PreferenceCategory(fragment.requireContext());subtitles.setKey("preview_opensubtitles");subtitles.setTitle("OpenSubtitles");integrations.addPreference(subtitles);move(root,subtitles,"subtitles_credentials");integrationToggle(fragment,subtitles,"supernova_integration_opensubtitles","Enable OpenSubtitles");
```

## SITE-1e1ecce9230e · PreviewSettings.consolidate

[Source line 89](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L89)

```java
PreferenceCategory intro=new PreferenceCategory(fragment.requireContext());intro.setKey("preview_introdb");intro.setTitle("IntroDB");integrations.addPreference(intro);
```

## SITE-e9e973466ba0 · PreviewSettings.consolidate

[Source line 90](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L90)

```java
SwitchPreferenceCompat skip=new SwitchPreferenceCompat(fragment.requireContext());skip.setKey(com.archos.mediacenter.video.player.SegmentSkippingPolicy.MASTER);skip.setTitle("Enable IntroDB");skip.setSummary("Use available segment boundaries. Uncertain material plays normally.");skip.setDefaultValue(true);skip.setVisible(false);intro.addPreference(skip);
```

## SITE-44c30e2e7841 · PreviewSettings.consolidate

[Source line 92](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L92)

```java
ListPreference policy=new ListPreference(fragment.requireContext());policy.setKey(com.archos.mediacenter.video.player.SegmentSkippingPolicy.key(type));policy.setTitle(type.name().substring(0,1)+type.name().substring(1).toLowerCase(java.util.Locale.ROOT));policy.setEntries(new String[]{"Play Normally","Show Skip (5 seconds)",type==com.archos.mediacenter.utils.introdb.IntroSegments.Type.CREDITS?"Safe Auto Skip":"Auto Skip","Smart"});policy.setEntryValues(new String[]{"NORMAL","PROMPT","AUTO","SMART"});policy.setDefaultValue(com.archos.mediacenter.video.player.SegmentSkippingPolicy.defaultMode(type).name());policy.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());intro.addPreference(policy);
```

## SITE-e1036b15bea5 · PreviewSettings.consolidate

[Source line 96](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L96)

```java
if(hidden instanceof TwoStatePreference){TwoStatePreference original=(TwoStatePreference)hidden;original.setVisible(false);SwitchPreferenceCompat positive=new SwitchPreferenceCompat(fragment.requireContext());positive.setKey("preview_subtitles_by_default");positive.setTitle("Subtitles by Default");positive.setPersistent(false);positive.setChecked(!original.isChecked());positive.setOnPreferenceChangeListener((p,value)->{original.setChecked(!(Boolean)value);return true;});categories.get("Subtitles").addPreference(positive);}
```

## SITE-0776db5931c4 · PreviewSettings.consolidate

[Source line 98](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L98)

```java
for(String key:new String[]{"rescan_storage","auto_rescan_on_app_restart","preview_scan_interval_control","share_folders","preferences_torrent_path","preferences_torrent_blocklist","uimode","uimode_leanback"}){Preference p=root.findPreference(key);if(p!=null)p.setVisible(false);}
```

## SITE-2cdc1d3fa93c · PreviewSettings.consolidate

[Source line 99](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L99)

```java
categories.get("Legacy").setVisible(false);
```

## SITE-7ef5506aa678 · PreviewSettings.integrationToggle

[Source line 101](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L101)

```java
private static void integrationToggle(PreferenceFragmentCompat fragment,PreferenceCategory group,String key,String title){SwitchPreferenceCompat enabled=new SwitchPreferenceCompat(fragment.requireContext());enabled.setKey(key);enabled.setTitle(title);enabled.setDefaultValue(true);enabled.setOrder(-100);enabled.setVisible(false);enabled.setSummary("Connection credentials are kept when disabled.");group.addPreference(enabled);}
```

## SITE-caa774bb1659 · PreviewSettings.restoreVisibility

[Source line 103](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L103)

```java
private static void restoreVisibility(PreferenceGroup group,Map<Preference,Boolean> visibility){String enable="trakt_category".equals(group.getKey())?"supernova_integration_trakt":"preview_opensubtitles".equals(group.getKey())?"supernova_integration_opensubtitles":"preview_introdb".equals(group.getKey())?com.archos.mediacenter.video.player.SegmentSkippingPolicy.MASTER:null;boolean enabled=enable==null||PreferenceManager.getDefaultSharedPreferences(group.getContext()).getBoolean(enable,true);for(int i=0;i<group.getPreferenceCount();i++){Preference child=group.getPreference(i);child.setVisible(Boolean.TRUE.equals(visibility.get(child))&&(enabled||Objects.equals(enable,child.getKey())));if(child instanceof PreferenceGroup)restoreVisibility((PreferenceGroup)child,visibility);}}
```

## SITE-7e6fcd21a0a8 · PreviewSettings.auditPresentation

[Source line 113](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L113)

```java
for(String key:new String[]{"smart_recently_rows","separate_anime_movie_show","show_last_added_row","show_last_played_row","show_watching_up_next_row","show_all_movies_row","show_all_tv_shows_row","show_all_animes_row","show_documentaries","hide_trailer_row","show_by_rating"}){Preference pref=root.findPreference(key);if(pref!=null){pref.setEnabled(false);pref.setSelectable(false);pref.setSummary("Legacy interface only — saved value preserved");}}
```

## SITE-a8bf807dc171 · PreviewSettings.adapter

[Source line 120](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L120)

```java
public static androidx.recyclerview.widget.RecyclerView.Adapter<?> adapter(PreferenceScreen root){return new PreferenceGroupAdapter(root){@Override public void onBindViewHolder(PreferenceViewHolder holder,int position){super.onBindViewHolder(holder,position);Preference p=getItem(position);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(holder.itemView,"settings.preference."+p.getKey());boolean category=p instanceof PreferenceCategory;holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);holder.itemView.setOnFocusChangeListener((v,focused)->{if(focused){TextView help=helpFor(activityContext(v.getContext()));if(help!=null)help.setText(contextFor(p));java.util.function.Consumer<Preference> editor=EDITORS.get(activityContext(v.getContext()));if(editor!=null)editor.accept(p);}});holder.itemView.setForeground(category?null:com.archos.mediacenter.video.leanback.SharedThreePanel.accentFocus(holder.itemView.getContext()));if(!category)com.archos.mediacenter.video.leanback.SharedThreePanel.rowSeparator(holder.itemView);holder.itemView.setFocusable(!category&&p.isEnabled()&&p.isSelectable());holder.itemView.setFocusableInTouchMode(!category&&p.isEnabled()&&p.isSelectable());if(holder.itemView.hasFocus()){TextView help=helpFor(activityContext(holder.itemView.getContext()));if(help!=null)help.setText(contextFor(p));}if(p instanceof ListPreference||p instanceof MultiSelectListPreference)holder.itemView.setOnClickListener(v->{java.util.function.Consumer<Preference> editor=EDITORS.get(activityContext(v.getContext()));if(editor!=null)editor.accept(p);View right=holder.itemView.getRootView().findViewWithTag("settings.inline.choices");if(right instanceof ViewGroup){ViewGroup group=(ViewGroup)right;for(int n=0;n<group.getChildCount();n++)if(group.getChildAt(n).isFocusable()){group.getChildAt(n).requestFocus();break;}}});View summary=holder.findViewById(android.R.id.summary);if(summary!=null)summary.setVisibility(View.GONE);holder.itemView.setAlpha(category||p.isEnabled()?1f:.38f);androidx.recyclerview.widget.RecyclerView.LayoutParams lp=new androidx.recyclerview.widget.RecyclerView.LayoutParams(-1,category?-2:PreviewDialog.dp(holder.itemView.getContext(),54));lp.setMargins(0,0,0,PreviewDialog.dp(holder.itemView.getContext(),6));holder.itemView.setLayoutParams(lp);android.view.View widget=holder.findViewById(android.R.id.widget_frame);if(widget!=null&&!(p instanceof androidx.preference.TwoStatePreference)){android.view.ViewGroup.LayoutParams wp=widget.getLayoutParams();wp.width=PreviewDialog.dp(holder.itemView.getContext(),48);wp.height=PreviewDialog.dp(holder.itemView.getContext(),30);widget.setLayoutParams(wp);fitBadge(widget);}tintSwitches(holder.itemView);holder.setDividerAllowedAbove(false);holder.setDividerAllowedBelow(false);}};}
```

## SITE-f03ae7ae2528 · PreviewSettings.move

[Source line 133](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L133)

```java
private static void move(PreferenceScreen root,PreferenceCategory dest,String key){Preference p=root.findPreference(key);if(p==null||p.getParent()==dest)return;PreferenceGroup parent=p.getParent();if(parent!=null)parent.removePreference(p);dest.addPreference(p);}
```

## SITE-1f2949ca03d5 · PreviewSettings.sidebar

[Source line 141](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L141)

```java
TextView help=com.archos.mediacenter.video.leanback.SharedThreePanel.text(c,"",16);
```

## SITE-31fb1f1ebec0 · PreviewSettings.sidebar

[Source line 142](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L142)

```java
LinearLayout helpBody=new LinearLayout(c);helpBody.setOrientation(LinearLayout.VERTICAL);com.archos.mediacenter.video.leanback.SharedThreePanel.heading(helpBody,"Information");helpBody.addView(help);LinearLayout choices=new LinearLayout(c);choices.setOrientation(LinearLayout.VERTICAL);choices.setTag("settings.inline.choices");helpBody.addView(choices);ScrollView helpScroll=new ScrollView(c);helpScroll.addView(helpBody);helpScroll.setFocusable(true);
```

## SITE-5289b5176274 · PreviewSettings.dispatchKeyEvent

[Source line 164](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L164)

```java
final PreferenceCategory category=found;TextView button=sidebarButton(c,name,false);button.setTag("semantic:settings:category:"+name);if(Arrays.asList("Advanced","Integrations","About").contains(name))button.setText(name+"  ›");links.addView(button,new LinearLayout.LayoutParams(-1,Math.max(dp(fragment,32),Math.round(22*c.getResources().getDisplayMetrics().scaledDensity)+dp(fragment,8))));parents.add(button);
```

## SITE-478900e585dd · PreviewSettings.dispatchKeyEvent

[Source line 166](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L166)

```java
rememberedLeft[0]=button;list.setTag(button);list.setVisibility(View.VISIBLE);overviewBody.setVisibility(View.GONE);helpScroll.setVisibility(View.VISIBLE);split.widths(.28f,.46f,.26f);for(int i=0;i<root.getPreferenceCount();i++)root.getPreference(i).setVisible(root.getPreference(i)==category);
```

## SITE-20e410e8a947 · PreviewSettings.dispatchKeyEvent

[Source line 167](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L167)

```java
if(category!=null)for(int k=0;k<category.getPreferenceCount();k++){Preference p=category.getPreference(k);if(p instanceof PreferenceGroup)restoreVisibility((PreferenceGroup)p,visibility);p.setVisible(Boolean.TRUE.equals(visibility.get(p))&&!(p instanceof PreferenceCategory));}
```

## SITE-8d7050d63032 · PreviewSettings.dispatchKeyEvent

[Source line 168](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L168)

```java
choices.removeAllViews();help.setText(name+"\n\n"+(name.equals("Advanced")?"Compatibility controls and local diagnostics. Change compatibility settings only when needed.":name.equals("Integrations")?"Trakt, IntroDB and OpenSubtitles can be enabled independently. Saved account credentials are kept when disabled.":"Select a setting to see its description."));list.scrollToPosition(0);
```

## SITE-cdf8af3323cb · PreviewSettings.dispatchKeyEvent

[Source line 170](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L170)

```java
if("Advanced".equals(name)){list.setVisibility(View.GONE);overviewBody.setVisibility(View.VISIBLE);helpScroll.setVisibility(View.GONE);split.widths(.28f,.72f,0);overviewBody.removeAllViews();com.archos.mediacenter.video.leanback.SharedThreePanel.heading(overviewBody,"Advanced");overviewBody.addView(com.archos.mediacenter.video.leanback.SharedThreePanel.text(c,"Compatibility controls and local diagnostics\n\nThese options support devices and media requiring specific behaviour. Change compatibility settings only when needed. Expand Advanced to select a section.",17));}
```

## SITE-9254825c0e76 · PreviewSettings.dispatchKeyEvent

[Source line 171](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L171)

```java
if("Integrations".equals(name)){list.setVisibility(View.GONE);overviewBody.setVisibility(View.VISIBLE);overviewBody.removeAllViews();com.archos.mediacenter.video.leanback.SharedThreePanel.heading(overviewBody,"Integrations");String[] titles={"Trakt","IntroDB","OpenSubtitles"},keys={"supernova_integration_trakt",com.archos.mediacenter.video.player.SegmentSkippingPolicy.MASTER,"supernova_integration_opensubtitles"};String[] explanations={"Trakt synchronises supported viewing activity with your connected account. Disabling stops integration without removing credentials.","IntroDB supplies available intro, recap, outro and credit boundaries. Disabling preserves your segment policies.","OpenSubtitles supplies downloadable subtitles. Disabling preserves your account credentials."};for(int n=0;n<titles.length;n++){final int integrationIndex=n;androidx.appcompat.widget.SwitchCompat toggle=new androidx.appcompat.widget.SwitchCompat(c);toggle.setShowText(false);toggle.setText(titles[n]);toggle.setTextColor(-1);toggle.setTextSize(17);toggle.setFocusable(true);toggle.setChecked(PreferenceManager.getDefaultSharedPreferences(c).getBoolean(keys[n],true));toggle.setBackground(com.archos.mediacenter.video.leanback.SharedThreePanel.accentFocus(c));toggle.setOnFocusChangeListener((v,yes)->{if(yes)help.setText(explanations[integrationIndex]);});toggle.setOnCheckedChangeListener((v,enabled)->{PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(keys[integrationIndex],enabled).apply();Preference model=root.findPreference(keys[integrationIndex]);if(model instanceof TwoStatePreference)((TwoStatePreference)model).setChecked(enabled);for(TextView child:nested){String tag=String.valueOf(child.getTag());if(tag.endsWith(integrationIndex==0?"trakt_category":integrationIndex==1?"preview_introdb":"preview_opensubtitles"))child.setVisibility(enabled?View.VISIBLE:View.GONE);}});overviewBody.addView(toggle,new LinearLayout.LayoutParams(-1,dp(fragment,46)));com.archos.mediacenter.video.leanback.SharedThreePanel.divider(overviewBody);}}
```

## SITE-8a975718d134 · PreviewSettings.dispatchKeyEvent

[Source line 185](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L185)

```java
Runnable select=()->{list.setVisibility(View.VISIBLE);overviewBody.setVisibility(View.GONE);helpScroll.setVisibility(View.VISIBLE);split.widths(.28f,.46f,.26f);rememberedLeft[0]=child;list.setTag(child);for(int n=0;n<category.getPreferenceCount();n++)category.getPreference(n).setVisible(category.getPreference(n)==p);restoreVisibility((PreferenceGroup)p,visibility);choices.removeAllViews();help.setText(String.valueOf(p.getTitle()));list.scrollToPosition(0);if("preview_opensubtitles".equals(p.getKey())){list.setVisibility(View.GONE);overviewBody.setVisibility(View.VISIBLE);new InlineOpenSubtitles(c,overviewBody,help);}};
```

## SITE-438ca2a473cb · PreviewSettings.handleOnBackPressed

[Source line 203](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L203)

```java
ViewTreeObserver.OnGlobalFocusChangeListener focus=(oldView,newView)->back.setEnabled(middle.hasFocus()||helpScroll.hasFocus()||about!=null&&about.hasFocus());split.getViewTreeObserver().addOnGlobalFocusChangeListener(focus);
```

## SITE-f40330c32808 · PreviewSettings.sidebarButton

[Source line 207](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java#L207)

```java
private static TextView sidebarButton(android.content.Context c,String name,boolean child){TextView button=new TextView(c);button.setText(name);button.setTextSize(16);button.setSingleLine(true);button.setEllipsize(android.text.TextUtils.TruncateAt.END);button.setTextColor(android.graphics.Color.WHITE);button.setGravity(Gravity.CENTER_VERTICAL);button.setPadding(PreviewDialog.dp(c,child?24:10),0,PreviewDialog.dp(c,10),0);PreviewIcon.apply(button,name,22);button.setFocusable(true);button.setFocusableInTouchMode(true);button.setBackground(com.archos.mediacenter.video.leanback.SharedThreePanel.accentFocus(c));com.archos.mediacenter.video.leanback.SharedThreePanel.rowSeparator(button);return button;}
```

## SITE-96fdab1e85b7 · VideoSettingsLicencesFragment.onCreateView

[Source line 91](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesFragment.java#L91)

```java
android.widget.TextView title=new android.widget.TextView(requireContext());title.setText("Licences & acknowledgements");title.setTextColor(0xffc8e2f3);title.setTextSize(20);title.setPadding(0,0,0,pad/2);panel.addView(title);panel.addView(list,new android.widget.LinearLayout.LayoutParams(-1,0,1));return panel;
```

## SITE-cf7793567148 · VideoSettingsMoreLeanbackFragment.onCreateView

[Source line 96](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackFragment.java#L96)

```java
android.widget.TextView title=new android.widget.TextView(requireContext());title.setText("Home & Discovery · More options");title.setTextColor(0xffc8e2f3);title.setTextSize(20);title.setPadding(0,0,0,pad/2);panel.addView(title);panel.addView(list,new android.widget.LinearLayout.LayoutParams(-1,0,1));return panel;
```
