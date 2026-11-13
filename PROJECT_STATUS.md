# Supernova — project status

Updated 6 November 2026 (night). Keep this page short and current: update it at the end of every session.

## Released

**0.155** — signed 4 November 2026 (run 41922310251 of `supernova-0155-qa.yml`, from a green conformance run on
`claude/supernova-github-access-yfhe89`). Installed on the owner's Shield and confirmed working. Highlights: NOVA
6.6.4 engine, the new native stack, Settings and Network & Files groundwork, IntroDB skipping.

## Next: 0.156 (in progress)

All 45 items are approved: [`docs/0.156/RELEASE_LIST.md`](docs/0.156/RELEASE_LIST.md) (see its Progress section)
and the GitHub milestone **0.156**. The owner gave the go-ahead to code on 5 November 2026.

### In progress

- **Claude:** every 0.156 screen is built (6 Nov): Network Shares (Put.io row, Edit/Test/Remove), Add Connection
  types and Test connection, Fix match side panel, Audio & Subtitles Style tab, Backup three choices, About credits
  and version history, Details for files not in the library, and the in-session focus-memory fixes from Codex's audit.
  Build and signing now use `supernova-0156-conformance.yml` / `supernova-0156-qa.yml` (version 0.156, code 156).
  **Built and fully green (6 Nov 2026):** conformance run 42226746764 on commit 9fae8eccb — every app, MediaLib,
  FileCore and tooling test passed, lint clean. Unsigned artifact `supernova-0156-unsigned-candidate`.
  **Signed 6 Nov 2026** (owner approved) by run 42239912490 of `supernova-0156-qa.yml`, from green conformance run
  42233995251 on commit c3d7e6e97. Signed APK: artifact `supernova-0156-signed-qa` of that run. Note: the signing gate
  allows only files under `docs/` to change after conformance (not root files such as this one).
  Next: the owner's Shield check with
  [`docs/0.156/SHIELD_CHECKLIST.md`](docs/0.156/SHIELD_CHECKLIST.md).
- **Not done in 0.156 (flagged to the owner):** focus memory after Android recreates the app (audit F1, F6, F8, F10–F13)
  and F2 (Down from the top bar), which conflicts with the older "first control" rule.
- **If Claude is unavailable:** Codex can finish the 0.156 build using `docs/codex/BUILD_0156_STANDIN.md`.
- **Codex:** 0.156 finished and merged. Now on 0.157 (first partial delivery 6 Nov, `71f7818a9`: 13 of 23
  items done) and **continuation handed back 6 Nov evening** (`bff775a35`): 19 of 23 done; #15 Key information not
  fully done, #20 seek loading engine delivered (Shield acceptance pending), #21 newer NOVA not done, #23 legacy
  removal waiting for owner approval; 349 host tests pass; eight add-only MediaLib patch registrations. Report
  `docs/codex/reports/2026-11-06-0157.md`. Not yet merged: Claude merges when 0.157 coding starts. Brief `docs/codex/BRIEF_0157.md`,
  branch `codex/0157-2026-11-06`.
- **0.157:** decided items in [`docs/0.157/OWNER_NOTES.md`](docs/0.157/OWNER_NOTES.md), including the Files page
  redesign (§16; mock-ups `docs/design/proposals/network-files-tabs/`, principles `docs/design/FLAT_PAGE_PRINCIPLES.md`).

## Waiting for the owner

- Parked for later: the Details page redesign and library statistics colouring
  ([`docs/REVISIT_LATER.md`](docs/REVISIT_LATER.md)).

## History

- Every version's records: `docs/0.135/` … `docs/0.155/` (these also feed the app's What's new page).
- The older status page (0.140–0.142 and the Preview 4.1.7 baseline):
  [`docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md`](docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md).
- Older Preview-era files and early planning: [`docs/archive/`](docs/archive/README.md).

### Design rulebook (12 Nov 2026)
Every agreed design rule is in [`docs/design/RULES.md`](docs/design/RULES.md); pages are built from it. Re-tick of
older designs against the newer rules, in six steps: 1 Search (done), 2 Files (done), 3 Details (done), 4 Player (done), 5 the shared
choice list (done), 6 Customise Home and Edit Page. Then the Settings, Home and library-page redesigns.

### Mock-ups still to make (list agreed 11 Nov 2026)
1. Settings: tab arrangement and each tab; the shared choice list (white tick, flags); Backup; Diagnostics; About
   (release notes, licences, credits).
2. ~~Tabs style~~ — done 12 Nov: the focus-and-path rule (`tab-style/08`–`15`).
3. Home on charcoal: hero with capped description and carousel dots, rows with the artwork edge, row arrow spacing,
   Hide/Move buttons, move mode.
4. Library pages on charcoal: posters with the edge, captions, toolbar, filters and sort menus, long-press menu
   (Fix match, Remove from library, Delete file), list view.
5. Details by kind: film, series, season, episode (what each shows and offers); More menu; artwork picker.
6. ~~Search~~ — approved 12 Nov (`search-flat/17`–`21`, 0.157 notes §26–27).
7. Player: title block (option C) with age rating; Playback Settings rows (speed, Stop playing at, Still watching,
   play mode); Download Subtitles as a replacing panel; seek spinner.
8. Top bar: "Files" with scan ring and the attention mark.
9. Delete file: one confirmation. Fix match as a page reached from the library.
10. Launcher icon/banner and splash on the new look.
11. Edit Page and Customise Home in the new panel look.

