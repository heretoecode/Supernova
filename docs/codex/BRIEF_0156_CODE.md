# Codex brief — 0.156 underlying code (owner approved 2026-11-05)

Replaces the order of all earlier open briefs. **Parked by the owner — do not start:** `BRIEF_GITHUB_CLEANUP.md`.
`BRIEF_0156_THUMBS_DIAGNOSTICS.md` is now **Part D** of this brief (do it here, in this order).

Rules: `BRIEF_0155_MORE.md` rules 1–5 (branch `codex/0155-code-2026-11-04` only; no PR, merge, rebase, force-push or
workflow dispatch; Java 17, full imports, a JUnit/Robolectric test for every change; Claude builds and tests).
**`docs/codex/HANDS_OFF.txt` has a new 0.156 section — read it first.** Claude is coding the screens at the same time
on `claude/supernova-github-access-yfhe89`, so stay strictly inside the files this brief and the 0.156 section allow.

**Code layout (owner, standing rule):** write normal, readable Java — one statement per line, braces on their own
lines as in the surrounding file, lines ≤ 120 characters (string text may wrap with `+`), no chains of statements
packed onto one line, no one-line methods with bodies. Every file you touch must meet this when you finish, including
lines you didn't write. Claude rejects a Part that doesn't. Also reformat (layout only, separate commits) the files
in `docs/codex/REFORMAT_QUEUE.txt` that fall inside your 0.156 area (`utils/backup/`, `diagnostics/`, `metadata/`,
`streaming/` engine, player engine classes, and their tests); leave the rest for Claude.

**No UI:** no layouts, drawables, colours, sizes, wording on screen or Settings rows. Every part below ends in a small,
documented Java API (plus preference keys with defaults) that Claude's screens call. Where a task needs a screen
change to be visible, describe the call Claude must make in `findings/0156-ui-hooks.md` (file, method, what to call).

What everything is for: `docs/0.156/RELEASE_LIST.md` (item numbers below) and `docs/0.155/OWNER_NOTES.md`.

