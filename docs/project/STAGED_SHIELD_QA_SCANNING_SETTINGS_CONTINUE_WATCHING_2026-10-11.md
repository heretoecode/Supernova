# Staged Shield QA: indexing, Settings crash, Continue Watching refresh

**Recorded:** 2026-10-11
**Status:** User-reported findings for next-version Codex handover; documentation only, not authorisation to implement.

## Severity and reproduction

### P0/P1 — Settings navigation crash
On physical NVIDIA Shield with latest standalone 0.136 QA, Settings left rail navigates normally through Playback, Video, Audio, Subtitles, Library Metadata, Home, Appearance, Streaming and Network. **Pressing Down once more when Network is highlighted crashes Supernova entirely.** Earlier focus/highlighting defect was reportedly marked fixed; do not assume identical root cause. Capture Android logcat and crash stack, identify actual next destination and lifecycle/focus exception. The crash blocks all categories below Network, specifically **Integrations, About and Advanced**, and prevents the user from accessing **Export Diagnostic Report**. Restore all those sections and report export; do not skip/hide categories as a workaround. Fix and test every Settings row and both directional navigation and focus restoration. Do not claim fixed without physical Shield validation.

### P1 — External storage indexing apparently slow or stuck
Two external drives contain media. User inspected folder selection in Network & Files: of five folders on one drive, four correctly show **Add to Library** (excluded) and one correctly shows **Remove from Library** (included); equivalent UI inclusion state appears correct on second drive. Despite long runtime, expected new media has not appeared. Bottom-right **Scanning Local Library** pill remains visible and animated; Network & Files → Overview says **local library is indexing**, including after app restart. User is waiting for outcome; **do not claim scan has stalled or failed without further evidence**.

Investigation:
- Trace confirmed selection/persisted path → storage URI/permissions/mount availability → scan enqueue → traversal → media detection → metadata/index DB writes → row/UI refresh.
- Distinguish legitimately long scan (large disks), queued work, blocked worker, repeat scanning, retries, inaccessible paths, stale progress flag, slow metadata extraction and UI not refreshing.
- Add bounded diagnostics/progress evidence: per-source job state, files/folders visited, indexed/skipped/error counts, last-progress timestamp, clear completion/failure status; avoid leaking private paths in user-facing messages.
- Investigate performance bottlenecks and safe improvements (batching, redundant work, throttling, incremental indexing) without changing media selection or introducing premature auto-scans.
- Preserve confirmed **no scan before explicit folder confirmation** invariant. Do not trigger scanning merely by opening Network & Files or Overview.
- Test multi-drive, excluded vs included folders, restart, large library, transient disconnect, duplicates, scan completion and newly indexed media appearing without restarting app.

### P1 — Continue Watching only appears after restart
User played **Vigil S2E1**; resume progress was recorded and visible on Recently Added card, but Continue Watching row was missing even though Customize Home showed **Show**. Hidden/Show toggle did not restore it. After closing and relaunching app, Continue Watching **appeared correctly**, Vigil was **removed from Recently Added**, and Hidden/Show toggle **worked correctly**. This strongly suggests missing live invalidation/refresh, but root cause is unproven.

Trace playback state persistence → eligibility/query → Home row composition → update notifications/subscriptions → activity resume and row refresh. Ensure return from playback immediately updates eligible Continue Watching content, removes it from Recently Added as existing restart behaviour does, and Show/Hidden toggle behaves consistently without app restart. Do not independently rewrite membership/deduplication until the row's root cause is established. Test cold start, player return, in-app resume, Show/Hidden/Show, multiple media and zero-history first run.

### P2 — Erroneous Continue Watching row on TV Shows library page
User reports an unexpected Continue Watching row **below library information and above toolbar buttons** on the TV Shows page. It should not exist in that location. Investigate erroneous Home-row insertion/shared layout routing; remove that unintended row while preserving Home Continue Watching and approved TV Shows layout. Check Movies and custom library pages for analogous leakage.

## P1 — Maximum diagnostic detail from first launch (development/QA builds)
User confirms the existing selector represents two levels (basic/simple versus more detailed; the precise implementation names require inspection). **Approved decision:** use the **highest supported diagnostic detail level as the single Supernova default**, from the first launch, including fresh installs and cleared app data, without opt-in or Settings navigation. **Remove the diagnostic detail/severity selector completely from the UI**, not merely hide the selected value. Keep **Export Diagnostic Report** available and functional after fixing the Settings crash. User is the sole current tester and does not want a user-facing diagnostic mode selection.

Before changes, inspect the actual existing diagnostic modes, defaults, config persistence and export pipeline; user recalls a term like "salt" but is unsure of its meaning. **Do not confuse cryptographic salt/anonymisation with logging verbosity**: preserve privacy-preserving salting, token/credential redaction, safe handling of filesystem paths and other sensitive data. Maximum diagnostics means maximum safe and useful detail, not secrets or uncontrolled collection. Ensure logging is bounded (retention/rotation/storage), non-blocking, and avoids measurable performance degradation on Shield, especially during scanning. Preserve user-visible export action and verify a fresh-install build produces an informative report without requiring Settings access. This is the approved current Supernova default, not merely a temporary QA toggle. If public distribution is planned later, separately review disclosure and diagnostics policy without reintroducing a selector now.

## P2 — Network & Files “All Files” toggle does nothing
**Reproduction:** On physical Shield, open Network & Files → Local Storage → select an external backup drive. Grid/List View works and Sort works, but activating **All Files** appears to have no effect.

**Approved intended semantics:** This is a **browser visibility toggle**, unrelated to folder inclusion or scanning.
- **All Files ON:** show all files and navigable folders in the selected folder, including unsupported files.
- **All Files OFF:** show supported video files and navigable folders; hide unsupported non-video files. Preserve folder visibility so nested supported content remains reachable.
- Toggle must update the current directory listing immediately, indicate ON/OFF clearly, and preserve current path, stable focus and sensible scroll restoration when the focused item disappears.
- Reuse shared button icons, title casing, background, curvature, padding and focus rules.
- Do not add/remove library sources, trigger scanning, modify files or change sort/grid state when toggling.
- Check supported video detection against actual app-supported formats (not filename assumptions alone); cover extension case, hidden files, folders, subtitles, image/document files and unsupported formats.
- Audit applicable internal/external storage browser surfaces and network locations where the same shared browser is used. Verify no regression in functioning Sort and Grid/List buttons.
- Determine whether the defect is missing click handler, stale filter state, failure to refresh adapter, or an incorrect source-specific query; capture evidence before fixing.

## QA / handover guardrails
- Prioritise crash and functional indexing/refresh defects ahead of cosmetic polishing; avoid guessing root causes or claiming completion from source inspection alone.
- Collect reproduction logs, relevant counters, screenshots and real Shield acceptance results.
- Keep scope focused on interface consistency, navigation reliability and first-run experience, plus existing functional regressions.
- Cross-reference [Hero/Continue Watching/Custom Library QA](STAGED_HERO_CONTINUE_WATCHING_CUSTOM_LIBRARY_QA_2026-10-11.md), [Next Codex handover](STAGED_NEXT_CODEX_HANDOVER_FIRST_RUN_AND_RESPONSIVE_UI_2026-10-11.md), and [Global Responsive Standard](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md).
- User may provide a later scan outcome. Update this record before handover if new evidence arrives.
- Do not touch active Codex branch, start coding, merge or publish without explicit user approval.
