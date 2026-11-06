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

## 0.157 continuation — complete refreshed service and utility read

Inventory refreshed against 24aa1d4eb and the current recursive diagnostics, foundation, metadata, streaming and
utility trees, retaining removed historical entries. Every line of all 165 extant files was read sequentially:
37,457 lines at initial inventory; four historical files are removed. This includes supporting screen reads without
editing screens. DiagnosticExpandedSummary157 was reread after an interrupted output, and MiscUtils 427–462 was
reread after truncation. Subsequent implementation hunks were reviewed again; final hashes are below.
The old 0/130 table above remains an honest record of the earlier run. Excluded U1/N1/N2/L1/P1/U2/L2 and DP-01…07
are not re-reported. No source read is a claim of device acceptance or absence of every possible defect.

| ID | Source / trigger | Kind | Severity | Confidence | Evidence / disposition |
| --- | --- | --- | --- | --- | --- |
| DG157-01 | ExternalPlayerResultListener.init/onVideoDb; PlayUtils:462 | Data ownership | User-visible | High | Singleton retained previous video and duration while the next async lookup ran; previous callback could replace current state. PlayUtils supplies null info in the normal external route. Reset per launch, abort prior helper and generation-fence replies. Fixed `56fe06e38`; actual result/write callbacks and per-video duration tests, both fail on previous source. Third-party result intents still carry no session token, so arbitrary late external results cannot be attributed by this helper alone. |
| DG157-02 | OpenSubtitlesApiHelper search/download TOKEN_EXPIRED | Retry lifetime | User-visible | High | Each response clears authTokenValid; recursive public calls reset the supposed one-retry budget even after failed login. At most one successful refresh and one retry per operation now; failed refresh terminates. Fixed `967bc6e38`; real HTTP persistent-invalid/failed-login/success cases; previous source fails two regressions. |
| DG157-03 | SubtitleFontsFolderSync.queryFontDocs/copyToCache | Provider input | User-visible security | High | Provider-controlled display name was used as File child; ../escape.ttf overwrote a sibling app file. Reject separators/NUL and require canonical parent equal private store before opening. Fixed `ed31a873e`; real file I/O plus SAF cursor/stream boundary test preserves sibling file, permits Unicode font; previous source overwrites it. |
| DG157-04 | ExternalPlayerResultListener init/result logging | Privacy | User-visible | High | Raw source/player URI, arbitrary Bundle keys/values and end_by strings reached appenders. Redact URI arguments and remove unstructured dump; fixed `14675bc02`, captured DEBUG appender checks arguments and messages while original operational URI survives. |
| DG157-05 | SubtitlesDownloaderActivity2.stageSubtitle:916 onward | Credential origin | User-visible security | High | Sends Api-Key and bearer token to the API-returned download URL before validating origin/TLS. Screen owner must allow-list credential origins and fetch allowed CDN URLs without account headers; reject insecure URLs/redirect credential forwarding. Screen unchanged. |
| DG157-06 | OAuthDialog host handling:190,225 and URL logging | Origin/privacy | User-visible | High for host predicate and logs | endsWith("trakt.tv") accepts eviltrakt.tv; mixed content is ALWAYS_ALLOW and URLs can include code/state. Exact scheme/host, existing UUID callback validation and redacted logging needed. No claim of a state bypass or trust-all SSL: superclass SSL error handling cancels. Screen unchanged. |
| DG157-07 | TraktDeviceAuthActivity asynchronous generation/exchange | Owner lifetime | User-visible | High | Cancel/destroy stops poll timer but does not cancel/fence in-flight generation/exchange; late success can resume polling or save a token. Screen owner should fence by generation and terminal owner state. Screen unchanged. |
| DG157-08 | CredentialsManagerAdapter click; CredentialsEditorDialog restored instance | View/restore | Crash | High for identified paths | Click uses getBindingAdapterPosition without NO_POSITION guard; restored editor has null transient modify listener. Screen owner must rebind listener and reject detached positions. CredentialsManagerPreferenceActivity also adds another fragment after restored state. Screens unchanged. |
| DG157-09 | ScraperResultsAdapter.updateItemData | View owner | User-visible / conditional crash | Medium | Position is checked before posting; setResultList may replace items before execution. Repeat position/identity guard inside posted action. Screen unchanged; scheduling/device regression needed. |

