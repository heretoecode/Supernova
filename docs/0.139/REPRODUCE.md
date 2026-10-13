# Reproduce the staged verification

These are Android/Robolectric and unit checks, not Shield acceptance. Use the exact source SHA in each JSON receipt and an isolated detached worktree; preserve any existing checkout changes. App source receipts precede documentation-only commits. Foundation build preparation changes the package/provider authorities in the build worktree only.

Requirements: JDK21 with javac, Android platform37.0/build-tools36.0.0, Gradle wrapper9.5.0, the repository's pinned sibling modules and existing patch stack. Do not disable TLS validation. A managed proxy may require its configured JVM proxy properties and CA trust store; these are environment-specific, not source settings.

Sibling sources: MediaLib5758074049bc153ac4967b4ecd21886469270076; FileCoreLibraryc4b760c55102c72d45f68b0d16999fc1f4f013a7. Apply the existing0.138 patch order from `.github/workflows/supernova-0138-qa.yml`, then run `.github/build/prepare-foundation.py <isolated-parent>` for Foundation identity. No signing workflow, key, namespace or application branding is changed by these tests.

Base command in isolated Video worktree:

```sh
OPERATION=verify ./gradlew testNoamazonReleaseUnitTest -PfoundationVerify -PsupernovaQa -Puniversal --no-daemon --no-configuration-cache --max-workers=4 -x :FileCoreLibrary:ndkBuild -x :MediaLib:ndkBuild
```

Add `--tests '*ClassName'` for each class below for a staged run. Baseline receipt is the full release unit suite with no class filter:564 tests/142 classes, no failures/errors/skips. Excluding native build tasks means native decoder/network behavior is not exercised.

| Stage | Selected classes | Receipt |
|---|---|---|
| Customise Home | Supernova139HomeEditorTest, Preview41RowsTest, CustomLibraryPageTest, PreviewMatchMembershipTest, PreviewHomeMaximumTest, PreviewHomeVisualTest, PreviewDialogStackTest, NextQaUiTest, Preview414Test, PreviewInput*Test | CUSTOMISE_HOME_VERIFICATION.json |
| Hero | HeroRotationPolicyTest, HeroPlaybackOutputTest, FoundationHomeRegressionTest, Supernova138HomeTest, PreviewPagesTest, PreviewFeatured*Test, SharedMetadataTest, PlaybackResumePolicyTest, Supernova138PlaybackTest | HERO_STAGE_VERIFICATION.json; later passing regression receipt |
| Library and independent Details | Supernova139LibraryNavigationTest, PreviewLibraryReturnTest, PreviewLibraryCompositionTest, Preview417NavigationTest, TopNavigationTest, PreviewEpisodeChoiceTest, PreviewSeasonTest, PreviewDetails*Test, Preview414Test, NextQaUiTest, Supernova138HomeTest | LIBRARY_DETAILS_STAGE_VERIFICATION.json |
| Settings/archive safety | PortableSettingsRegistryTest, LogicalBackupArchiveTest, SafeBackupTest, VerifiedBackupTest, BackupFormatTest | LOGICAL_ARCHIVE_VERIFICATION.json |
| Legal/torrent app regressions | FoundationAboutTest, FoundationAboutActivityTest, BundledNoticePagerTest, Supernova139LibraryNavigationTest, PlaybackCompatibilityTest, ExternalResumeIntentTest, PlaybackResumePolicyTest, HeroPlaybackOutputTest, PreviewPlaybackLoadingTest | RETIREMENT_LEGAL_VERIFICATION.json |
| Home portable intent | HomeIntentCodecTest, Supernova139LibraryNavigationTest, FoundationAboutTest, FoundationAboutActivityTest, PreviewLibraryReturnTest, PortableSettingsRegistryTest, LogicalBackupArchiveTest | HOME_INTENT_VERIFICATION.json |
| Provider/flag source fidelity | Supernova139ProviderMarksTest, Supernova139LanguageFlagsTest, PreviewLanguageIconTest, FoundationAboutTest, BundledNoticePagerTest | PROVIDER_FLAG_VERIFICATION.json |
| Backup standalone right workspace | BackupWorkspaceTest, Supernova139HomeEditorTest, HomeIntentCodecTest, Supernova139ProviderMarksTest | New stage receipt after execution |

JUnit XML and HTML: `build/test-results/testNoamazonReleaseUnitTest/` and `build/reports/tests/testNoamazonReleaseUnitTest/`. Native images: `build/reports/preview-ui/`. Tests use public synthetic fixtures and offline provider transport. Viewport and window-focus assertions must be inspected with captures, not replaced with only focus method return values. Android-native fixtures do not establish real WindowManager/compositor blur, Shield held-key dispatch, performance, signed upgrades or live network playback.

BackupWorkspace is staged and not yet attached to production Settings/export/restore. Its information test copy demonstrates the approved flat layout; final production copy and archive instructions must be generated/reviewed against the completed codecs and restore paths. Never interpret a standalone render as a complete Backup43 pass.
