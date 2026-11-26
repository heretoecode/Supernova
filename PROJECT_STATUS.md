# Supernova — project status

Updated 26 November 2026 (discussion: Search done; Files review rounds 1–2 agreed, paused at round 3). Keep this page short and current: update it at the
end of every session.

## Next: the rest of 0.158 part B

Report, "not built" list and run links: `docs/0.158/REPORT_0158_B.md`; judgement calls:
`docs/0.158/JUDGEMENT_CALLS_0158_B.md`. Still to do from `docs/0.158/BRIEF_0158_B.md`: the look sweep on the speckle,
the rest of Details (DT1, DT3–DT6, DT8, R3–R5, episodes), titles (group 6), Files (group 7), Back and page position
(group 8), Settings' remaining sheet items (in-row backup flow, OpenSubtitles and Trakt panels), PL8, X4. Work branch
for part B: `claude/build-0158-part-b-wsupv2`, merged onto the working branch. Waiting for the owner: install the last
signed APK; send a diagnostics report from it.

## Discussion (19–26 Nov): polish phase
Reviewed and written up: library pages, Home, Details, Player, Search (page sheets in `docs/design/pages/`; decisions
in `docs/0.158/BRIEF_0158_B.md`). Files in review (rounds 1–2 agreed, resume at round 3); then Settings. NOVA fully
audited 25 Nov (eleven items in the brief). Installed on the Shield: 0.158.7. Handover:
`docs/DISCUSSION_HANDOVER.md` (start at "25 Nov — START HERE").

