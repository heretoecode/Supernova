# Overnight brief for Codex — tidy, never change behaviour

You are doing **mechanical tidying**. You make **no decisions about behaviour, design or naming**. Every change
is checked by a script in the morning and by a full build; work that fails is thrown away, so there is nothing
to gain by changing anything beyond what each part allows.

Tonight has two parts. Do them **in order (A, then B)**, each file in its own commit.

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/overnight-YYYY-MM-DD` (today's date; if that branch exists, add `-2`, `-3`, …).
   Push only to that branch. Never push to `claude/supernova-github-access-yfhe89`, `main` or any other
   existing branch. Do not open a pull request, merge, rebase or force-push.
3. Never edit anything under `.github/`, `tools/`, `docs/` (except your report), build files (`*.gradle`,
   `gradle.properties`), `res/` or `assets/`. `docs/codex/HANDS_OFF.txt` lists any further files to avoid.

## Part A — add missing `@Override` (every `.java` file under `src/`)
Add `@Override` on its own line directly above a method that **overrides or implements** a method of a
superclass or interface and does not already have it. This labels the method for readers, and makes the
compiler catch a misspelt method name in future.
- Only where you are **certain** the method overrides: you can see the superclass or interface (an Android or
  Java class you know, e.g. `Activity.onCreate`, `View.onDraw`, `Runnable.run`, `Object.equals/hashCode/
  toString`, `BaseAdapter.getView`, `RecyclerView.Adapter.onBindViewHolder`, `Handler.handleMessage`), or a
  class in this repository whose source declares the method with the same name and parameter types. Anonymous
  classes (`new Runnable() { public void run() … }`) and lambdas' enclosing types count too.
- **Skip** when unsure: overloads with different parameters, methods whose superclass you can't see, static
  methods (they can't override), private methods, and constructors. A wrong `@Override` breaks the build, and
  the whole night is then discarded — so when in doubt, leave it out.
- Same indentation as the method; put it **above** any other annotations on the method (e.g. `@Override` then
  `@SuppressWarnings`); don't touch existing annotations.
- Commit each file: `Add missing @Override in <FileName>.java`, then run
  `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --files <path> --allow-added-override`
  — must say `ok`.

## Part B — a one-line summary on every test class that lacks one (`src/test/java`, plus any left in `src/main/java`)
Exactly as last night's class summaries: for each top-level class, interface or enum with **no** `/** … */`
comment directly above its declaration (and its annotations), add one immediately above it, e.g.
```java
/** Checks that the playback HUD keeps its controls in place and rounds its buttons. */
```
- One sentence, plain English, under about 120 characters, saying **what the tests check**, based only on the
  test method names and code. If you can't tell, skip it and list it in the report.
- Don't touch existing comments or add comments anywhere else.
- Commit each file: `Add class summary to <FileName>.java`, then run
  `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --files <path> --allow-new-comments --allow-added-override`
  — must say `ok`.

## Hard rules — breaking any of these discards the whole night
- Change only what each part allows: no renaming, reordering, new or removed code, no string or number
  changes, no edits to existing comments or annotations.
- Don't fix bugs, warnings or typos you notice — list them in the report.
- No Gradle, builds or tests; the checker is the proof (the owner runs the full build afterwards).
- If something needs a judgement call, skip it and write the question in the report.

## Pace
Work briskly: one checker run per file, no extra hand-verification. Partial progress is fine — every file is
its own commit — but always do "Before you finish".

## Before you finish
1. Run `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89 --allow-new-comments --allow-added-override`
   on the whole branch. Every file must be `ok`.
2. Add a **new** report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md` (never overwrite an earlier one):
   Part A — files changed and the number of `@Override` added, with the skipped cases grouped by reason;
   Part B — classes summarised and any left without a summary and why; anything odd you noticed (listed, not
   fixed); the exact final checker output. Commit and push.
