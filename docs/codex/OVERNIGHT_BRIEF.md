# Overnight brief for Codex — tonight: three parts

Parts A and B are mechanical and proved by a checker. Part C is a **read-only** bug hunt: you report, you
change no code.

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/overnight-YYYY-MM-DD` (today's date; if it exists, add `-2`, `-3`, …). Push only
   to that branch. No pull request, merge, rebase or force-push.
3. Never edit `.github/`, `tools/`, build files (`*.gradle`, `gradle.properties`), `res/` or `assets/`.
   In `docs/` you may only add your report and the Part C findings files.

## Part A — fix 34 misleading indentations (layout only)
Android lint reports 34 `SuspiciousIndentation` errors, all in
`src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java`. Each is a brace-less
`for`/`if`/`while` whose single-statement body sits at the **same** indentation as the statement that owns it,
e.g.
```java
for (String key : new String[] { … })
move(root, audio, key);          // body: must be indented one level deeper
```
Indent every such body one level (4 spaces) deeper than its owner. Look for the same pattern anywhere else in
`src/` and fix it too. **Do not add braces** (that changes tokens). Commit per file:
`Fix misleading indentation in <FileName>.java (layout only)`, then run
`python3 tools/check_format_only.py --base claude/supernova-github-access-yfhe89 --files <path>` — must say `ok`.

## Part B — the 94 `@Override` labels restored last time
Last night's report (`docs/codex/reports/2026-11-03-overnight-2026-11-03-5.md`, "Files restored after the
single checker attempt") lists nine files whose `@Override` additions were rejected only because of a checker
bug, now fixed. Re-apply the same rules as last time (only where certain; skip when unsure; `@Override` above
any other annotations) to those nine files. Commit per file: `Add missing @Override in <FileName>.java`, then
run `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --files <path> --allow-added-override`
— must say `ok`.

## Part C — read-only bug hunt, area 1: playback and the Details page
Read **every line** of every `.java` file in these folders (under `src/main/java/com/archos/mediacenter/video/`):
`player/`, `leanback/details/`, plus `leanback/PreviewPlayback*.java` and `utils/Player*.java` if present.
Also read the lint findings for those files: `git fetch origin reports/lint` then
`git show origin/reports/lint:lint-findings.json` (filter by `file`).

Look for real defects — things that would misbehave for a user, not style:
- **crashes**: values that can be null/empty where code assumes not; index out of range; wrong casts; missing
  try/catch around I/O or parsing; use after a view/activity is destroyed;
- **leaks**: listeners, receivers, handlers, cursors, streams, bitmaps or threads never released; static
  references to Activity/View/Context (lint `StaticFieldLeak`);
- **threading**: UI touched off the main thread; heavy work (disk, network, database) on the main thread;
  shared state changed from two threads without protection;
- **logic**: inverted or impossible conditions, off-by-one, copy-paste mistakes (lint `CutPasteId`), unit mix-ups
  (ms vs s, dp vs px), state not reset between videos, a setting read but never applied;
- **security/data**: secrets in logs, certificates accepted blindly (lint `TrustAllX509TrustManager`), data lost
  on error.

**Change no code.** Write findings to `docs/codex/findings/area1-playback-details.md` as a table, one row per
finding:

| ID | File:line | Kind | Severity (crash/user-visible/minor) | Confidence (high/medium/low) | What goes wrong, for whom, when | Evidence (the lines and why) | Suggested fix (described, not applied) |

Rules: one row per distinct problem; quote the line numbers you read; say "low" confidence when you are
guessing; don't report style, naming or formatting; don't report the lint items above unless you confirm them
by reading the code. At the end of the file, list the files you read (all of them) so coverage is visible.
Commit: `Bug hunt area 1: playback and Details (read-only findings)`.

## Hard rules — breaking any of these discards the whole night
- Parts A and B change only what they allow; Part C changes no code at all.
- No Gradle, builds or tests.
- If something needs a judgement call, skip it and write the question in the report.

## Before you finish
1. Run `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --allow-added-override`
   on the whole branch — every file `ok` (the Part C findings under `docs/codex/findings/` are allowed).
2. Add a **new** report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`: Part A files and counts, Part B
   `@Override` counts and skips, Part C number of findings by severity and confidence, files read, and the exact
   final checker output. Commit and push.
