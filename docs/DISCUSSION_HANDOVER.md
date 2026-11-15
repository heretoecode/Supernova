# Discussion handover: roadmap, parked ideas and the tags feature (15 Nov 2026)

For a fresh **discussion** session (designs, ideas, planning). Coding of 0.157 runs in a separate session from
`docs/0.157/HANDOVER.md`; do not code from a discussion session. Start by reading `CLAUDE.md`, `PROJECT_STATUS.md`,
this file and `docs/REVISIT_LATER.md`.

## Where the project stands
- Every redesign is designed and approved (Search, Files, Details, Player, choice list, library pages incl. empty
  pages and the + page, Home, Settings, onboarding, the new mark and loading ring). Rulebook: `docs/design/RULES.md`.
- 0.157 coding started 15 Nov (owner's go-ahead). Time is being measured in `docs/0.157/TIMELOG.md`.
- Codex is not used for now; the owner may bring it back if usage becomes the bottleneck.

## Roadmap (picture: `docs/0.157/board/version-board.png`, script beside it; keep it current)
**Undecided:** nothing. The owner may still move items between columns.

**0.157** (`docs/0.157/VERSION_PLAN.md`, sections "0.157 without the redesigns" and "0.157 build order"):
- Redesigns: foundation R0 (panels, menus, choice list, focus, headings, artwork edge, move gesture, the one keyboard
  with phone-app paste and password eye), the new mark (icon, banner, splash, loading ring), Files (without
  Transfers), Details (with portrait cast and crew), Preparing playback, the Player and Playback Settings.
- Engine: Codex merge (done), all of NOVA 6.6.5+ (library part done), seek loading signal.
- Old NOVA out: SAFE list, hidden grid and screens, picture in picture, Issue digest, All files access once.
- Speed and reliability: memory, no rebuild on unchanged scans, scan rules, reopen where you were, focus after
  standby, held Down, network-drop and Put.io checks.
- Every bug from diagnostics and the walkthrough; data items (subtitles plan, UK age ratings, reception offline,
  technical info, calmer hero backdrops, region from device, sort titles, diagnostics retention, backup groups,
  licences); 0.157 release notes written properly (grouped, more detail, one line per point; approved style in
  `docs/design/proposals/0157-checks/01`).

**0.158:** Home, library pages, Search, Settings, onboarding, **Media Transfers (all of it)**, and **users and tags**
(pinned, still in discussion).

**Parked** (`docs/REVISIT_LATER.md` is the parked list): newer NOVA engine stack; make the repo private and a private
Releases page; "going public" checklist incl. Supernova's own TMDB key; release-notes rethink for older versions;
screensaver; picture in picture could return; documentation website; built-in OMDb key; discovery in Search; the old
concept-only ideas (launcher channels, person pages, more than one custom library page, anime metadata, subtitle
presets, Trakt social, AI, themes, content warnings, Drive delivery).
- **Rule (owner, 15 Nov):** "park that" adds to the parked list; anything the owner raises that matches a parked idea
  is added to that idea's notes, so each builds into a proper discussion.

## Media Transfers (moved whole to 0.158)
Everything decided, plus open questions for the owner: `docs/0.158/MEDIA_TRANSFERS.md`.

## Users and tags (pinned for 0.158, in discussion) — replaces "profiles"
Full notes: `docs/REVISIT_LATER.md`, "Profiles" and "Tags and views instead of profiles". Mock-ups
`docs/design/proposals/tags-views/01`–`03` (03 is the whole flow, 13 screens).
- **Why:** the owner mostly watches alone, sometimes with a partner; titles are mine, my partner's or shared, and
  nothing must be walled off. No separate folders, ideally no profiles.
- **Tags:** two kinds. **User tags** (Me, Alex, Together) say whose a title is; **general tags** (Christmas, Classics)
  say what kind. A title can have many tags. Tag from long-press, from Details › More › Tags, or many at once in a
  library select mode ("Select all" then untick; "Tag 12 titles").
- **Filters:** library Filters show Users and Tags groups; OK on a tag cycles **Off › Hide › Only** (like the order
  button flipping).
- **User switcher:** "SUPERNOVA" leaves the top bar; a fixed-size round icon at the far left, left of Home, shows the
  current user's own monochrome icon (each user picks one from the one app-wide icon picker, extended with new
  icons; Together defaults to two overlapping people). Its menu: Everything, each user, Together, Edit users and
  views. Switching applies everywhere: Home hero and rows, every library page, Continue Watching; page filters work on
  top of it; Search always finds everything.
- **Users are managed only from the user switcher** (create, edit name and icon, delete with warning). Elsewhere users
  can only be picked. **Default user per installation**, changeable ("Open Supernova as").
- **Tags show on the Details hero card** itself; "New tag" opens the one keyboard in its own panel beside the list.
- **Known limit:** one shared watch history. It only matters for titles both people watch separately.
- **Open questions (owner to answer):** (1) can a user's view also hide general tags (e.g. Me hides Classics)? Claude:
  yes. (2) per-user tags (seen only by their owner) as well as global tags, or all tags shared?
- Dropping the name also leaves room for a fourth library page one day (parked FD-017).

## Mock-up rules the owner added recently (also in CLAUDE.md)
- Whole flow: show every screen the drawn pieces lead to.
- Whole picture, light-grey frame, captions only, fewest pictures, made-up names, "this device" (never "Shield").

## Practical
- Usage: long conversations burn the 5-hour allowance fast; keep sessions short and start fresh often. Discussion and
  coding sessions share one allowance.
- Owner's notes while Claude is unavailable: `docs/INBOX.md` or a GitHub issue titled "Inbox: …".
