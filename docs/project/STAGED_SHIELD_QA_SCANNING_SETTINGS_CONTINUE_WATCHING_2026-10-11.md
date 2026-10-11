# Staged Shield QA: indexing, Settings crash, Continue Watching refresh

**Recorded:** 2026-10-11
**Status:** User-reported findings for next-version Codex handover; documentation only, not authorisation to implement.

## Severity and reproduction

### P0/P1 — Settings navigation crash
On physical NVIDIA Shield with latest standalone 0.136 QA, Settings left rail navigates normally through Playback, Video, Audio, Subtitles, Library Metadata, Home, Appearance, Streaming and Network. **Pressing Down once more when Network is highlighted crashes Supernova entirely.** Earlier focus/highlighting defect was reportedly marked fixed; do not assume identical root cause. Capture Android logcat and crash stack, identify actual next destination and lifecycle/focus exception. Fix and test every Settings row and both directional navigation and focus restoration. Do not claim fixed without physical Shield validation.

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

## QA / handover guardrails
- Prioritise crash and functional indexing/refresh defects ahead of cosmetic polishing; avoid guessing root causes or claiming completion from source inspection alone.
- Collect reproduction logs, relevant counters, screenshots and real Shield acceptance results.
- Keep scope focused on interface consistency, navigation reliability and first-run experience, plus existing functional regressions.
- Cross-reference [Hero/Continue Watching/Custom Library QA](STAGED_HERO_CONTINUE_WATCHING_CUSTOM_LIBRARY_QA_2026-10-11.md), [Next Codex handover](STAGED_NEXT_CODEX_HANDOVER_FIRST_RUN_AND_RESPONSIVE_UI_2026-10-11.md), and [Global Responsive Standard](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md).
- User may provide a later scan outcome. Update this record before handover if new evidence arrives.
- Do not touch active Codex branch, start coding, merge or publish without explicit user approval.
