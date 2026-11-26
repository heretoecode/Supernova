# Diagnostics read: 0.158.9 (report exported 26 Nov 2026, 12:22 UTC)

Build 0.158.9 (code 1589, source `a4bf56e`), the owner's Shield (Android 11). The report holds 19–26 Nov; this read
covers **only 0.158.9**: first started 26 Nov 11:00 UTC, about 80 minutes of use, 3,949 events (0.158.7 was read in
`DIAGNOSTICS_0158_7.md`). Read by Claude on 26 Nov; every fix is in `BRIEF_0158_C.md` (M2–M4 notes, D14–D18).

## Better than 0.158.7
- **No crashes.** The scan-lock / AutoScrape crash (D1) and the SambaDiscovery crash (D3) did not appear. One background
  scanner process was ended by Android (12:07, signal 9, cached importance): not a crash; the next scan carried on.
- **The hidden grid is gone (D4):** no focus slips on `container_list`.
- **Home skips needless redraws:** 27 redraws skipped as unchanged; 5 still caused by storage broadcasts (D9).
- No freezes; no TV-poster cache failures in this window.

## Faults
| # | What | Evidence | Brief |
| --- | --- | --- | --- |
| 1 | **The MobLand file, again.** Playing S2 E9 over SFTP failed (11:24:36, error 1001, **no reason recorded**: message `null`, class `not_supplied`); two deletes then failed with "not found" (11:24:51, 11:26:05). The episode is still in the library and not in Missing files. | `stream_backend_read_error`, `player_error`, `delete_failed reason=not_found` ×2 | M2, M3 (confirmed on 0.158.9); item 9 |
| 2 | **SFTP scans can't tell new from known.** The same two SFTP sources (14 and 85 files) report "14 new" and "85 new" at 11:56 and 12:14, "0 new" at 12:07; no scan reports any missing count; "sources total" flips 1 / 2 / unknown. | `scan_complete`, `scan_reconciled` | M4 (added) |
| 3 | **Scanning far too often.** The local import runs on every return to the app: **20 times in 80 minutes**, each 4–6 minutes, checking 97,041 rows to find nothing new. The SFTP sources were scanned 8 times in 80 minutes, twice at the same moment ("scheduled" and "native_scheduler"). | `scan_started trigger=resume` ×20, durations 258–390 s | D14 |
| 4 | **The report records many events twice** (same payload, new sequence number): about 770 duplicates in 80 minutes (focus moves 311, focus 168, scanner state 117, scans, focus warnings). Counts in the summary are inflated. | identical events | D15 |
| 5 | **A show TMDB no longer knows.** TMDB answers 404 for the show itself (TMDB id 205037: the show, its season and its where-to-watch), retried every 30 minutes (11:00, 11:30, 12:00), and recorded as "Availability service unavailable", which is the wrong reason. | `metadata_network_failed status=404 endpoint=tv/id`, `metadata_package_failed` | D16 |
| 6 | **Focus slips still left:** Home: focus on an off-screen text label (17, `TextView:generated/0/0/9/3` and `/0/0/1/4`) and presses that do nothing at a row's end (12); Settings off-screen (3); player restore fallback (2). Counted once each. | `focus_anomaly_suspected` | D17 |
| 7 | **Memory:** Android asked the app to trim memory 23 times in 80 minutes; playback 534 MB, the network (Files) page up to 433 MB; library pages about 410 MB. Slow frames on a custom library page 2.4 % (others under 1 %). | `memory_trim`, `performance_trend`, `ui_frame_summary` | D18, D13 |

## What the owner didn't see (summary for the owner)
The MobLand episode still can't be removed, and the app still doesn't say why it won't play; SFTP scans think every file
is new half the time and never count missing ones (the root of M4); the app re-reads the whole local library for five
minutes every time you come back to it; the report counts many things twice; one show has vanished from TMDB and the app
keeps asking about it every half hour; Android keeps asking the app to give memory back.
