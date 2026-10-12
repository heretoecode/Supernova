# 08-playback-subtitles — exact source expressions

Generated read-only from signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.

## SITE-661f34d4b6b9 · DialogRetrieveSubtitles.onCreateDialog

[Source line 45](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/browser/dialogs/DialogRetrieveSubtitles.java#L45)

```java
npd.setMessage(getString(R.string.dialog_subloader_copying));
```

Resolved English: `{"dialog_subloader_copying": "Copying subtitles…"}`

## SITE-0c1f3883a21f · PreviewSubtitleChooser.render

[Source line 68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L68)

```java
if(!heading){labels.add(associated?"— Associated with this title":"— Other subtitle files");rows.add(null);heading=true;}
```

## SITE-2c0622e56a1b · PreviewSubtitleChooser.render

[Source line 71](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L71)

```java
labels.add(entry.name+" · "+entry.source+(sameFile(active,entry.path)?" · Active":""));rows.add(entry);
```

## SITE-23b2cce692a7 · PreviewSubtitleChooser.render

[Source line 74](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L74)

```java
choices=PreviewDialog.choose(host,"Choose Subtitles",labels.toArray(new String[0]),focus,checked,false,index->{Entry entry=rows.get(index);if(entry!=null){focusPath=entry.path;review(entry);}});
```

## SITE-968b1e0c3955 · PreviewSubtitleChooser.review

[Source line 80](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L80)

```java
if(entry.associated&&playback){labels.add("Use Subtitle");actions.add(()->selected(entry.path));}
```

## SITE-0e7c7fff9484 · PreviewSubtitleChooser.review

[Source line 81](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L81)

```java
if(!entry.associated){labels.add("Associate with This Title"+(entry.writable?"":" — unavailable"));actions.add(()->{
```

## SITE-caec15d6903e · PreviewSubtitleChooser.review

[Source line 83](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L83)

```java
child=trackChild(PreviewDialog.review(host,"Associate Subtitle",entry.name+"\n\nThis renames the subtitle file to associate it with this title. The media file is not changed.","Associate",()->mutate(entry,false)));
```

## SITE-c24ad47d8d64 · PreviewSubtitleChooser.review

[Source line 85](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L85)

```java
labels.add("File Information");actions.add(()->{child=trackChild(PreviewOperationDialog.notice(host,"Subtitle File",entry.name+"\n"+entry.source+"\n"+entry.size+"\n"+(entry.associated?"Associated with this title":"Not associated with this title")+(sameFile(host.getIntent().getStringExtra(ACTIVE_PATH),entry.path)?"\nActive in playback":""),()->{}));});
```

## SITE-61b0f87ef8dc · PreviewSubtitleChooser.review

[Source line 86](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L86)

```java
labels.add("Delete Subtitle File"+(entry.writable?"":" — unavailable"));actions.add(()->{
```

## SITE-94934062b168 · PreviewSubtitleChooser.review

[Source line 87](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L87)

```java
if(entry.writable)child=trackChild(PreviewDialog.confirmDelete(host,"Delete Subtitle File",entry.name+"\nThis deletes the subtitle file, not the video.",()->mutate(entry,true)));
```

## SITE-92c0adb04622 · PreviewSubtitleChooser.review

[Source line 89](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java#L89)

```java
child=trackChild(PreviewDialog.choose(host,"Subtitle Actions",labels.toArray(new String[0]),-1,Collections.emptySet(),false,index->actions.get(index).run()));
```

## SITE-46bbe1b1e656 · PreviewSubtitleResults.show

[Source line 15](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java#L15)

```java
parent[0]=PreviewDialog.choose(context,"Download Subtitles",labels,-1,Collections.emptySet(),false,index->{
```

## SITE-fe27b4d4cdc7 · PreviewSubtitleResults.show

[Source line 20](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java#L20)

```java
PreviewDialog.review(context,"Subtitle Download",details,"Download",()->{
```

## SITE-cde34fc48070 · SubtitlesWizardFragment.createActions

[Source line 163](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/wizard/SubtitlesWizardFragment.java#L163)

```java
ActionData deleteData = new ActionData(true, fileId);
```

## SITE-b905f4fbd281 · SubtitlesWizardFragment.createActions

[Source line 206](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/wizard/SubtitlesWizardFragment.java#L206)

```java
ActionData associateData = new ActionData(false, fileId);
```

## SITE-77f783bd1b47 · SubtitlesWizardFragment.createActions

[Source line 215](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/wizard/SubtitlesWizardFragment.java#L215)

```java
ActionData deleteData = new ActionData(true, fileId);
```

## SITE-55c178bb04d4 · AudioDelayPicker.class scope

[Source line 54](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java#L54)

```java
mMinusButton.setText("-");
```

## SITE-85bdcd84a92d · AudioDelayPicker.onTouch

[Source line 79](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java#L79)

```java
mPlusButton.setText("+");
```

## SITE-9f7ff461dfb2 · AudioDelayPicker.setEnabled

[Source line 110](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java#L110)

```java
super.setEnabled(enabled);
```

## SITE-6d3f0eae528c · AudioDelayPicker.updateDelay

[Source line 117](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java#L117)

```java
mAudioDelayTv.setText(getFormattedDelay().toString());
```

## SITE-d4ee407806c0 · AudioDelayPicker.updateButtons

[Source line 134](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java#L134)

```java
mMinusButton.setEnabled(isEnabled() && (!hasMin || getDelay() > mMin));
```

## SITE-48bdf666ab32 · AudioDelayPicker.updateButtons

[Source line 135](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java#L135)

```java
mPlusButton.setEnabled(isEnabled() && (!hasMax || getDelay() < mMax));
```

## SITE-9a8622ed41fd · AudioDelayPickerAbstract.setEnabled

[Source line 74](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerAbstract.java#L74)

```java
super.setEnabled(enabled);
```

## SITE-7c4534f1122b · AudioSpeedPicker.class scope

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java#L61)

```java
mMinusButton.setText("-");
```

## SITE-2f01bcebecb4 · AudioSpeedPicker.onTouch

[Source line 90](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java#L90)

```java
mPlusButton.setText("+");
```

## SITE-75ea8f04b8a0 · AudioSpeedPicker.setEnabled

[Source line 125](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java#L125)

```java
super.setEnabled(enabled);
```

## SITE-69ac2f88953e · AudioSpeedPicker.setEnabled

[Source line 126](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java#L126)

```java
mMinusButton.setEnabled(enabled);
```

## SITE-f1a6b4858560 · AudioSpeedPicker.setEnabled

[Source line 127](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java#L127)

```java
mPlusButton.setEnabled(enabled);
```

## SITE-ede2aff81b19 · AudioSpeedPicker.updateSpeed

[Source line 133](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java#L133)

```java
mAudioSpeedTv.setText(getFormattedSpeed().toString());
```

## SITE-12f1bc7f8745 · AudioSpeedPickerAbstract.setEnabled

[Source line 67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerAbstract.java#L67)

```java
super.setEnabled(enabled);
```

## SITE-2c6ee7b9a21d · BrightnessDialog.onStart

[Source line 123](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java#L123)

```java
mSeekBar.setEnabled(true);
```

## SITE-4ff1fe1e45d7 · BrightnessDialog.onStart

[Source line 124](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java#L124)

```java
mSaveSetting.setEnabled(true);
```

## SITE-15538059012c · BrightnessDialog.onStart

[Source line 130](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java#L130)

```java
mSeekBar.setEnabled(false);
```

## SITE-fd42b9a5bcc8 · BrightnessDialog.onStart

[Source line 131](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java#L131)

```java
mSaveSetting.setEnabled(false);
```

## SITE-ab3fe2237b9a · BrightnessDialog.onCheckedChanged

[Source line 214](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java#L214)

```java
mSeekBar.setEnabled(!b);
```

## SITE-89b8c22f4406 · BrightnessDialog.onCheckedChanged

[Source line 215](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java#L215)

```java
mSaveSetting.setEnabled(!b);
```

## SITE-2b79a785cbd0 · ExternalPlayerService.createNotification

[Source line 91](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/ExternalPlayerService.java#L91)

```java
.addAction(0, getString(R.string.stop), createStopServicePendingIntent())
```

Resolved English: `{"stop": "Stop"}`

## SITE-e6a171aef96d · FloatingPlayerService.onCreate

[Source line 136](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java#L136)

```java
filter.addAction("DISPLAY_FLOATING_PLAYER");
```

## SITE-17da980f5825 · FloatingPlayerService.onCreate

[Source line 137](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java#L137)

```java
filter.addAction(PlayerService.PLAY_INTENT);
```

## SITE-77141bdd1d35 · FloatingPlayerService.onCreate

[Source line 138](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java#L138)

```java
filter.addAction(PlayerService.PAUSE_INTENT);
```

## SITE-64f7271d2fd9 · FloatingPlayerService.onCreate

[Source line 139](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java#L139)

```java
filter.addAction(PlayerService.EXIT_INTENT);
```

## SITE-d95ab40b6207 · FloatingPlayerService.onCreate

[Source line 140](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java#L140)

```java
filter.addAction(PlayerService.FULLSCREEN_INTENT);
```

## SITE-332a1f5cf3f3 · FloatingPlayerService.handleMessage

[Source line 599](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java#L599)

```java
((TextView)torrent_status).setText(toDisplay);
```

## SITE-f114c2c260cf · PlayerActivity.class scope

[Source line 279](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L279)

```java
private static final String[] GENERIC_TEXT_SUBTITLE_FORMATS = {"srt", "vtt"};
```

## SITE-4c6456c00f37 · PlayerActivity.handleMessage

[Source line 341](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L341)

```java
((TextView)torrent_status).setText(toDisplay);
```

## SITE-e9a29564db42 · PlayerActivity.onStart

[Source line 964](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L964)

```java
intentFilter.addAction(Intent.ACTION_SHUTDOWN);
```

## SITE-488c8a7fa49c · PlayerActivity.onStart

[Source line 965](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L965)

```java
intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
```

## SITE-7b74d30d58ea · PlayerActivity.onStart

[Source line 966](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L966)

```java
intentFilter.addAction(PlayerService.PLAYER_SERVICE_STARTED);
```

## SITE-c3380f231142 · PlayerActivity.onStart

[Source line 967](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L967)

```java
intentFilter.addAction(ACTION_HDMI_PLUGGED);
```

## SITE-04eb8aab6ce0 · PlayerActivity.createTVTimerDialog

[Source line 1674](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1674)

```java
((TVCardDialog)dialogMainView.findViewById(R.id.card_view)).setText((String) getText(R.string.sleep_timer_title));
```

Resolved English: `{"sleep_timer_title": "Sleep timer"}`

## SITE-1707f872fed6 · PlayerActivity.createTVSubtitleDialog

[Source line 1714](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1714)

```java
((TVCardDialog)dialogMainView.findViewById(R.id.card_view)).setText((String) getText(R.string.player_pref_subtitle_delay_title));
```

Resolved English: `{"player_pref_subtitle_delay_title": "Delay"}`

## SITE-c9e702e3eeb4 · PlayerActivity.createTVSubtitleSettingsDialog

[Source line 1773](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1773)

```java
((TVCardDialog)dialogMainView.findViewById(R.id.card_view)).setText((String) getText(R.string.menu_player_settings));
```

Resolved English: `{"menu_player_settings": "Settings"}`

## SITE-9772c5194eed · PlayerActivity.createTVSubtitleSettingsDialog

[Source line 1788](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1788)

```java
tvPicker.setText(getTVSizeText(mSubtitleManager.getSize()));
```

## SITE-9f12e509f5e4 · PlayerActivity.onDelayChanged

[Source line 1798](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1798)

```java
tvPicker.setText(getTVSizeText(delay / 100));
```

## SITE-dbff2e42da17 · PlayerActivity.onClick

[Source line 1877](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1877)

```java
tvPickerOpacity.setText(String.valueOf(mSubtitleManager.getBackgroundOpacity()));
```

## SITE-89750d524882 · PlayerActivity.onDelayChanged

[Source line 1885](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1885)

```java
tvPickerOpacity.setText(String.valueOf(opacity));
```

## SITE-2bc9c7a45426 · PlayerActivity.createTVAudioDelayDialog

[Source line 1914](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1914)

```java
((TVCardDialog) dialogView).setText((String) getText(R.string.player_pref_audio_delay_title));
```

Resolved English: `{"player_pref_audio_delay_title": "Audio delay"}`

## SITE-4c95f90b39c0 · PlayerActivity.createTVAudioSpeedDialog

[Source line 1996](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L1996)

```java
((TVCardDialog) dialogView).setText((String) getText(R.string.player_pref_audio_speed_title));
```

Resolved English: `{"player_pref_audio_speed_title": "Playback speed"}`

## SITE-7899298f260d · PlayerActivity.refreshPlayModeIntroSummary

[Source line 2240](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2240)

```java
mIntroSummaryMenuItem.setText(summary);
```

## SITE-9cc80fb7ef29 · PlayerActivity.onClick

[Source line 2327](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2327)

```java
tcv.setText(decoder);
```

## SITE-d30a8429fca3 · PlayerActivity.onCreateOptionsMenu

[Source line 2617](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2617)

```java
menuItem.setVisible(VideoPreferencesCommon.isAudioSpeedEnabled(mPreferences));
```

## SITE-fd1f4366731a · PlayerActivity.onCreateOptionsMenu

[Source line 2622](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2622)

```java
menuItem.setEnabled(isSpatializationToggleAvailable());
```

## SITE-9c35c4b332a1 · PlayerActivity.onCreateOptionsMenu

[Source line 2640](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2640)

```java
mBrightnessMenuItem.setVisible(brightnessMode == 0);
```

## SITE-340313766b2c · PlayerActivity.onPrepareOptionsMenu

[Source line 2659](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2659)

```java
mInfoMenuItem.setVisible(mStreamingUri != null);
```

## SITE-d1005828370a · PlayerActivity.onPrepareOptionsMenu

[Source line 2665](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2665)

```java
mBookmarkMenuItem.setVisible(canSetBookmark());
```

## SITE-001c7cd27e13 · PlayerActivity.onPrepareOptionsMenu

[Source line 2667](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2667)

```java
menu.findItem(MENU_S3D_ID).setVisible(isStereoEffectOn());
```

## SITE-030a7f7d2265 · PlayerActivity.onPrepareOptionsMenu

[Source line 2670](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2670)

```java
menu.findItem(MENU_SPATIALIZATION_ID).setEnabled(isSpatializationToggleAvailable());
```

## SITE-1bd5e7cfbb19 · PlayerActivity.onPrepareOptionsMenu

[Source line 2673](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2673)

```java
menu.findItem(MENU_WINDOW_MODE).setVisible(mPreferences.getBoolean(KEY_ADVANCED_VIDEO_ENABLED, false));*/
```

## SITE-225e0a5feb9a · PlayerActivity.onOptionsItemSelected

[Source line 2727](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2727)

```java
adb.setTitle(R.string.notification_mode);
```

Resolved English: `{"notification_mode": "Notifications"}`

## SITE-dc9dc800ef60 · PlayerActivity.onClick

[Source line 2739](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2739)

```java
adb.setTitle(R.string.pref_play_mode_title);
```

Resolved English: `{"pref_play_mode_title": "Play mode"}`

## SITE-0b923ddc49ad · PlayerActivity.onClick

[Source line 2751](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2751)

```java
rb.setText(playModeEntries[i]);
```

## SITE-bd982f124616 · PlayerActivity.onClick

[Source line 2767](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2767)

```java
if(BuildConfig.FOUNDATION)tb.setText("Segment Skipping (this session)");else tb.setText(R.string.pref_introdb_autoskip_title);
```

Resolved English: `{"pref_introdb_autoskip_title": "Skip intro/outro"}`

## SITE-5a6257869130 · PlayerActivity.onCheckedChanged

[Source line 2782](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2782)

```java
footer.setText(introSummaryPhone);
```

## SITE-d89e7f0ccf83 · PlayerActivity.onCheckedChanged

[Source line 2783](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2783)

```java
footer.setEnabled(false);
```

## SITE-a80973dc2f11 · PlayerActivity.onCheckedChanged

[Source line 2797](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2797)

```java
adb.setTitle(R.string.pref_audio_parameters_title);
```

Resolved English: `{"pref_audio_parameters_title": "Sound"}`

## SITE-9005e6dd8c04 · PlayerActivity.onProgressChanged

[Source line 2830](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2830)

```java
tb.setText(R.string.pref_audio_filt_title);
```

Resolved English: `{"pref_audio_filt_title": "Audio Boost"}`

## SITE-e426f1dbfcfb · PlayerActivity.onProgressChanged

[Source line 2833](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2833)

```java
tb.setEnabled(!isPassthroughActive);
```

## SITE-88530083ebad · PlayerActivity.onCheckedChanged

[Source line 2844](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2844)

```java
tb.setText(R.string.pref_audio_filt_night_mode);
```

Resolved English: `{"pref_audio_filt_night_mode": "Night Mode"}`

## SITE-2564841b7026 · PlayerActivity.onCheckedChanged

[Source line 2847](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2847)

```java
tb.setEnabled(!isPassthroughActive);
```

## SITE-a98c1981ac7d · PlayerActivity.onClick

[Source line 2893](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2893)

```java
tb.setText(R.string.pref_s3d_mode_title);
```

Resolved English: `{"pref_s3d_mode_title": "3D mode"}`

## SITE-dbafb2c4f7fb · PlayerActivity.getView

[Source line 2911](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2911)

```java
rb.setText(t[position]);
```

## SITE-0b614d9fb1c9 · PlayerActivity.getView

[Source line 2912](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2912)

```java
rb.setEnabled(dialogInitItem!=VideoEffect.NORMAL_2D_MODE);
```

## SITE-e9d73932a765 · PlayerActivity.onClick

[Source line 2948](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2948)

```java
ad.getListView().setEnabled(false);
```

## SITE-113e4cbca726 · PlayerActivity.onCheckedChanged

[Source line 2957](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2957)

```java
ad.getListView().setEnabled(true);
```

## SITE-978fe13910cf · PlayerActivity.onCheckedChanged

[Source line 2959](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2959)

```java
rb.setEnabled(true);
```

## SITE-0c913e8c7cb4 · PlayerActivity.onCheckedChanged

[Source line 2964](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2964)

```java
ad.getListView().setEnabled(false);
```

## SITE-8d671969ac6d · PlayerActivity.onCheckedChanged

[Source line 2966](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2966)

```java
rb.setEnabled(false);
```

## SITE-df6988fb467e · PlayerActivity.displayFloatingWindowPermissionDialog

[Source line 2986](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L2986)

```java
new AlertDialog.Builder(this).setTitle(R.string.error).setMessage(R.string.error_permission_display_over_apps).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
```

Resolved English: `{"error": "An error occurred", "error_permission_display_over_apps": "Window mode needs special permissions to be displayed in front of other apps. Please, grant SUPERNOVA Video Player this permission to switch to window mode.", "allow": "Allow"}`

## SITE-026eebf50af4 · PlayerActivity.myShowDialog

[Source line 3157](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3157)

```java
.setTitle(R.string.player_err_cantplayvideo)
```

Resolved English: `{"player_err_cantplayvideo": "Cannot play video"}`

## SITE-e4beaff35a78 · PlayerActivity.myShowDialog

[Source line 3158](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3158)

```java
.setMessage(R.string.error_downloading_not_enough_space)
```

Resolved English: `{"error_downloading_not_enough_space": "Error while downloading torrent : not enough space"}`

## SITE-035eeb4e67fd · PlayerActivity.onClick

[Source line 3171](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3171)

```java
.setTitle(R.string.player_err_cantplayvideo)
```

Resolved English: `{"player_err_cantplayvideo": "Cannot play video"}`

## SITE-e51fd0ec39e9 · PlayerActivity.onClick

[Source line 3172](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3172)

```java
.setMessage(buildErrorMessage(mErrorCode, mErrorQualCode, 0, mErrorMsg))
```

## SITE-390b8fffa174 · PlayerActivity.onClick

[Source line 3197](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3197)

```java
.setTitle("Incompatible device")
```

## SITE-2363c2af9ef4 · PlayerActivity.onClick

[Source line 3198](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3198)

```java
.setMessage("This application runs only on Amazon Kindle")
```

## SITE-992a78b16e6a · PlayerActivity.getPluginNeedUpdateDialog

[Source line 3226](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3226)

```java
.setTitle(R.string.plugin_update_required_title)
```

Resolved English: `{"plugin_update_required_title": "Codec update required"}`

## SITE-c890ace5b30e · PlayerActivity.getPluginNeedUpdateDialog

[Source line 3227](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3227)

```java
.setMessage(R.string.plugin_update_required_message)
```

Resolved English: `{"plugin_update_required_message": "It looks like you already have additional codecs installed for Nova Video Player.\\n\\nThis new version of Nova Video Player requires an update of these codecs.\\n\\nPlease go to Google Play to update and re-install the codecs."}`

## SITE-f99c6427ac2a · PlayerActivity.postVideoInfoAndPrepared

[Source line 3381](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3381)

```java
mInfoMenuItem.setVisible(mStreamingUri != null);
```

## SITE-010b91c70c5e · PlayerActivity.postVideoInfoAndPrepared

[Source line 3385](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3385)

```java
mBookmarkMenuItem.setVisible(canSetBookmark());
```

## SITE-0f4d0b800825 · PlayerActivity.start

[Source line 3424](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L3424)

```java
mBufferView.setText("");
```

## SITE-bc3e23e1b183 · PlayerActivity.onOSDUpdate

[Source line 4387](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L4387)

```java
mPlayerController.setEnabled(true);
```

## SITE-f437f5e9129b · PlayerActivity.onBufferingUpdate

[Source line 4537](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L4537)

```java
mBufferView.setText(" "+percent+"%");
```

## SITE-4f1e3f21d575 · PlayerActivity.onVideoDb

[Source line 4600](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java#L4600)

```java
builder.setMessage(R.string.use_remote_resume)
```

Resolved English: `{"use_remote_resume": "Use network resume position?"}`

## SITE-d09bbc3953d0 · PlayerController.reset

[Source line 386](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L386)

```java
mClock.setText(getDateFormat().format(new Date()));
```

## SITE-64c00a4fed75 · PlayerController.initMenuAdapter

[Source line 426](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L426)

```java
mTVMenuAdapter.setOnFocusOutListener(new TVCardView.onFocusOutListener() {
```

## SITE-fd76ad5d70be · PlayerController.onKey

[Source line 752](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L752)

```java
if (title != null && mVideoTitle != null) title.setText(mVideoTitle.getText());
```

## SITE-905bced22961 · PlayerController.updatePauseButton

[Source line 854](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L854)

```java
mPauseButton.setEnabled(mEnabled && Player.sPlayer.canPause() && !Player.sPlayer.isBusy());
```

## SITE-2614513b0f4b · PlayerController.updatePauseButton

[Source line 856](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L856)

```java
mPauseButton2.setEnabled(mEnabled && Player.sPlayer.canPause() && !Player.sPlayer.isBusy());
```

## SITE-dfe6751d37c5 · PlayerController.updatePauseButton

[Source line 859](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L859)

```java
mFormatButton.setEnabled(mEnabled);
```

## SITE-c3a83d762b7f · PlayerController.updateButtons

[Source line 869](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L869)

```java
mBackwardButton.setEnabled(mEnabled && Player.sPlayer.canSeekBackward());
```

## SITE-5b7f0ef1bf71 · PlayerController.updateButtons

[Source line 872](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L872)

```java
mForwardButton.setEnabled(mEnabled && Player.sPlayer.canSeekForward());
```

## SITE-217d11f6080a · PlayerController.updateButtons

[Source line 875](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L875)

```java
mProgress.setEnabled(mEnabled && (Player.sPlayer.canSeekForward() || Player.sPlayer.canSeekBackward()));
```

## SITE-66771e9c6435 · PlayerController.setProgress

[Source line 1374](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1374)

```java
mEndTime.setText(endText);
```

## SITE-8725970b98ae · PlayerController.setProgress

[Source line 1376](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1376)

```java
mEndTime2.setText(endText);
```

## SITE-c171ea9d4043 · PlayerController.setProgress

[Source line 1379](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1379)

```java
mCurrentTime.setText(currentText);updateClock();updatePreviewScrubber();
```

## SITE-a78e04bb087b · PlayerController.setProgress

[Source line 1381](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1381)

```java
mCurrentTime2.setText(currentText);
```

## SITE-772a77501edf · PlayerController.updatePausePlay

[Source line 1417](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1417)

```java
if(experimentalUi()&&mControllerViewLeft!=null){TextView label=mControllerViewLeft.findViewById(R.id.preview_pause_label);if(label!=null)label.setText(mSeekWasPlaying||Player.sPlayer.isPlaying()?"Pause":"Play");}
```

## SITE-983321339e59 · PlayerController.refreshPreviewContext

[Source line 1496](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1496)

```java
private void refreshPreviewContext(){if(!experimentalUi()||mControllerViewLeft==null||!(mContext instanceof PlayerActivity))return;PlayerActivity a=(PlayerActivity)mContext;View audio=mControllerViewLeft.findViewById(R.id.preview_audio);if(audio!=null){audio.setVisibility(View.VISIBLE);audio.setEnabled(true);audio.setAlpha(a.previewHasAudio()?1f:.5f);}mPauseButton.setNextFocusLeftId(R.id.pause);for(int id:new int[]{R.id.preview_previous,R.id.preview_next}){View step=mControllerViewLeft.findViewById(id);if(step!=null)step.setVisibility(View.GONE);}for(int id:new int[]{R.id.preview_audio_label,R.id.preview_subtitle_label}){TextView label=mControllerViewLeft.findViewById(id);if(label!=null)label.setText(id==R.id.preview_audio_label?"Audio":"Subtitles");}TextView title=mControllerViewLeft.findViewById(R.id.preview_playback_title),episode=mControllerViewLeft.findViewById(R.id.preview_playback_episode);if(title!=null){title.setText(a.previewTitle());a.bindPreviewTitleArtwork(title);}if(episode!=null){episode.setText(a.previewEpisode());episode.setVisibility(mControlBarShowing&&!a.previewEpisode().isEmpty()?View.VISIBLE:View.GONE);}}
```

## SITE-aeca6515bb13 · PlayerController.updatePreviewScrubber

[Source line 1512](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1512)

```java
bubble.setVisibility(View.VISIBLE);bubble.setText(mCurrentTime==null?"":mCurrentTime.getText());
```

## SITE-f70a6e0f0bb2 · PlayerController.setVideoTitle

[Source line 1518](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1518)

```java
mVideoTitle.setText(title);
```

## SITE-82ce2132660e · PlayerController.setVideoTitle

[Source line 1521](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1521)

```java
if (hudTitle != null) hudTitle.setText(title);refreshPreviewContext();
```

## SITE-43b516b8ea92 · PlayerController.onProgressChanged

[Source line 1774](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1774)

```java
mEndTime.setText(endText);
```

## SITE-663d4642c0d5 · PlayerController.onProgressChanged

[Source line 1777](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L1777)

```java
mCurrentTime.setText(currentText);updateClock();updatePreviewScrubber();
```

## SITE-0324e4b20bc6 · PlayerController.onDoubleTapEvent

[Source line 2181](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2181)

```java
mOsdLeftTextView.setText("");
```

## SITE-516ddc87d307 · PlayerController.onDoubleTapEvent

[Source line 2202](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2202)

```java
mOsdRightTextView.setText("");
```

## SITE-8c8cce70b628 · PlayerController.scrollIncrementalVolumeUpdate

[Source line 2263](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2263)

```java
mOsdLeftTextView.setText(String.valueOf(currentVolume));
```

## SITE-455b94a6a6ff · PlayerController.scrollIncrementalVolumeUpdate

[Source line 2270](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2270)

```java
if (mOsdLeftTextView != null) mOsdLeftTextView.setText(String.valueOf(newVolume));
```

## SITE-330579b59a0f · PlayerController.scrollIncrementalBrightnessUpdate

[Source line 2287](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2287)

```java
mOsdRightTextView.setText(String.valueOf(currentIntBrightness));
```

## SITE-c1499d872edc · PlayerController.showAudioSpeedOSD

[Source line 2303](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2303)

```java
mOsdRightTextView.setText(speedText);
```

## SITE-e9863a58c164 · PlayerController.updateClock

[Source line 2912](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2912)

```java
if(mClock==null)return;long now=System.currentTimeMillis();String currentClockText=getDateFormat().format(new Date(now));mClock.setText(currentClockText);
```

## SITE-2f1a544bee83 · PlayerController.updateClock

[Source line 2915](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2915)

```java
if(mEndTime!=null){boolean episode=mContext instanceof PlayerActivity&&!((PlayerActivity)mContext).previewEpisode().isEmpty();long end=PreviewHudTiming.end(now,duration,position,speed);mEndTime.setText((duration>0?stringForTime(duration):"—")+(end>=0?" · "+(episode?"Episode Ends ":"Movie Ends ")+getDateFormat().format(new Date(end)):""));}
```

## SITE-659cbdfb97fc · PlayerController.updateClock

[Source line 2916](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerController.java#L2916)

```java
}else if(duration>0&&duration>position)mClock.setText(Clock.formatTimeWithArrow(currentClockText,getDateFormat().format(new Date(PreviewHudTiming.end(now,duration,position,speed)))));
```

## SITE-3873e8f6fc5c · PlayerService.select

[Source line 320](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L320)

```java
private static final String[] GENERIC_TEXT_SUBTITLE_FORMATS = {"srt", "vtt"};
```

## SITE-cbb724e61c50 · PlayerService.startStatusbarNotification

[Source line 820](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L820)

```java
nb.addAction(new NotificationCompat.Action(R.drawable.video_pause, getString(R.string.floating_player_pause), pausePendingIntent));
```

Resolved English: `{"floating_player_pause": "Pause"}`

## SITE-cf82f3672a9a · PlayerService.startStatusbarNotification

[Source line 822](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L822)

```java
nb.addAction(new NotificationCompat.Action(R.drawable.video_play, getString(R.string.floating_player_play), playPendingIntent));
```

Resolved English: `{"floating_player_play": "Play"}`

## SITE-51782ce8a762 · PlayerService.startStatusbarNotification

[Source line 827](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L827)

```java
nb.addAction(new NotificationCompat.Action(R.drawable.ic_menu_unfade, getString(R.string.floating_player_restore), contentIntent));
```

Resolved English: `{"floating_player_restore": "Restore"}`

## SITE-5b9125d999a5 · PlayerService.startStatusbarNotification

[Source line 829](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L829)

```java
nb.addAction(new NotificationCompat.Action(R.drawable.video_format_fullscreen, getString(R.string.format_fullscreen), fullscreenPendingIntent));
```

Resolved English: `{"format_fullscreen": "Fullscreen"}`

## SITE-d0ab5e08fe00 · PlayerService.stopTrakt

[Source line 1431](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L1431)

```java
String[] whereArgs = new String[]{Long.toString(mVideoInfo.id)};
```

## SITE-f9666df7d436 · PlayerService.onSubtitleMetadataUpdated

[Source line 2421](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PlayerService.java#L2421)

```java
if (log.isDebugEnabled()) log.debug("onSubtitleMetadataUpdated: no default sub found mVideoInfo.subtitleTrack: {} -> setting {} or external text ({}) track if exists -> videoInfo.subtitleTrack={}", mVideoInfo.subtitleTrack, revertTrackName, fallbackTextTrack, newTrack);
```

## SITE-327902f9b523 · PreviewPlaybackInfo.show

[Source line 9](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackInfo.java#L9)

```java
if(media instanceof Video){Video v=(Video)media;boolean same=v instanceof Episode?title.equals(((Episode)v).getShowName()):title.equals(v.getName());if(same){String meta=(v instanceof Movie&&((Movie)v).getYear()>0?((Movie)v).getYear()+"  ·  ":"")+(v.getDurationMs()>0?v.getDurationMs()/60000+" min":"");label(text,meta,12);label(text,v.getDescriptionBody(),14);java.util.concurrent.ExecutorService metadata=java.util.concurrent.Executors.newSingleThreadExecutor();metadata.execute(()->{try{com.archos.mediascraper.BaseTags tags=v.getFullScraperTags(a.getApplicationContext());if(tags!=null)text.post(()->{if(!d.isShowing())return;String director=tags.getDirectorsFormatted(),writers=tags.getWritersFormatted();StringBuilder subset=new StringBuilder(),complete=new StringBuilder();if(director!=null&&!director.isEmpty()){label(text,"Director  ·  "+director,12);complete.append("Director · ").append(director).append("\n\n");}if(writers!=null&&!writers.isEmpty())complete.append("Writers · ").append(writers).append("\n\n");int count=0;for(java.util.Map.Entry<String,String> person:tags.getActors().entrySet()){String line=person.getKey()+(person.getValue()==null||person.getValue().isEmpty()?"":" · "+person.getValue());complete.append(line).append("\n");if(count++<3)subset.append(line).append("\n");}if(subset.length()>0)label(text,"Cast & Crew\n"+subset,12);if(complete.length()>0){TextView all=new TextView(a);all.setText("See All Cast & Crew");all.setTextColor(-1);all.setTextSize(13);all.setPadding(dp(a,10),dp(a,8),dp(a,10),dp(a,8));all.setFocusable(true);all.setBackground(PreviewDialog.buttonFocus(a));all.setOnClickListener(vw->PreviewDialog.read(a,"Cast & Crew",complete.toString()));text.addView(all);}});}catch(Exception unavailable){/* Existing local metadata is optional. */}finally{metadata.shutdown();}});}}
```

## SITE-a3bc68aa729a · PreviewPlaybackInfo.show

[Source line 11](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackInfo.java#L11)

```java
String[] labels={"Resume","Play from beginning","File and technical details"};for(int i=0;i<labels.length;i++){final int index=i;TextView b=new TextView(a);b.setText(labels[i]);b.setTextColor(-1);b.setTextSize(13);b.setPadding(dp(a,10),0,dp(a,10),0);b.setGravity(Gravity.CENTER_VERTICAL);PreviewIcon.apply(b,labels[i],17);b.setBackground(PreviewDialog.focus(a));b.setFocusable(i<3);b.setEnabled(i<3);if(i==3)b.setAlpha(.4f);b.setOnClickListener(v->{d.dismiss();if(index==1)restart.run();if(index==2)full.run();});actions.addView(b,new LinearLayout.LayoutParams(-1,dp(a,38)));}
```

## SITE-ceca4d155219 · PreviewPlaybackInfo.dp

[Source line 14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackInfo.java#L14)

```java
private static int dp(Activity a,int v){return PreviewDialog.dp(a,v);}private static void label(LinearLayout p,String s,int size){if(s==null||s.isEmpty())return;TextView t=new TextView(p.getContext());t.setText(s);t.setTextSize(size);t.setTextColor(size>=19?-1:0xffbfd1de);t.setShadowLayer(3f,0f,1f,android.graphics.Color.BLACK);t.setMaxLines(size>=19||size==12?2:4);t.setEllipsize(android.text.TextUtils.TruncateAt.END);t.setPadding(0,0,0,PreviewDialog.dp(p.getContext(),10));p.addView(t);}
```

## SITE-a3ac33536a5c · PreviewPlaybackLoading.class scope

[Source line 29](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L29)

```java
title=text("",32);title.setTypeface(null,android.graphics.Typeface.BOLD);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);labels.addView(title);
```

## SITE-a68b4c550cd9 · PreviewPlaybackLoading.class scope

[Source line 30](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L30)

```java
episode=text("",16);episode.setMaxLines(2);episode.setEllipsize(android.text.TextUtils.TruncateAt.END);episode.setPadding(0,dp(10),0,dp(20));labels.addView(episode);
```

## SITE-2f8f27e1cf6d · PreviewPlaybackLoading.class scope

[Source line 31](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L31)

```java
LinearLayout status=new LinearLayout(context);status.setGravity(Gravity.CENTER_VERTICAL);status.setPadding(0,dp(18),0,0);labels.addView(status);
```

## SITE-9a94ec65a1e0 · PreviewPlaybackLoading.class scope

[Source line 33](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L33)

```java
TextView starting=text("Preparing playback…",16);starting.setPadding(dp(14),0,dp(8),0);status.addView(starting);
```

## SITE-e7cc09665e5a · PreviewPlaybackLoading.class scope

[Source line 34](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L34)

```java
TextView buffer=text("",13);buffer.setId(R.id.buffer_percentage);status.addView(buffer);
```

## SITE-db97e2d519c6 · PreviewPlaybackLoading.class scope

[Source line 35](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L35)

```java
TextView torrent=text("",13);torrent.setId(R.id.torrent_status);torrent.setVisibility(GONE);labels.addView(torrent);
```

## SITE-0c0441dbd6e7 · PreviewPlaybackLoading.class scope

[Source line 39](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L39)

```java
title.setText(video instanceof Episode?((Episode)video).getShowName():video.getName());
```

## SITE-9f06e24ab99e · PreviewPlaybackLoading.class scope

[Source line 42](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L42)

```java
Episode selected=video instanceof Episode?(Episode)video:null;episode.setText(PreviewTaglines.cached(context,selected!=null,online,selected==null?0:selected.getSeasonNumber(),selected==null?0:selected.getEpisodeNumber()));
```

## SITE-ac9801a8cb42 · PreviewPlaybackLoading.reveal

[Source line 66](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L66)

```java
if(info==null){if(fallback!=null)title.setText(fallback);return;}
```

## SITE-81fc05f8eee8 · PreviewPlaybackLoading.reveal

[Source line 68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L68)

```java
title.setText(info.isScraped&&info.scraperTitle!=null?info.scraperTitle:fallback);
```

## SITE-c4dfa5b3cf13 · PreviewPlaybackLoading.reveal

[Source line 70](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L70)

```java
long id=0;try{id=Long.parseLong(info.isShow?info.scraperShowId:info.scraperMovieId);}catch(Exception unavailable){}episode.setText(PreviewTaglines.cached(getContext(),info.isShow,id,info.scraperSeasonNr,info.scraperEpisodeNr));episode.setVisibility(episode.length()==0?GONE:VISIBLE);
```

## SITE-44ce1939235d · PreviewPlaybackLoading.text

[Source line 92](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java#L92)

```java
private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextSize(size);t.setTextColor(Color.WHITE);return t;}
```

## SITE-826ee5f69be6 · PreviewPlaybackMenus.root

[Source line 60](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L60)

```java
labels.add("— PLAYBACK");actions.add(()->{});
```

## SITE-1c7e4087857c · PreviewPlaybackMenus.root

[Source line 61](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L61)

```java
if(speedItem!=null){final TVMenuItem item=speedItem;labels.add("Playback Speed");actions.add(()->{restoreParent=()->root(activity,adapter);item.previewClick();});}
```

## SITE-957001f9f051 · PreviewPlaybackMenus.root

[Source line 62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L62)

```java
for(TVCardView card:cards)if(activity.getString(R.string.pref_play_mode_title).equals(card.previewTitle())){labels.add("Play Mode");actions.add(()->select(activity,card,()->root(activity,adapter),-1,false));}
```

Resolved English: `{"pref_play_mode_title": "Play mode"}`

## SITE-edc82f008173 · PreviewPlaybackMenus.root

[Source line 63](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L63)

```java
labels.add("— VIDEO");actions.add(()->{});
```

## SITE-3e7f1b72b5f7 · PreviewPlaybackMenus.root

[Source line 64](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L64)

```java
for(TVCardView card:cards){String title=card.previewTitle();if(audio.equals(title)||subs.equals(title)||activity.getString(R.string.menu_info).equals(title)||activity.getString(R.string.pref_play_mode_title).equals(title)||activity.getString(R.string.preferences).equals(title))continue;labels.add(title);actions.add(()->select(activity,card,()->root(activity,adapter),-1,false));}
```

Resolved English: `{"menu_info": "Info", "pref_play_mode_title": "Play mode", "preferences": "Preferences"}`

## SITE-3013726e9fea · PreviewPlaybackMenus.root

[Source line 65](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L65)

```java
labels.add("Technical Information");actions.add(()->((PlayerActivity)activity).showVideoInfos());
```

## SITE-737bb980b3b0 · PreviewPlaybackMenus.root

[Source line 66](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L66)

```java
labels.add("Report a Problem");actions.add(()->com.archos.mediacenter.video.diagnostics.Diagnostics.reportProblem(activity));
```

## SITE-bddd75b95399 · PreviewPlaybackMenus.root

[Source line 67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L67)

```java
current=PreviewDialog.choose(activity,"More",labels.toArray(new String[0]),rootFocus,Collections.emptySet(),false,n->{rootFocus=n;actions.get(n).run();});
```

## SITE-dd213f776cd8 · PreviewPlaybackMenus.select

[Source line 85](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L85)

```java
if(!group.equals(lastGroup)){labels.add("— "+group);actions.add(null);lastGroup=group;}
```

## SITE-a417187bac71 · PreviewPlaybackMenus.select

[Source line 90](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L90)

```java
actions.add(item);labels.add(label+(item.isEnabled()&&item.isFocusable()?"":" — unavailable"));if(item.isChecked()){checked.add(actions.size()-1);if(selected<0)selected=actions.size()-1;}
```

## SITE-70fa36ff8900 · PreviewPlaybackMenus.select

[Source line 92](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L92)

```java
if(hasOther&&!otherLanguages){labels.add("— TRACK");actions.add(null);actions.add(null);labels.add("Other languages");}
```

## SITE-24951f194fb0 · PreviewPlaybackMenus.select

[Source line 93](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L93)

```java
if(settings!=null){labels.add("— TIMING & APPEARANCE");actions.add(null);actions.add(settings);labels.add("Subtitle Appearance"+(settings.isEnabled()&&settings.isFocusable()?"":" — unavailable"));}
```

## SITE-b90d155a7c09 · PreviewPlaybackMenus.select

[Source line 94](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L94)

```java
if(subtitles&&!otherLanguages){labels.add("Subtitle Settings");actions.add(null);}
```

## SITE-9c62e2986584 · PreviewPlaybackMenus.select

[Source line 97](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java#L97)

```java
current=PreviewDialog.choose(activity,otherLanguages?"Subtitles · Other languages":card.previewTitle(),labels.toArray(new String[0]),selected,checked,false,n->{
```

## SITE-cc6e73c52ca9 · PreviewTechnicalInfo.text

[Source line 26](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java#L26)

```java
private static android.widget.TextView text(Activity a,String value,int size){android.widget.TextView t=new android.widget.TextView(a);t.setText(value);t.setTextSize(size);t.setTextColor(0xffd2e1ed);return t;}
```

## SITE-c39f1e042f0e · PreviewTechnicalInfo.show

[Source line 39](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java#L39)

```java
String[] titles={"Source","Video","Audio","File"};String[] bodies={"Source: "+com.archos.mediacenter.video.diagnostics.Diagnostics.sourceType(uri),"Not available","Not available",metadata==null?"Not available":"File size: "+metadata.getFileSize()+" bytes"};
```

## SITE-750818e74cae · PreviewTechnicalInfo.show

[Source line 43](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java#L43)

```java
for(int i:new int[]{1,2,3,0}){android.widget.LinearLayout column=new android.widget.LinearLayout(activity);column.setOrientation(android.widget.LinearLayout.VERTICAL);column.setPadding(dp(activity,10),0,dp(activity,10),0);column.addView(text(activity,titles[i],15));android.widget.TextView body=text(activity,bodies[i],12);body.setPadding(0,dp(activity,12),0,0);column.addView(body);columns.addView(column,new android.widget.LinearLayout.LayoutParams(0,-2,1));}
```

## SITE-e9091591c4d9 · PreviewUpNext.run

[Source line 18](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java#L18)

```java
if(!dismissed&&next!=null&&service.previewAutoNextEnabled()&&duration>0&&position>=trigger(duration,service.getIntroSegments())){if(panel==null)show(next,service);if(!player.isPlaying()&&lastTick>0)deadline+=now-lastTick;long seconds=Math.max(0,(deadline-now+999)/1000);countdown.setText("UP NEXT  ·  Starting in "+seconds+"s");if(seconds==0){dismissed=true;remove();service.previewNavigateEpisode(1);}}
```

## SITE-8a012e0827c0 · PreviewUpNext.show

[Source line 21](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java#L21)

```java
private void show(Episode next,PlayerService service){deadline=android.os.SystemClock.elapsedRealtime()+5000;panel=new LinearLayout(activity);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(16),dp(12),dp(16),dp(12));android.graphics.drawable.GradientDrawable surface=PreviewDialog.surface(activity,false);surface.setColor(0xec08121b);surface.setCornerRadius(dp(10));panel.setBackground(surface);panel.setElevation(dp(8));countdown=text("Up Next",15);countdown.setTextColor(PreviewAccent.color(activity));panel.addView(countdown);if(next.getPictureUri()!=null){ImageView still=new ImageView(activity);still.setScaleType(ImageView.ScaleType.CENTER_CROP);LinearLayout.LayoutParams frame=new LinearLayout.LayoutParams(-1,dp(90));frame.topMargin=dp(8);panel.addView(still,frame);com.squareup.picasso.Picasso.get().load(next.getPictureUri()).resize(dp(283),dp(90)).centerCrop().networkPolicy(com.squareup.picasso.NetworkPolicy.OFFLINE).noFade().into(still);}TextView name=text("S"+next.getSeasonNumber()+" E"+next.getEpisodeNumber()+" · "+next.getEpisodeName(),14);name.setMaxLines(2);name.setPadding(0,dp(8),0,dp(8));panel.addView(name);LinearLayout actions=new LinearLayout(activity);for(String label:new String[]{"Play Now"}){TextView button=text(label,14);button.setFocusable(true);button.setBackground(PreviewDialog.focus(activity));button.setPadding(dp(12),dp(8),dp(12),dp(8));button.setOnClickListener(v->{dismissed=true;remove();if(label.equals("Play Now"))service.previewNavigateEpisode(1);else service.previewCancelAutoNext();});actions.addView(button);}panel.addView(actions);FrameLayout root=activity.findViewById(android.R.id.content);FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(dp(315),-2,Gravity.BOTTOM|Gravity.END);params.setMargins(dp(26),dp(20),dp(26),dp(24));root.addView(panel,params);}
```

## SITE-5ce8a25f6a91 · PreviewUpNext.segmentPill

[Source line 25](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java#L25)

```java
skipPill=com.archos.mediacenter.video.leanback.SharedThreePanel.action(activity,"Skip "+PlayerService.introLabels(activity).get(prompt.type),()->{service.skipPrompt();removeSkip();});
```

## SITE-8784f7856ec8 · PreviewUpNext.text

[Source line 45](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java#L45)

```java
private TextView text(String value,int size){TextView v=new TextView(activity);v.setText(value);v.setTextSize(size);v.setTextColor(0xffe7eff5);return v;}
```

## SITE-ad75d6d1eb83 · PrivateMode.showDialog

[Source line 64](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/PrivateMode.java#L64)

```java
.setTitle(R.string.private_mode)
```

Resolved English: `{"private_mode": "Private mode"}`

## SITE-509b775abc3c · SDKNotSupportedDialogActivity.onCreateDialog

[Source line 50](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SDKNotSupportedDialogActivity.java#L50)

```java
.setTitle("Oops...")
```

## SITE-2d32887a8453 · SDKNotSupportedDialogActivity.onCreateDialog

[Source line 51](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SDKNotSupportedDialogActivity.java#L51)

```java
.setMessage(R.string.sdk_not_supported)
```

Resolved English: `{"sdk_not_supported": "Android 4.3 is not supported yet.\\nWe\\'re working hard to fix it and hope to have a new version soon!"}`

## SITE-19f40879fbbc · StereoMergeArchosEffect.class scope

[Source line 32](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/StereoMergeArchosEffect.java#L32)

```java
private static final String[] S3D_MODE = { S3D_OFF, S3D_SBS, S3D_TOPBOTTOM, S3D_ANAGLYPH };
```

## SITE-ea1a9293b6c0 · Subtitle3DTextView.setText

[Source line 123](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/Subtitle3DTextView.java#L123)

```java
mPrimaryTV.setText(s);
```

## SITE-807be4e27335 · Subtitle3DTextView.setText

[Source line 125](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/Subtitle3DTextView.java#L125)

```java
mSecondaryTV.setText(s);
```

## SITE-9febd2d2f4e1 · Subtitle3DTextView.setText

[Source line 154](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/Subtitle3DTextView.java#L154)

```java
mPrimaryTV.setText(spannableStringBuilder);
```

## SITE-b29643bfedea · Subtitle3DTextView.setText

[Source line 156](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/Subtitle3DTextView.java#L156)

```java
mSecondaryTV.setText(spannableStringBuilder);
```

## SITE-991eabd436c3 · SubtitleDelayPicker.class scope

[Source line 67](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java#L67)

```java
mSignButton.setText("+");
```

## SITE-68b647e47a66 · SubtitleDelayPicker.setEnabled

[Source line 164](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java#L164)

```java
super.setEnabled(enabled);
```

## SITE-bb288f1d7141 · SubtitleDelayPicker.setEnabled

[Source line 165](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java#L165)

```java
mMinutePicker.setEnabled(enabled);
```

## SITE-5b24cc5b9534 · SubtitleDelayPicker.setEnabled

[Source line 166](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java#L166)

```java
mSecondPicker.setEnabled(enabled);
```

## SITE-640c24cabee5 · SubtitleDelayPicker.setEnabled

[Source line 167](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java#L167)

```java
mDeciSecondPicker.setEnabled(enabled);
```

## SITE-565dcbf0a3bb · SubtitleDelayPicker.updateSpinners

[Source line 184](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java#L184)

```java
mSignButton.setText(mSign == 1 ? "+" : "-");
```

## SITE-496e3b82b7fc · SubtitleDelayPickerAbstract.setEnabled

[Source line 77](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerAbstract.java#L77)

```java
super.setEnabled(enabled);
```

## SITE-9ff09c896568 · SubtitleManager.removeView

[Source line 178](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleManager.java#L178)

```java
mSubtitleTxtView.setText("");
```

## SITE-60218375ee90 · SubtitleManager.displayView

[Source line 224](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/SubtitleManager.java#L224)

```java
mSubtitleTxtView.setText(mSpannableStringBuilder);
```

## SITE-a2e908788add · TimerPickerAbstract.setEnabled

[Source line 68](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TimerPickerAbstract.java#L68)

```java
super.setEnabled(enabled);
```

## SITE-5ea7ec3e614d · TorrentLoaderActivity.handleMessage

[Source line 108](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TorrentLoaderActivity.java#L108)

```java
.setTitle(R.string.torrent_file_to_play)
```

Resolved English: `{"torrent_file_to_play": "Choose a file to play"}`

## SITE-5ecf4b2b42ca · TorrentLoaderActivity.showErrorDialog

[Source line 337](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TorrentLoaderActivity.java#L337)

```java
.setTitle(R.string.error_listing)
```

Resolved English: `{"error_listing": "An error occurred"}`

## SITE-6480011c39fc · TorrentLoaderActivity.showErrorDialog

[Source line 338](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TorrentLoaderActivity.java#L338)

```java
.setMessage(message)
```

## SITE-589be9802886 · TrackInfoController.getView

[Source line 199](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java#L199)

```java
((TextView) v.findViewById(R.id.name)).setText(spannableText);
```

## SITE-2c41e76ec1cb · TrackInfoController.getView

[Source line 204](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java#L204)

```java
textView.setText(trackInfo.summary);
```

## SITE-76ee507cfbbe · TrackInfoController.getView

[Source line 216](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java#L216)

```java
tv.setText(trackSettings.name);
```

## SITE-0cf1b3e03974 · TrackInfoController.getView

[Source line 217](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java#L217)

```java
tv.setEnabled(trackSettings.enabled);
```

## SITE-b984549aa8dd · TrackInfoController.setVisible

[Source line 415](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java#L415)

```java
mMenuItem.setVisible(getTrackCount()>1||mIsAlwaysDisplayed);
```

## SITE-9ffc4ddf2b68 · UpdateNextTask.setListener

[Source line 108](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L108)

```java
private final static String[] QUERY_COLUMNS_ID_DATA = { BaseColumns._ID, VideoStore.MediaColumns.DATA };
```

## SITE-9b8dc384c021 · UpdateNextTask.getNextInBucket

[Source line 125](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L125)

```java
new String[] { String.valueOf(bucketId), fullPathOfVideo },
```

## SITE-39c46d7aa728 · UpdateNextTask.getFirstInBucket

[Source line 134](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L134)

```java
new String[] { String.valueOf(bucketId) },
```

## SITE-084f7fdef341 · UpdateNextTask.findEpisode

[Source line 139](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L139)

```java
String [] projection2 = new String[]{VideoStore.MediaColumns.DATA,VideoStore.Files.FileColumns._ID,VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE};
```

## SITE-2d4de0e204bd · UpdateNextTask.findEpisode

[Source line 142](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L142)

```java
args2= new String[]{String.valueOf(episode),String.valueOf(show), String.valueOf(season)};
```

## SITE-6aeeed014071 · UpdateNextTask.findEpisode

[Source line 144](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L144)

```java
args2= new String[]{String.valueOf(show), String.valueOf(season)};
```

## SITE-5e9d44de0f24 · UpdateNextTask.run

[Source line 221](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L221)

```java
try (Cursor cursor = mResolver.query(VideoStore.RAW_QUERY, null, selection, new String[]{mPlaylistId + ""}, null)) {
```

## SITE-12461c0bb460 · UpdateNextTask.run

[Source line 262](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L262)

```java
String[] projection1 = new String[]{VideoStore.MediaColumns.DATA, VideoStore.Files.FileColumns._ID, VideoStore.Video.VideoColumns.SCRAPER_E_EPISODE, VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID, VideoStore.Video.VideoColumns.SCRAPER_E_SEASON};
```

## SITE-310ab997d623 · UpdateNextTask.run

[Source line 263](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java#L263)

```java
String[] args1 = new String[]{mUri.toString()};
```

## SITE-38a1852aa3d9 · AudioDelayTVPicker.setEnabled

[Source line 136](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java#L136)

```java
super.setEnabled(enabled);
```

## SITE-87a156bb2253 · AudioDelayTVPicker.setText

[Source line 273](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java#L273)

```java
((TextView)findViewById(R.id.text_delay)).setText(txt);
```

## SITE-da21055fc33b · AudioDelayTVPicker.setText

[Source line 275](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java#L275)

```java
slaveView.setText(txt);
```

## SITE-9fab6e089095 · AudioDelayTVPicker.setSlaveView

[Source line 329](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java#L329)

```java
slaveView.setText(getText());
```

## SITE-46ff1dc82aec · AudioSpeedTVPicker.setEnabled

[Source line 103](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java#L103)

```java
super.setEnabled(enabled);
```

## SITE-c2a92e805213 · AudioSpeedTVPicker.setText

[Source line 226](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java#L226)

```java
((TextView)findViewById(R.id.text_speed)).setText(txt);
```

## SITE-8b0253345bec · AudioSpeedTVPicker.setText

[Source line 228](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java#L228)

```java
slaveView.setText(txt);
```

## SITE-b646c911bee1 · AudioSpeedTVPicker.setSlaveView

[Source line 276](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java#L276)

```java
slaveView.setText(getText());
```

## SITE-c736cb10121d · SubtitleDelayTVPicker.setEnabled

[Source line 138](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java#L138)

```java
super.setEnabled(enabled);
```

## SITE-145fad90cde7 · SubtitleDelayTVPicker.setText

[Source line 268](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java#L268)

```java
((TextView)findViewById(R.id.text_delay)).setText(txt);
```

## SITE-c61d7f0a54d1 · SubtitleDelayTVPicker.setText

[Source line 270](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java#L270)

```java
slaveView.setText(txt);
```

## SITE-deb4c7786b30 · SubtitleDelayTVPicker.setSlaveView

[Source line 327](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java#L327)

```java
slaveView.setText(getText());
```

## SITE-5b49c34623aa · TVCardDialog.setSlaveView

[Source line 110](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java#L110)

```java
this.slaveView.setText(text);
```

## SITE-552bdce96daa · TVCardDialog.setText

[Source line 216](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java#L216)

```java
((TextView) findViewById(R.id.info_text)).setText(txt);
```

## SITE-c30d09b2d68e · TVCardDialog.setText

[Source line 218](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java#L218)

```java
slaveView.setText(txt);
```

## SITE-b9576ada0fc7 · TVCardView.setSlaveView

[Source line 232](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardView.java#L232)

```java
this.slaveView.setText(text);
```

## SITE-5a572664ba1f · TVCardView.setText

[Source line 321](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardView.java#L321)

```java
((TextView) findViewById(R.id.info_text)).setText(txt);
```

## SITE-9b2f18939e8f · TVCardView.setText2

[Source line 328](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardView.java#L328)

```java
textView.setText(txt);
```

## SITE-5b3f1840602c · TVMenu.createAndAddTVMenuItem

[Source line 196](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenu.java#L196)

```java
tvmi.setText(text);
```

## SITE-ba4e193acbbf · TVMenu.createAndAddTVSwitchableMenuItem

[Source line 208](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenu.java#L208)

```java
tvmi.setText(text);
```

## SITE-feb8563df381 · TVMenu.createAndAddSlideTVMenuItem

[Source line 228](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenu.java#L228)

```java
tvmi.setText(text);
```

## SITE-70b13149c702 · TVMenuAdapter.createView

[Source line 114](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuAdapter.java#L114)

```java
((TVCardView)v.findViewById(R.id.card_view)).setText(text);v.findViewById(R.id.card_view).setTag(text);
```

## SITE-c09221fcf876 · TVMenuItem.setText

[Source line 133](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuItem.java#L133)

```java
((TextView) findViewById(R.id.info_text)).setText(spannableText);
```

## SITE-04dc5be987b2 · TVMenuItem.setText

[Source line 135](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuItem.java#L135)

```java
slaveView.setText(text);
```

## SITE-7552491e24ab · TVMenuItem.setSlaveView

[Source line 223](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuItem.java#L223)

```java
slaveView.setText(text);
```

## SITE-1effc5b0c6b1 · TimerDelayTVPicker.setEnabled

[Source line 97](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java#L97)

```java
super.setEnabled(enabled);
```

## SITE-7387b1d66018 · TimerDelayTVPicker.setText

[Source line 211](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java#L211)

```java
((TextView)findViewById(R.id.text_delay)).setText(txt);
```

## SITE-354df3946955 · TimerDelayTVPicker.setText

[Source line 213](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java#L213)

```java
slaveView.setText(txt);
```

## SITE-f13dda03b1e0 · TimerDelayTVPicker.setSlaveView

[Source line 266](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java#L266)

```java
slaveView.setText(getText());
```

## SITE-69f8e7d965a6 · OpenSubtitlesCredentialsDialog.onCreateDialog

[Source line 86](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesCredentialsDialog.java#L86)

```java
usernameEt.setText(mUsername);
```

## SITE-1e14d4c8a682 · OpenSubtitlesCredentialsDialog.onCreateDialog

[Source line 87](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesCredentialsDialog.java#L87)

```java
passwordEt.setText(mPassword);
```

## SITE-9cd691e0643c · OpenSubtitlesCredentialsDialog.onCreateDialog

[Source line 90](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesCredentialsDialog.java#L90)

```java
.setTitle(R.string.dialog_subloader_credentials)
```

Resolved English: `{"dialog_subloader_credentials": "Opensubtitles credentials"}`

## SITE-23ceeeb822cd · SubtitlesDownloaderActivity2.onStart

[Source line 160](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L160)

```java
dialogNoNetwork.setTitle(R.string.dialog_subloader_nonetwork_title);
```

Resolved English: `{"dialog_subloader_nonetwork_title": "No Internet connection"}`

## SITE-be4842b676e6 · SubtitlesDownloaderActivity2.onStart

[Source line 161](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L161)

```java
dialogNoNetwork.setMessage(getString(R.string.dialog_subloader_nonetwork_message));
```

Resolved English: `{"dialog_subloader_nonetwork_message": "An internet connection is required to download subtitles"}`

## SITE-2a3cc1bea2d0 · SubtitlesDownloaderActivity2.askSubChoice

[Source line 535](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L535)

```java
((TextView) view.findViewById(R.id.video_name)).setText(HtmlCompat.fromHtml(getString(R.string.select_sub_file, getFriendlyFilename(videoFilePath)), HtmlCompat.FROM_HTML_MODE_LEGACY));
```

Resolved English: `{"select_sub_file": "Exact subtitles not found for file <font color=#29b6f6><i>%s</i></font><br />Please try among this list:"}`

## SITE-9225184a2c3a · SubtitlesDownloaderActivity2.getView

[Source line 559](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L559)

```java
((TextView) view.findViewById(R.id.video_name)).setText(searchResults.get(i).getFileName());
```

## SITE-41746d522500 · SubtitlesDownloaderActivity2.getView

[Source line 566](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java#L566)

```java
((TextView) view.findViewById(R.id.lang)).setText(searchResults.get(i).getLanguage());
```

## SITE-f9db06652eca · SubtitlesWizardActivity.onCreate

[Source line 91](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java#L91)

```java
helpMessageHeader.setText(helpMessage);
```

## SITE-c3d016dcd5c7 · SubtitlesWizardActivity.enableEmptyView

[Source line 123](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java#L123)

```java
mEmptyView.setText(R.string.subtitles_wizard_no_files);
```

Resolved English: `{"subtitles_wizard_no_files": "No subtitles files found"}`

## SITE-b87cf4bd0fe9 · SubtitlesWizardActivity.getView

[Source line 384](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java#L384)

```java
holder.text.setText((String)getItem(position));
```

## SITE-b0970d2eaaf4 · SubtitlesWizardActivity.getView

[Source line 395](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java#L395)

```java
holder.size.setText(size);
```