Candidates not promoted to high-confidence current-route defects: DbUtils empty-ID bulk-hidden selection (all
current producers pass one ID); pending-bind accounting in AdditionalServiceSingleton (caller/teardown proof still
needed); null provider cursor in DbUtils; OpenSubtitlesHasher exceptional stream/HTTP cleanup; font source-vs-copy
mtime causing excess refresh; FolderPicker late listing ownership. No finding asserts a demonstrated ANR.
Performance mechanisms and credential storage risks are recorded separately in performance-audit.md and
security-audit.md. Native summary wording predating optional counter support needs screen/export-owner review.

| File | Final lines | SHA-256 | Review |
| --- | ---: | --- | --- |
| `src/main/java/com/archos/mediacenter/video/diagnostics/ArtworkRequest.java` | 35 | `1e6d3b466a1d1987160c3c284160ac31fa69300db06a6d52b43984d7c3dbeae9` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/ArtworkTrace.java` | 61 | `8d7d01a4278256fdeeb6a29b2f50c65a2d3f3d230b576bdcd8d151f08168965c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticArchive.java` | 284 | `4b0058baf244181f49275a56e5af341f2966f87e758e2164ce1bc3f23ecd1e77` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticBackupState.java` | 77 | `ae8c708a8d13f2a747c20d6f927a2c03f18490302437bccd491772a0f13049a3` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticCodecEvent.java` | 36 | `7e1a6820cb4132a253cd75bcf29597176f93c4364998016bf437cca31fa5963e` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticExpandedSummary.java` | 239 | `39ce689c7164e3253ca35b921b3c7dbe8ccf7b89eb592ec2a6f462164b816738` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticExportActivity.java` | 37 | `c042a876d817189c73d8e0630342cc64be28facee26c8408195933aa5ea9c901` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFlightRecorder.java` | 91 | `1e1b0d02c06cdbc7e810f59b9707db80fa2893d25c27f582a768832f6cfbea38` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFrameMetrics.java` | 43 | `23abc6f51ad211ccab44d0d9b87a38c764087de8519bfdaf0e58d8d65fab8c3b` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticFrameSampler.java` | 40 | `85f6de48f2f45d8b1bcf6939f87eaf512416086ff17fe540a650aed8f6df8d00` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticHttpEvent.java` | 32 | `5a59a630b02c8bc1273a27e81d4dc8310eb74d85c91397b02869af511b2c86df` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticRedactor.java` | 99 | `6751653320ea8d21f94765c86e1196ac34cc17b690b71c4dffd4a62e7d62cb69` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticReference.java` | 33 | `b6eda024f9478f40990dec481ea19f8dab4eca35b55f337f6469e2ad87f4bde8` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticTransportMetrics.java` | 56 | `f444a2538537b5e88e1122b7e701bece84b4cf5539b9546838a510d8959e8e59` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/Diagnostics.java` | 1932 | `74ca1c097c69e7b6b996fba5a85d76825b40b3110a50601ab8eb0a9bb484ca75` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/FocusSearchGuard.java` | 45 | `2c95bf16a4bf43325b10ba54a918c755a6d6a96fc062d9b32602d16efd27aecc` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/PlaybackDiagnosticState.java` | 128 | `6d31eaf89fa2a140062a0b3c936b063259af780689f8f8f1771fd9513c08d196` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/RetentionSnapshot.java` | 41 | `36a9547e92662e8e32a14d5870a6f3b2ed0209fd382a98b07df5baf73d0eb8f8` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/diagnostics/ThumbnailTelemetry.java` | 104 | `ebfaabb06752b20013d892f93fa2b24c8ed92670eb848565cc288c14866ea6cb` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/BundledNoticeReader.java` | 173 | `bcf2e359de43fc99b205724e88fcf1923bb83559ea3638fb7738c4cb5b686185` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/DoubleRingDrawable.java` | 66 | `a76099d67e39271b68cd71016f4cf8c8fc5e0fe025d9e458ca9a7caa3fa59515` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/DoubleRingMotion.java` | 8 | `770041d95d54f08ccf67b2480ea9a4d462c718b150b715353c562339cf95486e` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutActivity.java` | 100 | `fa9c72d85017408e891b2031586f347b694080b89adfc49b4edf5217ece55102` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutEntry.java` | 21 | `0d392158c1ba96b5b1e2afa77599e4da8619114dd9a4b6a30b68eee89ada1d06` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationAboutWorkspace.java` | 868 | `5a816f74e346c7a5ba1393c2f45d78f0bf3463fd3e75d4520f25f956135aa200` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationUiPolicy.java` | 15 | `53fdbd2ebfa6d047d50c7db969eae48efe9ccc73d2f6e299e6709f9db00f7092` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/FoundationVersion.java` | 10 | `8daf3c9acde1e2b6638b22b24903a8b672d94500e57f9f8db200b7d1b52914f4` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/foundation/LicenseDisclosure.java` | 106 | `41e7dbd6bdb71ae7387c727df6779c705eb72e5fd1e27277eecb4ddfa7cdcee5` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/metadata/FixMatchRepository.java` | 226 | `16fe5d1e850761c03c7cc5e2072221969134edb1120a7a495a1b9198c3f3566b` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/metadata/FixMatchSearchJob.java` | 148 | `bca7311c43ebef583c4925833c9780d4abe10b12eff0a2ed1df60532dcfbaed4` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/metadata/ForgivingMatchSearch.java` | 235 | `a0c66661cefeed2c3a9fbbc445dd488d0cce6e968e432d3edad928352bf13329` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/metadata/OmdbRatingsCache.java` | 95 | `a8e6e4e5ccb5de381155c813bfd3df9995400165223a20fb41eab2ad3684da7a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/metadata/OmdbSupplement.java` | 376 | `b92b93ae50f4f6028b64f7868bd42c67a35cff561e448b3368a429c9717d8dde` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/metadata/UnlibraryLookup.java` | 283 | `9d5c0646b780e42f0dd2c39edd59ed17f6f5a248155b51df0824ecca4be736fb` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/PreviewProviderIcons.java` | 127 | `b10f245d74b92b524954ee1fddad89d3f2cb00973a1d84944f882964596b8c43` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/PreviewRemoteDetailsActivity.java` | 112 | `7e53cb6219ae0c36d8f9c2d82a388b98a575bc44d2231d2b40aa9d264ebe00e1` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/ProviderCatalogueCache.java` | 90 | `563cf5a9af5403950071e71e5741bc39b85593d2a57a6147a105263e3da2c769` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingActionPresenter.java` | 109 | `76ef4bd99d8be42e5a8e1bfc7f578deb5ea58164bff6cdec513972990b80b248` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingActions.java` | 382 | `16ec2e6b4a2dfdc2473f85330a987e0042bd6bcc26e50545df90da2d8bd8b8af` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingPreferences.java` | 196 | `385aaac3b9ff59e28dddb6ac9e29b2afca381a13b5e6878d944f41bfe2687977` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/StreamingRepository.java` | 956 | `b43217f16f818a391bc302d026ee4eaf2167e17488756ee3b4f1c46dcd3d891d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAccountController.java` | 699 | `a9a9ec5eb68cc7ff0d9e8c9376201ac53f7ca59bec94afc7d684a5648164aa0a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAssociationStore.java` | 384 | `ee5c9db3b6195d9dd815f8cece0d5cbc2595757330d7f48e3bbd74f40ad9d7a3` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAuthFlow.java` | 187 | `87b4c5e15494412455e96ce1c762320203fcb1f6f7f68da044cb957b409528d2` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserActivity.java` | 21 | `cfbfc6f75b0c7be3ca3f79a1c6ec4c42086a6b88d0c3164bc9da6ba6512db405` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserController.java` | 358 | `ec7f46cafba05c6a623e0f93ecbc0ec084736df9d442d16d7db1af574791cbf5` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioCodeSignIn.java` | 184 | `abf3346ce222b82601d910b6f29afb28be0b66c6c1f5a6aa2c3e55294584f3c0` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioLibraryBridge.java` | 160 | `4f6bb481e575ccc2d5ccc0a0a1ccc02ca085157dd549f1dd2abea8b7aa221eff` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioOAuthClient.java` | 147 | `0f5901e025852189b9b42d0cc98b92d8b23f524e5f849c69486d712a6c9dc64d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReadClient.java` | 442 | `51c4ab4eae962250fcf6260dcff75603c1488a1a271b3e20c3f09fe202812ef0` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReassignment.java` | 242 | `11328c6dd878c3dc9d204d6b83a7f36cb13c247f47855697809f067e3d618495` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioReconciliation.java` | 131 | `6f45c99ff222ce56afae37ece1036460cf327d2384fc003eaecb359046fb05c0` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioRootLocation.java` | 47 | `b219f9a7cb31a0a8d2a4e3ee96e759420e4cba08e341596cece42c447a569652` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioServiceData.java` | 69 | `b297c7966fdaeaa34aeab8896f32e7302cc996409d6ee82ce542143d28905966` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioServiceStatus.java` | 35 | `e2b89c0208d67dd767ac66bb0cfdde0bfa4e5fd7ebf02df6b98721c396fb93c6` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSnapshotReader.java` | 115 | `4599559ba1ef8fcdb4039447819960d6061689e2c59fa3dd3b0849a686c09281` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSync.java` | 181 | `bcbb6543a001e1042d3b0d5c635a18dd6b7f59c3f36ea777db3c0c2733316ce1` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioSyncScheduler.java` | 300 | `1c96d0c96c047a085fee3900e43b5c81ef98c0cd8e08b4d5549d19b2cae8f147` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/streaming/putio/PutioTokenStore.java` | 156 | `8095fc6b1637408ca8c677330c458879be19819d989a5ab8f3a112a7446a530d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/ActiveOperationMonitor.java` | 68 | `b0b4825e74272e1d050a33f9147118e005b5faf6c8999540c33ce8fb2b173914` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/AdditionalServiceSingleton.java` | 148 | `e668c80bb0c12467e5ebed6e51c935e0aa73519a3239417e303e04601afbc6db` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/BackupFormat.java` | 58 | `cde8e1e1de3d2ddeefd64b81cc652b78c6d1beedfc94222e0c671bb10741f6bf` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/BackupPrivacy.java` | 325 | `99d29b97f4dffeb5912009b036fea5d50420c90d8ea50d43d3f80813113d2e01` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/BackupReview.java` | 82 | `97d9fff9686ca26808fff90cb3d0a673e1568b13c779a7bacccfa382714e9859` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/CodecDiscovery.java` | 632 | `4692e74d3e9954138a431cf2b4e0a381741af229d4d904a0670271610040c51e` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/DbUtils.java` | 438 | `77c2e54dbbdcc13d3d61b255cf68b84e688144557054c4a3538c3eea833ad393` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/DebugDbExportDialogFragment.java` | 159 | `36b5bf8e9c756ca7b305d2dce1befd50f5805785f484dc64f51eb4aafd08b467` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/DelayedBackgroundLoader.java` | 135 | `d6422df2c0322ef181f0679604fb29091a89797b2a75476488c7261a1b8f4807` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/DirectEpisodeLookup.java` | 75 | `c89033eaa0905e31ceae98d4c4a97c7ebe08745a3940f8940ce1137bbffdd047` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/DirectMovieLookup.java` | 31 | `c0636b1d90729c9f9961696a19d63ca24bfd4f60ce04dae2b902506d70e99cd9` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/DirectShowLookup.java` | 94 | `37f677a977ef01a8243b02f8a86a1a5e0eca427d7f4cdbe8d99d214213afaeeb` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/ExternalPlayerResultListener.java` | 237 | `64b7089d2a1151ac23490128b6aa8c2a206ace08b9f9e08c0155149ae3e81248` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/ExternalPlayerWithResultStarter.java` | 28 | `580d76ff6cc94f13bbbba274b2c6062fb002c3b7d5bdf9f958d8c76f981aad28` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/FolderPicker.java` | 632 | `1d5b491c53114d67c74728959226abdcf194cebfc97ee5864d884add738baee2` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/FontNameParser.java` | 115 | `0e926404fd8b065e7ddc913158138a3a51fd4359f4910913742838ae62a01d7d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/FontsFolderSafPreference.java` | 112 | `488d21c82e8447b0204286746c58b60e6883d2c1c4c415f6f51902db02d6a7e7` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/LibrarySortPreferences.java` | 58 | `2c17bc341bc80b46b1f1fb153da1a20a5a73b30b9a65baa28d78e838bfa4e2e0` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/LocaleConfigParser.java` | 61 | `6eea74bd68eecf33d4a81812634dd55568a5f3d96cf8708307babe7551227c84` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/MediaLibraryBackupService.java` | 506 | `3a8899064bc41046aeb7b252e29faec13a4a613a692cea1072d3a38b29a7f50a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/MigrationBackup.java` | 196 | `0dd7b8b3e8de0eaf357e766f32d00caecbb21869b406d703819a4d4d4e8ceece` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/MiscUtils.java` | 709 | `defe60a86b364854aca1951af5a97cb4d0ed4e3541c4064bb2838f5fc715008c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/NetworkBrowserPolicy.java` | 7 | `3674506ce56bb1b1eb97a5d13983b263845774355eb73dbb42f34e22acb8fb0c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/NovaWebView.java` | 73 | `b63ac142e76c450aa496a6f6a7b305b045bd2bab14b1a856477d6e81bcc8510a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OnlineId.java` | 46 | `017e57d295e1aae0554c08533d6585779dee19378ab494ccec2daf33425273bd` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OnlineIdUtils.java` | 50 | `6b94f881b9ad0bd58034d6201d838eaa142df47165ce73b439949b22d2d383bf` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesApiHelper.java` | 635 | `bc66d89ff2b2b7e8ab6e2fe380aca6095446c56a768edd3966bae2aafff58a16` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesCredentialsDialog.java` | 157 | `2aca2d8a6e1ccca49e57e25c69f73a57924e1d4cfefafd22f983ec1087fe6a7f` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesHasher.java` | 165 | `3e2e95324d2144c59b2d71e8d5e7432b2ab4779f11a12ebe7c6876b4f698f0ce` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesQueryParams.java` | 61 | `b8a31369cb2db65291dbe8609ec53a2b43ebefe3ab3cf9f0940fb0d134701d84` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/OpenSubtitlesSearchResult.java` | 81 | `698aacd3b192aed76da7e47fbf21187dcbd5aeb5b82e1aea10b2c4c5d2ebef02` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/PlayUtils.java` | 528 | `c4ad02682c2816a4d17e2443fec8d8bfd12a60e25eb327d49e5b5e9b2b72954c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/PreviewScanReceiver.java` | 36 | `941208cb2623caef0bb668f1a61cb9d46e96f5d4649d9fca8f73465d8407ba55` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/PrivateModeUIHelper.java` | 108 | `cf0325c6cc893569d517032e24b00e99a802f330d4e8d00159ec9b6e3b72733b` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/RestoreJournal.java` | 70 | `a51698cd28efb0a7fbe43b2d8d46aba1c52dcc27c160deacccbe31a55b255b40` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java` | 423 | `c21f10a34e45b8ac1ba9cf93bf86bf25121f968a13a9f5533b8c57f9c79cd5c1` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/ScraperResultsAdapter.java` | 361 | `fe97e6e916f02d6be8c116ade1810fdf9be15624fbd0ed147898523d85cc4fc0` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SettingsBackup.java` | 123 | `9a88cd1968283dba4740fa86f0c09de749cc2d0db334edde696a4733c5a4f02d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SortOrder.java` | 61 | `e12ce0f0d6badd290d391b29e33fbd49c17e8246ecdf886960276ddb3e9def4f` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SortUtils.java` | 151 | `5e5580b1b008bfaa735a7d07e3f89e05f5ffc8b4f1c41076d1652bfbb1d1384b` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/StoreRatingDialogBuilder.java` | 75 | `0d2df386a21c9b33d244083436806ccbd5c14796f60d0bd47fd599c61937ddac` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscovery.java` | 446 | `fe4d459b89ac047a345dcaee84ec0806e62f509183839d039f5c4ac42c81dad9` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleDiscoveryPolicy.java` | 113 | `54364e86ad81226c75114636447b668d5befcba77a0cc1a37c7ba9dedd4bea25` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleFontsFolderSync.java` | 345 | `2968345ef7066138520f0fda39117e005343621a4e7f7cd6794a8b00bd028a36` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleReplacement.java` | 26 | `ecff0d2f834146384fbc72da1df265e3a86bf4318cb99de785d442352df4638a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitleTransfer.java` | 31 | `6abc0941023a88b95b45bf288aa0afe4489b3b53769e7537f96732f859d7edd2` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java` | 1090 | `84d5bfe754bd0e2e01ff828163fcae410be02dedc7b6499235187bbea6f4b519` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardActivity.java` | 0 | — | removed; excluded |
| `src/main/java/com/archos/mediacenter/video/utils/SubtitlesWizardCommon.java` | 499 | `080692bffe105ea3c0d42bcf71ce35655395e39b8f4d3dd5ea6be667f4a227bd` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/ThemeManager.java` | 427 | `7bc90eb6f5df951f2b7896c327928c9bbf5d8ea13ac0b6dee047b42834a4ccc0` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/TrailerServiceIconFactory.java` | 30 | `a50414bf1014efa3364d308f965ed959949079cebec49997c005fc7d909ea597` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/TraktDeviceAuthActivity.java` | 527 | `44f78e9609d9bdccd9ce5fc931e5305f3a00890964dc860d1b746f67277c7488` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/TraktSigninDialogPreference.java` | 145 | `571c75d59bd9554ad48dc16365b275f9a80e61c8493e3bc929a9f23e0b829efc` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/TraktWipeDialogPreference.java` | 40 | `f812bf719a4cfb2344665a67727601451da0e4a7bdef1fa0f5668b2c7629148d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/TrustingOkHttp3Downloader.java` | 121 | `4db3216d37d1e741f3e7029c30412f7fe80dd153cf5b5d797b13e760a6ff4435` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/VideoCodecSelection.java` | 32 | `91ca52426ee65c28af27cf8e2d786714c404a91e8291227a047956952e9b317b` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/VideoMetadata.java` | 447 | `ff56fe0f5f3b9b961addc6074f8b5a66d6f0b65acb0af8c8cc2db30102f25482` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesActivity.java` | 0 | — | removed; excluded |
| `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java` | 2455 | `82afd8d63c72edb80470373c82a85a1435c8ddd6f41ce4f2d450a842e762d61c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesLicencesActivity.java` | 0 | — | removed; excluded |
| `src/main/java/com/archos/mediacenter/video/utils/VideoUtils.java` | 418 | `4ba3f88a5c946c4a9f0220ec5311303014994d9e8f9785a90e9320a1f0f6d8fa` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/WebUtils.java` | 77 | `aaff49e2811555b94c9224aa71ca300ebbf645bd0af3cc8df5707522243012a5` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/WebViewActivity.java` | 137 | `d77dc668a27a622a95930efc844182d53b392d16941e53134d73f154cfa37721` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupCategory.java` | 119 | `ba5014942e096dac211d74e33dde6923341e15131b020f2bdeec6ab6857cd5de` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupDiagnostics.java` | 61 | `f6678b939d97bc080a1d9ea6c47c946d6dfc8e4e149b377014fe07046496e54c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupImportPreparation.java` | 191 | `4e5f7788ed8b8aa0a52374877c15329095cfc1cd3b9c023b029857cb3a9fb193` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupPresets.java` | 114 | `d5b51bd8e0542eb700665f6841197eb9b0a8329618280adcb65fc05fba97f5d9` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BackupSelection.java` | 38 | `4d2ace3b71395a06870835e35a305eadf140da45e5bd34bcfda19e84f2dbad10` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/BookmarkStoreRecovery.java` | 260 | `4b3c37352314fee449b7186232a0d7f468be7ee4e7c64244e63a2f8b26970e57` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalMatchExecutor.java` | 53 | `b4403b39e204432bebadc3d0c3ff4c3dca93471cc3c8ee757c6684effe421e77` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalMatchMaterial.java` | 360 | `9909b1385d54f14193025efc1272e85db4c738defe80c8f5534fc3882955e018` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/CanonicalNativeRecovery.java` | 192 | `eaedb63c2a5f71b716a6b94666d063306f79bdfde925ee076f706d64e7dccf09` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/HiddenItemReplacement.java` | 0 | — | removed; excluded |
| `src/main/java/com/archos/mediacenter/video/utils/backup/IndexedSourceStoreRecovery.java` | 303 | `63c535f3fa59ea3bbd1c3991ae0b62d313d23f1cb204e7a19d96c16517234e09` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupArchive.java` | 447 | `4ebd2e7d2a77232d2cb8d8e8964fd63d414b054596dbe27b91967e9ab170a192` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupCatalog.java` | 181 | `d47276482037a14f2889c90397dc52b0e14999a8aef6c539b424bb1c7e2c25d5` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupExporter.java` | 232 | `62a53578297b7918f2883f573aef7594757c6e42324cb454b2cd9fda417c34e5` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/LogicalBackupImport.java` | 200 | `e0452b609df4e08dd1e02349fcd320d6d36cd8a57c1cb39e79d88054a7fc228a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchIntents.java` | 238 | `6092c76254549fc49a1869f639cba959118ed74877c52dba45c34d6cc5bc4ccc` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchInventory.java` | 197 | `86e4412a84b186fd457ef05d8ca4ebcabae7c74dc7e6a5cd593ec38d31f01db1` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchManualChanges.java` | 41 | `cdcf63bae357839f29aff15c2c11b427d0e3df993c548f32e0654c1757e05e33` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaMatchReconciler.java` | 32 | `6a0117b20cfda7cb8760eadbbfb4f9f0fa78029480a20b555febf83429336393` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/MediaStateRecovery.java` | 477 | `7ba52d4373e1bdadbdcced71e25779abc4888d83a6cc5f69243d9196c0188ea7` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PlaylistStateRecovery.java` | 345 | `afee116c617ba8d51cb1818d05ed6567f1c2b4a54839ce9cda7d0c806e06b434` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableBookmarks.java` | 181 | `842995a72bab6bad8b1eb59f2b86836472bd4c888548bbc8e99230e067974458` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableCustomPage.java` | 108 | `fffbb61b99671d735e6a59bea166112aaf1692e4d3a7b712884ab3a4e0208c25` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableHiddenItems.java` | 186 | `9ff148180d932c364f305bbf6a402f8c396ce61baf1aea9a92d9f1adc04c72d8` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableIndexedSources.java` | 220 | `dfb2042d2bc85a01a7499bfe89ca39ba1c77bb4aa8212e1aa42940d617036e7a` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableLibrarySources.java` | 189 | `1ed885f76f3b2a58e8233fa4b6e3a07fadaf81c6a6914d6961c79641d8e2512b` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableLocations.java` | 131 | `ea8bb711c6c920c8b309b5246784a4f8e23ec6c142cdece65b9beb8c8cf2e1ea` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableMediaMatches.java` | 272 | `d0fa2a337059b68057ee2b01d46b773a9ffa800a065b41dbec731983f3878af1` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortablePhysicalReferences.java` | 47 | `f4ed3de5bb37d71f8795ec6ad605054c262e4b41bf55783d2e670355ba45a64c` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortablePinnedTitles.java` | 283 | `5114a1629ba31cda113f01ac8341c1a980c79b5a14ec3be3488a342fef56406d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableSavedPlaylists.java` | 232 | `fc6a1cdcd49b4c9a15c91faaf2c1b71a8964839c420694a7d6e857282db9f573` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableSettingsRegistry.java` | 289 | `48f0e65ef2bfa9b6e2b1ca5b847c9d080ace4a008a48775815ea15df5f351f11` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableTitlePlayback.java` | 233 | `b4b43228053e0f3c984ca8bd9a5b14d6844b3e1b66aa05f6bcae7f74e48e3c5f` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PortableTitleReferences.java` | 42 | `aef3d59c0ed9aa8889f0c7b9fc414849a9161941beb4aa88f8ea58abd010d1be` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/PreferenceRestoreTransaction.java` | 1818 | `3def7ee95e118e9e2e6d32cd6067411e58663a98888dfb04bb763eccb2ab2ba2` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackCompatibility.java` | 55 | `97f04d6b49626f17bf3c5f2d9224891f0cd108498364c50242b8d59bb638569d` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackInventory.java` | 113 | `b9c9cabeb0f7b153665b63ecbcda98a978b018d576a4ac9ac6dee5aac0fbd6b8` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackOverrides.java` | 323 | `4ec6a023e07c52addba3260643c602b10076746936d3d7a1349d306e66f407db` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/TitlePlaybackSession.java` | 116 | `e1a495b0f83cbcc0c1edd87a9a2d2e63a9fd96001cb615889d068eda3abd69d8` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/backup/VerifiedBackupPublication.java` | 245 | `0ef3fcc2c25652cdee251b97ea55c31f7aeda1f287b276ef27e8a5e52d35faca` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsEditorDialog.java` | 108 | `14ef2259993f4ec19c7db51d8bd4a94e620be81bf0a9a1ffb0280e3521d5b368` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerAdapter.java` | 99 | `95b42efac570dae1441843b7bc0c34cc3784db95eb8367113de91c327dfa58fa` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerPreferenceActivity.java` | 52 | `41ad79af7296b5aaa33d8aeb0738f613a1ae189be57896953ca9a4fc09982a21` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/credentialsmanager/CredentialsManagerPreferencesFragment.java` | 107 | `8ca8a86f4c3bf92eed8e8b6246c31897409289ce0e63a8061c5a7b66041d84d1` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthCallback.java` | 29 | `3e3cda50be2e124559bf20063053a4d554170f759156507034213e7434b89551` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthData.java` | 41 | `a9b40dc9c9b315a5d7f742b9fdf10b172c802125dfc46279ca11e063ed0d6cde` | full read + changed hunks |
| `src/main/java/com/archos/mediacenter/video/utils/oauth/OAuthDialog.java` | 382 | `42ab4516314ea54bd284e494f79d64ad0874beae7bbc46db2259d4befd6ae1e2` | full read + changed hunks |

Later reviewed lint-only hunk removes the impossible pre-23 token-store guard; its envelope tests expand to
SDK 23/28/30 without changing assertions. SubtitleFileHash157Test adds test-only coverage of unchanged hashing
(overlapping chunks, ignored middle and sparse >4GiB tail); no utility source change was bundled with those tests.
