# Overnight brief for Codex — tidy, never change behaviour

You are doing **mechanical tidying**. You make **no decisions about behaviour, design or naming**. Every change
is checked by a script in the morning; work that fails the check is thrown away, so there is nothing to gain
by changing anything beyond what each part allows.

Tonight has two parts. Do them **in order (A, then B)**, each file in its own commit.

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/overnight-YYYY-MM-DD` (today's date; if that branch exists, add `-2`, `-3`, …).
   Push only to that branch. Never push to `claude/supernova-github-access-yfhe89`, `main` or any other
   existing branch. Do not open a pull request, merge, rebase or force-push.
3. Never edit anything under `.github/`, `tools/`, `docs/` (except your report), build files (`*.gradle`,
   `gradle.properties`), `res/` or `assets/`. `docs/codex/HANDS_OFF.txt` lists any further files to avoid.

## Part A — imports, round 2 (every `.java` file under `src/`)
Last time only `android.`/`androidx.`/`java.`/`javax.` names were folded, and names whose simple form was
already in use were skipped. The checker now understands more, so tonight fold **every** fully qualified
class name in code where the simple name will mean exactly that class:
- **Already imported:** the file already has `import x.y.Foo;` → write `x.y.Foo` as `Foo` (no new import).
- **Same package:** the class lives in the file's own package (e.g. `com.archos.mediacenter.video.player.Foo`
  inside package `com.archos.mediacenter.video.player`) → write `Foo` (no import needed).
- **Not yet imported:** add `import x.y.Foo;` and write `Foo` — any package, including `com.archos.`, `org.`,
  `com.` — **but skip** when:
  - the file already imports a *different* class with the same simple name (e.g. it imports the app's `R`,
    so never fold `android.R`); or
  - the simple name is already used in the file for something else: a class in the same package, a nested or
    inner class, or a java.lang class (`String`, `Object`, `Math`, `Thread`, `Override`, …) or something
    reached through a wildcard (`import x.*;`). When in doubt, skip.
- Fold all occurrences of a name in the file, or none of them. Never touch names inside strings or comments.
- Also remove imports that are now unused (simple name nowhere else in the file, code **or** comments).
  Never remove a wildcard import. Ordinary single-type imports only (no `import static`, no `*`).
- Put new imports in alphabetical position among the existing ones, keeping the file's existing style.

Commit each file: `Tidy imports in <FileName>.java (round 2)`, then run
`python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --files <path>` — must
say `ok`. If it says `FAIL` and the cause isn't clear within a few minutes, restore the file
(`git checkout claude/supernova-github-access-yfhe89 -- <path>`) and list it as skipped. Find the work with
`grep -rcE "(^|[^.[:alnum:]_])(android|androidx|java|javax|com|org|net)\.[a-z][a-z0-9_]*\.[A-Za-z]" src --include=*.java | grep -v ':0$' | sort -t: -k2 -rn`.

## Part B — a one-line summary on every class that lacks one (`src/main/java` only)
For each top-level class, interface or enum in `src/main/java` that has **no** `/** … */` comment directly
above its declaration (above any annotations), add one, immediately above the declaration (and above its
annotations):
```java
/** The Settings page: eight category tiles, then each category's sections and preferences. */
```
- **One sentence**, plain English, under about 120 characters: what the class is for, as a reader would want to
  know before opening it. Mention the screen or feature it serves when obvious (e.g. "Playback HUD", "Details
  page", "Network & Files").
- Base it **only on what the code shows** (its name, its methods, who calls it). No guesses about history,
  authors or intentions. If you cannot tell what a class is for in a minute or two, write nothing for it and
  list it in the report.
- Don't touch existing comments, don't add comments anywhere else, don't add `@author`/`@since` tags.
- Commit each file: `Add class summary to <FileName>.java`, then run
  `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --files <path> --allow-new-comments`
  — must say `ok`.

## Hard rules — breaking any of these discards the whole night
- Change only what each part allows: no renaming, reordering, new or removed code, no string, number or
  annotation changes, no edits to existing comments.
- Don't fix bugs, warnings or typos you notice — list them in the report.
- No Gradle, builds or tests; the checker is the proof (the owner runs the full build afterwards).
- If something needs a judgement call, skip it and write the question in the report.

## Pace
Work briskly: one checker run per file, no extra hand-verification. Partial progress is fine — every file is
its own commit — but always do "Before you finish".

## Before you finish
1. Run `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --allow-new-comments`
   on the whole branch. Every file must be `ok`.
2. Add a **new** report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md` (never overwrite an earlier one):
   files done per part; names skipped in Part A grouped by reason (counts, plus examples); classes left
   without a summary in Part B and why; anything odd you noticed (listed, not fixed); the exact final checker
   output. Commit and push.
