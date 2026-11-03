# Deep bug hunt 2 — not reached in this run

Source snapshot: `72b0756cb`. Read-only: no fixes, tests or builds were performed. The known U1, N1, N2, L1, P1, U2 and L2 findings are excluded.

Coverage: **0 / 90 files fully read**. A yes means every source line was read and the constructors/methods, callbacks, guards and teardown were considered. An excerpt, reference scan or prior-run read is not counted. This is partial progress; no unreviewed file is certified clean.

Priority was kept: work stopped within player/, so no later group received a deep-review yes. Cross-file excerpts used to verify a trigger are supporting evidence only. The four coverage inventories include Part A exclusions as no; those exclusions are listed separately below. Group 4 includes a conservative transitive utils reference/import closure so utility dependencies are not silently omitted.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (quote the lines, explain the path) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|

No new finding is asserted for this group: it has not yet been deep-reviewed. This does not mean no defects exist.

## Part A exclusions

`src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeopleRail.java`

## Coverage

| File | Lines | Fully read (yes/no) |
|---|---:|---|
| `src/main/java/com/archos/mediacenter/video/leanback/CinematicCard.java` | 29 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java` | 384 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryPage.java` | 136 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/CustomiseHomeEditor.java` | 736 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/EditPagePanel.java` | 653 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/HomeGenrePicker.java` | 391 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/HomeIntentCodec.java` | 230 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/HomePlaceholder.java` | 77 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryHealth.java` | 77 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryHealthActivity.java` | 376 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryHealthPanel.java` | 145 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryPageHeader.java` | 179 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryScanRecord.java` | 39 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryStatistics.java` | 48 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/MenuFoundation.java` | 98 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/OfficialTitleArtwork.java` | 503 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewAccent.java` | 122 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewArtworkPicker.java` | 184 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewAutoScanPolicy.java` | 24 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewBackdrop.java` | 271 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewContentFocus.java` | 14 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java` | 703 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewDiscovery.java` | 82 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewEnrichmentQueue.java` | 307 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java` | 232 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedIndicators.java` | 46 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusGlow.java` | 35 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusRail.java` | 117 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusRecycler.java` | 124 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusUnderline.java` | 21 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFolderActions.java` | 26 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFrost.java` | 30 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewGenres.java` | 260 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java` | 402 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewIcon.java` | 397 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java` | 251 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLanguageIcon.java` | 255 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java` | 471 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java` | 710 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryScan.java` | 300 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibrarySummary.java` | 75 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLocalScanState.java` | 90 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMappingCursor.java` | 45 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java` | 237 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMediaClassification.java` | 82 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMenuPlacement.java` | 18 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMetadata.java` | 210 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMetadataCache.java` | 60 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNavigationShade.java` | 55 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkScanning.java` | 115 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkWorkspace.java` | 76 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNotice.java` | 44 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewOperationDialog.java` | 43 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java` | 2700 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewReleaseOrder.java` | 24 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewScanProgress.java` | 21 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSeriesJourney.java` | 70 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSources.java` | 297 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewStartupSurface.java` | 53 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java` | 313 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java` | 30 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java` | 128 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewToolbar.java` | 132 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewUtilityBackground.java` | 61 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewVariants.java` | 351 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewVersionsDialog.java` | 91 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewWatchedScopeDialog.java` | 24 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/SharedMetadata.java` | 307 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java` | 191 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/SidePanelKit.java` | 308 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/StandardSwitchDrawable.java` | 129 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/SubtitlePanelStyle.java` | 403 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/SubtitleResultsPanel.java` | 139 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/SupernovaStyle.java` | 553 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java` | 583 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/TvButton.java` | 38 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsData.java` | 380 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsFacts.java` | 124 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeChoice.java` | 32 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeRow.java` | 202 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewExtraDuration.java` | 34 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewLandscapeCard.java` | 101 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMediaInfo.java` | 58 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoreActions.java` | 38 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java` | 2552 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeople.java` | 85 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeopleRail.java` | 44 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewSeasonActivity.java` | 117 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTrailer.java` | 119 | no |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTvTrailers.java` | 55 | no |
