# Codex brief — preparation for 0.155 / 0.156 (owner approved 2026-11-04). Read-only.

**You change no app code in this run.** Every part produces a findings report that the owner and Claude review.
Do the parts in order; stop cleanly when time runs out — each part is useful on its own.

## Setup
1. Start from the tip of `claude/supernova-github-access-yfhe89` (0.154 is signed from it).
2. Create a new branch `codex/0155-prep-YYYY-MM-DD` (add `-2`, … if it exists). Push only to that branch. No pull
   request, merge, rebase or force-push.
3. Write only the files named below and your report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`. Read
   `docs/codex/HANDS_OFF.txt` (do not edit anything in it). No Gradle, builds or tests are needed.

## Part 1 — why the Put.io (network) scan never finishes
**Symptom (owner's Shield, 0.153, 3 Nov 2026):** Network & Files › Network Scanning shows for hours
`Status: Requested · Current source: webdav.put.io/TV · Files checked 0 · Sources completed 0/2 · 5368 seconds ·
Last completed 4 hours ago`, with `Scanning indexed sources · queued`. The local part completes
(`61050 media rows checked · 0 new · 168 seconds`) and restarts every 15 minutes. Pressing **Scan Now** or **Scan
Library** changes nothing. Library Health shows "Confirmed missing: 2" while every category says "No issues".

**Lead (Claude):** `leanback/PreviewLibraryScan.requestNetwork` returns early ("scan_request_coalesced") while
`active()` is true; `active()` is true while `NetworkScannerServiceVideo.isScannerAlive()` or
`AutoScrapeService.getNetworkScanCount() > 0`. If either never resets, every scan request is swallowed.

Trace the whole path and write `docs/codex/findings/stuck-network-scan.md`:
- every place that sets/clears the scanner-alive flag and the network scan count (MediaLib included), and every way
  a scan can end (success, error, timeout, cancel, service killed, WebDAV hang) — which of them fail to reset;
- what `NetworkAutoRefresh.forceRescan` and `PutioSyncScheduler.request` actually start, and how the "queued" phase
  can outlive the 15-second "not_started" guard in the UI while nothing runs;
- whether the WebDAV listing of `webdav.put.io/TV` can block forever (timeouts, retries, threads);
- why Library Health's summary ("Confirmed missing: 2") and its categories ("No issues") disagree;
- a ranked list of root-cause candidates with evidence (file:line, quoted code, the exact sequence), and for the top
  one a **proposed fix described in words** (not applied) plus the test that would prove it.
Commit: `Stuck network scan analysis (read-only)`.

## Part 2 — engine update plan for 0.156
Exactly **Part 3 of `docs/codex/BRIEF_0154.md`** (upstream avos commits since `95a9e6b3`, which Supernova patches
still apply, MediaLib fix `e5886e0`, the three aos-Video HDR commits from `9ec1bdc2`, subtitle box padding end to
end, workflow/lock changes, and permanent storage of the rebuilt engine before the pinned artifacts expire on
27 Jan 2027). Write `docs/codex/findings/engine-0156-plan.md`. Commit: `Engine 0.156 update plan (read-only)`.

## Part 3 — TV-only stage 3 unblocking plan
Your run on `codex/0154-2026-11-03` stopped before stage 3 because its deletions still have live callers that later
stages remove. Read that branch. Write `docs/codex/findings/tv-only-stage3-plan.md`: the exact callers blocking
stage 3, and a re-ordered plan for stages 3–6 so each stage builds on its own (which files move together, which
constants or helpers must be extracted first, and which tests go with each). Nothing is deleted in this run.
Commit: `TV-only stage 3–6 re-ordering plan (read-only)`.

## Part 4 — continue the deep bug hunt (only if time remains)
As in `docs/codex/OVERNIGHT_BRIEF.md` Part B: the 56 unread files of group 1 (player) first, then groups 2–4.
Append to the existing `docs/codex/findings/deep-hunt-*.md` files and update their coverage tables. Do not re-report
U1, N1, N2, L1, P1, U2, L2 or DP-01…DP-05. Skip `leanback/filebrowsing/` (being rewritten).

## Before you finish
Write the report: per part, what was done (commit hashes), what was skipped and why, anything odd. Confirm
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` lists only files under `docs/codex/`. Commit and
push.
