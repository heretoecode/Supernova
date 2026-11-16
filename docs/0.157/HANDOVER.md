# 0.157 coding: handover for a fresh session (15 Nov 2026)

Read `CLAUDE.md` first, then this. The owner approved coding 0.157 on 15 Nov. Keep replies short; work in small
batches; commit and push after each piece; update `docs/0.157/TIMELOG.md` (the owner is measuring real time).

## Scope and order
`docs/0.157/VERSION_PLAN.md`: "0.157 without the redesigns" (what is in) and "0.157 build order" (the sequence).
Roadmap picture: `docs/0.157/board/`. Design rules: `docs/design/RULES.md`. Owner decisions: `docs/0.157/OWNER_NOTES.md`
(§35–46 are the latest). Diagnostics findings: `docs/0.157/DIAGNOSTICS_2026-11-13.md`, `…-14.md`.

## Done so far (all pushed on `claude/supernova-github-access-yfhe89`)
- Codex's 0.157 work merged (parts A–C); its six Java dependency patches registered.
- Dependency changes go in as **hash-locked patches** in `.github/build/supernova155-dependency-patches.json` (the
  build refuses any other MediaLib/FileCore revision). Patch files under `.github/build/` are git-ignored: `git add -f`.
- Seek previews (retriever headers) and the hidden switch; NOVA 6.6.5+ library changes (matching by file name, SMB
  credentials, Trakt, Samba); manual match search by file name; software-decoding notice; decoder order.
- Recent scans store; no rebuild on an unchanged snapshot; 15-minute rest for automatic scans; background timer skips
  quietly; Recently Added; Skip button; memory by kind and page in diagnostics.

- 15 Nov, second session (not yet built: they go into the next conformance run): time-bubble chapter names numbered
  like the strip; "Scanning local library…" pill gives up on an import stuck for 3 minutes (`StalledWorkWatch`,
  logs `local_import_stalled`); reopen on the page and item you were on when Android ends the app (`ReopenMemory`;
  Back or a device restart opens Home); focus recovered after standby on the four main pages (`focus_recovered`);
  held Down/Up on Movies and TV Shows waits for the grid to settle.

- Old NOVA out (step 6), partly: **Issues Digest** removed from Settings; **picture in picture** no longer offered
  (menu choice gone, manifest says false; handling kept, see `docs/REVISIT_LATER.md`); **All files access** declared
  and asked for once (`PermissionChecker`, pref `supernova_all_files_access_asked`; refused → media permissions, no
  nagging). The request still uses NOVA's own dialog before Android's one-time screen: restyle it with the panels in
  step 7. Shield check needed: deleting a local file now goes straight through with no Android screen.
- The SAFE clean-out list does not exist yet: Codex's report (`docs/codex/findings/legacy-cleanup-candidates.md`) found
  nothing SAFE, only UNSURE. NOVA's hidden grid needs proof per route (see that report) before removal.

- R8 (new mark): **loading ring** redrawn as the mark (`foundation/DoubleRingDrawable`, used by the scan pill, Files,
  Home genre picker, playback loading); **icon, banner and splash** replaced (`design/brand-mark/`: run
  `export-mark.js` with Roboto, then `export-mark.py`). Not yet: the splash's ring spinning (it is a still picture),
  and the ring in the Files top-bar slot (comes with Files, step 8). `tools/export_foundation_branding.py` is the old
  glowing-ring export: do not run it.

