# Codex brief — everything left for 0.156 (5 Nov 2026)

One brief for all remaining Codex work on 0.156. Work through every section in order, then hand back once.

**Rules** (unchanged from `BRIEF_0156_CODE.md`): your branch `codex/0155-code-2026-11-04` only; merge
`claude/supernova-github-access-yfhe89` first and again before you hand back; no PR, no merge into Claude's branch,
no workflow dispatch, no `.github/` workflow or Gradle edits (new `.github/build/*.patch` files are fine), nothing in
`docs/codex/HANDS_OFF.txt`. One statement per line, lines ≤ 120, `python3 tools/test_code_layout.py` passes. Every
change has a test; never weaken, skip or delete a test to make it pass. For each failure say whether the code or the
test was wrong.

## 1. Compile before you hand back (new rule)

Your last hand-back did not compile, which cost three builds:
- `medialib-supernova156-screen-scan.patch` adds `PreviewLocalImportTrace.source(path);` in two places in
  `VideoStoreImportImpl` where no `path` exists (around new lines 863 and 2179). Claude added
  `.github/build/medialib-supernova156-screen-scan-compile-fix.patch` (registered after `screen-nfo-cancel`) that
  removes both lines. **Replace that with a proper fix:** put the trace call where the source is known (after
  `String path = …` in the first place; use the storage path you mean in the second), as a new patch registered
  in place of Claude's fix-up patch. Say in the report which patch to unregister.
- `PutioReadCancellationTest` needed `com.squareup.okhttp3:mockwebserver` in the app's test dependencies; Claude
  added it to `build.gradle`. Tell Claude in the report if any new test needs another dependency.
- From now on, before handing back, compile every changed Java file of the app and of each dependency module with
  `javac` against the real classpath you can assemble (the pinned dependency sources, Android stubs, the libraries
  in the Gradle cache). If something cannot be compiled locally, list it by name in the report rather than assuming.

## 2. Failing tests in your areas

- `docs/codex/BRIEF_0156_TEST_FIXES.md`, **third section** (after conformance run 42095803889): the eleven
  `MediaMatchJournalTest` / `CombinedCurrentRestoreTest` failures (`java.io.IOException`), new since the
  `MediaMatchInventory` raw-path change.
- **Section 5 of this brief:** Claude adds any further failures in your areas from conformance run 42106873655 (the
  first run with all of today's work) as soon as it finishes. Re-read this file after section 2 and fix those too.

## 3. Check every Codex-owned 0.156 item is really done

Go through `docs/0.156/RELEASE_LIST.md` and `docs/codex/BRIEF_0156_CODE.md` item by item. For each item that is
yours (engine, data, scanning, backup, diagnostics, Put.io, IntroDB, chapters, subtitles engine, PiP engine), state
in the report: done (commit) or not done. Finish anything not done. In particular confirm:
- #1 the failing library source and the stuck network scan; #2 the local import check giving up after 1½–3 min on
  resume; #3 backup "could not be prepared" with the real 61k library; #4 the diagnostics crash; #12 auto-next never
  plays a hidden file; #16 OMDb ratings kept a week; #18a Put.io pause/resume re-sync; #44 diagnostics.
- #45 "DP-01…DP-07, TV-only stages and the rest of `BRIEF_0155_NEXT.md`": list each sub-item with done / not done /
  deliberately left for 0.157 (with the reason). Do not start the 0.157 engine work (newer NOVA engine stack).

## 4. Shield checklist for the owner

Write `docs/0.156/SHIELD_CHECKLIST_ENGINE.md`: the short list of things only the owner's Shield can confirm for your
work in 0.156 (Put.io pause/resume re-sync, seek previews on large 4K Put.io files, PiP OK/reopen, Library Health
Find it again, Test connection for SMB/SFTP/FTPS, .nfo export to a writable share), each as one plain step with
what "good" looks like. Plain British English; no code words. Claude merges it with the screen checklist.

## 5. Failures from conformance run 42106873655

Finished 5 Nov 2026 21:27 UTC: 1,514 app tests, 13 failed, all in your areas; every screen-side test passes.
- The eleven restore tests of section 2 (`MediaMatchJournalTest` ×7, `CombinedCurrentRestoreTest` ×4).
- `PreviewScanLifecycleTest > localQueueBoundariesShareBatchAndObservedTrigger` (new).
- `PreviewScanLifecycleTest > localTelemetryBroadcastFailureDoesNotBreakImport` (new).
The app test task failed, so the MediaLib and FileCore module tests did not report in this run; run your module
suites yourself and fix what fails there too.

## Hand back

One line in `docs/codex/HANDOVER.md` and one report in `docs/codex/reports/` covering sections 1–5, including the exact
patch registration changes (add / replace / unregister) with SHA-256s. Then stop.
