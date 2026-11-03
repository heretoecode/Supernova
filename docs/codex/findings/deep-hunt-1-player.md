# Deep bug hunt 1 — player

Source snapshot: `72b0756cb`. Read-only: no fixes, tests or builds were performed. The known U1, N1, N2, L1, P1, U2 and L2 findings are excluded.

Coverage: **28 / 84 files fully read**. A yes means every source line was read and the constructors/methods, callbacks, guards and teardown were considered. An excerpt, reference scan or prior-run read is not counted. This is partial progress; no unreviewed file is certified clean.

Priority was kept: work stopped within player/, so no later group received a deep-review yes. Cross-file excerpts used to verify a trigger are supporting evidence only. The four coverage inventories include Part A exclusions as no; those exclusions are listed separately below. Group 4 includes a conservative transitive utils reference/import closure so utility dependencies are not silently omitted.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (quote the lines, explain the path) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|
| DP-01 | `src/main/java/com/archos/mediacenter/video/player/VideoEffectRenderer.java:283` | threading | crash | high | Potential TV playback ANR on surface loss/stop: a late video-frame callback can block indefinitely after the renderer has been paused, preventing main-thread lifecycle recovery. | onFrameAvailable first executes `mSourceFrameAvailable.offer(mTrue)` but ends with `mSourceFrameAvailable.put(mTrue)` on a capacity-one queue. Player.closeCurrentPlayer calls `mEffectRenderer.pause()`; TextureSurfaceRenderer.run skips draw() while paused, so nothing drains the queue. initGLComponents installs the SurfaceTexture listener on the render thread without a Looper, allowing callbacks on the main Looper. A queued callback delivered after pause fills the latch and blocks at the final put. This is a timing-dependent path, not a reproduced device ANR. | Use a nonblocking coalescing wake after subtitle production as well as before it; ensure teardown rejects late callbacks. Keep the wake after queuing updated subtitle pixels so the compositor still sees them. |
| DP-02 | `src/main/java/com/archos/mediacenter/video/player/SubtitleEngine.java:392` | logic/threading | user-visible | high | Paused SBS/TB playback can keep an old subtitle or fail to show a newly selected cue until video resumes or a style control is changed. | Native paused-content notification → onNativeSubtitleContentChanged → mPullRunnable → `pull3DSubtitles()` → `draw3DSubtitlesInternal(..., false)` posts the new overlay buffer, but never calls `wakeDrawLoop()`. By contrast redraw3DIfNeeded does call it. VideoEffectRenderer.draw is blocked on `mSourceFrameAvailable.take()` and mUISurfaceTexture has no frame listener; posting its buffer alone cannot composite it. | After a successful paused native-content pull/post, coalesce a renderer wake using the same safe wake mechanism as the style-change path, with release/lifecycle guards. |
| DP-03 | `src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java:204` | focus/navigation | minor | medium | While the five-second Skip pill owns focus, pressing Back is swallowed without dismissing the pill, returning focus to the HUD or allowing the player Back action. | handleKey handles BACK/UP by only setting `consumeKeyUp = key; return true`; it neither removes the pill nor requests skipOrigin focus. PlayerActivity.dispatchKeyEvent returns immediately when this handler returns true. Down/Left/Right do restore the origin, so the user can escape with other keys. This concerns Back behavior, not known finding P1 about global skip enablement. | Confirm the intended TV Back contract; return focus/dismiss the focused transient pill, or deliberately pass Back through to its owner while consuming only the matching release when appropriate. |
| DP-04 | `src/main/java/com/archos/mediacenter/video/player/ExternalResumeIntent.java:97` | security/data | user-visible | high | With trace logging enabled, an external player sending a malformed String/Bundle value under position/startfrom/resume can place private URI, header or token content in application logs. | describeForTrace uses `if (isResumeKey(key)) result.append('=').append(value)` without checking the value type, while readPosition intentionally accepts only Integer/Long. PlayerService.onStart logs describeForTrace before numeric validation. Thus an invalid position value is rejected for playback but still expanded into the trace. Unlike known N2 this is the playback extra-logging allowlist, not FTP identity/location logging. | Only print validated numeric resume values; for all other types print the name/type and a redacted or invalid marker. Never call arbitrary extra toString for logging. |
| DP-05 | `src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java:644` | focus/navigation | user-visible | high | Left/Right on Subtitles, Outline, Background and option switches does not change the value, despite the panel's in-place Left/Right control contract; only OK works. | row() installs `rowKeys(row, key, event, null)`. rowKeys consumes Left/Right but only calls an action when its Stepper is non-null. toggle() and option() install only OnClickListener and do not replace that key listener. Choice and value rows do install steppers. The class contract states values change in place with Left/Right. | Give switch/option rows a direction-aware enabled key action through the same backend callbacks, preserving focus and held-key semantics; leave unavailable rows inert. |

Review notes: ChaptersDbClient intentionally never promotes external v1 sets to verified playback chapters; that is not re-reported as a missing-chapters bug. SubtitleManager defaults currently come from resource values of 2, so the suspected zero-width Outline toggle issue does not apply to this snapshot. Native render locks and generation checks were read; no native C code was audited, so native lifetime safety is not certified. The callbacks of Player/PlayerActivity/PlayerService were checked only at the relevant trigger sites, not in full.

