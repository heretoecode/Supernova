# 0.157 time log (owner asked, 15 Nov 2026: measure how long it really takes)

All times local. Coding time only; waiting for the owner or for a blocked build is noted separately.

| When | What |
| --- | --- |
| 15 Nov 01:43 | Coding starts (first commit: diagnostics read, build order). |
| 15 Nov 01:43–01:53 | Codex's 0.157 work merged; seek-preview cause fixed; hidden switch shown. |
| 15 Nov 01:53–02:00 | Blocked: GitHub Actions disabled on the fork (owner re-enabled it at 02:00). |
| 15 Nov 02:00 | First build: failed in 25 s at set-up (the build only accepts the pinned MediaLib plus registered patches). |
| 15 Nov 02:07 | Owner's usage ran out and was reset. Clock noted by the owner. |
| 15 Nov 02:07–02:35 | Recent scans store, no rebuild on unchanged snapshots, NOVA 6.6.5+ carried into MediaLib and FileCore as registered patches; software-decoding notice; Skip button fixes. Second build started 02:13. |
| 15 Nov 02:35–07:37 | **Not working: the owner's 5-hour usage limit was reached** (about 5 hours waiting for it to reset; no coding). The second build had failed at 02:28 (test compile errors in merged work). |
| 15 Nov 07:37– | Resumed: failure report extended to show compile errors; fixes; Recently Added; the minute-by-minute scan cause (failed scans never counted as done) with the 15-minute rule; third build started 07:39. Scan timer skips quietly in the background; memory by kind and page in diagnostics. |
| 15 Nov 07:42–12:52 | **Not working: the 5-hour usage limit was reached again** (about 5 hours waiting). The third build had failed at 07:51. |
| 15 Nov 12:52–12:58 | Resumed: test fix, fourth build started 12:54, handover note written for a fresh session. |
| 15 Nov 13:00–13:10 | New session takes over from the handover note. Fourth build still running. Meanwhile: chapter names in the time bubble, stuck scanning pill, reopen where you were, focus after standby, held Down. |
| 15 Nov 13:10–18:10 | **Not working: the session stopped** (about 5 hours; usage limit or a restart of the session). |
| 15 Nov 18:10– | Resumed. Fourth build had failed at 13:39: compiled, 10 of 2,145 tests failing. All ten fixed; fifth build started 18:12. |

Pace so far (15 Nov): each 5-hour usage window gives roughly 30 minutes of coding before the limit is reached.
| 15 Nov 18:22 | Fifth build failed after 10 minutes (compile stage). Meanwhile: Issues Digest out, picture in picture out, All files access asked once. |
| 15 Nov 18:48 | Owner asked whether finished: no; reading the fifth build's failure report. |
| 15 Nov 18:55 | Fifth build's cause: one line lost in an edit (compile error). Fixed; sixth build started 18:50. One MediaLib scan test failed by timing (passed in build 4, nothing changed); watched. |
| 15 Nov 18:50–18:53 | While build 6 runs: new loading ring, app icon, banner and splash (the new mark). |
| 15 Nov 18:56 | Paused to answer the owner. Build 6 still running. |
| 15 Nov 19:04 | Resumed. |
| 15 Nov 19:04–19:13 | While build 6 runs: choice-list search, Selected A–Z, menu headings, keyboard Phone key and password eye, region from device, diagnostics retention. |
| 15 Nov 19:19 | Owner: keep working while builds run. Carrying on. |
| 15 Nov 19:19–19:23 | Build 6: 2 failing tests (one was the MediaLib test I had wrongly called flaky; real cause found). Fixed; build 7 started. |
| 15 Nov 19:23–19:41 | While build 7 runs: top-bar signal slot, pill retired, Files rename, shared tab row, new Files page (three tabs, wired behind a switch), artwork edge on cards. |
| 15 Nov 19:50–23:16 | **Not working: the session stopped** (about 3½ hours). Build 7 had failed meanwhile. |
| 15 Nov 23:16– | Resumed. |
| 15 Nov 23:16–23:18 | Build 7: 3 failing tests, fixed; build 8 started 23:17. |
| 15 Nov 23:18–23:25 | While build 8 runs: Playback Settings (Stop playing at, Play mode, Skip intros and credits, Still watching?), AI names out of code comments, release notes draft. |
| 15 Nov 23:25–23:32 | While build 8 runs: JustWatch line, delete logging, Preparing playback episode line and age badge, age rating 8 s, audio track pills, single-season shows. |
| 15 Nov 23:47 | Loop mode: working through the rest on my own. New Files page switched on (QUESTIONS.md #2). |
| 15 Nov 23:47–23:54 | Files page on; track lists without back header and None; sort titles automatic; Put.io source name; Couldn't be read row; questions logged. |
| 15 Nov 23:54–23:59 | Details tabs no longer pop in; player title block top left; Files scan control and Found on your network. |
| 16 Nov 00:00–00:01 | Build 8: 10 failing tests, fixed; build 9 started. Connections reachability check added. |
| 16 Nov 00:06–05:49 | **Not working: the session stopped** (about 5¾ hours). Build 9 had failed meanwhile. |
| 16 Nov 05:49– | Resumed. |
| 16 Nov 05:49–05:51 | Build 9: 1 failing test, fixed; build 10 started. |
| 16 Nov 05:51–06:01 | Subtitles (next two episodes, full subtitles default, pills), calmer backdrops, skip button row, seek preview card, version 0.157 with 0.156 history evidence, 0.157 build workflow. Signing blocked (QUESTIONS #9). |
| 16 Nov 06:45 | First green 0.157 conformance (run 43813823463 on 571224b). Final run on the last commit next; signing waits for the owner. |
| 16 Nov 07:15 | Final 0.157 conformance green (run 43817914116 on 3c3ffbf). Stopped: signing waits for the owner (QUESTIONS #9). |
| 16 Nov (later) | Owner allowed the 0.157 signing copies; added (version strings only). Conformance on the latest commit, then signing. |
