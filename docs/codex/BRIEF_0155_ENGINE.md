# Codex brief — engine release prep (owner approved 2026-11-04)

The owner has decided the **engine update ships first, on its own**, as the next Supernova version. This brief
puts the engine work first. It replaces the order of `BRIEF_0155_NEXT.md`; NEXT's rules and MORE's rules 1–5 still
apply in full (branch `codex/0155-code-2026-11-04` only, no UI/design work, hands-off list, allowed new files
`.github/build/*supernova156*` with `git add -f` and `.github/workflows/supernova-0156-*.yml`, tests under
`src/test/`, never modify existing `.github/` files). File names keep the working label "0156".

NOVA released **6.6.3 (1 Nov) and 6.6.4 (4 Nov)**. Claude's mapping is `docs/0.155/UPSTREAM_NOVA_6.6.md` on
`claude/supernova-github-access-yfhe89` — read it first.

## The list
1. **Refresh the engine plan to NOVA 6.6.4.** Move the frozen upstream aos-avos tip from `2540c4f` to `5cf3ae9`
   (adds `f84855b`, `eb5250d`: ASS/SSA kept on screen when the video box overflows). Update
   `findings/engine-0156-plan.md` with every new commit (table as before), and the Java/app side the engine
   release needs: aos-Video `9ec1bdc2`, `5c721514`, `8bbfb666` (`95f233f9` is portrait-only; say whether any of it
   applies to TV), `d0be0f11`, `e1642b6b`, `73df6e4d`/`b777104f` (box padding — plumbing only, no Settings control),
   `73327baa`; MediaLib `cf75fdcd`, `e5886e04`. For each: already in Supernova (prove with file:line), needed, or
   not applicable, and what it touches.
2. **Engine 0.156 patches** — `BRIEF_0155_MORE.md` item 7, against the new tip `5cf3ae9`:
   `.github/build/avos-supernova156-*.patch` (each `git apply --check` clean, in order, on a clean checkout of that
   tip) and `.github/build/native-supernova156.lock.draft.json` (proposed sources and order; no invented hashes).
   Record any rework you could not finish in the plan.
3. **App-side engine changes (draft code + tests).** The non-UI Java changes from item 1 marked "needed" (JNI
   wrappers, preference keys with defaults that reproduce today's look, decoder ranking/HDR capability plumbing,
   reopen-at-latest-seek, subtitle association on rename failure, subtitle cache fallback). One commit per upstream
   change, upstream hash in the message, a JUnit/Robolectric test each. Anything that changes a screen or a setting
   the user sees: describe it in the plan instead — Claude does it.
4. **0.156 Shield test plan** — `BRIEF_0155_NEXT.md` item 10, plus steps for Fit width / zoom with ASS subtitles and
   for a network file that fails and reopens.
5. **Handover document.** Create and keep current `docs/codex/HANDOVER.md`: one table of everything ready for
   Claude — item, commits, files, tested how (offline / `git apply --check` / JUnit written but not run), how to
   apply, open questions. Engine release section first, then everything else from MORE/NEXT already done.
6. **Then continue `BRIEF_0155_NEXT.md`** from item 3 (DP-10; the FileCore cancellation hook belongs in a new
   `.github/build/filecore-supernova155-*.patch`, not in app code that reaches into FileCore internals), and on
   through its list.

## Before you finish
Report `docs/codex/reports/YYYY-MM-DD-0155-engine.md` (per item: commits, what changed, tests, skipped and why,
exact place to continue). Update HANDOVER.md. Confirm with
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no hands-off or existing `.github/` file
changed in this run. Commit and push.