Reviewed method families include playback interval union/serialization; resume source/generation decisions; chapter validation/HTTP/cache/teardown; subtitle native-surface/3D pull/style lifetime; backend track/toggle mapping; panel building/key/focus/list refresh paths; age overlay acquisition/dwell/teardown; loading first-frame/PixelCopy cancellation; seek/HUD arithmetic; external-position parsing/tracing; Up Next key/countdown behavior; drawing helpers and small shared control contracts.

## Part A exclusions

None in this group. Shared player classes were retained for review; no whole player class was labelled phone-only.

## Coverage

| File | Lines | Fully read (yes/no) |
|---|---:|---|
| `src/main/java/com/archos/mediacenter/video/player/ArchosSeekBar.java` | 63 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java` | 157 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerAbstract.java` | 109 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerDialog.java` | 169 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerDialogInterface.java` | 33 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java` | 155 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerAbstract.java` | 94 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerDialog.java` | 138 | no |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerDialogInterface.java` | 33 | no |
| `src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java` | 230 | no |
| `src/main/java/com/archos/mediacenter/video/player/ChapterMarkers.java` | 45 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ChaptersDbClient.java` | 326 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ChaptersDbPlayback.java` | 81 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ExternalPlayerService.java` | 170 | no |
| `src/main/java/com/archos/mediacenter/video/player/ExternalResumeIntent.java` | 108 | yes |
| `src/main/java/com/archos/mediacenter/video/player/FloatingPlayerActivity.java` | 79 | no |
| `src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java` | 1047 | no |
| `src/main/java/com/archos/mediacenter/video/player/FocusableTVCardView.java` | 26 | yes |
| `src/main/java/com/archos/mediacenter/video/player/IPlayerControl.java` | 45 | yes |
| `src/main/java/com/archos/mediacenter/video/player/NoneEffect.java` | 32 | no |
| `src/main/java/com/archos/mediacenter/video/player/OpenGLUtils.java` | 78 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackAgeOverlay.java` | 200 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackChapters.java` | 32 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackResumePolicy.java` | 90 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java` | 1137 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackViewingCoverage.java` | 58 | yes |
| `src/main/java/com/archos/mediacenter/video/player/Player.java` | 1767 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java` | 5869 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerBrightnessManager.java` | 148 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerController.java` | 3622 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerControlsRelativeLayout.java` | 129 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerService.java` | 3672 | no |
| `src/main/java/com/archos/mediacenter/video/player/PreviewAdjustmentValue.java` | 18 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java` | 14 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackInfo.java` | 152 | no |
| `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java` | 290 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java` | 832 | no |
| `src/main/java/com/archos/mediacenter/video/player/PreviewSeekPolicy.java` | 17 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewTaglines.java` | 20 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java` | 160 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewTrackLabel.java` | 18 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java` | 275 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PrivateMode.java` | 93 | no |
| `src/main/java/com/archos/mediacenter/video/player/SDKNotSupportedDialogActivity.java` | 63 | no |
| `src/main/java/com/archos/mediacenter/video/player/SeekTooltipDrawable.java` | 21 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SegmentSkippingPolicy.java` | 19 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SkipProgressDrawable.java` | 95 | no |
| `src/main/java/com/archos/mediacenter/video/player/StereoDiveEffect.java` | 654 | no |
| `src/main/java/com/archos/mediacenter/video/player/StereoMergeArchosEffect.java` | 84 | no |
| `src/main/java/com/archos/mediacenter/video/player/StereoMergeEffect.java` | 47 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleColorPicker.java` | 170 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java` | 192 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerAbstract.java` | 94 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerDialog.java` | 178 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerDialogInterface.java` | 46 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleEngine.java` | 581 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleManager.java` | 829 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitlePlaybackBackend.java` | 317 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleSettingsDialog.java` | 341 | no |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleSpacerView.java` | 41 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SummaryListPreference.java` | 39 | no |
| `src/main/java/com/archos/mediacenter/video/player/SurfaceController.java` | 751 | no |
| `src/main/java/com/archos/mediacenter/video/player/TextShadowSpan.java` | 56 | yes |
| `src/main/java/com/archos/mediacenter/video/player/TextureSurfaceRenderer.java` | 265 | yes |
| `src/main/java/com/archos/mediacenter/video/player/TimerPickerAbstract.java` | 98 | no |
| `src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java` | 433 | no |
| `src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java` | 444 | no |
| `src/main/java/com/archos/mediacenter/video/player/VideoEffect.java` | 114 | no |
| `src/main/java/com/archos/mediacenter/video/player/VideoEffectRenderer.java` | 288 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java` | 358 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java` | 311 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java` | 366 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java` | 398 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardView.java` | 633 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenu.java` | 359 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuAdapter.java` | 396 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuItem.java` | 276 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuSeparator.java` | 35 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVOnScrollListener.java` | 20 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVPillItem.java` | 197 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVScrollView.java` | 54 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVSlaveView.java` | 30 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVUtils.java` | 72 | no |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java` | 300 | no |
