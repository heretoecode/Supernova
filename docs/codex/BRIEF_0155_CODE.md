# Codex brief — 0.155 code work (owner approved 2026-11-04)

Claude builds and tests everything you write on GitHub (you cannot run Gradle), so write code that compiles for
Java 17 with complete imports, and keep each item in its own commit so a failure can be traced and reverted alone.
Do the parts **in order**; stop cleanly when time runs out.

## Setup
1. Start from the tip of `claude/supernova-github-access-yfhe89` (it contains your preparation findings).
2. Create a new branch `codex/0155-code-YYYY-MM-DD` (add `-2`, … if it exists). Push only to that branch. No pull
   request, merge, rebase of other branches, or force-push.
3. Bring your earlier work onto it: `git cherry-pick` the DP-01…DP-05 commits and both TV-only stage 1 and stage 2
   commits (including the stage-2 backup-test corrective commit) from `codex/0154-2026-11-03`, in their original
   order. Resolve conflicts by keeping the current tip's version of anything in `docs/codex/HANDS_OFF.txt` and
   report every conflict you resolved.
4. Read `docs/codex/HANDS_OFF.txt` first and never edit anything listed there. Claude is rewriting the Network & Files
   area and the library scan for 0.155 at the same time.

## Part 1 — DP-06 and DP-07, each with a test
From `docs/codex/findings/deep-hunt-1-player.md`; smallest change, one JUnit/Robolectric test each, one commit each:
- **DP-06** `player/AudioDelayPicker.java` and `AudioSpeedPicker.java`: the repeat started on ACTION_DOWN must also
  stop on ACTION_CANCEL (and when the view is detached). Test: DOWN then CANCEL leaves no pending repeat.
- **DP-07** `player/PlayerBrightnessManager.java`: a decrease while the window uses system brightness (-1) must set a
  manual override one step below the current system level. Test: from -1, decrease produces a value below the
  starting level.

## Part 2 — TV-only stages 3–6
Implement exactly the re-ordered plan in `docs/codex/findings/tv-only-stage3-plan.md`, **one commit per stage**,
message `TV-only removal stage N/6: <summary> (<files> files, <lines> lines removed)`. Rules:
- The manifest must keep declaring a TV-only app (leanback required, touchscreen not required).
- Keep every live TV route and every exported contract the plan says to keep; move shared constants/helpers before
  deleting their old home; delete or move tests that only cover removed code in the same stage.
- Never touch hands-off files. If a stage needs one changed, skip that item and list it in the report.
- After each stage, check with `grep` that nothing still references a deleted class, resource or string (code, XML,
  manifest, tests). A stage you cannot make reference-clean is not committed; stop and explain why.

## Part 3 — continue the deep bug hunt (read-only), only if time remains
As before: the 40 unread player files, then groups 2–4. Append to `docs/codex/findings/deep-hunt-*.md` and update
coverage. Skip hands-off files and anything removed in Part 2. Do not re-report earlier IDs (U1, N1, N2, L1, P1, U2, L2,
DP-01…DP-07).

## Before you finish
Write `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`: per part, commits, files/lines removed per stage, tests
added, cherry-pick conflicts, anything skipped and why. Confirm with `git diff --name-only
claude/supernova-github-access-yfhe89...HEAD` that no hands-off file changed. Commit and push.
