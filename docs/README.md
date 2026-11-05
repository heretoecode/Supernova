# Supernova documentation

Start with [`/CLAUDE.md`](../CLAUDE.md) and [`/PROJECT_STATUS.md`](../PROJECT_STATUS.md). This page says what each
folder under `docs/` holds.

## Current

| Folder or file | What it holds |
| --- | --- |
| `0.156/RELEASE_LIST.md` | Everything in the next version, approved, with progress. |
| `0.155/OWNER_NOTES.md` | The owner's numbered reports and decisions (items 1–40). |
| `0.155/DIAGNOSTICS_2026-11-04.md`, `0.155/DATA_PROVIDERS.md` | Diagnostics reviews; every data provider and its fallbacks. |
| `REVISIT_LATER.md` | Parked ideas and deferred decisions. |
| `codex/` | Briefs for Codex, its reports and findings, `HANDS_OFF.txt`, `HANDOVER.md`, `REFORMAT_QUEUE.txt`. |
| `design/` | Design rules per area and every mock-up (`design/proposals/<topic>/`). |
| `issues/` | Source files for the GitHub Issues, labels and milestones. |
| `SUPERNOVA_BUILD_ARCHITECTURE.md` | How the repositories and the build fit together. |

## Version records

`0.135/` … `0.155/` hold each version's release report, QA and notes. The app's What's new page reads them through
`assets/foundation/release-history.json`, so **they must not move**.

## Reference

| Folder | What it holds |
| --- | --- |
| `foundation/` | Fork identity, branding and signing set-up (read by the signing workflow and tools). |
| `nova/` | Notes on the NOVA upstream code (read by some tests). |
| `audits/` | Settings and Network & Files audits (read by a tooling test). |
| `next-qa/`, `next-refinement/`, `next-version/`, `handovers/` | Working records from 0.148–0.154. |
| `release-notes/` | The HTML release-history builder's inputs. |
| `Future Development/` | The old FD-xxx idea register; open items are now Issues and `REVISIT_LATER.md`. |
| `MARK-EDITION-3.md`, `NOVA_Release_Notes.txt` | The original fork notes (read by the app's strings) and NOVA's own release notes. |

## History

[`archive/`](archive/README.md) — the Preview 3.x/4.x era, early planning (to 0.141) and the old root-level files.
Kept for the record; nothing in it is current.

The legacy `doc/` folder (no "s") is NOVA's inherited technical documentation.
