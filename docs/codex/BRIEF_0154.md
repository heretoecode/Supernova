# Codex brief — 0.154/0.155 support work (approved by the owner on 2026-11-03)

Claude is building 0.154 on `claude/supernova-github-access-yfhe89` at the same time, so this brief is split
into parts that touch **different files**. Do the parts **in the order listed**. Each part ends with commits you
push; stop cleanly when time runs out, since every part is useful on its own. Read `docs/codex/HANDS_OFF.txt`
first and never edit a file listed there, unless a part below names it explicitly.

## Setup
1. Start from the tip of `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/0154-YYYY-MM-DD` (add `-2`, `-3`, … if it exists). Push only to that branch. No
   pull request, merge, rebase or force-push. Never edit `.github/`, `build.gradle`, `tools/native146/` or
   anything in `HANDS_OFF.txt`.
3. No Android SDK is needed. If Gradle cannot run in your environment, do not install anything; Claude runs the
   full build and test suite on GitHub after you finish. Write code that compiles for Java 17 with the imports it
   needs.
4. Commit messages: what changed and why, plus the part/finding ID. One commit per item as stated below.

## Part 1 — fix five verified bugs (DP-01 … DP-05), each with a test
Claude verified all five against the code (`docs/codex/findings/deep-hunt-1-player.md`). Fix each one with the
smallest change, and add one JUnit/Robolectric test per fix under `src/test/java/...` in the same package as
the class (look at neighbouring tests such as `player/PreviewUpNextTest.java` and
`player/PlaybackSubtitlePanelTest.java` for the setup). One commit per finding.
- **DP-01** `player/VideoEffectRenderer.java` `onFrameAvailable`: the final `mSourceFrameAvailable.put(mTrue)`
  can block the main thread forever once the renderer is paused (one-slot queue, nobody draining it). Replace it
  with the non-blocking wake (`offer`), keeping the wake after the 3D subtitle draw. Test: with a full queue
  and a paused renderer, `onFrameAvailable` returns promptly (run it on a thread and join with a timeout).
- **DP-02** `player/SubtitleEngine.java` `pull3DSubtitles()`: after posting the new overlay buffer, wake the draw
  loop the same way `redraw3DIfNeeded()` does (null-safe, `Player.sPlayer` / effect renderer may be absent).
  Test: whatever is practical without native code (for example, that the wake is requested when a surface is
  primed); if no meaningful test is possible, say so in the report instead of writing an empty one.
- **DP-03** `player/PreviewUpNext.java` `handleKey`: while the Skip pill has focus, **Back** must return focus to
  `skipOrigin` (like Down/Left/Right do) and consume the key; **Up** stays consumed as now. Test: focus the pill,
  press Back, focus is on the origin.
- **DP-04** `player/ExternalResumeIntent.java` `describeForTrace`: print the value of a resume key only when it
  is an `Integer` or `Long`; otherwise print `=<invalid>`. Test: a `String`/`Bundle` value under `position` is
  not printed; an `Integer` is.
- **DP-05** `player/PlaybackSubtitlePanel.java` `toggle()` and `option()` rows. **Owner decision:** on a switch
  row, **Left turns it Off and Right turns it On** (no change if it is already in that state; still consumed so
  focus never leaves the panel); OK still flips it. Use the existing `rowKeys(row, key, event, stepper)` with a
  stepper that sets the value from the direction, through the same backend calls the click uses, then
  `refresh()`. Touch only those two methods (Claude edited other parts of this file for 0.154). Test: Right on
  an Off switch turns it On; Right again leaves it On; Left turns it Off.

## Part 2 — TV-only removal, as six stages (owner approved removal on 2026-11-03)
Use your map `docs/codex/findings/tv-only-removal-map.md`. Regroup its 45 stages into **six** larger stages,
because each stage costs one full build (~22 minutes):
1. **Route cuts, no deletions** (your S01 + S02): TV-only entry routing; shared constants moved to neutral
   classes; phone preferences/info fallbacks rerouted to the TV screens; service notifications pointed at the TV
   entry. Behaviour on the TV must not change.
2. **Disconnected and test-only classes** (S03–S10).
3. **Phone launcher widgets and phone library screens** (S11–S17).
4. **Phone shell, browser, folder/network, metadata, settings and scraper screens** (S18–S27).
5. **Classic TV screens superseded by the Preview screens** (S28–S36) — only those whose TV behaviour already has
   a replacement; anything still reached on the TV stays and is listed in the report.
6. **Resources, strings, manifest entries and tests** belonging to the removed code (S37–S45).
Rules: **one commit per stage**, message `TV-only removal stage N/6: <summary>` with the file count and lines
removed. Delete or move tests that only cover removed code in the same stage. The manifest must keep declaring a
TV-only app (leanback required, touchscreen not required). Never delete anything in `HANDS_OFF.txt`; if a removal
needs a change in a hands-off file, skip that item and list it in the report. If a stage cannot be done safely,
stop after the previous stage and explain why.

## Part 3 — engine update preparation for 0.155 (read-only for existing files)
Supernova's native engine (`avos`) is NOVA 6.6.2 snapshot `95a9e6b3` plus Supernova patches in
`.github/build/avos-*.patch` (read them, do not edit them). NOVA has 46 newer engine commits (HDR / Dolby Vision
signalling and fallbacks, hardware-decoder retry, OMX removal, gralloc/native-window safety, subtitle box padding
`461685e`), plus MediaLib fix `e5886e0` and three aos-Video commits that use the new HDR capabilities
(starting with `9ec1bdc2`). Write `docs/codex/findings/engine-0155-plan.md`:
- a table of every upstream commit since `95a9e6b3`: what it changes, whether it touches code Supernova's patches
  also touch, expected conflicts, risk, and whether to take it (and why);
- for each Supernova patch, whether it still applies on the new upstream tip, and the exact rework needed;
- the three aos-Video commits and the MediaLib fix: what they need from the engine and where they land in
  Supernova's app code today (Supernova's player code differs from NOVA's; name the files and methods);
- the subtitle box-padding setting end to end: native style value → JNI → Java preference → where it would sit in
  the Playback Settings › Subtitles tab (rows: Size, Colour, Position, Outline, Background, Opacity, Delay);
- what the native build workflows and lock files would need to change (described, not edited);
- how to store the rebuilt engine permanently (the current pinned build artifacts expire on 27 Jan 2027).
Commit: `Engine 0.155 update plan (read-only)`.

## Part 4 — continue the deep bug hunt (read-only), only if time remains
Continue exactly as in `docs/codex/OVERNIGHT_BRIEF.md` Part B: the 56 unread files of group 1 first, then groups
2, 3 and 4, skipping files Part 2 removed. Append findings and update the coverage tables in the existing
`docs/codex/findings/deep-hunt-*.md` files. Do not re-report U1, N1, N2, L1, P1, U2, L2 or DP-01…DP-05.

## Before you finish
Write `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`: per part, what was done (commit hashes), what was
skipped and why, tests added, anything odd. Confirm with `git diff --name-only claude/supernova-github-access-yfhe89...HEAD`
that no hands-off file changed. Commit and push.