- R0 started: one panel value (95%, `SupernovaStyle.GLASS`); the shared choice list (`PreviewDialog.choose`) uses it
  and gains automatic search above 12 items (`ChoiceListSearch`, keyboard beside the list via
  `PreviewTextInput.showLive`); group headings centred on a line in Title Case (`SupernovaStyle.groupHeading`).
  Also done: shared tab row `PageTabs` (focus and path), the artwork edge `ArtworkEdge` on every card (thick ring
  and 440 px when focused), Selected A–Z with the glide. Stacking look (`PreviewDialog.stackLook`) and focus and path on
  the top bar (`TopNavigation.refreshPath`) done. Still to do in R0: menu icons/ticks audit, the move gesture app-wide. Keyboard: Phone key and password eye done (Shield check with the phone app
  needed; if the phone app appears without Android's keyboard showing, the key could go).

- Step 11 (data), started: Where to Watch region from the device when none was chosen (services already saved
  under one country keep it; `StreamingRepository.defaultCountry`); Keep diagnostics for 1/2/4 weeks (default 2,
  `Diagnostics.KEEP_WEEKS`, pruned by age in `trimStorage`).

- Step 8 (Files): a new page, `leanback/files/FilesPage` (tabs via the shared `PageTabs`, summary facts
  `FilesSummary`). Library Care: Library Health rows (`LibraryCareCounts`, open `LibraryHealthActivity`), Scan one
  source, Recent scans (`RecentScanLine`). This Device: storage rows with usage bars (`DeviceLine`). Connections:
  rows with buttons on the focused row (`ConnectionLine`), Add a connection. Wired in `PreviewPages.filesLayout`
  with data from `FilesPageData`, **behind the preference `supernova_files_page` (default off)**. To finish: real
  reachability for Connections (Codex's `SavedConnectionReachability` needs drafts from the saved credentials; until
  then no pill shows), Check matches, Couldn't be read, Found on your network; move the old page's tests
  (Network142*, NextQaUi*) to the new page; then switch it on by default. Transfers waits (0.158).
  Also done for Files: the top-bar signal slot (`TopBarSignal`), the old floating pill retired, "Network & Files"
  renamed "Files".

- Step 9 (Details) started: portrait cast and crew cards (`PersonInitials`, `ArtworkEdge`); tab renamed Episodes
  in the master order Details · Episodes · Extras · More Like This; heroes' metadata row Age rating · Year · Length ·
  Genres (`SharedMetadata`); episode title line "Season 2 · Episode 8 · Title".

- Step 10 (Player) started, Playback Settings: Stop playing at (clock times; `stopLabels`), Play mode Stop after
  this · Play next · Repeat this · Repeat all (`SubtitlePlaybackBackend.playModes`), Skip intros and credits Off ·
  Show button · Automatic (`skipModes`), Still watching? with episodes and time (`StillWatchingChoices`, uses
  the existing idle-minutes setting); Preparing playback episode line and age badge (`PreviewPlaybackLoading`).
  Audio tracks with pills (`TrackDetails` + `PlaybackSubtitlePanel.pills`); age rating 8 s. Still to do: subtitle
  track pills (SDH, Forced; the menu has extra rows so indexes differ), lists on top of the panel without the back
  row, the rest of R5 (title block, seek preview card, skip button in the
  control row, track names and pills, subtitle lists stacked on the panel).

## Now: fix 0.157 against the approved designs (owner, 16 Nov)
- Source: `docs/0.157/REVIEW_0157.md` on `claude/project-discussion-planning-fccq9a` (26 Shield problems; an audit
  adds more). Order: shared pieces first, then pages. Each item is done only when rendered with
  `preview-renders.yml` and compared with its mock-up; note the render beside the item. Sign when all ticked and green.
- Discussion branch merged (REVIEW_0157.md in `1fe324b`). Progress per item: the "Fix progress" section at the end of
  REVIEW_0157.md. Render test classes: `Review0157RenderTest` (Files in the app frame),
  `settings/Review0157SettingsRenderTest`; request file `.github/diagnostics/preview-request.json` (pushing it runs
  `preview-renders.yml`; PNGs land on branch `previews/claude`).
- Done (code; ✓ = checked in a render, see REVIEW_0157 "Fix progress"): charcoal page frame ✓, top-bar focus
  ink ✓, Settings shared list ✓, Settings flat ✓ and rows on their centre line, no blue switches, Playback Settings
  rework ✓ (tabs, chips, plain Playback list, sized per tab, headings Title Case, For This Series at the bottom, series
  format+skipping save row), track lists stacked on the panel, Files tab row ✓ / margins ✓ / USB icon ✓ / Your Library
  tab / folder grid browser, Fix match page ✓ (renders `find-a-match`), Details tab v20 ✓ (three columns; tab row
  without the old box pending render), portrait cast, Details hero flicker, Preparing playback logo space, seek
  time card, chapter renumbering, player tab row, Episodes row text, centred chapter row.
- In flight: renders of 66e9fa1 (`preview-renders.yml`), conformance run 43855741788 (on a200b34, older). Next:
  check renders, then a conformance run on the latest commit, fix failures, record renders in REVIEW_0157, then
  sign (owner approved signing for this build) and report the APK.
- Open questions for the owner: QUESTIONS.md #10–#14.

## In flight (loop mode: owner asked for the rest of 0.157 unattended; decisions go in `QUESTIONS.md`)
- **Version is 0.157 (157)** since `571224b`; builds now use **`supernova-0157-conformance.yml`** (the 0.156 one
  checks for 0.156 and will fail). **Run 43813823463** (0.157 conformance on `571224b`) **passed** (16 Nov ~06:45). **Final run 43817914116 on `3c3ffbf` passed**
  (16 Nov ~07:15): the candidate for signing. If later commits are made, run conformance again before signing. On
  failure: `report-test-failures.yml` with `run_id`; log via `curl -sL api.github.com/repos/heretoecode/Supernova/
  actions/jobs/<job>/logs`; fix, push, dispatch `supernova-0157-conformance.yml` again.
- **Signing (owner approved, 16 Nov):** 0.157 copies added in `bfca787` (`supernova-0157-qa.yml`,
  `.github/signing/*0157*`; version strings only). Conformance **run 43839950017 on `bfca787`** passed; **signed by run 43846921936**
  of `supernova-0157-qa.yml`. APK: artifact `supernova-0157-signed-qa` (`Supernova_0.157.apk`, kept 14 days).
- Build-order items all coded except those skipped in QUESTIONS.md (#3 hidden grid, #4 memory cuts, #5 backup
  groups, #6 TV in Check matches, #7 Experimental translate, #8 chapter row (not approved)).
- Release notes: in `assets/foundation/release-history.json` (current 0.157), from `RELEASE_NOTES_DRAFT.md`; About
  shows "## " lines as group headings.
- Tests must not touch Android-static classes outside Robolectric. Tests of the old Files screens set
  `supernova_files_page` false.

## Not done yet
- **Dead connection still scanned** (diagnostics 14 Nov, finding 3): the source list is chosen inside MediaLib's
  scheduler, so skipping a source that fails at root resolution needs a new registered MediaLib patch; the "Not
  reachable" label arrives with Files (R6). Do both together in step 8.
- Standby focus on Settings and Search (separate screens) is not covered yet; a stuck local import still keeps the
  screen awake through `ActiveOperationMonitor` (use `StalledWorkWatch` there too).
- NOVA app-side: duplicate Samba discovery at start-up (8a1a231), picture-subtitle position and clearance (e13f8a3,
  68e316f; with the Player work). Native: Codex's two avos patches and libdovi need a new engine archive build.
- Everything from step 5 of the build order on: memory cuts (after Shield numbers), focus restore, old NOVA out,
  foundation R0, Files (without Transfers), Details (portrait cast), Player, data items, version bump to 0.157,
  release notes (owner wants them detailed, one line each), final conformance, then the owner's Shield test.
- `main` still to bring up to date with 0.156: ask the owner before pushing `main`.

## Practical notes
- Usage: one 5-hour window gives about 30 minutes of work in a long conversation. Keep tool output small (never
  dump build logs; use the failure report).
- Commits: no AI model names (CLAUDE.md). Never sign without the owner asking.
