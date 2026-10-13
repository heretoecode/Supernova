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
| Backup privacy/focus retest | BackupWorkspaceTest, HomeIntentCodecTest, PortableViewingChoicesTest, Supernova139HomeEditorTest | BACKUP_PRIVACY_VERIFICATION.json |

JUnit XML and HTML: `build/test-results/testNoamazonReleaseUnitTest/` and `build/reports/tests/testNoamazonReleaseUnitTest/`. Native images: `build/reports/preview-ui/`. Tests use public synthetic fixtures and offline provider transport. Viewport and window-focus assertions must be inspected with captures, not replaced with only focus method return values. Android-native fixtures do not establish real WindowManager/compositor blur, Shield held-key dispatch, performance, signed upgrades or live network playback.

BackupWorkspace is staged and not yet attached to production Settings/export/restore. Its information test copy demonstrates the approved flat layout; final production copy and archive instructions must be generated/reviewed against the completed codecs and restore paths. Never interpret a standalone render as a complete Backup43 pass.

Device environment check: no `/dev/kvm`, three available CPUs, and `adb devices` returned no connected device. No physical Shield or decoder verification is implied. Drive searches for Supernova/diagnostic/backup ZIPs and IMG_4925/IMG_4941 found no accessible supporting files; the actual incident and owner photograph checks remain blocked.

The preference recovery journal is private and covers registered settings, Home, Library-page and queue/dismissal keys. SQLite viewing replacement is separately atomic; combined cross-store recovery and legacy-media watched-state handling remain open. Fourteen archive codecs are registered; other displayed categories are not yet wired to production export. Provider publication currently has synthetic stream tests and a SAF adapter, with no connected picker/progress/retention lifecycle. Do not invoke the legacy whole-library writer as the new logical exporter.
