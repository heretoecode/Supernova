# Brief: 0.158 part B (for Supernova Coding), to run overnight

Approved by the owner, 19 Nov 2026. Part B = the new look (decided 18 Nov) plus the owner's review of 0.157.2
(`docs/0.157/REVIEW_0157_2.md`). The owner has 0.158 (part A) installed and will review it separately; their notes on
it come later, not in this brief.

## The aim for tonight
**The owner wakes up to a signed APK.** Follow CLAUDE.md › Working unattended to the letter:
- **Never sit idle while a GitHub build runs.** Dispatch the conformance run in the background, carry straight on with
  the next item, and look at the run only when it has finished. A failing run is read with
  `report-test-failures.yml`, fixed, and dispatched again, while other work carries on.
- **Don't stop for questions.** Use the rules, the page sheets and the review; write every judgement call down with its
  reason (`docs/0.158/JUDGEMENT_CALLS_0158_B.md`).
- **Push after every finished item.** Check `git ls-remote` matches HEAD after each push.
- **Sign as you go, so there is always something to install.** Signing has standing approval (CLAUDE.md). When a
  group below is finished and its conformance run is green, sign it: the first signing is **0.158.1** (code 1581),
  then 0.158.2 (1582) and so on if more groups are signed later in the night. The last signed APK is the one the owner
  installs; the report says what each one contains. If the night is nearly over, sign whatever is finished and green
  rather than nothing.

## Before you start
1. **Merge `claude/design-planning-discussion-9poejm` into the working branch.** It holds everything decided on 18
   Nov: this brief, RULES changes, the review notes, the mock-ups. Part A only merged it up to 17 Nov.
2. Read: `CLAUDE.md` (Definition of done, Working unattended); `docs/design/RULES.md` §1 (background), §3 (one shape
   for small boxes), §16 (Back and page position), §19 (titles); `docs/0.157/REVIEW_0157_2.md` (every PP, PL, PS, DT,
   FL, R and X item; "[ ]" marks Claude's recommendation, "Agreed" / "Decided" / "Approved" are the owner's); the page
   sheets in `docs/design/pages/`; `docs/ROADMAP.md` › 0.158 part B.

## Order of work (finish each group, with everything behind it, before the next)

### Group 1 — the new look (first, so everything after it is built and checked on it)
- **Speckled background app-wide** (RULES §1; `docs/design/proposals/background-speckle/tv3-2-current.png`, recipe in
  `tv3.html`): base `#111215`, about 20 grains per 1,000 pixels, mostly 7–22% white, about 1 in 7 at 45%, fixed
  seed, drawn once at the screen's own size, never smoothed, never moving. Through the shared page background
  (`PreviewUtilityBackground` paints charcoal today); pages that paint their own charcoal (Files, the folder browser,
  the top bar's solid strip, the library pages' fade under the top bar) use it too. **No pane behind lists.** Panels,
  menus and caption boxes stay solid. The player keeps the video behind it.
- **One shape for every small box** (RULES §3; `background-speckle/corners-A-B-C.png`, B): buttons, pills, chips and
  the focus and open boxes in tabs and the top bar: 52 px high on a row, **20 px corners**, about 22 px side padding.
  Rows, cards and heroes keep their own corners.
- **Text centred on its letters in every box** (the 0.158 top bar fix, applied everywhere with the new shape).
- **Look sweep:** render every page on the new background (`preview-renders.yml`) and fix only what now fails: faint
  row lines, grey second lines, "Unknown", the open-page grey box, outlined pills and chips at rest, panels over the
  page, the keyboard's keys, loading placeholders, dimmed rows. **Playback Settings panel colour** (P6 / PS10) is
  settled here: `#16181C` at 90%, no outline, no glow, no old blue (RULES). List every change in the report.

### Group 2 — Playback Settings (the owner's worst page; PS1–PS11)
Every item in the review: tab focus (PS1), gap under the tabs (PS2), every tab fits (PS3), focused-row switches,
pills and chips (PS4–PS6), subtitles on top at every position (PS7), the track list on top with the panel behind
darkened at about 96% and the same width (PS8), and all of PS11 (chips instead of ‹ › steppers, − + for Vertical
position, names on the two nameless rows, Audio boost chips, Extra large, Subtitle delay in Subtitle Selection, For
This Series wording, the Download subtitles list). Then PS9: render the Playback tab and tidy what is left.

### Group 3 — crashes and engine (from the diagnostics read, X1–X5)
X1 (guard the discovery listener), X2 (Scan this folder crash), X3 with PL7 (seek previews engine fixed and the
switch back where Settings shows it), X4 (scans queue instead of waiting on a lock). X5 is covered by the focus fixes
in the groups around it.

**Good point to sign 0.158.1** if groups 1–3 are green.

### Group 4 — Preparing playback and the player (PP1–PP3, PL1–PL8)
Badge first on the details line, no reasons line, space kept from the start (PP1, PP2); one title block for
Preparing playback and the HUD (PP3, PL1); clock font (PL2); gaps (PL3); controls linger (PL4); no stub chapters
under 10 s (PL5); card edges (PL6); skip button in line with the controls (PL8, `player/29`, `30`).

### Group 5 — Details (DT1–DT9, R3, R4, R5)
Details tab on one screen, one size for cast and crew portraits, values ("2 h 3 min", "$25 million", "Arabic"),
tab focus, rows running off the edges, gaps (DT1–DT4, DT9); **one row design for Episodes, Extras and More Like
This, More Like This in rows by service with the service's colour logo** (DT5, `review-0157-2/01` 1–2); artwork
side panel (DT6); full description is the text alone (DT7); Fix match in More (DT8); season artwork (R3); season More
menu beside its button with Delete season (R4); **choose a version on Play** (R5, `review-0157-2/01` 4).

### Group 6 — titles (RULES §19)
Official logo with the **placement fix** (`title-style/05-logo-placement.png`: empty space trimmed, same area inside
900 × 240, foot 24 px above the details line, soft shade from the left); titles without a logo in **house style A**
with C's colon split (`title-style/04-house-style-lengths.png`).

### Group 7 — Files (FL1–FL12)
Content clipped below the toolbar and tabs (FL1); loading placeholders back (FL2); **turn a connection off** with the
smaller buttons (FL3, FL5; `review-0157-2/01` 3); connection form on one screen (FL4); FL6–FL12 as written (FL10
agreed: focus moves through the scans; FL12 decided: closed fully → Home).

### Group 8 — behaviour and leftovers
**Back and page position** (RULES §16): Back goes straight to the top bar; a page left for another one reopens at its
top; poster → Details → Back returns to the poster. R2: if the own library page still shows a solid bar under the
top bar, make it match Movies and TV Shows. The icon picker tile width (part A's "not built").

## Not in part B
Users and tags, Guest, episodes (OK plays, long-press, deleting earlier episodes): still being designed. Settings and
onboarding (0.159). The Trakt list row and Media Transfers (Ready, no version yet).

## Finish with
`docs/0.158/REPORT_0158_B.md`: what each signed APK contains, the look-sweep changes, the "not built" list (plain,
for the owner, before they install), the judgement calls file, the run links; update `PROJECT_STATUS.md` and the
roadmap. The owner will send a fresh diagnostics report from 0.158; it is read in the next session.
