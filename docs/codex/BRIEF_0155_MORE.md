# Codex brief — further 0.155/0.156 work (owner approved 2026-11-04)

Continue on your existing branch **`codex/0155-code-2026-11-04`** (pull it first; push only to it). No pull request,
merge, rebase or force-push. Read `docs/codex/HANDS_OFF.txt` and never edit anything listed there, except the one
exception named in Part 4. Do the parts in order; one commit per item; stop cleanly when time runs out.

## Part 1 — fix the one failing test (Claude ran your branch: everything else passed)
Claude built `0efd8e448` on GitHub: compile OK, release lint 0 errors, full unit suite passes except
`browser/QueryCompatibilityTest.searchAndViewForwardAllLaunchDataAndLeaveNoCompatibilityHostOnBackStack`, which fails
at `assertNull(origin.getComponent())`: Robolectric's `buildActivity(cls, intent)` sets the component on the intent it
is given. Pass a copy (`new Intent(origin)`) to `buildActivity` so `origin` stays the caller's untouched intent. Change
the test only. Commit: `Test: give Robolectric a copy of the launch intent`.

## Part 2 — device checklist for the deferred classic TV removals (stage 5)
You deferred removing some classic TV screens pending device evidence. Write
`docs/codex/findings/tv-only-stage5-device-checks.md`: a short, numbered list the owner can follow on the Shield
(plain words, one line each: where to go, what to press, what to look for), each step proving whether a deferred
classic screen is still reachable or fully replaced by a Supernova screen. For each step say which files can be
removed if the owner reports "never reached". Keep it under 20 steps. Commit: `Stage 5 device checklist (read-only)`.

## Part 3 — finish the deep bug hunt (read-only)
As in `docs/codex/OVERNIGHT_BRIEF.md` Part B: the remaining unread player files, then groups 2, 3 and 4. Skip
hands-off files and anything your TV-only stages removed; refresh each group's coverage inventory against the current
branch first. Append findings to `docs/codex/findings/deep-hunt-*.md` with the usual table and coverage columns. Do not
re-report U1, N1, N2, L1, P1, U2, L2 or DP-01…DP-07. One commit per group: `Deep bug hunt <group> (read-only)`.

## Part 4 — engine 0.156: prepare the refreshed native patches (new files only)
Using your plan `docs/codex/findings/engine-0156-plan.md`. **Exception to hands-off:** you may add **new** files
under `.github/build/` whose names contain `supernova156`; never modify or delete any existing file there.
- Against the upstream avos tip named in the plan, produce the refreshed Supernova patches as
  `.github/build/avos-supernova156-*.patch`, one per existing Supernova patch you rework, each applying cleanly
  (`git apply --check`) in the documented order on a clean upstream checkout.
- Add `.github/build/native-supernova156.lock.draft.json` listing the proposed source commits and patch order
  (marked draft; no hashes of binaries you cannot build).
- Record in the plan which upstream commits each patch now relies on and any rework you could not finish.
Commit: `Engine 0.156: refreshed native patches (draft, unbuilt)`.

## Before you finish
Add a new report `docs/codex/reports/YYYY-MM-DD-<suffix>-more.md`: per part, commits, findings by severity, what was
skipped and why. Confirm `git diff --name-only claude/supernova-github-access-yfhe89...HEAD` shows no hands-off file
changed (other than new `.github/build/*supernova156*` files). Commit and push.
