# 0.156 test repairs after conformance 42046189841 — 5 November 2026

Ready at `8d9a91e0e` on `codex/0155-code-2026-11-04`.
The owner-authorised merge of Claude’s fetched head `be1291e11` fast-forwarded cleanly from `ec85a509b`.
That baseline includes the first test-repair hand-back and registration of its two dependency patches.
This report covers the second section of `BRIEF_0156_TEST_FIXES.md`, added after run `42046189841` on `079847b95`.

The new section describes **20 failing test methods**: fourteen audio-policy methods, one CSV suite, one controller
layout check, three thumbnail-cache methods and one large-export method. Every method and all existing policy,
CSV, layout, eviction, ownership, backup-count and previous-backup assertions remain enabled. No CSV case, layout,
production diagnostic event or ownership precondition was removed to make a test pass.

| Failing test | Code or test? | Cause and repair |
|---|---|---|
| `AudioTrackSelectionPolicyTest` — all 14 methods | Test | `buildService(...).get()` deliberately attaches the Service without calling `onCreate()`. Production initialises `mPreferences` in `onCreate()` before registering playback callbacks. The new audio-output diagnostic reads that field, exposing the incomplete selection fixture. Initialise it with the real default SharedPreferences before invoking callbacks; retain the isolated fixture without starting HDMI/bootstrap workers. All fourteen selection specifications and expected outcomes remain unchanged. |
| `TrackSelectionTest.testCsvTrackSelectionSuite` | Test | The same partial-Service preference dependency. Supply real default preferences in setup. The CSV file, row parser, malformed-row rejection and all expected audio/subtitle comparisons are unchanged. |
| `FirstPlayHelpRemovalTest.bothControllerLayoutsLackNovaHelpEvenForFreshPreferences` | Test | The plain Application context lacked the AppCompat `actionBarSize` dimension used by RelativeLayout parameters in the controller layout. Inflate through `ContextThemeWrapper` using the manifest’s actual `ArchosThemeBlueOverlay`. Clear preferences explicitly and assert that the attribute resolves to a dimension before inflating both original layouts. Both overlay-absence assertions and the removed-string assertion remain. No resource or player UI source was edited. |
| `ThumbnailDiskCacheTest.restartRebuildsIndexNearestHonorsInclusiveIntervalAndWatchedDeletesWholeSet` | Test | The class used ordinary JUnit while the production cache serialises Android `org.json`. The Android unit-test stubs cannot supply real JSON objects/strings, causing the reported constructor/index failure. Add the existing Robolectric runner and a plain Application at SDK 30. Retain restart, inclusive interval, whole-set deletion and directory cleanup assertions; additionally parse the generated index and require schema 1 and both frame rows. |
| `ThumbnailDiskCacheTest.lruCapEvictsOldestVideoAndCountsIndexBytesToo` | Test | Same missing Android JSON runtime. With Robolectric, retain the original byte cap, index-byte accounting, oldest-video eviction and cleanup checks. No cache production code or thresholds changed. |
| `ThumbnailDiskCacheTest.manualWatchedCleanupRemovesEveryCachedVersionOfOnlyThatOwner` | Test | Same missing Android JSON runtime. Retain checks that both versions belonging to the watched owner disappear while the other owner’s frame survives, followed by complete cleanup. |
| `LargeLogicalBackupTest.actualExporterRetainsEveryMatchAndPreviousBackupForOwnersDefaultSelection` | Code | `MediaMatchInventory.capture()` hashes the exact database `_data` text. Validation instead hashed `Video.getFileUri()`, which adds `file://` for raw local paths. Thus the mixed local/WebDAV inventory was rejected despite being unchanged. Validate against `Video.getFilePath()`, which preserves `_data`, without changing the guard hash or canonicalising stored locations. The 61,050-row fixture, default category selection, closed verification and previous-file byte comparison remain. Also require unique identities and every expected `tmdb:movie:1`…`tmdb:movie:61050`, rather than checking the count alone. |

The backup repair adds two regressions in `MediaMatchInventoryTest`:

- An unchanged raw `/storage/USB/Raw one.mkv` path must validate against the unfiltered physical snapshot even
  though its playable URI has a `file://` prefix. The inventory signature must remain unchanged.
- Changing stored `_data` from that raw path to its prefixed URI must still invalidate the captured snapshot,
  **even when the playable URIs compare equal**. This protects the original exact stored-location guard.

Both new regressions fail against the pre-fix production class. The first reproduces the brief’s
`MediaMatchInventory.validatePhysical` line-164 IOException. The second exposes acceptance of a changed stored path
because the old validation compared a normalised playback URI. Both pass with the raw-path comparison.
Source audit confirms `VideoCursorMapper` passes `_data` unchanged into Video, `getFilePath()` returns that value,
and `VideoUtils.getFileUriFromMediaLibPath()` adds the local scheme. No guard hash, backup schema, fingerprint,
row cap, cancellation check, replacement rule or private-data selection changed.

| Commit | Change |
|---|---|
| `d3c80520c` | Separate layout-only preparation of five existing files. Tokens/comments are identical after folding adjacent string literals split by the formatter. |
| `fd6dc40b2` | Real preference dependencies for both isolated selection fixtures. |
| `f169c6844` | Manifest-matching themed context, fresh preferences and resolved-dimension guard for first-play layout inflation. |
| `2cbf6e328` | Robolectric JSON runtime for the three cache tests and an explicit index JSON check. |
| `8d9a91e0e` | Stored-path validation fix, both guard regressions and stronger full-inventory export assertions. |

Validation completed without Gradle:

- **All three ThumbnailDiskCacheTest methods pass** in an isolated ECJ Java 17 / Robolectric 4.17 / Android 30
  run using the actual production ThumbnailDiskCache class and Android JSON, with no cache implementation stub.
- **Seven selected MediaMatchInventoryTest methods pass** under Robolectric 4.17 and native SQLite at Android 28,
  including both new regressions and existing read-only, metadata-ownership, changed-preview, signature and Put.io
  exclusion controls. The harness compiles the actual MediaMatchInventory, PortableMediaMatches and
  PortablePhysicalReferences classes. It supplies a minimal database/catalog/Video/API fixture; it does not prove
  the complete provider schema or production VideoCursorMapper integration. The separate intent-export test was
  not selected because that exporter is outside this isolated harness.
- The two new inventory regressions were also executed against `be1291e11`’s pre-fix class and failed for the
  expected opposite guard errors, then passed with the repair.
- All **seven changed Java files** parse as Java 17, have complete imports and lines at most 120 characters.
- `python3 tools/test_code_layout.py` passes both checks; `git diff --check` passes.
- Read-only resource checks confirm that the test theme matches PlayerActivity’s manifest declaration and that
  neither original controller layout contains the legacy overlay.

These are **10 isolated passing component tests**, not a claim of green full conformance. The fourteen audio
policy tests, complete CSV selection suite, real resource inflation, complete inventory/export integration and
both large-backup methods still require Claude’s normal Gradle conformance run. No Gradle command or workflow
was run here. The large fixture still contains 61,050 mixed local/WebDAV owners and the archive-size assertion;
its size was not reduced for local testing.

All changes are app-side Java/test files plus this hand-back, project status and local issue notes.
**No dependency patch registration or UI API wiring is needed for this follow-up.** There are no public API,
preference-key or resource changes. Shield follow-up remains the existing mixed-source backup/export smoke check;
no device result is claimed. Only `codex/0155-code-2026-11-04` was changed/pushed. No PR, subsequent merge, rebase,
force-push, workflow dispatch, signing or publication was performed. GitHub cleanup remains completed.
