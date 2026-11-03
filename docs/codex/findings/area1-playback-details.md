# Bug hunt area 1 — partial semantic review

Read-only findings against the source after Parts C and D. No code was changed for this review.

Coverage limitation: every listed Java file was read in full by the inventory/risk-pattern pass and joined with its file-filtered lint findings. This is **not a completed every-line semantic review** of all files. Direct code review was concentrated on the evidence sites below and their callers; the remaining semantic review is outstanding. A file with no finding is not certified defect-free.

The fetched reports/lint snapshot contains pre-edit line numbers; lint entries below retain those original coordinates. Findings use the current code coordinates. SuspiciousIndentation was addressed in Part A; no new lint run or tests were performed.

Inventory: 127 files, 42314 lines, 61 lint entries.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (lines and why) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|
| P1 | `src/main/java/com/archos/mediacenter/video/player/PlayerService.java:1733` | logic | minor | medium | An already pending skip prompt can remain actionable for up to five seconds after the global master preference is disabled, if no per-video override is set. | applySegmentPolicies returns when the master is disabled, but segmentPrompt checks only expiry and skipPrompt checks only the target; neither rechecks the master. segmentOverride clears it, but a global preference write does not. | Clear the pending prompt on global policy changes and revalidate the enabled policy before accepting a prompt. |

Lint triage: the StaticFieldLeak warnings for PreviewPlaybackMenus are not promoted to leak findings: close() clears the detached window tree and owner references. PreviewTrailer validates the YouTube key and disables file/content access; enabling JavaScript alone does not establish an XSS defect. These observations do not replace lifecycle testing.

## Files processed and filtered lint findings

This is the full scan inventory, not an assertion of complete manual line-by-line review. Each lint entry is shown only with the file to which it belongs; “none” means no entry in the supplied snapshot.

