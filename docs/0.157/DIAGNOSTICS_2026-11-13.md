# Diagnostics, 13 Nov 2026 (export 20:48 local time)

Owner report: 20:47, TV Shows page open, focus on the first item, remote untouched — the screen seemed to jump up and
down slightly. The export file itself is not stored in the repository.

## Finding: a scan every minute rebuilds the library page although nothing changed
- A scheduled scan of the Put.io (WebDAV) source runs **every minute** (`scan_requested trigger=scheduled
  scheduler=indexed_sources` at 20:40:00, 20:41:00, 20:42:00, … 20:46:36, 20:47:36, 20:48:37). Each checks 10 items and
  finds 0 new, 0 updated.
- About 6 seconds after each one, the **TV Shows page is rebuilt** (`ui_rebuild surface=tv.library
  reason=indexed_snapshot`, rebuild_count 28 then 29) with **325 items before and after, 0 inserted / removed / moved**.
- The frame summary for the minute containing 20:46:42 shows 9 slow frames, the longest **417 ms**; most minutes from
  20:38 to 20:48 show 200–380 ms stalls. That is the jump the owner saw.
- Also: on resume at 20:43 the local scan re-checked all **61,050** rows (0 new, 0 updated), the scan-on-resume cost
  already noted on 10 Nov.

## Fixes for 0.157
1. **Rebuild nothing when nothing changed:** compare the new snapshot with the one on screen and skip the rebuild (and
   any re-layout) when no title was added, removed or changed; when something did change, update only those cards in
   place, keeping focus and scroll exactly where they are.
2. **Scheduled scans follow the agreed rule** (owner notes §23.4): automatic scans no more often than the Automatic scan
   setting, and never within 15 minutes of the last one finishing; Put.io's minute-by-minute check must not count as a
   full scan or trigger page refreshes.
3. Scan on resume: incremental, not a re-check of every row (already listed).
