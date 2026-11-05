# Supernova — project status

Updated 5 November 2026 (evening). Keep this page short and current: update it at the end of every session.

## Released

**0.155** — signed 4 November 2026 (run 41922310251 of `supernova-0155-qa.yml`, from a green conformance run on
`claude/supernova-github-access-yfhe89`). Installed on the owner's Shield and confirmed working. Highlights: NOVA
6.6.4 engine, the new native stack, Settings and Network & Files groundwork, IntroDB skipping.

## Next: 0.156 (in progress)

All 45 items are approved: [`docs/0.156/RELEASE_LIST.md`](docs/0.156/RELEASE_LIST.md) (see its Progress section)
and the GitHub milestone **0.156**. The owner gave the go-ahead to code on 5 November 2026.

### In progress

- **Claude** (screens, on `claude/supernova-github-access-yfhe89`): see the release list's Progress section. Done
  on 5 Nov: the whole player list (remote scheme, first-play tip, HUD look, Chapters · Episodes strip, subtitle
  position and live sample, sleep timer, Still watching?, seek pictures, per-series choices), Search and the shared
  keyboard, the Network & Files landing. Next: Network Shares and Add Connection, Library Health page, Settings
  stage 3, Customise Home, Fix match, empty states, Local Storage grid.
- **Codex** (engine and data): final-brief repairs through `ace2f7eee` on `codex/0155-code-2026-11-04`.
  The eleven restore failures and two scan-context fixtures are repaired; the replacement local-trace patch needs
  Claude's registration in place of the earlier compile-only fix. All 1,237 production/generated Java files and
  429 test sources compile locally; 137 selected regressions and the full module suites pass (four existing
  private-data/server checks remain unavailable). The item-by-item audit, explicit remaining
  coverage limits and NEXT/MORE deferrals are in
  [`final engine report`](docs/codex/reports/2026-11-05-0156-final.md). Device checks are in
  [`Shield engine checklist`](docs/0.156/SHIELD_CHECKLIST_ENGINE.md). The owner's source-specific scan diagnosis,
  actual private-library acceptance and native measurements are not claimed complete.
- **GitHub:** cleaned up on 5 Nov 2026: this page, `CLAUDE.md`, `AGENTS.md`, the archive, and Issues #31–#102.

### Known problems being fixed in 0.156

- Put.io: after pausing for a few minutes, resuming can glitch the picture and put audio out of sync, or fail
  (Codex Part B 17a; until then, seeking back re-syncs).
- One library source fails every scan with no reason recorded; the network scan can get stuck (Codex Part A).
- Backup "could not be prepared" (Codex Part A).
- Diagnostics recorder crash in `trimStorage` (Codex Part A).

## Waiting for the owner

- Parked for later: the Details page redesign and library statistics colouring
  ([`docs/REVISIT_LATER.md`](docs/REVISIT_LATER.md)).

## History

- Every version's records: `docs/0.135/` … `docs/0.155/` (these also feed the app's What's new page).
- The older status page (0.140–0.142 and the Preview 4.1.7 baseline):
  [`docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md`](docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md).
- Older Preview-era files and early planning: [`docs/archive/`](docs/archive/README.md).
