# Overnight brief for Codex — tidy, never change behaviour

You are doing **mechanical tidying**. You make **no decisions about behaviour, design or naming**. Every change
is checked by a script in the morning; work that fails the check is thrown away, so there is nothing to gain
by changing anything beyond what each part allows.

Tonight has three parts. Do them **in order (A, B, C)**, each file in its own commit.

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/overnight-YYYY-MM-DD` (today's date; if that branch exists, add `-2`, `-3`, …).
   Push only to that branch. Never push to `claude/supernova-github-access-yfhe89`, `main` or any other
   existing branch. Do not open a pull request, merge, rebase or force-push.
3. Never edit anything under `.github/`, `tools/`, build files (`*.gradle`, `gradle.properties`), resources
   (`res/`, `assets/`) or tests' expected values. `docs/codex/HANDS_OFF.txt` lists any further files to avoid.

## Part A — reformat the files in `docs/codex/REFORMAT_QUEUE.txt` (layout only)
Same job as before, top to bottom:
- one statement per line; each `{` ends its line and each `}` starts its own line; 4-space indentation, no
  tabs; aim for lines under 120 characters; one blank line between methods; keep member order exactly;
- a brace-less `if`/`else`/`for`/`while` body goes on its own line, one level deeper. Do not add braces.
- Commit: `Reformat <FileName>.java (layout only)`, then run
  `python3 tools/check_format_only.py --base claude/supernova-github-access-yfhe89 --files <path>` — must say `ok`.

## Part B — imports, across every `.java` file under `src/`
Two changes only, per file:
1. **Fold fully qualified names into imports.** Where code writes a full name such as
   `android.graphics.drawable.GradientDrawable`, add `import android.graphics.drawable.GradientDrawable;`
   and write `GradientDrawable` instead. Rules:
   - only names starting `android.`, `androidx.`, `java.` or `javax.`; ordinary single-type imports only
     (no `import static`, no `*`);
   - **skip a name** if its simple name (e.g. `GradientDrawable`) is already used on its own anywhere in the
     file, already imported from a different package, or declared in the file — that would change meaning;
   - never fold `android.R` (the app has its own `R`); never touch names inside strings or comments;
   - fold all occurrences of a name in the file, or none of them.
2. **Remove unused imports**: delete an `import` line only if its simple name appears nowhere else in the file
   (code **or** comments). Never remove a wildcard (`.*`) import.

Keep imports in the file's existing order and style; put new imports in alphabetical position among the
existing ones. Commit each file: `Tidy imports in <FileName>.java`, then run
`python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --files <path>` — must say
`ok`. If it says `FAIL` and you cannot see why within a few minutes, restore the file
(`git checkout claude/supernova-github-access-yfhe89 -- <path>`, then re-apply its Part A layout if it was in
the queue) and list it as skipped. Work through the files with the most full names first:
`grep -rcE "(android|androidx|java|javax)\.[a-z][a-z0-9_]*\.[A-Za-z]" src --include=*.java | sort -t: -k2 -rn`.

## Part C — Future Development register
Apply `docs/codex/REGISTER_UPDATES.md` exactly. One commit: `Register: move built features out of Parked`.

## Hard rules — breaking any of these discards the whole night
- Change only what each part allows. No renaming, reordering, new or removed code, no string, number,
  annotation or comment text changes.
- Don't fix bugs, warnings or typos you notice — list them in the report.
- No Gradle, builds or tests; the checkers are the proof (the owner runs the full build afterwards).
- If something needs a judgement call, skip it and write the question in the report.

## Pace
Work briskly: one checker run per file, no extra hand-verification. Partial progress is fine — every file is
its own commit — but always do "Before you finish".

## Before you finish
1. Run `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89` (whole branch; it
   covers Parts A and B, and allows the Part C and report changes). Every file must be `ok`.
2. Add `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md` (a new file — never overwrite an earlier report):
   files done per part, files skipped and why, anything odd you noticed (listed, not fixed), and the exact
   final checker output. Commit and push.
