# Diagnostics read: 0.158.7 (report exported 26 Nov 2026, local time 02:37)

Build 0.158.7 (code 1587, source `ecbfdde`), the owner's Shield (Android 11). Retained evidence 19–26 Nov: 143 launches,
88 app sessions, 33 playback sessions, 22,133 unique events (no dropped events, no write errors). Completeness PARTIAL
(native transport coverage). Read by Claude on 26 Nov; every fix is in `BRIEF_0158_C.md` › group 1 (D1–D13).

## Crashes and exits
| What | Count | Where | Brief |
| --- | --- | --- | --- |
| `IllegalStateException: Matching ownership unavailable` ← `InterruptedIOException: Scan lock wait interrupted` | 6 (20 Nov ×4, 22 Nov ×2) | `AutoScrapeService$3` → `PersonalVideoActions.saveIfMatchable` → `ScanProcessLock.acquire` | D1 |
| `IllegalStateException: This is not a listener (anymore?)` | 1 (19 Nov 03:31) | `NetworkDeviceDiscovery.attach` → `SambaDiscovery.removeListener` | D3 |
| OS-confirmed crash of the main process (PSS about 556 MB at the time) | 1 (20 Nov 15:13) | the D1 crash | D1 |

## Focus
359 `focus_anomaly_suspected`: 285 off-screen focus, 45 repeated unhandled navigation, 29 restoration fallbacks.
- 187 on the main page, all on `VerticalGridView:container_list/0/0/0/0`: a hidden, off-screen grid holding focus (D4).
- 61 in Settings (`VideoSettingsActivity`): focused rows not on screen (skipping rows, Dolby Vision, refresh rate,
  diagnostics) (D5, with the owner's Backup bug B1).
- 18 restoration fallbacks in the player; 3 unhandled Left / Right in the player (focus on a bare FrameLayout).

## Library, network and data
- Scans: 44 failed, 38 of them waiting for the scan lock (interrupted or cancelled; local on resume, SFTP from the
  scheduler); 4 Put.io (WebDAV) scans refused with "Source is owned by a provider API"; 2 SFTP listings interrupted (D2, D6).
- Delete: 6 failures over WebDAV (Put.io), 19 Nov 14:32–14:42 (the MobLand delete), through NOVA's `browser.Delete`, no
  reason recorded (D6). One SFTP delete on 22 Nov succeeded.
- TMDB: 43 × 404 on TV where-to-watch, 4 DNS failures, a few interrupted (D7).
- Artwork: 123 failures, all TV Shows grid posters whose cached file is missing, no fallback (D8).
- Local import reported stalled at 0/0 six times (D12).

## Smoothness and work done for nothing
- Slow frames (over 32 ms): main page 0.9 % (worst 1.1 s), player 0.7 % (worst 3.1 s), TV show page 0.4 % (worst 2.0 s),
  Details 7.6 % (worst 2.1 s), subtitle downloader 1.0 % (worst 4.5 s), Settings 1.2 % (worst 0.55 s) (D11).
- Home rendered 100 times; 54 changed nothing, 27 triggered by storage broadcasts (D9).
- The app process started on the hour 31 times (background wake-ups); the scanner process sat empty 30 min (D10).

## Memory (PSS from `performance_trend`)
Playback peak about 640 MB (average about 570 MB); Details up to about 510 MB; library pages 360–470 MB; native memory
up to about 400 MB, Java heap up to about 290 MB. 26 memory-trim requests from Android. Not a leak diagnosis; the
diagnostics upgrade adds memory per page, and 0.159's clean-out is where the cut is measured (D13).

## What the owner didn't see (summary for the owner)
The two crashes behind the app vanishing mid-scan; the hidden grid behind most focus slips; scans quietly giving up while
waiting their turn; the delete failing for a reason nobody recorded; TV posters missing for want of a re-download;
where-to-watch asking TMDB the wrong question for TV shows; Home redrawing itself for nothing; hourly wake-ups;
memory high in playback.
