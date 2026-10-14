# Reproduce the staged verification

These are Android/Robolectric and unit checks, not Shield acceptance. Use the exact source SHA in each JSON receipt and an isolated detached worktree; preserve any existing checkout changes. App source receipts precede documentation-only commits. Foundation build preparation changes the package/provider authorities in the build worktree only.

Requirements: JDK21 with javac, Android platform37.0/build-tools36.0.0, Gradle wrapper9.5.0, the repository's pinned sibling modules and existing patch stack. Do not disable TLS validation. A managed proxy may require its configured JVM proxy properties and CA trust store; these are environment-specific, not source settings.

Sibling sources: MediaLib5758074049bc153ac4967b4ecd21886469270076; FileCoreLibraryc4b760c55102c72d45f68b0d16999fc1f4f013a7. Apply the existing0.138 patch order from `.github/workflows/supernova-0138-qa.yml`, then run `.github/build/prepare-foundation.py <isolated-parent>` for Foundation identity. No signing workflow, key, namespace or application branding is changed by these tests.

Base command in isolated Video worktree:

```sh
OPERATION=verify ./gradlew testNoamazonReleaseUnitTest -PfoundationVerify -PsupernovaQa -Puniversal --no-daemon --no-configuration-cache --max-workers=4 -x :FileCoreLibrary:ndkBuild -x :MediaLib:ndkBuild
```

Add `--tests '*ClassName'` for each class below for a staged run. Baseline receipt is the full release unit suite with no class filter:564 tests/142 classes, no failures/errors/skips. Excluding native build tasks means native decoder/network behavior is not exercised.

