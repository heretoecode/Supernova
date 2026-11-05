# Bug hunt area 2 — partial semantic review

Read-only findings against the source after Parts C and D. No code was changed for this review.

Coverage limitation: every listed Java file was read in full by the inventory/risk-pattern pass and joined with its file-filtered lint findings. This is **not a completed every-line semantic review** of all files. Direct code review was concentrated on the evidence sites below and their callers; the remaining semantic review is outstanding. A file with no finding is not certified defect-free.

The fetched reports/lint snapshot contains pre-edit line numbers; lint entries below retain those original coordinates. Findings use the current code coordinates. SuspiciousIndentation was addressed in Part A; no new lint run or tests were performed.

Inventory: 139 files, 32174 lines, 68 lint entries.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (lines and why) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|
| U1 | `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java:295` | logic | user-visible | high | Renaming an associated subtitle fails to rename its cached counterpart, leaving stale cache filenames and associations. | cacheDirPath is a file:// URI string; that string is passed to new File(cacheOldFilePath) and new File(cacheNewFilePath). File(String) treats file: as a literal filesystem path, so exists() normally fails for the actual cache entry. | Build cache Files from the cache directory File and child filenames, not from URI strings; update lists only after verified operations. |
| U2 | `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java:257` | threading | user-visible | high | Legacy phone subtitle association/deletion can freeze the UI or trigger an ANR for slow network or storage providers. | onItemClick and context-menu callbacks call renameFile/deleteFile directly; those call FileEditor rename/delete and scanner operations synchronously. The TV wizard and Supernova chooser use workers, but this legacy manifest activity does not. | Move legacy file operations to a lifecycle-owned worker, serialize changes and post only the UI/result update to the main thread. |

## Files processed and filtered lint findings

This is the full scan inventory, not an assertion of complete manual line-by-line review. Each lint entry is shown only with the file to which it belongs; “none” means no entry in the supplied snapshot.

