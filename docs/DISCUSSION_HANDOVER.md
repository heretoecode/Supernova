# Discussion handover (updated 19 Nov 2026)

## 19 Nov — start here
- **0.158 (part A) signed and installed.** **Part B brief written and handed to Supernova Coding to run overnight:**
  `docs/0.158/BRIEF_0158_B.md` (new look first, then the 0.157.2 review; signs 0.158.1, .2 … as it goes).
- **Remind the owner, next session:**
  1. **Episodes and Guest:** owner said "remind me to get back to that". Decisions so far are recorded (episodes in
     `docs/0.158/OWNER_NOTES.md` §2, Guest in `docs/REVISIT_LATER.md` round 8); both need mock-ups.
  2. **0.158 part A checklist** for the owner's Shield review (include the Documentaries top bar, R2).
  3. **Diagnostics report from 0.158** (Drive › Supernova › Media › For Claude, or the inbox).


For a fresh **discussion** session (designs, ideas, planning; no coding). Read `CLAUDE.md`, then this, then
`docs/ROADMAP.md` and `docs/design/RULES.md`. The coding session is called **Supernova Coding**. This discussion
session's branch is `claude/design-planning-discussion-9poejm`; everything below is pushed there.

## Where things stand (18 Nov)
- **Installed:** 0.157.2 (code 1572). **Supernova Coding is building 0.158 part A** (`docs/0.158/BRIEF_0158_A.md`):
  top bar, one side margin, library pages, Home, Search are built with tests; its full checks are failing and being
  fixed; not signed yet. Its report: `docs/0.158/` on the working branch.
- **Owner's review of 0.157.2:** `docs/0.157/REVIEW_0157_2.md` (PP, PL, PS, DT, FL, R, X numbers), with the
  diagnostics read (crashes at 23:09 / 23:14 on 16 Nov explained: X1; new Scan this folder crash: X2; seek previews
  engine broken: X3). **This becomes 0.158 part B. Playback Settings is the worst; fix it first.** No brief for part B
  yet: write `docs/0.158/BRIEF_0158_B.md` from the review when the owner says go (Coding must merge this branch again
  first: part A only merged it up to 17 Nov 12:30).
- **Approved for part B (beyond bug fixes):** turn a connection off (FL3) and choose a version on Play (R5), mock-up
  `docs/design/proposals/review-0157-2/01` (3, 4); one row design on Details' Episodes / Extras / More Like This, More
  Like This in rows by service with the service's colour logo on the heading (DT5; same mock-up 1, 2). Decisions of
  18 Nov are in the review and in RULES (badge first everywhere; reopen on Home after a full close; Full description is
  the text alone).
- **Still to discuss:**
  1. **Titles in one house style instead of logo artwork:** owner leans to it (clean, no glow; likes "Modern block",
     styles Bold clean, Two-line, Modern bold). Decided 18 Nov: official logos for now, house style as the fallback (pick A–D still open); owner to send photos of badly placed logos. Next after that: the 0.158 part A checklist when the owner says the APK has landed.
  1b. **Background speckle** (roadmap To discuss): mock-ups `background-speckle/01`–`03`; owner to judge on the TV.
  2. Playback Settings panel colour (roadmap To discuss, P6/PS10).
  3. Anything else under To discuss on the roadmap (users and tags, episodes, network statistics, diagnostics health).
- **Page sheets** (build from these): `docs/design/pages/FILES.md`, `DETAILS.md`, `LIBRARY.md`, `HOME.md`, `SEARCH.md`.
  Player has no sheet yet; reference pictures `docs/design/proposals/player/26`–`28` and `player-current/01`.
- **Checklist for reviewing builds:** artifact https://claude.ai/artifact/JV3yiiYJexfrQC1Jxc2YpF (copy
  `docs/0.157/review-0157-2-checklist.html`).
