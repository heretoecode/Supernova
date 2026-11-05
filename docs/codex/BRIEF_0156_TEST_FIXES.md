# Codex brief — 0.156 failing tests (5 Nov 2026)

Rules as `BRIEF_0156_CODE.md` (your branch only, no PR/merge/dispatch, one statement per line, a test for every
change). Your Parts A–D and B17a are merged into `claude/supernova-github-access-yfhe89` (merge `11ed71d44`).
**Start from that branch's current head** (merge it into yours), because Claude fixed the build around your work:
- Your seven MediaLib/FileCore patches are now registered in `.github/build/supernova155-dependency-patches.json`.
- MediaLib tests needed sardine on their test classpath (new `medialib-supernova156-test-sardine.patch`).
- Two missing `java.io` imports (PutioTokenStore, LogicalBackupArchive).
- `DiagnosticFrameSampler` re-posted a frame callback forever, so every Robolectric Activity test hung; it now
  skips sampling under Robolectric.
- `PipPlaybackActions` is marked Android 8+ (lint NewApi).
- `DiagnosticDetailTest` now uses the Robolectric runner (org.json).

Conformance run 42021765313 (on `e34d8afa6`, before the last two fixes) still has these failures in your areas. For
each: find whether the code or the test is wrong, fix it, and say which in your report. Do not weaken a test to make
it pass.

- AutoScrapeServiceNetworkScanTest > completionHandlerStartsForcedScrapeForCleanSuccessfulOwner — java.lang.AssertionError at AutoScrapeServiceNetworkScanTest.java:86
- NetworkScanTransportTest > disconnectTerminatesWithoutSuccessHandoff — java.lang.AssertionError at NetworkScanTransportTest.java:104
- NetworkScanTransportTest > silentRawListingReleasesBatchOnlyAfterCancellationUnwinds — java.lang.AssertionError at NetworkScanTransportTest.java:104
- NetworkScanTransportTest > tricklingRawListingCannotOutliveSourceDeadline — java.lang.AssertionError at NetworkScanTransportTest.java:104
- NetworkScannerServiceVideoTest > uncheckedScanFailureStillCompletesBatch — java.lang.AssertionError at NetworkScannerServiceVideoTest.java:80
- WebdavScanDeadlineTest > actualRawListingTrickleIsCancelled — java.lang.AssertionError at WebdavScanDeadlineTest.java:67
- DiagnosticStorageTest > oversizedProducerRecordIsDroppedRatherThanCreatingAnUnparseableOrOversizedFile — java.lang.AssertionError at DiagnosticStorageTest.java:178
- PlaybackDiagnosticStateTest > productionDiagnosticZipContainsTypedEvidenceAndNoSourceOrExceptionMessageCanaries — java.lang.AssertionError at PlaybackDiagnosticStateTest.java:231
- PreviewScanLifecycleTest > localMetadataQueueReportsActualServiceAcceptance — java.lang.AssertionError at PreviewScanLifecycleTest.java:125
- PreviewScanLifecycleTest > metadataQueueDistinguishesAcceptedNullAndRestrictedStarts — java.lang.AssertionError at PreviewScanLifecycleTest.java:250

**Added after conformance run 42046189841 (on `079847b95`, 5 Nov 2026).** The hang is fixed and the run completes
(36 min). These new failures are also in your areas:

- AudioTrackSelectionPolicyTest (all 14 tests) and TrackSelectionTest > testCsvTrackSelectionSuite — NPE at
  `PlayerService.onAudioMetadataUpdated` line 3078 (`mPreferences` is null when the tests build the service; added
  with the audio_output diagnostics in `9757306f1`).
- FirstPlayHelpRemovalTest > bothControllerLayoutsLackNovaHelpEvenForFreshPreferences — InflateException
  (UnsupportedOperationException in `TypedArray.getDimensionPixelSize`, RelativeLayout params) at line 27: inflating
  the controller layouts outside a themed context.
- ThumbnailDiskCacheTest (3 tests: restartRebuilds…, lruCapEvicts…, manualWatchedCleanup…) — fails in
  `ThumbnailDiskCache.<init>` / `writeIndex` (line 141).
- LargeLogicalBackupTest > actualExporterRetainsEveryMatchAndPreviousBackupForOwnersDefaultSelection — IOException
  from `MediaMatchInventory.validatePhysical` line 164 via `LogicalBackupExporter.preparePrivate`.

Claude fixed its own two (the Skip test, a lint error in `PlayerActivity`) and removed the obsolete Diagnostic Level
test (the level was dropped as approved).

Hand back as before: a line in `docs/codex/HANDOVER.md` and a report in `docs/codex/reports/`.

**Third section — after conformance run 42095803889 (5 Nov 2026, includes your follow-up `8d9a91e0e`).** Only
eleven restore tests remain in your areas, all new since the `MediaMatchInventory` raw-path change, all
`java.io.IOException` (likely the restore journal now rejecting fixtures whose `getFilePath()` differs from their
captured `_data`):
- `MediaMatchJournalTest` (7): coordinatedCanonicalDurableDecision…, coordinatedCanonicalPreDecisionDeaths…,
  coordinatedEmptyMatches…, durableIntentDecision…, earlyStartupCanonicalRecovery…, everyPreDecisionDeath…,
  laterNativeIdentificationInvalidatesPreview… (line 129).
- `CombinedCurrentRestoreTest` (4): canonicalCombinedRestoreRollsBack… (line 140), canonicalDecisionAndEvery…,
  currentCombinedIntentAndSixStore…, decisionAndThreeAcknowledgements….
Same rules: code or test, no weakening.
