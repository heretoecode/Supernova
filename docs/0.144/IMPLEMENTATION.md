# Supernova 0.144 / 144 — implementation and release gate

Baseline: PR #23 tip `800dcd1e0ea6b6df1682d3a17013317633a6d4d6`; delivered 0.143 application source `7b826443363af00b236e5b6af309046456d719df`. Clean clone; no existing work discarded. Main and prior release branches unchanged. No public release or merge.

Requirements: [approved handover](https://github.com/heretoecode/Supernova/blob/docs/settings-from-scratch-blueprint-2026-10-26/docs/design/NEXT_VERSION_CODEX_HANDOVER_2026-10-28.md), all ten linked NEXT_VERSION specifications, Settings blueprint/mapping, Details QA and work queue. Later instructions remove written reviews despite the earlier October 27 request to add them.

## Traceability

Every item remains **not visually Shield-verified**. Implementation and automated evidence are separate gates.

| Requirement | Source / outcome | Regression evidence / remaining gate |
|---|---|---|
| WebDAV deletion | `filecore-supernova144-webdav.patch`: only DELETE 404 is idempotent success; other failures propagate. Existing app removal follows remote success. | MockWebServer response matrix added to FileCore `WebdavSecurityAndRangeTest`; execution pending. Real server pending. |
| Local removal versus remote delete | `VideoDetailsFragment`: explicit keep-file library removal uses the existing hidden-by-user path; source deletion has a separate permanent-delete confirmation. | Existing source/provider protection retained; real remote deletion pending. |
| Safe reconciliation | `medialib-supernova144-reconciliation.patch`: WebDAV pruning only after complete directory traversal, retaining all records on listing errors, null lists, stops, interruption and depth limits. Other storage retention preserved. | `WebdavTraversalCompletenessTest`; real multiple-file/alternate-version database acceptance pending. |
| Scan Library | Overview header action in `UniversalFileBrowser`, live Scanning label/status, existing pipeline; queued race coalesced in `PreviewLibraryScan`. | Existing scan/network suites plus manual/real-source QA pending. |
| Page focus/artwork | `PreviewPages`: explicit initial library focus, displayed-order first artwork, stable identity restoration, no-art/removed-entry fallback. `PreviewBackdrop`: per-request generation guard. | Custom page first-item/empty-filter regression, library return/navigation suites. Shield rapid switches pending. |
| Global Settings | Shared native/workspace switch rendering, focus-only rows and separators, equal 12dp grid gutters, fullscreen transition override corrected, purpose-specific descriptions and removed picker boilerplate. Keys/mapping unchanged. | `SettingsWorkspaceTest`, Settings navigation/backup suites; all eight categories and large text require fresh visual acceptance. |
| Locale flags | Existing `PreviewLanguageIcon` already uses bundled PNGs for explicit regions and neutral icons for generic languages. Generic English/Arabic cannot legitimately acquire arbitrary country flags. Existing picker binding retained. | Existing locale/asset tests; actual Shield rendering pending. |
| A4 playback settings | Compact dark panel; Audio/Subtitles/Video/More, audio tracks versus sync/enhancements columns, native format choices inline. Supported actions retained. | Playback menu/render tests; **reference comparison blocked**: IMG_4968(1).jpeg and IMG_4967(1).jpeg absent from requirements branch and exact Drive search. Inline adjustment/value polish still requires comparison. |
| HUD / preparation | Raised transport/seek/time group using responsive safe inset; clamped seek tooltip with pointer; neutral official loading background; stale cached art invalidation. | Player/HUD/loading suites, supported resolutions and real playback pending. Original textured-art symptom not reproduced from original recording. |
| Playback age rating | Existing `PlaybackAgeOverlay` already provides region-scoped cached certification, first established output, passive red-accent text, 10-second dwell and fade, unknown suppression. Retained. | Existing `PlaybackAgeOverlayTest` and output tests; physical timing/AV chain pending. |
| Details | Written-review entry removed; existing rating rows retained; metadata explicitly single line/ellipsis, increased title separation. | Details regression/render suites; movie/TV physical geometry acceptance pending. |
| Diagnostic report | Original `Supernova Diagnostics - 2026-10-27 21-48-16-706.zip` inaccessible in repo and exact Drive search. Source-confirmed scan issues addressed above. | **Blocked**: cannot claim re-analysis or reproduction of report-only events without original artifact. |

## Release gates

Target version is 0.144/code 144. Not a completed or delivered release candidate yet. All previous history and certificate pins remain intact. Branch-specific conformance/signing workflows retain native rebuild, runtime hashes, full tests/lint, exact-source/payload parity and protected-key checks. No signing gate may be bypassed to accommodate a local build without rebuilt native libraries.

Environment setup required installing Android SDK and JDK, configuring the existing HTTP proxy and Java CA trust. Local tests exclude sibling NDK tasks; these runs cannot establish native APK conformance. No Shield is attached (`adb devices -l` empty).

Automated results, workflow URLs, source commit, APK SHA-256 and Drive readback must be recorded after actual completion. Do not reuse 0.143's APK link as 0.144 delivery. Do not mark this release complete while the above gates are pending.

## Validation corrections and delivery block (2026-10-28)

- Draft PR: https://github.com/heretoecode/Supernova/pull/24 .
- Tooling suite: 120 passing checks, including tampered historical-receipt rejection.
- Real FileCore DELETE response regression passes for 204/404 versus 401/403/405/423/500/503. Traversal completeness tests pass for empty success, null/error, stopped and depth-limited listing.
- Focused Android run: 89 cases, 88 passed; obsolete narrow-panel render assertion corrected for the two-column structure, with final render rerun required. Source-level initial-focus helper typo corrected after compiler detection.
- Software inspection detected vertically stretched switch thumbs; circular drawable correction and stretched-bounds regression added. Final software captures must replace preliminary captures.
- GitHub signing run 40726725691 rejected before execution: branch is not allowed by the `supernova-foundation-signing` environment deployment branch rules. Owner must allow this branch; no protection was changed or bypassed. No APK signed/uploaded and no 0.144 checksum available.
- Initial unsigned CI 40726695367 stopped at historical identity validation: 0.143 historical record lacked required evidence disposition. Fixed with an exact pinned, signature/payload/native-verified delivery-receipt check; archive availability is not falsely marked verified. Local source-conformance check passes. Native conformance must be rerun on final source.
- Google Drive HTML updated in place with an explicit undelivered candidate/status entry; byte-for-byte readback passed, all 12 prior articles preserved. URL: https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view .

## Final implementation safety refinements

- Excluded folders and `.nomedia` scopes explicitly retain their own descendant records while allowing the rest of a complete WebDAV scan to reconcile. Unknown stops, depth limits, null/failed listings and interrupted traversals remain incomplete. Production-scanner selection tests retain present alternatives, skipped subtrees and path-boundary peers correctly; any listing failure selects no deletions.
- Confirmed DELETE 404 carries an explicit absence result into the app. Missing metadata no longer blocks the authoritative WebDAV DELETE check; already-absent media removes its own library record and skips surviving sidecars. Deletion diagnostics use source categories/error classes, not raw URLs or paths.
- Playback Audio now orders Sync before Enhancements, Night Mode has an inline state switch, and Video formats and other controls own separate columns. Right-column Up navigation searches its own parent; an actual Right→Up key regression was added.
- Final-source conformance/signing now requires at least 1,135 Android cases and all new WebDAV regression classes, in addition to the existing native/runtime/branding/lint/signature/payload gates.
- The owner allowed this branch in the existing signing environment. API readback confirms the exact branch rule; the earlier signing rejection is resolved. Final signed QA is pending.
- Software captures under `software-renders/` were inspected for round On/Off switches, readable playback columns, rendered explicit-region PNG flags and inline formats. They are synthetic native renders, not Shield captures or A4 reference comparisons.