- **How the owner reviews when Claude is unavailable:** dictates into iOS Notes, one note per page, photos, times of
  crashes; pastes them in later (or `docs/INBOX.md` / an "Inbox:" issue). Plus the diagnostics export.

## 18 Nov
- 0.157.2 signed and installed (run 44020349261): much better. Owner: redesign first, then fix bugs on settled designs.
- **0.158 = library pages, Home, Search** + the new top bar (approved, mock-ups `files-review-0157-1/08`, `09` A),
  one side margin (the top bar's edges), connections by server, the full Player review. Settings and onboarding: 0.159.
- **Page sheets ready for 0.158:** `docs/design/pages/LIBRARY.md`, `HOME.md`, `SEARCH.md` (plus FILES, DETAILS).
  The owner won't critique Home or the library pages until the redesigns are built.
- Next: the owner reads the sheets, then gives the go-ahead for 0.158; write its brief then.

## Earlier (16 Nov), still current
## Ready (designed and approved)
- **Media Transfers** — complete: `docs/0.158/MEDIA_TRANSFERS.md`, mock-ups `docs/design/proposals/transfers-settings/`.
- **Trakt list as a Home row** — `docs/0.158/OWNER_NOTES.md`, mock-ups `docs/design/proposals/trakt-list-row/` (option B).

## To discuss
- **Users and tags** (out of 0.158 until settled): notes in `docs/REVISIT_LATER.md` ("Profiles", "Tags and views", rounds
  1–7), mock-ups `tags-views/01`–`06-v6-*`. Approved so far: shared tags, record who watched, editable users and tags,
  in Backup, one feature, delete-a-user options, Default user, Together hides one-person titles, nudges (v5), "Watch
  without Alex?", seasonal Home row, suggested tags. Open: "Ask every time" default user, per-user watchlist, Guest view.
  Parked: Trakt per user, per-user playback settings.
- **Episodes** (OK plays, long-press menu, deleting earlier episodes) and **network statistics by source** (with Files ›
  Your Library): `docs/0.158/OWNER_NOTES.md`.
- **Diagnostics that catch problems before the owner notices** (health snapshot, smoothness, speed, playback, leaks,
  Health summary with limits): on the roadmap.
- **Feature blueprint** `docs/research/FEATURE_BLUEPRINT.md`: pick ideas to take forward.

## Rules added today (all in RULES.md / CLAUDE.md)
- Current decisions live in RULES.md; replaced diary entries are marked. A sweep for old contradictions is still to do.
- Small menus fold inline (≤ about five buttons on the row).
- Fix match is one page on Files; from a title it offers Match and return · Match and stay.
- Streaming service order: the top of Your services is used everywhere.
- Mock-ups: full size, up to about four screens per picture; whole flow; the one keyboard in the app.
- Diagnostics read before each version ends with what the owner didn't see, with fixes.
- Revisit later: captions under the player's cards (match Home rows); panels whose rows swap with a choice (app-wide).

## Practical
- **Media swap in Google Drive (owner, 18 Nov):** `Supernova › Media` has two folders.
  - **From Claude (to view on the TV):** mock-ups to judge full screen (via Solid Explorer), one dated subfolder per
    topic (e.g. "2026-11-18 Background speckle"), files numbered in viewing order; 4K (3840 × 2160) when fine detail
    matters. Small comparisons the owner can judge on the iPad are sent in chat instead.
  - **For Claude (screenshots and videos from the Shield):** the owner's own screen captures (the Shield records
    screenshots and videos exactly as shown, unlike a camera). The owner names the file; Claude downloads it, and cuts
    videos into frames with ffmpeg (about 5 a second). Ask what to watch for and roughly when.
  - The Claude app takes photos only, so videos always come this way. Photos plus a line for looks; video for
    behaviour (focus jumps, timing, flicker).
- Push check: after every push confirm `git ls-remote` matches HEAD (a restart once left commits unpushed).
- Owner's notes while Claude is unavailable: `docs/INBOX.md` or a GitHub issue titled "Inbox: …".