- `src/main/java/com/archos/mediacenter/video/foundation/BundledNoticeReader.java` — 173 lines; lint: NotifyDataSetChanged@107: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; NotifyDataSetChanged@127: It will always be more efficient to use more specific change events if you can. Rely on `notifyDataSetChanged` as a last resort.; ViewConstructor@14: Custom view `BundledNoticeReader` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`; SetTextI18n@106: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/foundation/DoubleRingDrawable.java` — 66 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/foundation/DoubleRingMotion.java` — 8 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutActivity.java` — 100 lines; lint: SetTextI18n@56: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutEntry.java` — 21 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java` — 826 lines; lint: ViewConstructor@54: Custom view `FoundationAboutWorkspace` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`; SetTextI18n@516: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@525: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@528: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
- `src/main/java/com/archos/mediacenter/video/foundation/FoundationUiPolicy.java` — 17 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/foundation/FoundationVersion.java` — 10 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/foundation/LicenseDisclosure.java` — 106 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupOperationModel.java` — 351 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupSettingsController.java` — 739 lines; lint: SetTextI18n@192: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@234: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@246: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
- `src/main/java/com/archos/mediacenter/video/leanback/settings/BackupWorkspace.java` — 607 lines; lint: ViewConstructor@17: Custom view `BackupWorkspace` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`
- `src/main/java/com/archos/mediacenter/video/leanback/settings/InlineOpenSubtitles.java` — 131 lines; lint: SetTextI18n@84: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@97: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@102: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@124: String literal in `setText` can not be translated. Use Android resources instead.; SetTextI18n@127: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/leanback/settings/InlinePreferenceChoices.java` — 188 lines; lint: SetTextI18n@132: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
- `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewBuildInfo.java` — 73 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java` — 163 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java` — 1415 lines; lint: SuspiciousIndentation@423: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@428: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@432: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@458: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@468: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@479: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@532: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@545: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1020: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1037: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1041: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1051: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1058: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1066: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1074: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1076: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1094: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1096: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1139: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1148: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1154: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1175: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1202: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1286: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1288: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1293: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1299: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1303: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1311: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1322: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1327: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1329: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1331: Suspicious indentation: This is conditionally executed; expected it to be indented; SuspiciousIndentation@1346: Suspicious indentation: This is conditionally executed; expected it to be indented; SetTextI18n@1058: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@1083: Do not concatenate text displayed with `setText`. Use resource string with placeholders.
- `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsChoiceDialog.java` — 152 lines; lint: RtlHardcoded@85: Use "`Gravity.END`" instead of "`Gravity.RIGHT`" to ensure correct behavior in right-to-left locales
- `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsRegistry.java` — 144 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsSwitchThumb.java` — 18 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java` — 867 lines; lint: ViewConstructor@26: Custom view `SettingsWorkspace` is missing constructor used by tools: `(Context)` or `(Context,AttributeSet)` or `(Context,AttributeSet,int)`; SetTextI18n@788: Do not concatenate text displayed with `setText`. Use resource string with placeholders.; SetTextI18n@788: String literal in `setText` can not be translated. Use Android resources instead.; RtlHardcoded@719: Use "`Gravity.END`" instead of "`Gravity.RIGHT`" to ensure correct behavior in right-to-left locales
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsActivity.java` — 153 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsFragment.java` — 236 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesActivity.java` — 38 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesFragment.java` — 163 lines; lint: SetTextI18n@122: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackActivity.java` — 37 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsMoreLeanbackFragment.java` — 156 lines; lint: SetTextI18n@121: String literal in `setText` can not be translated. Use Android resources instead.
- `src/main/java/com/archos/mediacenter/video/utils/ActiveOperationMonitor.java` — 68 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/AdditionalServiceSingleton.java` — 148 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/BackupFormat.java` — 58 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/BackupPrivacy.java` — 325 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/BackupReview.java` — 82 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/BaseInfo.java` — 83 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/CodecDiscovery.java` — 409 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/CustomListPreference.java` — 79 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/DbUtils.java` — 438 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/DebugDbExportDialogFragment.java` — 159 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/DelayedBackgroundLoader.java` — 135 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/DirectEpisodeLookup.java` — 75 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/DirectMovieLookup.java` — 31 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/DirectShowLookup.java` — 94 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/EpisodeInfo.java` — 294 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/ExternalPlayerResultListener.java` — 245 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/ExternalPlayerWithResultStarter.java` — 28 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/FolderPicker.java` — 632 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/FontNameParser.java` — 115 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/FontsFolderSafPreference.java` — 112 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/LocaleConfigParser.java` — 61 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java` — 505 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/MigrationBackup.java` — 196 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/MiscUtils.java` — 709 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/MovieInfo.java` — 196 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/NovaWebView.java` — 73 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OnlineId.java` — 46 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OnlineIdUtils.java` — 50 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesApiHelper.java` — 622 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesCredentialsDialog.java` — 157 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesHasher.java` — 165 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesQueryParams.java` — 61 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesSearchResult.java` — 81 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/PlayUtils.java` — 525 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/PreviewScanReceiver.java` — 36 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/PrivateModeUIHelper.java` — 108 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/RestoreJournal.java` — 70 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java` — 418 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/ScraperResultsAdapter.java` — 361 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SerialExecutor.java` — 55 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SettingsBackup.java` — 123 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SortOrder.java` — 61 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SortUtils.java` — 151 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/StoreRatingDialogBuilder.java` — 75 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java` — 446 lines; lint: ApplySharedPref@270: Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background; ApplySharedPref@277: Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background; ApplySharedPref@305: Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background; ApplySharedPref@315: Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background; ApplySharedPref@413: Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background; DiscouragedApi@37: Use of this function is discouraged because resource reflection makes it harder to perform build optimizations and compile-time verification of code. It is much more efficient to retrieve resources by identifier (e.g. `R.foo.bar`) than by name (e.g. `getIdentifier("bar", "foo", null)`).
- `src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscoveryPolicy.java` — 113 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitleFontsFolderSync.java` — 333 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitleReplacement.java` — 26 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitleTransfer.java` — 31 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java` — 1074 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java` — 498 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java` — 492 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/ThemeManager.java` — 427 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/TrailerServiceIconFactory.java` — 30 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/TraktDeviceAuthActivity.java` — 527 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/TraktSigninDialogPreference.java` — 145 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/TraktWipeDialogPreference.java` — 40 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/TrustingOkHttp3Downloader.java` — 121 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VerifiedBackup.java` — 30 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoMetadata.java` — 447 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesActivity.java` — 97 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java` — 2396 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesFragment.java` — 57 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesLicencesActivity.java` — 43 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesLicencesFragment.java` — 46 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/VideoUtils.java` — 418 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/WebUtils.java` — 77 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/WebViewActivity.java` — 137 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/BackupCategory.java` — 41 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/BackupImportPreparation.java` — 191 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/BookmarkStoreRecovery.java` — 260 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalMatchExecutor.java` — 53 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalMatchMaterial.java` — 360 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalNativeRecovery.java` — 192 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/HiddenItemReplacement.java` — 103 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/IndexedSourceStoreRecovery.java` — 303 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupArchive.java` — 309 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupCatalog.java` — 77 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupExporter.java` — 186 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupImport.java` — 66 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchIntents.java` — 238 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchInventory.java` — 167 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchManualChanges.java` — 41 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchReconciler.java` — 32 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/MediaStateRecovery.java` — 435 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PlaylistStateRecovery.java` — 345 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableBookmarks.java` — 181 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableCustomPage.java` — 101 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableHiddenItems.java` — 88 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableIndexedSources.java` — 220 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableLibrarySources.java` — 189 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableLocations.java` — 58 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableMediaMatches.java` — 272 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortablePhysicalReferences.java` — 47 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortablePinnedTitles.java` — 283 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableSavedPlaylists.java` — 232 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableSettingsRegistry.java` — 289 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableTitlePlayback.java` — 233 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PortableTitleReferences.java` — 42 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/PreferenceRestoreTransaction.java` — 1510 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackCompatibility.java` — 55 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackInventory.java` — 113 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackOverrides.java` — 323 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackSession.java` — 116 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/backup/VerifiedBackupPublication.java` — 81 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsEditorDialog.java` — 108 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerAdapter.java` — 99 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerPreferenceActivity.java` — 52 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerPreferencesFragment.java` — 107 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthCallback.java` — 29 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthData.java` — 41 lines; lint: none
- `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthDialog.java` — 382 lines; lint: none
