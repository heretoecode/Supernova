# Diagnostic report review — owner's Shield, exported 4 Nov 2026 21:25 UTC (0.154)

15,645 unique events from 31 Oct – 4 Nov, builds 0.148–0.154. 236 ERROR, 710 WARNING, 2 FATAL.

## Needs fixing (0.156)
1. **Crash in the diagnostics recorder itself** (0.153, 3 Nov 16:45, thread `SupernovaDiagnostics`):
   `IllegalArgumentException` in `TimSort` from `Diagnostics.trimStorage` — `files.sort(Comparator.comparingLong(File::lastModified)…)`
   (`diagnostics/Diagnostics.java:1293`). `lastModified()` changes while the sort runs (files are still being
   written), so the comparator breaks its contract and the app dies. Still present in 0.155. Fix: read each file's
   `lastModified` once into a snapshot, sort the snapshot. Test: comparator over a list whose timestamps change mid-sort.
2. **One library source fails on every scan** — source `4ae5c08c…` fails in every 0.154 scan (40×, every trigger:
   startup, resume, scheduler), always "1 of 2 sources failed", and **no reason is recorded**. The other source
   (`c1ea09cd…`) completes. Two scans also run at once from two processes. Fix: record the failure class/stage for
   `scan_failed`; then fix the cause (part of the 0.156 stuck-scan work).
3. **Local import check fails after 1½–3 minutes** — `scan_metadata_failed` / `scan_partial` on resume, 61,050 rows
   checked, 0 new, 86–168 s; no reason recorded. Same fix pattern: log why, then fix.
4. **Backup failure left no trace.** The export path (`BackupOperationModel`, `utils/backup` writer/verifier) records
   nothing; only restore paths log. Add `backup_failed` with stage (collect / write / verify / copy), category, row
   count, byte size and exception class. Codex is analysing the cause separately.
5. **Same focus crash pattern elsewhere.** The 0.148 player crash (`FocusFinder` given a view outside its subtree,
   `PreviewPlaybackMenus`) is already guarded (`PreviewPlaybackMenus.java:794–801`), but the unguarded call sites
   remain: `PreviewSettings.java:959, 993`, `UniversalFileBrowser.java:2705, 2753, 2781, 2801`, `SidePanelKit.java:150`.
   Route them through the same guard.
6. **Focus anomalies in 0.154** (136): `offscreen_focus` on `container_list` grid (68), repeated unhandled
   navigation on Home rows `Row 0/2/0/3` and `0/2/0/1` (D-pad presses that go nowhere), restoration fallback (8).
   Worth reproducing on Home row ends.

## Noise to reduce
- **"Unclean exits"** 13 of 18 launches in 0.154 (123/157 overall). Nearly all are Android stopping the app in the
  background (`android_process_exit` reason 2, signal 9, importance 400 = cached) — normal on Android TV, not crashes.
  Record the Android exit reason with each one and stop calling these "unclean". Background memory was ~400 MB PSS,
  which makes Supernova the first thing Android stops.
- **1,359 events dropped** across the period — the recorder's queue overflows during scans. For full-detail logging,
  raise the queue and file budget.
- The report says "Exception messages … excluded" — for the owner-only build, include exception messages (minus
  credentials) so failures say *why*.

## Fine
Artwork 853 requests, 0 failures; TMDb requests healthy; playback sessions start and seek without errors.

# Second report — 5 Nov 2026 00:40 UTC (0.155)

## The glitch and audio drift the owner saw (Dark Matter, Put.io)
- 00:36:06 playback started (resume at 21:04), 00:36:28 paused, **00:39:42 resumed after 3 min 14 s**.
- **00:39:50 the Put.io (WebDAV) read failed** — the connection opened before the pause had been idle 223 s and the
  server dropped it. Supernova reconnected at 00:39:53 and carried on without re-syncing → a few broken frames, then
  audio out of step. No seek or buffering event was recorded around it.
- **Same pattern at 23:03 on 4 Nov, worse:** paused ~4 min, resumed, the read failed 1.5 s later (stream idle 235 s), and
  15 s later playback died (`player_error` 1001/1000); the app was then closed (Android exit reason 10, user request).
- A third failed read (idle 129 s) earlier in the evening.
- **Fix (0.156, Codex Part B):** for network sources, after a pause longer than ~60 s reopen the stream and seek to the
  current position before playing; on any mid-play read failure, reconnect and seek to the last shown frame so audio and
  video restart together; record `stream_reconnect` (idle time, cause, recovery ms, result). Workaround until then: a
  short seek back re-syncs.

## Other findings
- One source (`4ae5c08c…`) still fails nearly every scan in 0.155 (18 times); the other (`c1ea09cd…`) failed twice too.
- While paused the stream keeps reading ahead (67 MB in 3 min) and native memory grows ~3.5 MB/min — expected
  read-ahead, but cap it.
- Diagnostics: one write error (23:03), 14 events dropped per process; fast play/pause toggling while seeking at 00:29
  logged as `seek_callback_unattributed`.
- 76 off-screen focus warnings on the library grid (same as before).