- `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRow.java` — 22 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRowPresenter.java` — 225 lines; lint: PrivateResource@107: The resource `@dimen/lb_details_v2_left` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@112: The resource `@dimen/lb_details_v2_blank_height` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@114: The resource `@dimen/lb_details_v2_description_margin_top` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@145: The resource `@dimen/lb_details_v2_left` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@151: The resource `@dimen/lb_details_v2_left` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@164: The resource `@dimen/lb_details_v2_blank_height` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@170: The resource `@dimen/lb_details_v2_actions_height` is marked as private in androidx.leanback:leanback:1.2.0; PrivateResource@182: The resource `@dimen/lb_details_v2_actions_height` is marked as private in androidx.leanback:leanback:1.2.0; SetTextI18n@78: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@78: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosHorizontalGridView.java` — 100 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/BackgroundColorPresenter.java` — 22 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/CastRow.java` — 41 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/CastRowPresenter.java` — 125 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/ConnectedDetailsPanel.java` — 71 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsBlueprintPanel.java` — 489 lines; lint: ViewConstructor@22: Custom view `DetailsBlueprintPanel` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`; SetTextI18n@229: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsDimensions.java` — 10 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsPeopleGrid.java` — 70 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsReviews.java` — 248 lines; lint: SetTextI18n@186: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@198: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
- `src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRow.java` — 44 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java` — 312 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/FullWidthRow.java` — 32 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/FullWidthRowPresenter.java` — 87 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PlotAndGenresRow.java` — 39 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PlotAndGenresRowPresenter.java` — 122 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsData.java` — 380 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsFacts.java` — 124 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeChoice.java` — 32 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeRow.java` — 202 lines; lint: ViewConstructor@20: Custom view `PreviewEpisodeRow` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewExtraDuration.java` — 34 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewLandscapeCard.java` — 101 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMediaInfo.java` — 58 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoreActions.java` — 38 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java` — 2552 lines; lint: ViewConstructor@60: Custom view `PreviewMoviePage` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`; SetTextI18n@599: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@599: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@879: String literal in `setText` can not be translated. Use Android resources instead.; RtlHardcoded@1690: Use "`Gravity.START`" instead of "`Gravity.LEFT`" to ensure correct behavior in right-to-left locales
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeople.java` — 85 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeopleRail.java` — 44 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewSeasonActivity.java` — 117 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTrailer.java` — 119 lines; lint: SetJavaScriptEnabled@37: Using `setJavaScriptEnabled` can introduce XSS vulnerabilities into your application, review carefully; SetTextI18n@59: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@59: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTvTrailers.java` — 55 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/SelectableListRow.java` — 41 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/SelectableListRowPresenter.java` — 58 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/SubtitleInterface.java` — 26 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRow.java` — 47 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRowPresenter.java` — 249 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java` — 283 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsActivity.java` — 137 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java` — 423 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java` — 3024 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsOverlayActivity.java` — 22 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsTransitionBackdropCache.java` — 64 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsTransitionPosterCache.java` — 44 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/ArchosSeekBar.java` — 63 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioDelayPicker.java` — 157 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerAbstract.java` — 109 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerDialog.java` — 169 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioDelayPickerDialogInterface.java` — 33 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPicker.java` — 155 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerAbstract.java` — 94 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerDialog.java` — 138 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/AudioSpeedPickerDialogInterface.java` — 33 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/BrightnessDialog.java` — 230 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/ChapterMarkers.java` — 45 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/ChaptersDbClient.java` — 326 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/ChaptersDbPlayback.java` — 81 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/ExternalPlayerService.java` — 170 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/ExternalResumeIntent.java` — 108 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/FloatingPlayerActivity.java` — 79 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/FloatingPlayerService.java` — 1047 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/FocusableTVCardView.java` — 26 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/IPlayerControl.java` — 45 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/NoneEffect.java` — 32 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/OpenGLUtils.java` — 78 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PlaybackAgeOverlay.java` — 200 lines; lint: ViewConstructor@18: Custom view `PlaybackAgeOverlay` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`
- `src/main/java/com/archos/mediacenter/video/player/PlaybackChapters.java` — 32 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PlaybackResumePolicy.java` — 90 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java` — 1137 lines; lint: ViewConstructor@30: Custom view `PlaybackSubtitlePanel` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`; SetTextI18n@935: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@935: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/player/PlaybackViewingCoverage.java` — 58 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/Player.java` — 1767 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java` — 5869 lines; lint: InflateParams@2073: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); InflateParams@2085: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); InflateParams@2124: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); InflateParams@2180: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); InflateParams@2236: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); InflateParams@2248: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); UseSwitchCompatOrMaterialCode@3552: Use `SwitchCompat` from AppCompat or `MaterialSwitch` from Material library; SetTextI18n@3554: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/player/PlayerBrightnessManager.java` — 148 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PlayerController.java` — 3622 lines; lint: CutPasteId@431: The id `R.id.card_view` has already been looked up in this method; possible cut & paste error?
- `src/main/java/com/archos/mediacenter/video/player/PlayerControlsRelativeLayout.java` — 129 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PlayerService.java` — 3672 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewAdjustmentValue.java` — 18 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java` — 14 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackInfo.java` — 152 lines; lint: SetTextI18n@67: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackLoading.java` — 290 lines; lint: ViewConstructor@34: Custom view `PreviewPlaybackLoading` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`
- `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java` — 832 lines; lint: StaticFieldLeak@29: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@30: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@32: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@33: Do not place Android context classes in static fields (static reference to `TVMenuAdapter` which has field `mActivity` pointing to `Context`); this is a memory leak; StaticFieldLeak@34: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@35: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@35: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@35: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@35: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@36: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@37: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@40: Do not place Android context classes in static fields (static reference to `TVCardDialog` which has field `mContext` pointing to `Context`); this is a memory leak; StaticFieldLeak@41: Do not place Android context classes in static fields (static reference to `TVCardView` which has field `mContext` pointing to `Context`); this is a memory leak; StaticFieldLeak@43: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@44: Do not place Android context classes in static fields; this is a memory leak; StaticFieldLeak@44: Do not place Android context classes in static fields; this is a memory leak; RtlHardcoded@232: Use "`Gravity.END`" instead of "`Gravity.RIGHT`" to ensure correct behavior in right-to-left locales; RtlHardcoded@731: Use "`Gravity.END`" instead of "`Gravity.RIGHT`" to ensure correct behavior in right-to-left locales
- `src/main/java/com/archos/mediacenter/video/player/PreviewSeekPolicy.java` — 17 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewTaglines.java` — 20 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewTechnicalInfo.java` — 160 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewTrackLabel.java` — 18 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/PreviewUpNext.java` — 275 lines; lint: SetTextI18n@64: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@64: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/player/PrivateMode.java` — 93 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SDKNotSupportedDialogActivity.java` — 63 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SeekTooltipDrawable.java` — 21 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SegmentSkippingPolicy.java` — 19 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SkipProgressDrawable.java` — 95 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/StereoDiveEffect.java` — 654 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/StereoMergeArchosEffect.java` — 84 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/StereoMergeEffect.java` — 47 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleColorPicker.java` — 170 lines; lint: InflateParams@152: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element)
- `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPicker.java` — 192 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerAbstract.java` — 94 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerDialog.java` — 178 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleDelayPickerDialogInterface.java` — 46 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleEngine.java` — 581 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleManager.java` — 829 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitlePlaybackBackend.java` — 317 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SubtitleSettingsDialog.java` — 341 lines; lint: InflateParams@102: Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element); ClickableViewAccessibility@314: `SubtitleSettingsDialog#onTouch` should call `View#performClick` when a click is detected
- `src/main/java/com/archos/mediacenter/video/player/SubtitleSpacerView.java` — 41 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SummaryListPreference.java` — 39 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/SurfaceController.java` — 751 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/TextShadowSpan.java` — 56 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/TextureSurfaceRenderer.java` — 265 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/TimerPickerAbstract.java` — 98 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/TrackInfoController.java` — 433 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/UpdateNextTask.java` — 444 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/VideoEffect.java` — 114 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/VideoEffectRenderer.java` — 288 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioDelayTVPicker.java` — 358 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/AudioSpeedTVPicker.java` — 311 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/SubtitleDelayTVPicker.java` — 366 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardDialog.java` — 398 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVCardView.java` — 633 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenu.java` — 359 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuAdapter.java` — 396 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuItem.java` — 276 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVMenuSeparator.java` — 35 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVOnScrollListener.java` — 20 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVPillItem.java` — 197 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVScrollView.java` — 54 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVSlaveView.java` — 30 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TVUtils.java` — 72 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/player/tvmenu/TimerDelayTVPicker.java` — 300 lines; lint: none