| Stage | Exact selected classes from executed logs | Receipt |
|---|---|---|
| Customise Home | CustomLibraryPageTest, NextQaUiTest, Preview414Test, Preview41RowsTest, PreviewDialogStackTest, PreviewHomeMaximumTest, PreviewHomeVisualTest, PreviewMatchMembershipTest, Supernova139HomeEditorTest | CUSTOMISE_HOME_VERIFICATION.json |
| Hero | FoundationHomeRegressionTest, HeroPlaybackOutputTest, HeroRotationPolicyTest, PlaybackResumePolicyTest, PreviewFeaturedIndicatorsTest, PreviewPagesTest, Supernova138HomeTest, Supernova138PlaybackTest | HERO_STAGE_VERIFICATION.json (contains the recorded failure); later receipts document subsequent subchecks |
| Library and independent Details | NextQaUiTest, Preview414Test, Preview417NavigationTest, PreviewDetailsCacheTest, PreviewDetailsFactsTest, PreviewEpisodeChoiceTest, PreviewLibraryCompositionTest, PreviewLibraryReturnTest, PreviewSeasonTest, Supernova138HomeTest, Supernova139LibraryNavigationTest, TopNavigationTest | LIBRARY_DETAILS_STAGE_VERIFICATION.json |
| Settings/archive safety | LogicalBackupArchiveTest, PortableSettingsRegistryTest, VerifiedBackupTest | LOGICAL_ARCHIVE_VERIFICATION.json |
| Legal/torrent app regressions | BundledNoticePagerTest, ExternalResumeIntentTest, FoundationAboutActivityTest, FoundationAboutTest, HeroPlaybackOutputTest, PlaybackCompatibilityTest, PlaybackResumePolicyTest, PreviewPlaybackLoadingTest, Supernova139LibraryNavigationTest | RETIREMENT_LEGAL_VERIFICATION.json |
| Home portable intent | FoundationAboutActivityTest, FoundationAboutTest, HomeIntentCodecTest, LogicalBackupArchiveTest, PortableSettingsRegistryTest, PreviewLibraryReturnTest, Supernova139LibraryNavigationTest | HOME_INTENT_VERIFICATION.json |
| Provider/flag source fidelity | BundledNoticePagerTest, FoundationAboutTest, PreviewLanguageIconTest, Supernova139LanguageFlagsTest, Supernova139ProviderMarksTest | PROVIDER_FLAG_VERIFICATION.json |
| Backup standalone right workspace | BackupWorkspaceTest | BACKUP_WORKSPACE_VERIFICATION.json |
| Preference recovery | HomeIntentCodecTest, LogicalBackupArchiveTest, PortableCustomPageTest, PortableSettingsRegistryTest, PreferenceRestoreTransactionTest | PREFERENCE_JOURNAL_VERIFICATION.json |
| Category-aware archive | HomeIntentCodecTest, LogicalBackupArchiveTest, PortableCustomPageTest, PreferenceRestoreTransactionTest | CATEGORY_ARCHIVE_VERIFICATION.json |
| Independent queue/dismissal intent | HomeIntentCodecTest, LogicalBackupArchiveTest, PortableViewingChoicesTest, PreferenceRestoreTransactionTest, Preview41RowsTest, PreviewHomeMaximumTest | VIEWING_CHOICES_VERIFICATION.json |
| Viewing-state schemas | PortableViewingChoicesTest, PortableViewingStateTest, Supernova138PlaybackTest, ViewingHistoryTest | VIEWING_STATE_SCHEMA_VERIFICATION.json |
| SQLite viewing-state replacement | PortableViewingStateTest, PreferenceRestoreTransactionTest, ViewingHistoryTest | SQLITE_VIEWING_STATE_VERIFICATION.json |
| Verified provider publication | LogicalBackupArchiveTest, PreferenceRestoreTransactionTest, VerifiedBackupPublicationTest | PUBLICATION_VERIFICATION.json |
| Per-title playback schema | PortableTitlePlaybackTest, PortableHiddenItemsTest, PortableReviewNotesTest, PortablePreferredVersionsTest, FailedPlaybackCheckpointTest | TITLE_PLAYBACK_VERIFICATION.json |
| Physical privacy and native hidden replacement | PortablePinnedTitlesTest, PortableHiddenItemsTest, PortableReviewNotesTest, PortablePreferredVersionsTest, PreferenceRestoreTransactionTest, LogicalBackupArchiveTest | PHYSICAL_PRIVACY_VERIFICATION.json / HIDDEN_SQLITE_VERIFICATION.json |
| Pin and Hidden Items primitives | PortablePinnedTitlesTest, PortableHiddenItemsTest, PortableReviewNotesTest, PortablePreferredVersionsTest | PINNED_HIDDEN_VERIFICATION.json |
| Backup privacy/focus retest | BackupWorkspaceTest, HomeIntentCodecTest, PortableViewingChoicesTest, Supernova139HomeEditorTest | BACKUP_PRIVACY_VERIFICATION.json |
| Combined viewing | PortableViewingStateTest, ViewingHistoryTest, BackupCycleTest, CombinedViewingRestoreTest, PreferenceRestoreTransactionTest | COMBINED_VIEWING_VERIFICATION.json |
| Startup / history archive | PortableViewingStateTest, ViewingHistoryTest, BackupCycleTest, CombinedViewingRestoreTest, LogicalBackupArchiveTest, LogicalBackupImportTest, PreferenceRestoreTransactionTest, RestoreJournalTest | STARTUP_VIEWING_VERIFICATION.json |
| Combined media acknowledgement | CombinedMediaRestoreTest, CombinedViewingRestoreTest, PreferenceRestoreTransactionTest, PortablePinnedTitlesTest, PortableHiddenItemsTest, ViewingHistoryTest, LogicalBackupArchiveTest, LogicalBackupImportTest | MEDIA_ARCHIVE_VERIFICATION.json / MEDIA_ACKNOWLEDGEMENT_VERIFICATION.json; failed first run retained |
| Sources / Locations | PortableLibrarySourcesTest, CombinedMediaRestoreTest, CombinedViewingRestoreTest, PreferenceRestoreTransactionTest, LogicalBackupArchiveTest, LogicalBackupImportTest | SOURCES_LOCATIONS_VERIFICATION.json |
| Typed playback / diagnostic storage | DiagnosticStorageTest, PlaybackDiagnosticStateTest, DiagnosticsTest, DiagnosticsRefinementTest, DiagnosticArchiveTest, DiagnosticFlightRecorderTest, HeroPlaybackOutputTest, FailedPlaybackCheckpointTest | TYPED_PLAYBACK_DIAGNOSTICS_VERIFICATION.json (without DiagnosticStorageTest) / DIAGNOSTIC_STORAGE_VERIFICATION.json |
| Official title binding | OfficialTitleBindingTest, OfficialTitleNetworkTest, Preview412Test, HeroPlaybackOutputTest, HeroRotationPolicyTest, PreviewMoviePageTest, Supernova138HomeTest, Supernova139SharedCardMetadataTest | ARTWORK_BINDING_VERIFICATION.json; initial2-class FAIL in ARTWORK_BINDING_FIRST_VERIFICATION.json |
| Full continuation | Unfiltered testNoamazonReleaseUnitTest at1f8897e7 | FULL_CONTINUATION_VERIFICATION.json (732/171); precedes artwork changes |