## Part A — bugs (first)
1. **Finish `BRIEF_0155_BACKUP.md` analysis if still open, then fix it** (#3): the cause you rank first, with the test
   you proposed. Engine side only (`utils/backup/`); tell Claude any change to the messages it shows.
2. **Scans** (#1, #2; 0.154 notes #16): one source (`4ae5c08c…` in the owner's report) fails every scan with no reason;
   the local import check fails after 86–168 s on resume (61,050 rows); the stuck network scan. Record the failure
   class/stage on `scan_failed`, `scan_batch_failed`, `scan_metadata_failed`, `scan_partial`; find and fix the causes;
   tests that reproduce each. `findings/stuck-network-scan.md` is your starting point. Two processes scanning at once
   (seen in the report): make it impossible.
3. **Diagnostics recorder crash** (#4): `Diagnostics.trimStorage` sorts on a changing `lastModified` — snapshot first.
4. **Focus-search crash guard** (#5): one shared helper (the `PreviewPlaybackMenus.java:794–801` guard, generalised)
   and route every `FocusFinder.findNextFocus` caller through it (`PreviewSettings`, `SidePanelKit`,
   `UniversalFileBrowser` — allowed for this one change only). Behaviour must be identical except no crash.
5. **Auto-next plays a hidden file** (#12, your DP-09): fix and test.
6. **Local Storage library rows missing details** (#8, notes #12): find why the library lookup misses and fix the
   lookup (data side only; Claude does the row).
7. **OMDb ratings** (#16): store per title with the other Details data; refresh at most once a week; respect the
   900/day budget; fall back to the stored values when offline.

## Part B — player engine
8. **One timeline of parts** (#21): merge file chapters + ChaptersDB + IntroDB (recap, intro, preview, credits) into
   one ordered list `{name, kind, startMs, endMs}`. Rules: a chapter edge within 5 s of an IntroDB edge snaps to it;
   a chapter covering only an IntroDB part is replaced by it; no chapters → breaks only at IntroDB parts with the rest
   named "Episode"/"Film". `partAt(positionMs)` for the time bubble.
9. **Episodes for the strip** (#21): the current season's episodes with title, number, still, duration, watched,
   resume position, and the next playable one (skipping hidden files).
10. **Part pictures** (#21): a frame for each part at start + 2 s, via Part D's `frameAt` (use the episode still until
    ready).
11. **Subtitle Vertical Position** (#24): new scale 0–90 % of the screen height in 5 % steps (Bottom = 0 %, default 5 %,
    top = 90 %), replacing the one-third cap in `SubtitleManager.setVerticalPosition`; convert saved values so nobody's
    subtitles move; API for the sample-line preview (show/hide a sample line drawn with current settings, hide real
    subtitles meanwhile — Claude places it).
12. **Picture in picture** (#15): subtitles off in PiP and back after; OK/Select on the PiP window and reopening
    Supernova return to full screen; Play/Pause and Full screen `RemoteAction`s (`PictureInPictureParams.setActions`).
13. **Sleep timer and Still watching?** (#39): timer engine (30/60/90 min, end of this episode, cancel, remaining
    time), and an auto-play counter that asks after N episodes (default 3; stops playback 60 s after the question if
    unanswered). Preferences with defaults; events for Claude's card.
14. **Per-series audio/subtitle choices** (#42): store the chosen audio + subtitle track description per series, apply
    them when an episode of that series starts (best match by language/codec/channels/forced flag), switchable per
    series.
15. **Remove NOVA's first-play help overlay** (#17): `PlayerController.java:885–898`, its layout/strings if unused.
16. **Play/pause on OK from the bar**: the key-handling part only, behind a method Claude calls (Claude owns the rest of
    the remote scheme).

17a. **Network stream after a pause (owner bug, top priority in Part B):** see the "Second report" in
    `docs/0.155/DIAGNOSTICS_2026-11-04.md`. After a pause > 60 s on a network source, reopen and seek to the current
    position before playing; on a mid-play read failure, reconnect and seek to the last shown frame so audio and video
    restart together; never continue a broken byte stream; cap read-ahead while paused; event `stream_reconnect`.
    Test with a fake server that drops idle connections.

## Part C — data for Claude's screens
17. **Search matching** (#31): in `PreviewSearchText` (and the query in `PreviewSearch` only where the matching lives):
    original titles, cast/crew/studios by full name or surname, words in any order, start-of-word matches, ignore
    case/accents/punctuation/leading the/a/an, one wrong letter in words of 4+ letters, file names last. Ranked results
    API; tests with the examples in owner notes #20.
18. **Fix match** (#40): search TMDB films/series for a title (and TMDB/IMDb IDs), return candidates with poster, year,
    kind, current match flag; apply a chosen match to the file, refresh its details/artwork, keep watched state and
    progress.
19. **Library Health count** (#41): `needsAttentionCount()` + one-line reason, cheap enough for the landing tile.
20. **Backup choices** (#36): presets Everything (all except private), Settings only, Custom; a `private` flag on
    Library sources, File bookmarks, File names and locations (off by default) with a reveal text per category.
21. **Diagnostics settings API**: always full detail (drop the level in code, keep reading the old key), `clearAll()`.
22. **Seek previews switch**: preference `seek_thumbnails_enabled` (default on) wired to Part D's engine.
23. **Where to Watch provider list**: refresh automatically weekly and when the country changes; expose
    `lastUpdated`.
24. **.nfo export with progress**: progress callback (done/total/current title) from `NfoExportService`.

## Part D — seek thumbnails and diagnostics expansion
Everything in `BRIEF_0156_THUMBS_DIAGNOSTICS.md` (A, B, C), minus C.1 (now A.3 here).

## Order and hand-back
A → B → C → D. **After each Part**: commit, push, and add a line to `docs/codex/HANDOVER.md` ("Part A ready at
<sha>") so Claude can build, test and merge that Part while you continue — you don't need to wait for Claude.

## Before you finish (each Part)
Report `docs/codex/reports/YYYY-MM-DD-0156-code-part-X.md`: per item — commits, files, tests, what needs a Shield check,
the UI call Claude must make. Confirm with `git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that you
touched only files allowed by the 0.156 hands-off section. Commit and push.