## Discussion (19–20 Nov): polish phase
The owner is reviewing page by page (library pages first), improving what exists; no new features (CLAUDE.md).
Every decision is in RULES.md, the page sheets and `docs/0.158/BRIEF_0158_B.md` for the next build (which adds the
diagnostics upgrade). 0.159 is the NOVA clean-out. Handover: `docs/DISCUSSION_HANDOVER.md` (start at "19–20 Nov
evening").

## Released

**0.158.3** (code 1583) — signed 19 Nov 2026 (run 44506040551, from green conformance run 44500505615 on `4d310f4`).
Artifact `supernova-0157-signed-qa` (`Supernova_0.158.3.apk`). 0.158.2 plus the Settings redesign (seven tabs), DT4,
DT8, stronger row lines, pages reopening at their top. The one to install.

**0.158.2** (code 1582) — signed 19 Nov 2026 (run 44500001166, from green conformance run 44493050022 on `8b8a8ff`).
Artifact `supernova-0157-signed-qa` (`Supernova_0.158.2.apk`). 0.158.1 plus the title block, player and Details fixes.

**0.158.1** (code 1581) — signed 19 Nov 2026 (run 44479811283 of `supernova-0157-qa.yml`, from green conformance run
44470632446 on `731261f`). Artifact `supernova-0157-signed-qa` (`Supernova_0.158.1.apk`). Part B groups 1–3b: the
speckled background, one shape for small boxes, Playback Settings (PS1–PS11), crash fixes X1–X3, the Home and Details
hero one size with dots.

**0.158** (code 1580) — signed 19 Nov 2026 (run 44426716646 of `supernova-0157-qa.yml`, from green conformance run
44421862282 on `6d79049`). Artifact `supernova-0157-signed-qa` (`Supernova_0.158.apk`). Part A of
`docs/0.158/BRIEF_0158_A.md`: the 0.158 top bar, one side margin, the library pages, Home (rows, Move and Hide,
Customise Home, hero), Search, and F52 (connections grouped by server). Report and "not built" list:
`docs/0.158/REPORT_0158_A.md`; judgement calls: `docs/0.158/JUDGEMENT_CALLS_0158_A.md`. Waiting for the owner:
install and check on the Shield; send the latest diagnostics report.

**0.157.2** (code 1572) — signed 18 Nov 2026 (run 44020349261 of `supernova-0157-qa.yml`, from green
conformance run 44015287084 on `c3f75d5`). Artifact `supernova-0157-signed-qa` (`Supernova_0.157.2.apk`). Bug fixes from the owner's Shield review of 0.157.1
(`docs/0.157/REVIEW_0157_1.md`), built overnight from `docs/0.157/BRIEF_0157_2.md` and the page sheets. Report:
`docs/0.157/REPORT_0157_2.md` (fixed, not built, judgement calls in `docs/0.157/JUDGEMENT_CALLS_0157_2.md`).
Waiting for the owner: install and check on the Shield; send the latest diagnostics report (not in the inbox).

**0.157.1** (code 1571) — signed 16 Nov 2026 (run 43960203498 of `supernova-0157-qa.yml`, from green conformance run
43952836182 on `5f5f9f6`). Artifact `supernova-0157-signed-qa` (`Supernova_0.157.1.apk`). 0.157 plus every fix from the Shield
review (`docs/0.157/REVIEW_0157.md`). Waiting for the owner to install it.

**Numbering (owner, 16 Nov 2026):** point releases are allowed; version codes are version × 10 from 0.157.1 on
(0.157.1 = 1571, 0.158 = 1580). **0.157.1** = 0.157 plus the Shield review fixes (`docs/0.157/REVIEW_0157.md`).

**0.157** — signed 16 November 2026 (run 43846921936 of `supernova-0157-qa.yml`, from green conformance run
43839950017 on `bfca787`). Artifact `supernova-0157-signed-qa` (`Supernova_0.157.apk`). Waiting for the owner to
install it on the Shield. Status of each item: `docs/0.157/HANDOVER.md`; open decisions: `docs/0.157/QUESTIONS.md`.

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
- **Codex:** 0.157 Part C source handoff (8 Nov), source through `c359a5c3b`, latest source merged at
  `ceb128fdc`. Library-gated search, streaming master switch, Details data and wrong-match APIs delivered;
  **37 host tests pass**, new Android/JSON cases on API 23/30. Explicit gaps: uncompiled production
  FixMatchRepository hook, screen adoption and owner diagnostics/company/Shield acceptance. Newer native stack remains last, not started. Same report `docs/codex/reports/2026-11-06-0157.md`;
  APIs in `docs/codex/findings/0157-ui-hooks.md`. Branch `codex/0157-2026-11-06`; no release/signing claimed.

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

### 0.157 coding (started 15 Nov 2026)
- **Version 0.157 (157)**; every build-order item coded except those skipped with reasons in
  `docs/0.157/QUESTIONS.md`. Release notes are in the app (About › What's new), drawn from
  `docs/0.157/RELEASE_NOTES_DRAFT.md`.
- Builds: `supernova-0157-conformance.yml`. Final run in progress; see `docs/0.157/HANDOVER.md`.
- **Waiting for the owner:** signing 0.157 (QUESTIONS.md #9: the signing gate only accepts 0.156), and the other
  questions in that file.
- Build order and batches: `docs/0.157/VERSION_PLAN.md` (end).

### Where things stand (14 Nov 2026, end of the design phase)
- Every redesign is designed and approved, including the portrait cast cards and the release notes style
  (owner, 14 Nov; owner notes §43).
- What goes in which version: `docs/0.157/VERSION_PLAN.md` and the board `docs/0.157/board/version-board.png`.
  0.157 = foundation, Files, Details, Preparing playback, Player and Playback Settings, new mark, and all fixes and
  under-the-bonnet work. 0.158 = Home, library pages, Search, Settings, onboarding, NOVA 6.6.5+, Transfers part two.
- Open: whether Codex returns for 0.157. Before coding: the latest diagnostics report; merge Codex's 13 items;
  bring `main` up to date with 0.156.
- Owner's notes while Claude is unavailable: `docs/INBOX.md` or a GitHub issue titled "Inbox: …".

### Design status (14 Nov 2026)
Every page is designed and approved: Search, Files (with Transfers and Your Library), Details, Player and Playback
Settings, the shared choice list, library pages (incl. empty pages), Home, Settings, onboarding (14 Nov) and the new
mark and loading ring (14 Nov). Split between 0.157 and 0.158: `docs/0.157/VERSION_PLAN.md`, waiting for the owner. Records: `docs/0.157/OWNER_NOTES.md` §26–33,
`docs/0.157/SETTINGS_INVENTORY.md`, `docs/design/RULES.md`. Next: coding 0.157 when the owner gives the go-ahead
(diagnostics first).

### Design rulebook (12 Nov 2026)
Every agreed design rule is in [`docs/design/RULES.md`](docs/design/RULES.md); pages are built from it. Re-tick of
older designs against the newer rules, in six steps: 1 Search (done), 2 Files (done), 3 Details (done), 4 Player (done), 5 the shared
choice list (done), 6 Customise Home and Edit Page (moved into the Home and library-page redesigns, owner 13 Nov). Next:
the Settings, Home and library-page redesigns, built from the rulebook.

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

