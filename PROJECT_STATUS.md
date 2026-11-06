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
  Next: a fully green 0.156 build, then signing **only when the owner says so**, then the owner's Shield check with
  [`docs/0.156/SHIELD_CHECKLIST.md`](docs/0.156/SHIELD_CHECKLIST.md).
- **Not done in 0.156 (flagged to the owner):** focus memory after Android recreates the app (audit F1, F6, F8, F10–F13)
  and F2 (Down from the top bar), which conflicts with the older "first control" rule.
- **If Claude is unavailable:** Codex can finish the 0.156 build using `docs/codex/BUILD_0156_STANDIN.md`.
- **Codex:** 0.156 finished and merged. Now on 0.157 (`docs/codex/BRIEF_0157.md`, branch `codex/0157-2026-11-06`).
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
