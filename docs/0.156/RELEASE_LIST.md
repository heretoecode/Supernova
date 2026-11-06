# Supernova 0.156 — everything pencilled in (4 Nov 2026)

Sources: `docs/0.155/OWNER_NOTES.md` (items 1–39), `docs/0.154/OWNER_NOTES.md` (items 14–17),
`docs/0.155/SETTINGS_REVIEW.md`, `docs/0.155/DIAGNOSTICS_2026-11-04.md`, Codex's branch. Nothing is coded until the
owner gives the go-ahead. ✅ = approved, ◻ = approved in principle / still needs a final look.

## Bugs
1. ✅ Library source that fails on every scan (reason not recorded) + the stuck network scan (0.154 #16).
2. ✅ Local import check gives up after 1½–3 min on resume.
3. ✅ Backup "could not be prepared" (Codex analysing; fix by Claude).
4. ✅ Diagnostics recorder crash (`trimStorage` sort).
5. ✅ Unguarded focus-search calls that can crash (Settings, file browser, side panels).
6. ✅ Local Storage: Home grabs focus while a folder loads (OWNER #30).
7. ✅ Local Storage: header jumps on All files ↔ Videos only (#31).
8. ✅ Local Storage library rows missing details (#12).
9. ✅ Save Changes tick vanishes on focus (#37) — fixed at the root for every button.
10. ✅ Playback Settings: Audio/Playback tab icons invisible on focus (#3); colour swatch ring clipped (#7).
11. ✅ Details: flat blue card before artwork arrives (#9).
12. ✅ Auto-next can play a hidden file (#10, Codex DP-09).
13. ✅ Settings rows: values/switches one row out of step and said twice (every page, #16).
14. ✅ Credits say NOVA 6.6.2 (should be 6.6.4).
15. ✅ Picture in picture: subtitles off in PiP; OK/reopen returns to full screen; PiP buttons (#36).
16. ✅ OMDb ratings not saved — keep a week per title (#34).
17. ✅ Remove NOVA's blue "Press UP for the TV menu" first-play box (#38).
18. ✅ Home rows / top bar: blur and lingering colour on Documentaries (#21).
18a. ✅ Put.io playback: picture glitch and audio out of sync (or playback failing) after resuming from a pause of a few
    minutes — reconnect and re-sync (diagnostics review 2, 5 Nov; Codex Part B 17a, Claude verifies on the Shield).

## Player
19. ✅ HUD buttons, time bubble and seek bar (#8).
20. ✅ Remote scheme: bar Left/Right seek, OK play/pause, Up to the buttons, Down to Chapters · Episodes (#38).
21. ✅ Chapters and Episodes rows below the bar, Home-style cards, raised controls; chapters + IntroDB parts merged,
    thin lines on one continuous bar; a picture for every part (#33).
22. ✅ Our own first-play tip (#38).
23. ✅ Skip Intro button bottom right, draining button, 10 s (#17).
24. ✅ Subtitle Vertical Position step control with live sample line; appearance preview rule (#35).
25. ✅ Live subtitle preview in Playback Settings (#4); Download Subtitles without the centre pop-up (#5).
26. ✅ Seek thumbnails — experimental, measured in Diagnostics (#39; Codex engine, Claude UI).

## Home, Details, Search
27. ✅ Hero text readability and title logo placement (#1, #2).
28. ✅ Home row controls beside the row (#14).
29. ✅ Customise Home side panel + genre icons v2 (#22).
30. ✅ Top bar charcoal fade rebuilt (#21).
31. ✅ Search: QWERTY keyboard, symbols page, results layout, relevance, Clear recent; "What you can search" (#18, #20) —
    heading approved.
32. ✅ Edit Page: no scrolling, year range, separators, Delete Page at the bottom (#37).

## Network & Files
33. ✅ Local Storage: status on every row, single watched pill, grid artwork and long-press menu (#11, #13).
34. ✅ Network Shares, Add Connection, Saved Connections merged, Library Health tile (0.154 #14–16).
35. ✅ Landing tiles, Scan now with live status, Library Scanning, Library Health in plain words (0.154 #17).

## Settings (all eight sections, single-panel pattern)
36. ✅ Playback (incl. Skipping), Audio & Subtitles (fonts in Supernova's browser), Library & Home (.nfo export,
    Refresh details moved to Library Health), Where to Watch (renamed), Appearance (Accent removed; the screensaver is parked, owner 5 Nov 2026),
    Accounts & Services (renamed), System (Backup three choices + private items, Diagnostics full detail, Advanced),
    About (credits for every service, full version history, licences with search and QR codes, Device) (#23–#29, #32).
37. ✅ IntroDB tidy-up (#15).

## Former "in principle" ideas (#19) — all approved 5 Nov 2026 (mock-ups `suggestions/`, `network-files-v2/08`)
38. ✅ One shared choice list replacing every translucent pop-up.
39. ✅ Sleep timer back · ✅ "Still watching?".
40. ✅ Fix match from Details.
41. ✅ Library Health badge on the Network & Files tile.
42. ✅ Remember audio/subtitle choices per series.
43. ✅ Consistent empty states · ✅ focus memory everywhere.

## Diagnostics (Codex brief `BRIEF_0156_THUMBS_DIAGNOSTICS.md`)
44. ✅ Always full detail; reasons for every failure; real Android exit reasons; bigger log; playback, network and UI
    measurements; settings changes; clearer summary with top-20 errors.

## Codex work to bring in
45. ✅ DP-01…DP-07, TV-only stages and the rest of `BRIEF_0155_NEXT.md` as it is reviewed and re-tested.

## Waiting for the owner
- OMDb key built into the app: **no, for now** (owner) — kept in `docs/REVISIT_LATER.md`.
- Every design approved (5 Nov 2026). **Only the go-ahead to start coding remains.**

## Also decided 5 Nov 2026
- Seek previews (experimental) switch in Settings › System › Advanced, on by default.
- Old IntroDB settings page: covered by Accounts & Services and Playback › Skipping.

- Blue values stay as in the approved 0.156 mock-ups; the global "no blue values" sweep is done once, in 0.157
  (owner, 5 Nov 2026).
- The first-play tip shows once per install, so it appears once after updating to 0.156 (owner, 5 Nov 2026).

## Progress (Claude, updated 5 Nov 2026 16:10 UTC)
Built and fully tested (conformance run 41984891190 green, 07e6074d2):
- #6 Local Storage focus kept in page · #7 header no longer jumps · #9 icons follow focus at the source
  (Save Changes tick) · #14 NOVA 6.6.4 in Credits/About/licences · crammed-code smoke alarm (tools/test_code_layout.py)
- #36 Settings stage 1–2: single-panel pattern and the new section layout / plain-English rows (SettingsLayout.java)
- #32 Edit Page: no scrolling, header and Delete lines, year range
- #18/#30 top bar: charcoal fade driven by the real scroll
Committed, in the build running now (42053895432): #11 Details no flat card · #23 Skip button (option B) · #33 Local
Storage status tags + single watched pill · #38 shared choice list · Seek previews switch · #27 hero text and logo ·
#28 Home row Hide/Move · #10 tab icons and swatch ring · #25 Download Subtitles in the panel · Codex Parts A–D merged.
Committed 5 Nov afternoon, waiting for the next build (player):
- #20 remote scheme (Up to the buttons, Left/Right between them, OK on the bar plays/pauses, Down to the strip)
- #22 our own first-play tip · #19 HUD look (glass buttons, large time over a grey chapter, white seek bar, halo dot)
- #21 Chapters · Episodes strip (merged chapters + IntroDB parts, cards with pictures, lit stretch of the bar)
- #24 subtitle Vertical Position step control · #25 live appearance sample · #39 Sleep timer and Still watching?
- #26 seek preview pictures above the time bubble · #42 per-series audio/subtitle switch
- #31 Search rebuilt (one panel, QWERTY + symbols keyboard app-wide, recent searches, What you can search, filters,
  grouped results, bold typed letters, watched pill, voice)
- #35/#41 Network & Files landing (Library Health tile with count badge, live Scan now tile, Advanced dropped) and
  Library Scanning wording without duplicate scan buttons
Committed 6 Nov 00:07 UTC, in conformance run 42134557197: #34 Put.io service row on Network Shares (signed in as,
storage, reachable, library sync, Retry) and Test connection on Add Connection (nothing saved; names the field).
Still to do (Claude): rest of Network Shares (Edit/Test/Remove on saved connections) and Add Connection types (#34), Library Scanning/Health full pages (#35),
Settings stage 3 (System/About, Where to Watch, Appearance, Audio & Subtitles incl. Vertical Position), Customise Home
(#29), Fix match (#40), empty states and focus memory (#43), Local Storage grid and menu (#33).
Codex: the failing tests in `docs/codex/BRIEF_0156_TEST_FIXES.md`.
