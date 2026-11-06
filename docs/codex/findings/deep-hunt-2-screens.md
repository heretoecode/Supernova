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

## 0.157 continuation — refreshed complete allowed-file review

Refreshed after implementation 24aa1d4eb. Inventory includes current root Leanback classes and Details classes,
retaining removed/excluded historical entries. All 107 allowed files were read in full, sequentially, through their
constructors, callbacks, data guards and teardown; 47 protected files are skipped and two historical files are removed under MORE item 5, never marked
reviewed. DetailsBackdropController/Hero policies and PreviewLibraryScan were reread after output truncation.
Changed data-loader/queue/history/chart hunks were reviewed again. No screen file was edited.

| ID | File / trigger | Kind | Severity | Confidence | Evidence / disposition |
| --- | --- | --- | --- | --- | --- |
| DB157-01 | PreviewEnrichmentQueue.drain | Worker lifetime | User-visible | High | Store open/nextJob failed before the job finally; draining stayed true and later offers could not start. Admission now releases ownership and schedules bounded retry; injected open-failure regression checks a new drain is admitted. |
| DB157-02 | PreviewLibraryLoader.getSelection | Role ownership | User-visible | High | The override omitted the base personal-role filter, so the Preview snapshot still admitted a marked movie. Explicit role predicate plus real SQLite before/mark/Undo regression corrects this continuation defect. |
| DB157-03 | PreviewDiscovery.load body read | Input bound | User-visible | High | ResponseBody.string allocated the entire body before the 2MB check. Declared and chunked responses are now byte-bounded while reading; real HTTP oversize and UTF-8 regressions added. |
| DB157-04 | ViewingHistory.reconcile first migration | Data race | User-visible | High | A playback write between initial read and legacy seed could be replaced by older resume/time values. Recheck absence under the writer lock before seeding; controlled interleaving regression retains the later playback record. |
| DB157-05 | VideosByFragment.onLoadFinished category result | Null result | Crash | High for null path | Category cursor is dereferenced with c.getCount without a null guard; provider/loader null delivery reaches it. Screen owner should retain prior rows and show existing failure behaviour; protected screen unchanged. |
| DB157-06 | InlineMenuSelector.dispatchKeyEvent Back | Input lifetime | User-visible | Medium | Back-down cancels editing and clears its flag, while Back-up then falls through to super. Check whether the parent handles the unmatched release; consume the pair if so. Screen/device verification required. |

Other bounded observations: main-thread disk/cache reads occur in SharedMetadata.entry/PreviewMetadataCache.read
and variant compatibility initialisation enumerates codecs from the loader constructor; record in performance audit.
UnavailablePosterBroadcastReceiver still logs path/title/failure text; add its sites to the privacy fix queue.
No old U1/N1/N2/L1/P1/U2/L2/DP-01…07 finding is re-reported. A full source read is not device acceptance or proof
that every possible defect has been found. Per-file hashes below identify exactly what was read or excluded.

