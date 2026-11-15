# 0.157 coding: handover for a fresh session (15 Nov 2026)

Read `CLAUDE.md` first, then this. The owner approved coding 0.157 on 15 Nov. Keep replies short; work in small
batches; commit and push after each piece; update `docs/0.157/TIMELOG.md` (the owner is measuring real time).

## Scope and order
`docs/0.157/VERSION_PLAN.md`: "0.157 without the redesigns" (what is in) and "0.157 build order" (the sequence).
Roadmap picture: `docs/0.157/board/`. Design rules: `docs/design/RULES.md`. Owner decisions: `docs/0.157/OWNER_NOTES.md`
(§35–46 are the latest). Diagnostics findings: `docs/0.157/DIAGNOSTICS_2026-11-13.md`, `…-14.md`.

## Done so far (all pushed on `claude/supernova-github-access-yfhe89`)
- Codex's 0.157 work merged (parts A–C); its six Java dependency patches registered.
- Dependency changes go in as **hash-locked patches** in `.github/build/supernova155-dependency-patches.json` (the
  build refuses any other MediaLib/FileCore revision). Patch files under `.github/build/` are git-ignored: `git add -f`.
- Seek previews (retriever headers) and the hidden switch; NOVA 6.6.5+ library changes (matching by file name, SMB
  credentials, Trakt, Samba); manual match search by file name; software-decoding notice; decoder order.
- Recent scans store; no rebuild on an unchanged snapshot; 15-minute rest for automatic scans; background timer skips
  quietly; Recently Added; Skip button; memory by kind and page in diagnostics.

## In flight
- Conformance run 4 was started 15 Nov about 12:54 (`supernova-0156-conformance.yml`). Check it; on failure run
  `report-test-failures.yml` with `run_id` (prints unique compile errors and failing tests), fix, re-run. The app's own
  unit tests have not yet run on the 0.157 code, so expect some failures (e.g. tests that assumed old behaviour of
  Recently Added, the Skip prompt, scan scheduling).

## Not done yet
- NOVA app-side: duplicate Samba discovery at start-up (8a1a231), picture-subtitle position and clearance (e13f8a3,
  68e316f; with the Player work). Native: Codex's two avos patches and libdovi need a new engine archive build.
- Everything from step 5 of the build order on: memory cuts (after Shield numbers), focus restore, old NOVA out,
  foundation R0, Files (without Transfers), Details (portrait cast), Player, data items, version bump to 0.157,
  release notes (owner wants them detailed, one line each), final conformance, then the owner's Shield test.
- `main` still to bring up to date with 0.156: ask the owner before pushing `main`.

## Practical notes
- Usage: one 5-hour window gives about 30 minutes of work in a long conversation. Keep tool output small (never
  dump build logs; use the failure report).
- Commits: no AI model names (CLAUDE.md). Never sign without the owner asking.