JUnit XML and HTML: `build/test-results/testNoamazonReleaseUnitTest/` and `build/reports/tests/testNoamazonReleaseUnitTest/`. Native images: `build/reports/preview-ui/`. Tests use public synthetic fixtures and offline provider transport. Viewport and window-focus assertions must be inspected with captures, not replaced with only focus method return values. Android-native fixtures do not establish real WindowManager/compositor blur, Shield held-key dispatch, performance, signed upgrades or live network playback.

BackupWorkspace is staged and not yet attached to production Settings/export/restore. Its information test copy demonstrates the approved flat layout; final production copy and archive instructions must be generated/reviewed against the completed codecs and restore paths. Never interpret a standalone render as a complete Backup43 pass.

Device environment check: no `/dev/kvm`, three available CPUs, and `adb devices` returned no connected device. No physical Shield or decoder verification is implied. Drive searches for Supernova/diagnostic/backup ZIPs and IMG_4925/IMG_4941 found no accessible supporting files; the actual incident and owner photograph checks remain blocked.

The private restore journal covers selected default preferences, health annotations, identity viewing SQLite and media pin/hidden columns. Early application attachment recovers interrupted operations before providers and services initialize. Private journal schemas1–5 remain readable; the durable commit decision precedes the media SQLite commit, and its subsequent acknowledgement avoids replaying over later manual changes during cleanup. Legacy-media watched/resume columns, broader media participants and actual force-stop recovery remain open. Twenty-two archive codecs are registered; Media matches, Saved playlists, File bookmarks and Title playback are not registered yet. Production export and Settings integration remain unfinished. Sources reference optional Locations separately and keep missing references pending; they do not restore access grants or onboarding state. Provider publication currently has synthetic stream tests and a SAF adapter, with no connected picker/progress/retention lifecycle. Do not invoke the legacy whole-library writer as the new logical exporter.

Additional source receipts: GENRE_COUNT_VERIFICATION.json, IMPORT_LEGACY_VERIFICATION.json, PREFERRED_REVIEW_VERIFICATION.json, MULTISTORE_JOURNAL_VERIFICATION.json, RECEIVING_DENSITY_VERIFICATION.json and CANCELLED_PLAYBACK_VERIFICATION.json include the exact executed class names. FULL_STAGED_FIRST_VERIFICATION.json records635 tests/160 classes and two obsolete visual assertions; both targeted retests pass. FULL_STAGED_RETEST_VERIFICATION.json records the fresh unfiltered suite at7042a3c:656 tests/163 classes, zero failures/errors/skips. BACKUP_DENSITY_VERIFICATION.json is the1256×936px right-area/xhdpi fixture, not a full production Settings screenshot.

ARTWORK_PROVIDER_SNAPSHOT_VERIFICATION.json selects OfficialTitleBindingTest and OfficialTitleNetworkTest at24d951f4,7 tests/2classes; exact generated native240×80 PNG names/hashes are in the receipt. These are synthetic pixel-binding fixtures, not approved production screenshots.

This continuation: SOURCE_EXPORT_VERIFICATION.json (30/4), BOOKMARKS_VERIFICATION.json (90/8), OPERATION_MODEL_VERIFICATION.json (20/3), SETTINGS_INTEGRATION_VERIFICATION.json (24/5 subset of combined run), PRODUCTION_BACKUP_VERIFICATION.json (24/6). Use exact class_names and source_sha from each receipt, same Gradle command/flags above; native builds remain excluded. Counts overlap; do not sum them as unique tests. Earlier failure receipts retain each source/log hash. Private recovery schemas1–6 remain supported, including separate media and bookmark commit acknowledgements.

14 October playlist reproduction: use the exact source_sha/class_names in PLAYLISTS_STATE_VERIFICATION.json, PLAYLISTS_MEDIA_VERIFICATION.json and PLAYLISTS_JOURNAL_VERIFICATION.json. AFTER the0.138 MediaLib patch chain, apply `.github/build/medialib-supernova139-playlists.patch`; its native query and Trakt sync tests require local-only status4 (3 remains renamed). Isolated dependency SHA8424b8af3f54f498d8e1c1cac322487e15b9ab54 and baseline hashes are recorded in PLAYLISTS_DEPENDENCY_BASELINE.json. Original shared dirty dependencies must remain untouched. Current count is24 registered codecs; prior22/23 references are historical staged states. Connected Settings/SAF/controller evidence is in SETTINGS_INTEGRATION_VERIFICATION.json, OPERATION_MODEL_VERIFICATION.json and PRODUCTION_BACKUP_VERIFICATION.json; full provider/device acceptance remains open.