| File | Lines | SHA-256 | Current review |
| --- | ---: | --- | --- |
| `src/main/java/com/archos/mediacenter/video/leanback/BackdropTask.java` | 253 | `06059d7faebd33d0c7966305eecccc8edc1279f3ddec56862c37aae044c403e8` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CategoryBrowseFragment.java` | 48 | `15633b2b4e60e162965ea5b265a472fe07c6fa414e29b86a8324ff08cf6b05cd` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CategoryRowsDiff.java` | 70 | `94b82ee69f67692054159c8710c0d4d3bc83c111f0cba5de4b1377074de76efa` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CinematicCard.java` | 196 | `10b3f5414493846454bacf43ce9ba69d4aeccf15e3d5154893dbdb5d9079516f` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CompatibleCursorMapperConverter.java` | 43 | `140503e5400a95a04ec67618b5e40eae75678b09559734eef4141888a0f3c2e6` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java` | 384 | `af9e9d13262e979c6c211ee735f830d5e03e52fb8fb38ac0e5bf90b529eb104e` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryPage.java` | 161 | `7fa332dbe15e74d049a923541b7df4f2d8f5a3123e37916223338afb64880c26` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/CustomiseHomeEditor.java` | 808 | `6bc7041c38cc8b327032bf631a98a97aef37ffed144cd9f1c45af3d8cc9ab350` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/DetailsBackdropController.java` | 159 | `756ed3d09650bf47ca1fe09fa0bdf03017d7d72f9a587ea00d3075d97c2affdd` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/DisplayMode.java` | 23 | `a9cd22a04d69ad75c651e9d232c830569ca426e52eedd94f2d7b110f3f23c477` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/EditPagePanel.java` | 749 | `e30187088382d52b3bc208f594b53e197080f30067aac5a338a3d2dc8e718391` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/ExperimentalBrowseFragment.java` | 25 | `30e4dfc083bfde9a59d514483066845e67baed719af2d342ec95a1b07f66fc6e` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HeroEligibility.java` | 15 | `c0e7e416815f3c52efd5c0a6d6304014bd306dd6324a2ddb8e59297491d9efc4` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HeroPlaybackEligibility.java` | 32 | `7ca2971aa3e02055b102a841e6845dd80bd86d8ee0cfefeef77226dfee2d062c` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HeroRotationPolicy.java` | 72 | `f3fb3220b421672b7617e875151bcafa3754b3e519b60224a47d719beb929ac0` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HomeGenrePicker.java` | 391 | `70e9ccce32c7fed8cbe443416b6faf95d3b40a4dc32394af5a3d9d95d09e17fc` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HomeIntentCodec.java` | 230 | `8fc02bdc4cf305b15dacc9b7144716ec824751eb4a91d6a4619d5c19e628ac27` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HomePlaceholder.java` | 77 | `20ad93f70aa9e92c0302ac6eac67f521a6c41b87255df7fba5010d81d837dc19` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HomeRowControls.java` | 258 | `79a87cbbbd02c85e683b1e390d60793c7c64e1fd3bfc8390982b3bce44ad56e4` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/HomeRowMover.java` | 515 | `f101d78e9572106962a7d7985eb45cd8c0a89a226c5ba4e574b5781dbcee725b` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/IconListRow.java` | 43 | `992eb68f6f0f923bc5a7ba2ee5ecd755d89c44b99e899b5f9366e63aad863bc6` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/InlineMenuSelector.java` | 101 | `0888f6c3d97503a84a41781bd028f0f36e070bd50ec40471067639751bef2533` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/KeyboardInput.java` | 54 | `f5f5ccf1ca836af5c7092c1f61fc8970a7b9090a90c62cd81690d6f420c08fb9` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LandingTile.java` | 143 | `2000b76b3f1b39402d6042ddfabbd5ec5aa499235daf28fb92765d5cc59c34ad` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/LeanbackActivity.java` | 103 | `ebd300173d6981ef5748cfb856c0d1de6bccd85f727e319fa180c239b6f29bce` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LegacyViewingSnapshot.java` | 78 | `85808d5c1539a6e9d96987d75d8a1410fe9d5688cda32b53f211836fb81f91c6` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryAttentionCount.java` | 53 | `70af54740706554d4930bb51b7f0c4296f40e06117dbc8a12214519da3060151` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryHealth.java` | 177 | `1229c19499a2ef554b7ffccfa88cbf07e409f65695c6a1a9e9de1dcc541d67c5` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryHealthActivity.java` | 376 | `3978fa186711bca879057dfb0d38ef5c8e57e7db23bd577efa3dfad710e45c80` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryHealthPanel.java` | 267 | `c15f843c9b1369495ddd03d1287016492cfa0620fabb9fccb55ce81e326b8ee5` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryPageHeader.java` | 179 | `9abb87732e86b2ed871dbcf71b2f880f5ed93322494d91b22c4af38eecff5738` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryScanRecord.java` | 39 | `d517e9e08c1f849e5ea4ff0620b197bbb82bfcf28b9295dca36122994e4fb935` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/LibraryStatistics.java` | 48 | `c61fb877a863b816361dd085632395a38705b9dd76ca3e0b54177d036f9ea0ff` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/MainActivityLeanback.java` | 254 | `17f1bbf5a05862a35c39124d04ab60e0cf6ecbf337e10de53314e17801a725e7` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/MainFragment.java` | 2640 | `245993a4bea56d814e4dc7ed3cf0aaa668622c9289710f5645c982ba43c9d017` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/MenuFoundation.java` | 98 | `d4072aca5e66b8daf336a147bf675a4bb2e89d9b0a9a306af018a653c37e3a09` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/OfficialTitleArtwork.java` | 518 | `b50248c300c4a42f1648aa70511470bb613f3e864637436dbc025be6fe9a6649` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PageExitGuard.java` | 3 | `daf6d80ed94c5feb2d66ddac819308a109f63a638fb954ee512207b2bfe86263` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PicassoBackgroundManagerTarget.java` | 83 | `24991e3bfc79c92543932fdce42625d253213af88c846eafd82781775601592f` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PlaybackCompatibility.java` | 83 | `aca6e7860c8f97f769bca6c99f857e710048d30f180d076290653d2dd07006fe` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PortablePreferredVersions.java` | 193 | `c12e07dbc0baf2a827fb23328b28e010bb51f1898c37aa92043e7860062d0dba` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PortableReviewNotes.java` | 146 | `230f2f3fd1724f9959c4d4323e9887e4d4e9cf54e7c5cf8065b3043b99259796` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PortableViewingChoices.java` | 160 | `2fc83aa6e5ccaafa3c6376bb12a7b45ef0a354875b73eaf80f7126a76bb05222` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PortableViewingState.java` | 246 | `5a93c65eceb18eeb016fafa65f09c77a852f199b9cf2674738563aaa5a3dad4d` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewAccent.java` | 122 | `0d2a0f010127acf78f12e7a2a1f7bc9be8cae891df28fd946e48de17c34347f8` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewArtworkPicker.java` | 184 | `3fd43b8238168443017863078bb83068f186e5798db1a1e9fabc5407b2ba2b9c` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewAutoScanPolicy.java` | 36 | `8705cc133d9dc81f6ee224adcdfafc98fde6e008bd5d31a0498913eb983dc97f` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewBackdrop.java` | 271 | `9d2cdc85d40edf5530cb30a1bc838484a20e5e55a5424a15b195db751d8cd084` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewContentFocus.java` | 14 | `bfbe11fb36b5d0fcd87a2428a54083e3b79b312d9b4003d3624122053eb37bfa` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java` | 699 | `aa8fab1f38c8c507665971f03eb48109e282da490e39c0b10d85149888eea085` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewDiscovery.java` | 103 | `3abf26c0a66435b946ff729f3a1130088b775878167b8d81aef905fe46815843` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewEnrichmentQueue.java` | 316 | `14daa0b83e131e06cfea1687ab5ea70519b3ea275c9bc348e9712a978b131c13` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedCard.java` | 247 | `862cab343017ff54bf399465f4c76fdbc46643bca220021ebd730c4bc883f807` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFeaturedIndicators.java` | 46 | `f488ccb03140335f16df3862ca053943057a166382676a451fbab77c26f05395` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusGlow.java` | 30 | `91092836760abd4e9da5d45a4e3ecfb69d9f65c2a35c96ed982b821fcf9a63b7` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusRail.java` | 117 | `e96186d1444d121e01c5485ebdcb69e7b781d9e27caf4734f1d49efb02adae19` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusRecycler.java` | 124 | `836933806d61f85fe8b6f9204ce6389ab6ef3ec94ccd75d01a095764ee975571` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFocusUnderline.java` | 21 | `b5a09c57661b310a30a2ac7cabe78304e3fe9b66a384b5e5ce0c431ecbba2478` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFolderActions.java` | 26 | `30796f73c2b91830cc2c8da35bc22b629c0ba0c54429b03e0ad6a786c4a16794` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewFrost.java` | 30 | `814c3785d7983147bcbd96784edf2bc552e881df8e99db42af4beee6ff3dcaee` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewGenres.java` | 367 | `0885686bc3a312843688646dcbaf0bacea11292e8100ec6c8af814a11b28ff6b` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewHomeRows.java` | 370 | `759a5cba593a744f15236fb2d5dfbd2953885f50be24943c294293ea04ae1ef9` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewIcon.java` | 419 | `90a46727a3cb8a9b6690b0ded6bcdcf123e631d7f491f384acb5d796557bb72b` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java` | 369 | `8118fa420e0885b32701d78a882ac5c0fe62cb3efb8a83b51fb0dcaec9bd91b0` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLanguageIcon.java` | 255 | `a25f8dbe5e39427fbbc4c56e6206159bff2988f6e796ede5b52b06fac9730fcc` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryColumns.java` | 471 | `2a03bb1302528895c270bbe1cf986e5152efbd6250a95994878912b18444ed56` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java` | 716 | `44aff6c3e266082351c9acce7bc0b916828410f522a49a06818ec8f75fae7c7b` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryScan.java` | 371 | `a524eb5d63c9694f83268b1f0af000ef1632f897672c946c33be85ad2888801d` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLibrarySummary.java` | 75 | `be708902b6fbabe4460f02327466ee355a9759ece6bec964dc8340f458f72289` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewLocalScanState.java` | 103 | `89e5f92a783efe109d35c2daf25f02a6d37256f3c36e672dc39b48c3b4021007` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMappingCursor.java` | 45 | `5e71d007789dc5ce79547bfc22affe023cba18a6002abc2b2d3e4b7dd76dc391` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMatchSearch.java` | 336 | `99a40e8efcda4ee02d3b082e1a5143fe26edc0c3638ddc1b097ed481874d0a87` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMediaClassification.java` | 82 | `1aa49ba368154477b67397a100b956bb10911fa3557c0d19ec611221f193582e` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMenuPlacement.java` | 18 | `76dca951a27a8d9c9a577c64340481ec42540d93844bf8894d0fbab45da28149` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMetadata.java` | 210 | `d8ad2f31177e55c22e6a3a9990c97d6e853c1af0d698e4de9560669785de4377` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewMetadataCache.java` | 60 | `3c8a81790497434b8ca715d86aff3c683998968e142f5d7a9254deb0554b7e6d` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNavigationShade.java` | 0 | `None` | removed |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkScanning.java` | 115 | `0fe75de43e932996e9b90eadab8eb1225368f6c1ad6ecbf494b4a248eb8ee5cf` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkWorkspace.java` | 76 | `d44a6c5cb8462e70827316d1c688e66dcf59855681d319e1ef8965553511a25c` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewNotice.java` | 49 | `77bad091d27255a5dc8cda64b5955fb7b57f980005fb32997be66f4f96b9e7f3` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewOperationDialog.java` | 43 | `5950b59288c4c80de745a829ea9c4ed869ce9163faeab4005ab84eaeab929829` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java` | 2836 | `e681ff004db40ca5eef0be5b1f3f6be00b0d2abc757888475de617115c27bca3` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewReleaseOrder.java` | 24 | `550fe9fc46aaba3e4d7fd7418156456e8369a293441de2dc85dffe59cb045672` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewScanProgress.java` | 21 | `d63b302b7f3b2f9dac5bbdd898e7e68a19a34ac1a64926dc1fbdcc41bdb41adc` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSeriesJourney.java` | 70 | `aa31e99241949f014ea81940c90346e2bafcca9fb41c8175dbbc649748fc4ef2` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSources.java` | 297 | `3594efbf4e85ceb739822d8e0f4f13a64d4bf51ed6622245fc0f733fc4769dbe` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewStartupSurface.java` | 53 | `55b5ea5213e1da27cb0d6ef28b66a8a07ff6f39ffe0e0513c0bd783efc8d8b8a` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleChooser.java` | 313 | `2694679979e0f93f7e5ba5b44b1d1cdfc7e76caca6807e6fb15eea2e4f8d0d59` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewSubtitleResults.java` | 30 | `bb4127ba64c176371ded8bd2f7552dc59cccf4b594b67240343e208c7d5ed663` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewTextInput.java` | 128 | `40c088dc7605bae3c85d8a6a5b88954adc6ecf4e0f52aba89b8c026f29e060ff` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewToolbar.java` | 132 | `8f0699ec230cef68716c38b17aa95815fd1dced78a5cf13e5327a66747d6f220` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewUtilityBackground.java` | 93 | `ebdbf89ce30966c4651fe8a7e3588b6679afcdd189590f34d2a60c0f98d706f2` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewVariants.java` | 351 | `1cb7df346435d07798ca07e87fd32a5a8e6139cbd4286df30816084ee8331539` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewVersionsDialog.java` | 91 | `0821d6d92d4bd9c553d3d99c777d8d247f0b31d8554033c6b8ecb047a4261930` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/PreviewWatchedScopeDialog.java` | 24 | `1736a73301b941aa8865262ac769a7ca6830398384fb10b93858d5f77af52f1a` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/ProviderMonogram.java` | 88 | `66fff07bf11726ffcfc30c1485dacc67aa9951f0e7e453a7f7cb10eb6d43465e` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/ShadowLessListRow.java` | 33 | `bb0c5265d54104e4ec8de37b76ea8b6a505f0846c826b5802aaeb8f57edeed37` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/SharedMetadata.java` | 320 | `1792ea4ab77c0a15b380de8c090b2cc6ce807a309201e43a79fa75cce0569bde` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java` | 191 | `8f1080f1112bdb46f10a1122bce0ae231ddeefab31184979b7e9fe2fcda4f9e6` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/SidePanelKit.java` | 326 | `e7e05ab0a9c30c9ea619327fe6573776bcb82fc16986aaf0c7ba79b998014a64` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/SingleFragmentActivity.java` | 52 | `94c4ac2f7143a0776a6bb0b035c65029048d264af3fc1ed0ecdb9a945b435edf` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/StandardSwitchDrawable.java` | 129 | `a0482fb07a9bbcb209bc26c0d303d045ee83c2a3de62e319080ffdfadb0f919f` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/SubtitlePanelStyle.java` | 418 | `148d0952422891c80ac831d801285302fd34f0465b42d05a27087762738217d0` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/SubtitleResultsPanel.java` | 214 | `b0357bdc2498f34a117c03753d5234e00397f6f87b0537b3abf9079635d7bd77` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/SupernovaStyle.java` | 649 | `a1a57363393d4842e6cb82e8da9890b71dbeb2135515382d4b48584ee7c58c56` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/ThinSpinner.java` | 70 | `25c48972893813867ad3e8e184c32107a2eb3e98fa6cfd9e6084e574abefdace` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/TopNavigation.java` | 591 | `8dad747ce49f6245cbc03ca4a8e55fcd8bc938e098711e46fb35a12256423fdd` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/TvButton.java` | 38 | `d6b3bf76bf3ff2c749fb2a12f21e99797b886a59368e63e77adb997f44eabaa0` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/TvLayout.java` | 65 | `2a087810fe8a4d0ff9217c0a8103d8de2f397d6473582351449a9d037072e8db` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/UnavailablePosterBroadcastReceiver.java` | 203 | `21ffe7086fdf3092bf2ccbc627798fdb0404cd3a413071072f4dc9a64e0abfae` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/VideoViewClickedListener.java` | 225 | `f9157be58a1aaa93422da3cf9fbff21170714541a60ccaafc73534d7cd6d4bae` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/VideosByFragment.java` | 495 | `d0ccaf6568b2e0186abb5e167edc4b6e5966c7c7a8a6cb3f003b1f7016d43946` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/VideosByListActivity.java` | 25 | `40859cadf6528ec029f6d7768b5474fcdf84b29c02f18cbb621c18f18abdd812` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/VideosByListFragment.java` | 115 | `6f6b8e9972d2fbeb0d528cc4eeb37c6464d0b86518dd29015dd3b9a2a5fd2b54` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/ViewingHistory.java` | 754 | `d740f7424e9a18361cbf64e6d9c74768e27795ca3c883037281ac4c8b26ba856` | read |
| `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRowPresenter.java` | 225 | `583c786ca47961943af8138eea4f8f2339fd7a1c9b12e5d89ee5430734b27b85` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/BackgroundColorPresenter.java` | 22 | `5f30facce87028ae1c8d27727e36392b371d936db04202bf758f294c12bfdc80` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/CastRow.java` | 41 | `7f68370438050756c430c48d2236cdc4d488e0cf052a0903a15ef079ac0e6464` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/CastRowPresenter.java` | 125 | `eeaad84cfe60bfb08c49ce6ae82257ee219959a00fcaaecfb8903efb29d0b44b` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/ConnectedDetailsPanel.java` | 71 | `aa518b09ed50fa27c96a29196351b08dabfb713ae92ae558f56f0ed43e106d6a` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsBlueprintPanel.java` | 489 | `d4dfb926633b60c85e530f1385ec342118981766465dcdbc9c595d8e572f7ca1` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsDimensions.java` | 10 | `34bc431d5be97cd41126f85abacba295784623d0863a9d4fa996c3f71d7a5e1f` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsPeopleGrid.java` | 70 | `a186b8c11978e5f8acce5f23aa27ae711902e3a882b6ce4b504df4ba20c364bd` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/DetailsReviews.java` | 248 | `c9980d3ad82599a4e66f240a902c750a74d50c7ba0b6d7edb61b50ae8ea469b9` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRow.java` | 44 | `44611f47f04adbad7e3da440eefe9eda9bd7ca81d749bc590741eab1421ebdef` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/FileDetailsRowPresenter.java` | 312 | `9a3c355db68bd5338646a6a83a37cde3dd75eb804a71fd4cb7eac1de4db352bb` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/FullWidthRow.java` | 32 | `03ca9b744ba848868f08add6152fa0faa3aec340674764f63707e95bf8780138` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/FullWidthRowPresenter.java` | 87 | `8e3ac451cf2b486eba6e6918982137bb805f00e5ad19cb72f15e7a6e081d8b0e` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PlotAndGenresRow.java` | 39 | `d3a124296d3c2538a24431b9c13876c663b65c235060457076eb44e0632fcabc` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PlotAndGenresRowPresenter.java` | 122 | `27cb9f8dad74feccf3890bb7b9fc082666981e70ebeb17485d435deaa3be0f6a` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsData.java` | 375 | `bf13368d8d1904a377046fa597e685d57c7ab276e7d170e7ae0d61bc066fcd17` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewDetailsFacts.java` | 124 | `1e2eb755671d597e5514b167da3bd0ed7e35b611b5323e6cff22f1df00d338d1` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeChoice.java` | 32 | `0b00d39d62e72a1f48dfca28069ab2d279d806e28dd52aad48c363c69c968b81` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewEpisodeRow.java` | 202 | `1da02fc2f0281f54442707dfed56e4493a2fce971adf4f3914b10829d75d39e2` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewExtraDuration.java` | 34 | `c8d65195822e989e24d12ff269652e297a1553600101d15c7ca5365f02bc93f5` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewLandscapeCard.java` | 101 | `fb51485c4afcc93a25641a9f1f354bd19bd55151f32ab2f4e01e7b61fe465d89` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMediaInfo.java` | 58 | `3257cea2e9277c36f0abb24041c62907d460000f1486d8e6ebd2adabc41127a6` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoreActions.java` | 38 | `b25ff32c4b59523d71cf0fcbbb75e2d90b92162b374b450010b450d2b25e13fc` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java` | 2652 | `228fdde9ee8a1e77967729f2664e5ef93efdad9f5c5fc632d48abf29e5a960ab` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeople.java` | 85 | `47dce17cd662718f997b458f514114b0fa6a9ee5d5354354c8133953f7398a67` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeopleRail.java` | 0 | `None` | removed |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewSeasonActivity.java` | 117 | `3da5e3f55f4313e0ef544ea9a9e656ef86d659d5c5e2ddc94832c1fbe4e6a140` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTrailer.java` | 119 | `b10625701b6b11e17c4bc80b4a424b563d6840ee06b539794851a7e4cfcd871a` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewTvTrailers.java` | 55 | `6615f2b3922f6f043cbdc2a35f99f89ecd11a5e44753b7d5482587d9d4a34fdc` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/SelectableListRow.java` | 41 | `2e9e84c20e48f3deb2814a6ba7ca7ebd8272032d05a663413478b67fc3b5bfe5` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/SelectableListRowPresenter.java` | 58 | `d4e6be59f71842dc78b17fa683a163e1cf0caed01793b21699cc2fc3ee123d68` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/SubtitleInterface.java` | 26 | `5b4cb74e4c1b1fb7ce85db6fa92d9391b0abb20c9b223b6271be2b12e1e39308` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRow.java` | 47 | `b243437b961e731cc5701ce9e3f8761b0d5ce3aae14b02a3f1988ba006b30686` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/SubtitlesDetailsRowPresenter.java` | 249 | `24391aea24b426e1abc329f0241c1e43d1e94143a40575b42bfad8bc82d0c4bc` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoActionAdapter.java` | 283 | `d2c9e0bf33d62891609e5444ccb318c05b6a00437617e382d02cab30c52e370a` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsActivity.java` | 137 | `2166bc0ef271df996a4f8d94df9b7434accb610a3b59e387b5124b0ed3441fbc` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsDescriptionPresenter.java` | 423 | `62d824adfdbbaa4d6ddc13ce12dc345d2cf264b99602d35b8d768b795cdd7077` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java` | 3024 | `d90ce5c19b16d1605b33c192a1ac7036952f5f83162208abcca87b91da413530` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsOverlayActivity.java` | 22 | `0e3d8b2ac52ff07f32c4692d00f4132e3ad5f51c14e19f5d1d9b3afed0f266e5` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsTransitionBackdropCache.java` | 64 | `380ba36c075e0190efd2cd376cf1b440d0fcb9ac1f90b7dcc590845011f86f49` | skip-hands-off |
| `src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsTransitionPosterCache.java` | 44 | `8352983711817222a2ad2e816618084021f4e687dce73adebaa180c53c5bda03` | skip-hands-off |

Fix commits: DB157-01 `82d8b759b`; DB157-02 `fc439ab2c`; DB157-03 `3b361ff1f`; DB157-04 `4ff8348fa`.
The table includes reviewed post-hunt hunks. Negative controls against 787b6c684 reproduce the queue, role and
history defects; the current tests pass. See 0157-evidence/post-hunt-negative-controls.json.
