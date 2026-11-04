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
    ◻ heading size final look.
32. ✅ Edit Page: no scrolling, year range, separators, Delete Page at the bottom (#37).

## Network & Files
33. ✅ Local Storage: status on every row, single watched pill, grid artwork and long-press menu (#11, #13).
34. ✅ Network Shares, Add Connection, Saved Connections merged, Library Health tile (0.154 #14–16).
35. ✅ Landing tiles, Scan now with live status, Library Scanning, Library Health in plain words (0.154 #17).

## Settings (all eight sections, single-panel pattern)
36. ✅ Playback (incl. Skipping), Audio & Subtitles (fonts in Supernova's browser), Library & Home (.nfo export,
    Refresh details moved to Library Health), Where to Watch (renamed), Appearance (Accent removed, screensaver),
    Accounts & Services (renamed), System (Backup three choices + private items, Diagnostics full detail, Advanced),
    About (credits for every service, full version history, licences with search and QR codes, Device) (#23–#29, #32).
37. ✅ IntroDB tidy-up (#15).

## Approved in principle (#19) — each still gets a final look
38. ◻ One shared choice list replacing every translucent pop-up.
39. ◻ Sleep timer back · ◻ "Still watching?".
40. ◻ Fix match from Details.
41. ◻ Library Health badge on the Network & Files tile.
42. ◻ Remember audio/subtitle choices per series.
43. ◻ Consistent empty states · ◻ focus memory everywhere.

## Diagnostics (Codex brief `BRIEF_0156_THUMBS_DIAGNOSTICS.md`)
44. ✅ Always full detail; reasons for every failure; real Android exit reasons; bigger log; playback, network and UI
    measurements; settings changes; clearer summary with top-20 errors.

## Codex work to bring in
45. ✅ DP-01…DP-07, TV-only stages and the rest of `BRIEF_0155_NEXT.md` as it is reviewed and re-tested.

## Waiting for the owner
- OMDb key built into the app (yes/no).
- Final looks on the ◻ items.
