# Discussion handover (updated 17 Nov 2026, early hours)

For a fresh **discussion** session (designs, ideas, planning; no coding). Read `CLAUDE.md`, then this, then
`docs/ROADMAP.md` and `docs/design/RULES.md`. The coding session is called **Supernova Coding**.

## Where things stand (17 Nov)
- **0.157.1** (code 1571) is installed: much better, not right yet. The owner reviewed **Files** and **Details** in
  full and the **Player** briefly: `docs/0.157/REVIEW_0157_1.md` (F1–F52, D1–D20, P1–P5).
- **Next: 0.157.2, bug fixes only** (plus the Home and library background). Brief for Supernova Coding:
  `docs/0.157/BRIEF_0157_2.md`. It starts by merging this branch (`claude/design-planning-discussion-9poejm`).
- **New way of working (CLAUDE.md, 17 Nov):** the Definition of done, and **page sheets** in `docs/design/pages/`
  (Files, Details so far): every element and every action with where it leads. Reviewing a page: send its latest
  mock-ups and its checklist first, one page at a time.
- Still to review on the Shield: the Player properly, then the shared pieces. Settings, Home, library pages, Search and
  onboarding wait for their 0.158 redesigns.
- Waiting for the owner: the diagnostics report (crashes about 23:09 and 23:14, blank screen about 23:39, 16 Nov);
  P4 (Playback Settings size); go-ahead to sign 0.157.2 when it is ready.

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
- Push check: after every push confirm `git ls-remote` matches HEAD (a restart once left commits unpushed).
- Owner's notes while Claude is unavailable: `docs/INBOX.md` or a GitHub issue titled "Inbox: …".
