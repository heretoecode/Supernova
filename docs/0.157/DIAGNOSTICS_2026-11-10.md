# Diagnostics report of 10 Nov 2026 01:25 (0.156) — what it shows

Read by Claude on 11 Nov 2026 against the owner's timestamp index (`OWNER_WALKTHROUGH_2026-11-10.md`). Times are UTC
(= Irish time). The export covers 3–10 Nov but is trimmed by size, so busy hours push older ones out: **nothing is
retained for 6 Nov 00:00–21:00 or 8 Nov 00:00–02:00**, so the 6 Nov 17:51 Put.io pause and the 8 Nov 01:05 lag
cannot be checked. The export itself is not stored in the repository (private).

| When | Owner saw | What the log shows | Confidence |
| --- | --- | --- | --- |
| 8 Nov 02:15–02:17 | Holding Down in TV Shows jumps to the top; focus on Home while TV shows | While Down is held the grid reports "edge held", then focus becomes `none` and falls back to `topnav.home`; Down then starts again from the toolbar. Repeats. Also `offscreen_focus` warnings. | High: focus is lost when the next row is not ready, and the fallback is the first top-bar item |
| 8 Nov 11:37 | Deleting files: too many confirmations | Deletion dialogs are not logged. At 11:37:14 a focus restoration failed on Movies (`restoration_fallback success=False`). | Deletion flow: no evidence either way |
| 9 Nov 21:29–21:35 | Leaving Documentaries loses top-bar focus | 20 `offscreen_focus` warnings: focus sits on `VerticalGridView:container_list` — NOVA's old hidden grid behind the new pages — plus one `repeated_unhandled_navigation`. | High: focus goes to a hidden legacy view |
| 10 Nov 00:48–00:56 | Splash sometimes, Home sometimes, wrong tab highlighted, no focus | 28 `startup` events in the window: on some returns Android had ended the app and it started fresh (splash, Home); on others it resumed. After resumes, the same `container_list` off-screen focus (25+ warnings). | High for both; why Android ends the app so quickly is not shown (memory: the main process was ~750 MB when closed at 01:22) |
| 10 Nov 00:59–01:03 | Standby: Movies kept, focus invisible, Down goes to Home | Backgrounded 00:59:24, back 01:03:25 with a `startup`; then `offscreen_focus` on `container_list` and `repeated_unhandled_navigation` (Up/Left/Right ignored). | High: same hidden-view focus fault |
| 10 Nov ~01:07 | Freeze, then force close | A local re-check of **61,050 file rows taking 170 s** (trigger: resume) finished 01:06:16 and a new one started 01:07:31; scans start on every return to the app (104 scan starts in 27 min; 1,485 in the week). Focus warnings on Home at 01:06:35–46. Force close and fresh start at 01:07:47–53. | Medium: the repeated heavy scan is the likely cause; the freeze itself is not directly recorded |
| 10 Nov 01:09 | Resume went back to Details | `playback_exit_requested reason=user_back` at 01:09:23: the Back button was pressed. | High: not a fault |
| 10 Nov 01:11 | (the same or the next attempt) returned to Details | Paused 01:09:53. At 01:11:22 play pressed after 89 s: `stream_reconnect … result=started`, never `ready`; main process 22482 stops logging at 01:11:24 and a **new main process starts at 01:11:26**. Playback restarts from Details at 01:11:39. | High: **the app crashed during the Put.io reconnect after a pause over 60 s.** No crash reason captured |
| 10 Nov 01:13 | Brief glitch, sound in sync | Paused 01:11:50; at 01:13:03 reconnect started and was `ready` in 2.4 s. | High: the 0.156 reconnect worked this time; the glitch is the re-open |

## Other things the report shows
- **Seek previews:** 209 `thumb_error` — every attempt fails in `sync_frame_extract` with a NullPointerException
  ("Map.size() on a null object"). No preview was ever produced. (Codex's part B fix addressed the cache miss;
  check it also covers this.)
- **Artwork:** all 208 failures are TV Shows grid posters whose local file is missing (`FileNotFoundException`).
- **Scans:** one source (`4ae5c0…`) failed 128 times with no reason recorded; one WebDAV source returns 404 at its
  root; "Scan lock wait interrupted" 15 times.
- **Continue Watching:** playback positions were saved (`checkpoint_submitted` on every pause), so the slow or missing
  update is on the Home side, not the saving.
- **For Codex:** crash on reconnect; scan-on-every-resume and its cost; missing crash reason capture; NPE in seek
  previews; missing poster files; the failing source. **For Claude:** focus falling to `none` / the hidden
  `container_list`; state restore after Android ends the app.
