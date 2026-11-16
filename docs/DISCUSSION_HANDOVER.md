# Discussion handover (updated 16 Nov 2026, evening)

For a fresh **discussion** session (designs, ideas, planning; no coding). Read `CLAUDE.md`, then this, then
`docs/ROADMAP.md` and `docs/design/RULES.md` (current decisions, design and behaviour; the owner notes are history).
The coding session is called **Supernova Coding** (owner's name for it).

## Where things stand (16 Nov evening)
- **0.157** was signed and installed; the owner's Shield test found much of the approved redesign missing (passing
  tests ≠ matching mock-ups). Findings: `docs/0.157/REVIEW_0157.md` (26 points + answers). Supernova Coding is fixing
  them, checking each screen in a real render (`preview-renders.yml`) before ticking it, and will ship **0.157.1**
  (version code 1571; codes are version × 10 from now on, RULES §24). Signing for 0.157.1 is pre-approved by the owner.
- Roadmap is now a document, `docs/ROADMAP.md` (picture retired). **Send it to the owner in the app after every change.**
- Undecided is split into **Ready** (designed and approved, needs a version) and **To discuss**.

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
