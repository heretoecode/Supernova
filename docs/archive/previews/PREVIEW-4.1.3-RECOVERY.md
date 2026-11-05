# Preview 4.1.3 recovery — 20 September 2026

Continuation source: local branch `codex/apk-build-412`, HEAD `0d170b9`.
Source implementation `87dfd84` has the same tree as delivered remote
`53cf382800ca0fbc3261456aaebdf4b99611241d`. No staged, unstaged or untracked
changes were present. Prior APKs, extracted evidence and build diagnostics are
outside the repository. No reset or baseline replacement was performed.

Read all handover text, both visual references, all eight physical stills and
all seven physical video sequences. Content checksums match; the manifest's
self-entry does not (non-blocking self-referential checksum inconsistency).

## Completed and retained

Delivered 4.1.2: modern navigation/browser/settings architecture, compact Search
keyboard, parent-series and diacritic matching, working playback focus graph,
Back semantics, direct tracks, checkpoint safeguards and official artwork.

## Partially completed / explicitly superseded

Focus styling, Details and Information structure, Featured readiness, compact
selectors, source summaries and utility-background composition exist but have
documented physical failures or new 4.1.3 requirements. Modify their real paths,
not an older baseline. The previous underline design is explicitly superseded.

## Not yet implemented

4.1.3 diagnostic logging/export, poster-free hero, single Featured action and
directional motion, new shared glow language, and this pass's corrections/tests.

## Needs verification / potential regressions

Playback Information → File and technical details launches the legacy transparent
Details activity, including metadata retrieval and activity lifecycle changes.
Root cause is under investigation; no exception trace was supplied. Long-duration
playback termination, A/V sync and abnormal-exit progress remain
NOT TESTED — USER QA REQUIRED.

Confirmed summary bug: `Video.getUri()` is the indexed database content URI,
not the media source. Therefore all indexed network files were counted locally.
`getFileUri()` resolves the existing indexed file path without scanning.

All 4.1.3 changes require compilation, targeted checks and new APK identity.
Do not treat 4.1.2 screenshots/tests as 4.1.3 evidence.
