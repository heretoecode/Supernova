# Deep bug hunt 1 — player

Source snapshot: `72b0756cb`. Read-only: no fixes, tests or builds were performed. The known U1, N1, N2, L1, P1, U2 and L2 findings are excluded.

Coverage (current inventory at `eeba8c6ca`): **82 / 85 files fully read at the recorded source snapshots** — 58 inherited reads plus 24 new full reads in the BRIEF_0155_MORE item-5 continuation below. The new inventory includes PlayerLaunchContract; no historical inventory file was removed. The three no rows remain unread in full. Historical yes rows are not a claim of a fresh full-file read in this run. Every table line count was refreshed against this branch. A yes means every source line was read at its recorded review snapshot; supporting excerpts never earn yes. This is partial progress, not a clean bill of health.

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
| `src/main/java/com/archos/mediacenter/video/player/ArchosSeekBar.java` | 63 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java` | 164 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerAbstract.java` | 109 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerDialog.java` | 169 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerDialogInterface.java` | 33 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java` | 162 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerAbstract.java` | 94 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerDialog.java` | 138 | yes |
| `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerDialogInterface.java` | 33 | yes |
| `src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java` | 230 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ChapterMarkers.java` | 45 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ChaptersDbClient.java` | 326 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ChaptersDbPlayback.java` | 81 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ExternalPlayerService.java` | 170 | yes |
| `src/main/java/com/archos/mediacenter/video/player/ExternalResumeIntent.java` | 112 | yes |
| `src/main/java/com/archos/mediacenter/video/player/FloatingPlayerActivity.java` | 79 | yes |
| `src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java` | 1047 | yes |
| `src/main/java/com/archos/mediacenter/video/player/FocusableTVCardView.java` | 26 | yes |
| `src/main/java/com/archos/mediacenter/video/player/IPlayerControl.java` | 45 | yes |
| `src/main/java/com/archos/mediacenter/video/player/NoneEffect.java` | 32 | yes |
| `src/main/java/com/archos/mediacenter/video/player/OpenGLUtils.java` | 78 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackAgeOverlay.java` | 200 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackChapters.java` | 32 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackResumePolicy.java` | 90 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java` | 1143 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlaybackViewingCoverage.java` | 58 | yes |
| `src/main/java/com/archos/mediacenter/video/player/Player.java` | 1767 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java` | 5848 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerBrightnessManager.java` | 157 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlayerController.java` | 3622 | no |
| `src/main/java/com/archos/mediacenter/video/player/PlayerControlsRelativeLayout.java` | 129 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlayerLaunchContract.java` | 26 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PlayerService.java` | 3678 | no |
| `src/main/java/com/archos/mediacenter/video/player/PreviewAdjustmentValue.java` | 18 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java` | 14 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackInfo.java` | 152 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java` | 290 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java` | 832 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewSeekPolicy.java` | 17 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewTaglines.java` | 20 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java` | 160 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewTrackLabel.java` | 18 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java` | 275 | yes |
| `src/main/java/com/archos/mediacenter/video/player/PrivateMode.java` | 93 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SDKNotSupportedDialogActivity.java` | 63 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SeekTooltipDrawable.java` | 21 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SegmentSkippingPolicy.java` | 19 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SkipProgressDrawable.java` | 95 | yes |
| `src/main/java/com/archos/mediacenter/video/player/StereoDiveEffect.java` | 654 | yes |
| `src/main/java/com/archos/mediacenter/video/player/StereoMergeArchosEffect.java` | 84 | yes |
| `src/main/java/com/archos/mediacenter/video/player/StereoMergeEffect.java` | 47 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleColorPicker.java` | 170 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java` | 192 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerAbstract.java` | 94 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerDialog.java` | 178 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerDialogInterface.java` | 46 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleEngine.java` | 584 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleManager.java` | 829 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitlePlaybackBackend.java` | 317 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleSettingsDialog.java` | 341 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SubtitleSpacerView.java` | 41 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SummaryListPreference.java` | 39 | yes |
| `src/main/java/com/archos/mediacenter/video/player/SurfaceController.java` | 751 | yes |
| `src/main/java/com/archos/mediacenter/video/player/TextShadowSpan.java` | 56 | yes |
| `src/main/java/com/archos/mediacenter/video/player/TextureSurfaceRenderer.java` | 265 | yes |
| `src/main/java/com/archos/mediacenter/video/player/TimerPickerAbstract.java` | 98 | yes |
| `src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java` | 433 | yes |
| `src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java` | 444 | yes |
| `src/main/java/com/archos/mediacenter/video/player/VideoEffect.java` | 114 | yes |
| `src/main/java/com/archos/mediacenter/video/player/VideoEffectRenderer.java` | 288 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java` | 358 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java` | 311 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java` | 366 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java` | 398 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardView.java` | 633 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenu.java` | 359 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuAdapter.java` | 396 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuItem.java` | 276 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuSeparator.java` | 35 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVOnScrollListener.java` | 20 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVPillItem.java` | 197 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVScrollView.java` | 54 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVSlaveView.java` | 30 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVUtils.java` | 72 | yes |
| `src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java` | 300 | yes |


## 2026-11-04 continuation — BRIEF_0155_PREP Part 4

Reviewed source `160b429f014c86833ae89451f88c58e6acd1e913`. Added **16 full-file reads, 1,918 lines**, all within the formerly unread group-1 set; the existing 28 yes rows retain their original snapshot provenance and are not claimed as fresh full reads. Current line counts were refreshed only for the newly read files. Group 1 now has 44/84 yes, **40 still unread**. No later group was advanced and no leanback/filebrowsing code was reviewed for this part. This is a bounded continuation; the remaining large player/service/controller/effect/menu files need a separate full pass. Supporting caller excerpts do not earn full-file coverage.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (quote the lines, explain the path) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|
| DP-06 | `src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java:67` and `AudioSpeedPicker.java:78` | threading/lifecycle | user-visible | medium | In the retained touch-based audio delay/speed dialogs, holding +/− then having a parent intercept/cancel the gesture can leave repeated changes running after the press ended. This is a legacy touch surface, not the modern TV panel's D-pad switches. | Both touch listeners post a repeat after ACTION_DOWN and remove it only on ACTION_UP (`AudioDelayPicker:71–75,96–100`; AudioSpeedPicker:82–86,111–115). Neither handles ACTION_CANCEL or detach. Each runnable unconditionally calls performClick and posts itself again every 150 ms (delay :141–154; speed :139–152), with no pressed/attached/enabled ownership test. A DOWN followed by CANCEL without UP leaves the runnable pending and continually rescheduling; a long hold can also continue scheduling at the limit. PlayerActivity still constructs these dialogs at :4041–4068; ordinary Preview panel interactions do not exercise them. Static defect is clear; availability of an intercepted touch sequence on the owner's TV input is not established. | Stop both repeats on UP, CANCEL and detachment/dismissal; require an active matching press and enabled/attached owner before clicking or rescheduling. Add a looper-driven DOWN→CANCEL test and detach/limit cases, asserting no further listener callbacks and no queued repeats; also verify normal tap/hold and D-pad behavior. |
| DP-07 | `src/main/java/com/archos/mediacenter/video/player/PlayerBrightnessManager.java:79–108` | logic | minor | high | A decrease-brightness gesture cannot establish a manual brightness override while the window is using system brightness (-1). Repeated decreases do nothing until an increase/other control first creates an override. Limited to the retained gesture brightness path; external-TV displays may ignore brightness overrides entirely. | getLinearBrightness returns 10 for the -1 sentinel (:81), so PlayerController.scrollIncrementalBrightnessUpdate (:2767–2783; gesture caller :2601) asks for level 9 on decrease. setLinearBrightness computes a nonnegative gamma value but compares it to raw curBrightness=-1; decrease only writes under `if (newBrightness < curBrightness)` (:103). That is always false for valid levels, so neither window nor saved setting changes; getLinearBrightness remains 10 on each attempt. Increase uses the opposite comparison and does succeed. | Resolve the system/default sentinel to an effective brightness baseline before directional comparison (or deliberately establish a bounded manual baseline on the first gesture); use the same baseline for displayed level and application. Test a window with screenBrightness=-1 and repeated decreases/increases, plus manual 0/1 boundaries, without depending on real display brightness. |

No new crash finding is claimed. DP-06 is medium confidence because its canceled-touch trigger is concrete but normal TV route frequency is uncertain; DP-07 is high confidence in the arithmetic path, with the device-effect limitation stated. No U1/N1/N2/L1/P1/U2/L2 or DP-01…DP-05 issue was re-reported. Historical findings above remain historical records; this append does not imply their fixes are absent from other branches.

Additional review notes: ArchosSeekBar's optional listener is unguarded, but its only located inflation owner BrightnessDialog installs the listener before use, so no reachable new crash is asserted. ExternalPlayerService's API-33 receiver call is gated by its API-34-only startService path; the annotation alone is not the guard, but there is no normal API-23 start through that helper. FloatingPlayerActivity lacks an unbind, but no manifest declaration/current app launch reference was found, so it is not promoted to a live-user leak finding. PrivateMode is initialized from CustomApplication; no initialization defect is claimed. PreviewPlaybackInfo shuts down its worker in finally and checks dialog visibility before applying metadata. None of these notes certifies complete runtime correctness or replaces device verification.


## 2026-11-04 continuation — BRIEF_0155_CODE Part 3

Reviewed source `a7c8027c6` after TV-only stage 6. Added **14 full-file reads, 895 lines** from the
formerly unread group-1 set: NoneEffect, OpenGLUtils, PlayerControlsRelativeLayout,
SDKNotSupportedDialogActivity, StereoMergeArchosEffect, StereoMergeEffect, SummaryListPreference,
TimerPickerAbstract, VideoEffect, and tvmenu/{TVMenuSeparator, TVOnScrollListener, TVScrollView,
TVSlaveView, TVUtils}. Only those coverage rows were changed to yes and refreshed to the current
line counts. Group 1 now has **58/84 yes, 26 still unread**. This is a bounded continuation;
groups 2–4 were not advanced before finishing group 1. No hands-off or removed source was counted.
The newly added launch/search/sort contracts are implementation work, not retroactively added to
this historical 84-file audit denominator.

No new confirmed finding was added and no earlier ID was re-reported. Review notes:

- The standard stereo modes are 0, 1, 2, 4 and 8. The Archos broadcast adapter's trailing-zero
  conversion maps these into its four-element mode table; ordinary defined modes do not establish
  an out-of-bounds defect. Persisted/forced mode validation needs the larger caller pass.
- OpenGLUtils retains compiled shader handles after linking and on several early-return paths.
  Supporting excerpts show StereoDiveEffect creating programs and deleting programs at teardown,
  while TextureSurfaceRenderer ultimately tears down EGL. This suggests shader-object retention
  until context destruction; no sustained-context exhaustion or reachable new playback failure
  was established, so it is not promoted to a crash finding. StereoDiveEffect is still unread in
  the full-file coverage table; caller excerpts do not earn full coverage.
- The HUD layout walks parent Views before applying the chapter-bubble safe area and checks its
  required title/bar/bubble nodes. TVScrollView checks its optional listener. TVUtils stops at a
  non-View parent. Their inspected paths establish no new null-parent crash; this is not device
  focus or enlarged-font acceptance evidence.
- TimerPickerAbstract accepts arbitrary step/min/max inputs, but no reachable zero-step or
  overflow case was established in this bounded pass. SummaryListPreference and the small menu
  interfaces/separator add no active async lifetime of their own.

No production code changed for this part. Remaining large player/service/controller/menu/effect
files still require full reads; JNI/GL output and remote navigation were not exercised.


## 2026-11-04 continuation — BRIEF_0155_MORE item 5, group 1

Reviewed app source `eeba8c6cae66abc7a6f7203e8e0b24e3da003968` after pulling the existing branch. Read HANDS_OFF first. Refreshed the player inventory: all historical 84 Java files remain; `PlayerLaunchContract.java` is the one added file, now included in the denominator. No player file is under the current hands-off list. Updated every table line count; inherited yes rows retain their earlier review provenance.

Added **24 full-file reads, 10,569 lines**: FloatingPlayerService, Player, PlayerLaunchContract, PreviewPlaybackMenus, StereoDiveEffect, SubtitleColorPicker, SubtitleDelayPicker, SubtitleDelayPickerAbstract, SubtitleDelayPickerDialog, SubtitleDelayPickerDialogInterface, SubtitleSettingsDialog, SurfaceController, TrackInfoController, UpdateNextTask, and tvmenu/{AudioDelayTVPicker, AudioSpeedTVPicker, SubtitleDelayTVPicker, TimerDelayTVPicker, TVCardDialog, TVCardView, TVMenu, TVMenuAdapter, TVMenuItem, TVPillItem}. These are 23 formerly unread files plus the new contract. Group 1 is now **82/85 yes**; **PlayerActivity.java (5,848 lines), PlayerController.java (3,622), and PlayerService.java (3,678) remain no**. Their supporting caller excerpts below do not count as full reads. Finish those three before refreshing/advancing groups 2, 3 and 4.

This is read-only hunt work. No app code, resources, manifests, tests, native/dependency patches or configuration changed. No Gradle, Android/JUnit/Robolectric test or Shield run. Findings are static paths with explicit trigger bounds, not observed incidents on the owner's device.

| ID | File:line | What goes wrong | Evidence | Suggested fix (not applied) | Severity | Confidence |
| --- | --- | --- | --- | --- | --- | --- |
| DP-08 | `player/Player.java:701` (also `:532`); `res/raw/logback_full.xml:12,24,45` | Opening a network video writes its full source URI, including any user-info, private path or signed query, to ordinary INFO logs. No trace logging or malformed resume extra is required. | `openVideo` calls `log.info("openVideo: " + mUri)` before source setup. The full logging configuration enables Player at INFO and encodes `%msg` directly to Logcat and the rolling file; `CustomApplication.java:1075–1097` installs that configuration. This message does not pass through typed Diagnostics redaction. For a fixture URL containing `?token=PRIVATE_SENTINEL`, that query is part of the message by construction. The debug setter at :532 repeats the disclosure if debug is enabled. This is the playback source logger, distinct from N2's FTP-credential dialog and DP-04's malformed resume-value expansion. | Replace these source messages with a bounded protocol/outcome or existing safe location category; never format the raw URI. Add a captured-logger regression using URI user-info, signed queries, fragments and private file paths; assert neither INFO nor DEBUG contains fixture secrets while source open is still attempted. Audit neighboring source logs separately rather than claiming these two lines sanitize the whole application. Pure internal logging fix is a candidate for item 6 after the ordered hunt. | user-visible (security/privacy) | high |
| DP-09 | `player/UpdateNextTask.java:112–146,299–308`; supporting `player/PlayerService.java:414–431,2324–2357` | Folder auto-next/repeat, and the legacy/no-cache episode fallback, can choose a video explicitly hidden by the user. A hidden row that sorts immediately after the current file is selected ahead of a later visible row. | Folder predicates contain bucket/path only; findEpisode contains episode/show/season only. Both query the general video provider, without `LoaderUtils.HIDE_USER_HIDDEN_FILTER`. The pinned prepared MediaLib `VideoProvider.java:332–338` selects the video view without adding that predicate; the current view inherits only volume-hidden/media-type/active-server filtering (`VideoOpenHelper.java:1512–1517`), which is different from `Archos_hideFile`. By contrast the playlist SQL here explicitly includes the user-hidden filter at :218. PlayerService consumes the result and starts it on auto-next; the Preview cached-episode branch can bypass this fallback, so that branch is not claimed affected. A minimal SQLite fixture with visible A, hidden B and visible C reproduced B for the exact folder predicate; adding `Archos_hideFile=0` selected C. This was a predicate probe, not an Android playback test. | Owner/Claude: use the same eligibility contract for next-file selection as the relevant visible library; filter before LIMIT and before wrap/episode selection. Test hidden middle/first rows, all-hidden remainder, repeat wrap, cached versus uncached binge, and explicit playback of a hidden file separately. **No fix here: this changes which content plays and therefore visible screen/playback behavior under rule 2.** | user-visible | high |
| DP-10 | `player/UpdateNextTask.java:337–345,400–434`; supporting `player/PlayerService.java:410–412,1403–1406` | Cancelling obsolete next-file discovery does not stop an already-running raw network listing. A slow/trickling source can retain that task's worker and connection after playback moves on; repeated cancellations can accumulate obsolete workers. | Service replacement/end uses `cancel(false)`. That sets the volatile result fence, calls Future.cancel(false) and executor.shutdown(), neither of which interrupts a running callable. run() performs synchronous raw `getFileList()` with no cancellation checks or owned call cancellation. The completion-wait runnable is queued behind the blocked listing on the same single executor, so its finally cannot release it while the listing is stuck. Pinned prepared FileCore WebdavRawLister:39–45 delegates to synchronous Sardine; WebdavUtils clients specify no whole-call timeout. A silent socket may hit inactivity timeout; a continuing trickle can avoid it. Existing source-scan drafts are not installed and even their thread-local scan scope is not entered by UpdateNextTask. Callback publication itself *is* fenced by isCancelled; no stale next-result claim is made. | Give each lookup owned transport cancellation/deadline and cooperative traversal cancellation; cancel obsolete work without shutting down shared playback/network clients. Test a real-socket silent/trickle listing, repeated replacement/cancel, worker termination and absence of late result callbacks. Preserve native/platform ownership until any noninterruptible work exits. No fix in this read-only item; evaluate the purely internal ownership change at item 6. | user-visible (resource retention under stalled I/O) | high for retention path; impact depends on source/cancellation frequency |

Paths in the findings table beginning `player/` are relative to `src/main/java/com/archos/mediacenter/video/`. MediaLib/FileCore references mean the exact pinned sources plus the existing ordered `supernova146-dependency-patches.json` operations, not the moving upstream tips or the uninstalled scan drafts. Scratch dependency worktrees from the previous run contain draft edits; dependency evidence in this pass was checked against their prepared baseline commit where relevant.

### Review notes and rejected promotions

- Player source generation/instance checks reject obsolete source callbacks; audio-focus listener epochs, pause intent, resume context and surface suspend/reopen paths were read in full. No native/JNI lifetime-safety claim follows from that Java review. Source setup's release interleavings require the backend's synchronization contract, so they were not promoted to a new native crash.
- FloatingPlayerService binds/unbinds and registers/unregisters its receiver, but its destruction path differs from removeFloatingView and does not itself stop every frontend/window callback. Normal switching invokes onFrontendDetached via PlayerService and removes the old window. Without a proven live teardown sequence bypassing that handoff, no additional orphan-overlay crash/leak is claimed. It is manifest-declared but not established as a normal Shield route.
- The remaining touch-dialog repeat patterns belong to the already recorded cancelled-touch family; no additional DP-06 report or new ID was generated. Legacy spinner carry/format edge cases were read, but no screen behavior was changed or certified.
- TV menu/card focus, mirroring and callback code was read. Suspicious negative saved-focus indices and dialog scroll callbacks need a proven caller sequence; no new crash is asserted from an isolated setter contract. TimerDelayTVPicker.createSlaveView inflates an AudioDelayTVPicker root and casts it to the timer type, but the only located createTVTimerDialog invocation is inside a block comment (`PlayerActivity.java:3193–3204`). Consequently this is a dormant template mismatch, **not a confirmed reachable crash and not a finding ID**. An XML root-class probe verified only the type mismatch. Recheck reachability before any future cleanup/fix; no template was edited.
- SurfaceController's sizing, cutout, HDR and subtitle-canvas code and StereoDiveEffect's GL allocation/draw/deinitialization paths were read without editing presentation. GL output, native transaction lifetime and device focus are untested. Existing OpenGL shader-retention concerns were not re-reported as a confirmed failure.
- PlayerLaunchContract preserves explicit stereo-mode precedence over alias metadata and catches a missing component; normal callers provide a real Context. No confirmed issue added for that new contract.

### Verification and continuation

The SQLite predicate probe returned hidden fixture ID 2 for the current next-in-bucket predicate and visible ID 3 after applying the established hidden filter. The XML root-class probe confirmed AudioDelayTVPicker versus TimerDelayTVPicker in the dormant timer mirror; the final source caller check rejected promotion to a reachable crash because its menu entry is commented out. These are narrow read-only checks, not app tests. No credentials, live network media or device logs were used.

No U1/N1/N2/L1/P1/U2/L2 or DP-01…DP-07 finding was re-reported. Keep the three large no rows as the next full-file review boundary; do not skip into later groups or item 6 based on this partial player continuation. All fixes, including the internally fixable logging/cancellation candidates, are deferred until the ordered hunt reaches item 6; UI/playback/template changes remain owner/Claude findings under rule 2.
