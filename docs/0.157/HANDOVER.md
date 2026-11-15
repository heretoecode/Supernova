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

- 15 Nov, second session (not yet built: they go into the next conformance run): time-bubble chapter names numbered
  like the strip; "Scanning local library…" pill gives up on an import stuck for 3 minutes (`StalledWorkWatch`,
  logs `local_import_stalled`); reopen on the page and item you were on when Android ends the app (`ReopenMemory`;
  Back or a device restart opens Home); focus recovered after standby on the four main pages (`focus_recovered`);
  held Down/Up on Movies and TV Shows waits for the grid to settle.

- Old NOVA out (step 6), partly: **Issues Digest** removed from Settings; **picture in picture** no longer offered
  (menu choice gone, manifest says false; handling kept, see `docs/REVISIT_LATER.md`); **All files access** declared
  and asked for once (`PermissionChecker`, pref `supernova_all_files_access_asked`; refused → media permissions, no
  nagging). The request still uses NOVA's own dialog before Android's one-time screen: restyle it with the panels in
  step 7. Shield check needed: deleting a local file now goes straight through with no Android screen.
- The SAFE clean-out list does not exist yet: Codex's report (`docs/codex/findings/legacy-cleanup-candidates.md`) found
  nothing SAFE, only UNSURE. NOVA's hidden grid needs proof per route (see that report) before removal.

## In flight
- Conformance run 4 (15 Nov 12:54) compiled and failed with 10 of 2,145 tests; all ten are fixed (`34d07ab`).
  Run 5 (43745440241) was started 15 Nov 18:12 on everything up to then, including the second session's work. Check it; on
  failure run `report-test-failures.yml` with `run_id`, read the last 120 lines of its job log, fix, re-run.

## Not done yet
- **Dead connection still scanned** (diagnostics 14 Nov, finding 3): the source list is chosen inside MediaLib's
  scheduler, so skipping a source that fails at root resolution needs a new registered MediaLib patch; the "Not
  reachable" label arrives with Files (R6). Do both together in step 8.
- Standby focus on Settings and Search (separate screens) is not covered yet; a stuck local import still keeps the
  screen awake through `ActiveOperationMonitor` (use `StalledWorkWatch` there too).
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
