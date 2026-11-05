# Supernova — project status

Updated 5 November 2026. Keep this page short and current: update it at the end of every session.

## Released

**0.155** — signed 4 November 2026 (run 41922310251 of `supernova-0155-qa.yml`, from a green conformance run on
`claude/supernova-github-access-yfhe89`). Installed on the owner's Shield and confirmed working. Highlights: NOVA
6.6.4 engine, the new native stack, Settings and Network & Files groundwork, IntroDB skipping.

## Next: 0.156 (in progress)

All 45 items are approved: [`docs/0.156/RELEASE_LIST.md`](docs/0.156/RELEASE_LIST.md) (see its Progress section)
and the GitHub milestone **0.156**. The owner gave the go-ahead to code on 5 November 2026.

### In progress

- **Claude** (screens, on `claude/supernova-github-access-yfhe89`): done so far — Local Storage focus/header/status
  tags/watched pill, Settings single-panel shell and new sections, Edit Page, top bar fade, Details card, Skip
  button, shared choice list, hero readability and logo, Home row Hide/Move, Seek previews switch, Playback Settings
  icon and swatch fixes, Download Subtitles in the panel. Next: Customise Home, Search, Network & Files pages,
  Settings System/About pages, player controls and the Chapters · Episodes strip.
- **Codex** (engine and data): finished Parts A–D and B17a of
  [`docs/codex/BRIEF_0156_CODE.md`](docs/codex/BRIEF_0156_CODE.md) on `codex/0155-code-2026-11-04` (a5e4d5f97);
  merged into the working branch on 5 Nov 2026 (11ed71d44). Build and Shield checks pending. Claude now wires the
  screens to Codex's APIs (`docs/codex/findings/0156-ui-hooks.md`).
- **GitHub:** cleaned up on 5 Nov 2026: this page, `CLAUDE.md`, `AGENTS.md`, the archive, and Issues #31–#102.

### Known problems being fixed in 0.156

- Put.io: after pausing for a few minutes, resuming can glitch the picture and put audio out of sync, or fail
  (Codex Part B 17a; until then, seeking back re-syncs).
- One library source fails every scan with no reason recorded; the network scan can get stuck (Codex Part A).
- Backup "could not be prepared" (Codex Part A).
- Diagnostics recorder crash in `trimStorage` (Codex Part A).

## Waiting for the owner

- **Bring `main` up to date** — `main` still holds the pre-0.140 code; it needs a pull request from the working
  branch that the owner approves.
- **Archive `heretoecode/aos-AVP`** (not used by any build) in its repository settings.
- **Decisions:** Smart Collections; Put.io improvements; whether Where to Watch should open the service's app.
  These are GitHub Issues labelled `decision-needed`.
- Parked for later: the Details page redesign and library statistics colouring
  ([`docs/REVISIT_LATER.md`](docs/REVISIT_LATER.md)).

## History

- Every version's records: `docs/0.135/` … `docs/0.155/` (these also feed the app's What's new page).
- The older status page (0.140–0.142 and the Preview 4.1.7 baseline):
  [`docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md`](docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md).
- Older Preview-era files and early planning: [`docs/archive/`](docs/archive/README.md).
