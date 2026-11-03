# Deep bug hunt 4 — not reached in this run

Source snapshot: `72b0756cb`. Read-only: no fixes, tests or builds were performed. The known U1, N1, N2, L1, P1, U2 and L2 findings are excluded.

Coverage: **0 / 130 files fully read**. A yes means every source line was read and the constructors/methods, callbacks, guards and teardown were considered. An excerpt, reference scan or prior-run read is not counted. This is partial progress; no unreviewed file is certified clean.

Priority was kept: work stopped within player/, so no later group received a deep-review yes. Cross-file excerpts used to verify a trigger are supporting evidence only. The four coverage inventories include Part A exclusions as no; those exclusions are listed separately below. Group 4 includes a conservative transitive utils reference/import closure so utility dependencies are not silently omitted.

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (quote the lines, explain the path) | Suggested fix (described, not applied) |
|---|---|---|---|---|---|---|---|

No new finding is asserted for this group: it has not yet been deep-reviewed. This does not mean no defects exist.

## Part A exclusions

`src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java`, `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesActivity.java`, `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesLicencesActivity.java`, `src/main/java/com/archos/mediacenter/video/utils/backup/HiddenItemReplacement.java`

## Coverage

| File | Lines | Fully read (yes/no) |
|---|---:|---|
| `src/main/java/com/archos/mediacenter/video/diagnostics/ArtworkRequest.java` | 35 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/ArtworkTrace.java` | 25 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticArchive.java` | 144 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticExportActivity.java` | 37 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFlightRecorder.java` | 91 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticReference.java` | 33 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java` | 1512 | no |
| `src/main/java/com/archos/mediacenter/video/diagnostics/PlaybackDiagnosticState.java` | 54 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/BundledNoticeReader.java` | 173 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/DoubleRingDrawable.java` | 66 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/DoubleRingMotion.java` | 8 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutActivity.java` | 100 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutEntry.java` | 21 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java` | 826 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationUiPolicy.java` | 17 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationVersion.java` | 10 | no |
| `src/main/java/com/archos/mediacenter/video/foundation/LicenseDisclosure.java` | 106 | no |
| `src/main/java/com/archos/mediacenter/video/metadata/OmdbSupplement.java` | 275 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/PreviewProviderIcons.java` | 127 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/PreviewRemoteDetailsActivity.java` | 112 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingActionPresenter.java` | 109 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingActions.java` | 382 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingPreferences.java` | 196 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingRepository.java` | 693 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAccountController.java` | 699 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAssociationStore.java` | 384 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAuthFlow.java` | 187 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserActivity.java` | 21 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserController.java` | 358 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioLibraryBridge.java` | 82 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioOAuthClient.java` | 147 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReadClient.java` | 344 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReassignment.java` | 242 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReconciliation.java` | 131 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioRootLocation.java` | 47 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSnapshotReader.java` | 39 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSync.java` | 181 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSyncScheduler.java` | 54 | no |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioTokenStore.java` | 72 | no |
| `src/main/java/com/archos/mediacenter/video/utils/AdditionalServiceSingleton.java` | 148 | no |
| `src/main/java/com/archos/mediacenter/video/utils/BackupFormat.java` | 58 | no |
| `src/main/java/com/archos/mediacenter/video/utils/BackupPrivacy.java` | 325 | no |
| `src/main/java/com/archos/mediacenter/video/utils/BackupReview.java` | 82 | no |
| `src/main/java/com/archos/mediacenter/video/utils/CodecDiscovery.java` | 409 | no |
| `src/main/java/com/archos/mediacenter/video/utils/DbUtils.java` | 438 | no |
| `src/main/java/com/archos/mediacenter/video/utils/DebugDbExportDialogFragment.java` | 159 | no |
| `src/main/java/com/archos/mediacenter/video/utils/ExternalPlayerResultListener.java` | 245 | no |
| `src/main/java/com/archos/mediacenter/video/utils/ExternalPlayerWithResultStarter.java` | 28 | no |
| `src/main/java/com/archos/mediacenter/video/utils/FolderPicker.java` | 632 | no |
| `src/main/java/com/archos/mediacenter/video/utils/FontNameParser.java` | 115 | no |
| `src/main/java/com/archos/mediacenter/video/utils/FontsFolderSafPreference.java` | 112 | no |
| `src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java` | 505 | no |
| `src/main/java/com/archos/mediacenter/video/utils/MigrationBackup.java` | 196 | no |
| `src/main/java/com/archos/mediacenter/video/utils/MiscUtils.java` | 709 | no |
| `src/main/java/com/archos/mediacenter/video/utils/NovaWebView.java` | 73 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OnlineId.java` | 46 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OnlineIdUtils.java` | 50 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesApiHelper.java` | 622 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesCredentialsDialog.java` | 157 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesHasher.java` | 165 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesQueryParams.java` | 61 | no |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesSearchResult.java` | 81 | no |
| `src/main/java/com/archos/mediacenter/video/utils/PlayUtils.java` | 525 | no |
| `src/main/java/com/archos/mediacenter/video/utils/RestoreJournal.java` | 70 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java` | 418 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SettingsBackup.java` | 123 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SortOrder.java` | 61 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SortUtils.java` | 151 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java` | 446 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscoveryPolicy.java` | 113 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleFontsFolderSync.java` | 333 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleReplacement.java` | 26 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleTransfer.java` | 31 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java` | 1074 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java` | 498 | no |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java` | 492 | no |
| `src/main/java/com/archos/mediacenter/video/utils/ThemeManager.java` | 427 | no |
| `src/main/java/com/archos/mediacenter/video/utils/TraktDeviceAuthActivity.java` | 527 | no |
| `src/main/java/com/archos/mediacenter/video/utils/TraktSigninDialogPreference.java` | 145 | no |
| `src/main/java/com/archos/mediacenter/video/utils/VideoMetadata.java` | 447 | no |
| `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesActivity.java` | 97 | no |
| `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java` | 2396 | no |
| `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesLicencesActivity.java` | 43 | no |
| `src/main/java/com/archos/mediacenter/video/utils/VideoUtils.java` | 418 | no |
| `src/main/java/com/archos/mediacenter/video/utils/WebUtils.java` | 77 | no |
| `src/main/java/com/archos/mediacenter/video/utils/WebViewActivity.java` | 137 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupCategory.java` | 41 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupImportPreparation.java` | 191 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BookmarkStoreRecovery.java` | 260 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalMatchExecutor.java` | 53 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalMatchMaterial.java` | 360 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalNativeRecovery.java` | 192 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/HiddenItemReplacement.java` | 103 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/IndexedSourceStoreRecovery.java` | 303 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupArchive.java` | 309 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupCatalog.java` | 77 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupExporter.java` | 186 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupImport.java` | 66 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchIntents.java` | 238 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchInventory.java` | 167 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchManualChanges.java` | 41 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchReconciler.java` | 32 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaStateRecovery.java` | 435 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PlaylistStateRecovery.java` | 345 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableBookmarks.java` | 181 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableCustomPage.java` | 101 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableHiddenItems.java` | 88 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableIndexedSources.java` | 220 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableLibrarySources.java` | 189 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableLocations.java` | 58 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableMediaMatches.java` | 272 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortablePhysicalReferences.java` | 47 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortablePinnedTitles.java` | 283 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableSavedPlaylists.java` | 232 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableSettingsRegistry.java` | 289 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableTitlePlayback.java` | 233 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableTitleReferences.java` | 42 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PreferenceRestoreTransaction.java` | 1510 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackCompatibility.java` | 55 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackInventory.java` | 113 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackOverrides.java` | 323 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackSession.java` | 116 | no |
| `src/main/java/com/archos/mediacenter/video/utils/backup/VerifiedBackupPublication.java` | 81 | no |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsEditorDialog.java` | 108 | no |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerAdapter.java` | 99 | no |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerPreferenceActivity.java` | 52 | no |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerPreferencesFragment.java` | 107 | no |
| `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthCallback.java` | 29 | no |
| `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthData.java` | 41 | no |
| `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthDialog.java` | 382 | no |
