# Overnight brief for Codex — reformatting only

You are doing **mechanical reformatting** of Java files in this repository. You make **no decisions about
behaviour, design or naming**. Every change you make is checked by a script in the morning; work that
fails the check is thrown away, so there is nothing to gain by changing anything except layout.

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch named `codex/overnight-YYYY-MM-DD` (today's date). Push only to that branch.
   **Never** push to `claude/supernova-github-access-yfhe89`, `main`, or any other existing branch.
   Do not open a pull request, merge, rebase or force-push.

## The job
Work through `docs/codex/REFORMAT_QUEUE.txt` from the top. For each file:

1. Reformat it so a person can read it:
   - one statement per line; each `{` ends its line and each `}` starts its own line;
   - 4-space indentation, no tabs; aim for lines under 120 characters
     (break long argument lists and long `&&` / `||` / `?:` chains across lines);
   - one blank line between methods; keep the order of every member exactly as it is.
2. Run the checker on just that file:
   ```
   python3 tools/check_format_only.py --base claude/supernova-github-access-yfhe89 --files <path>
   ```
   It must print `ok` for the file. If it prints `FAIL`, fix your layout (you changed a token) or
   revert the file with `git checkout claude/supernova-github-access-yfhe89 -- <path>` and move on.
3. Commit that one file on its own, message: `Reformat <FileName>.java (layout only)`.

## Pace
Partial progress is fine: each file is its own commit, so stop whenever you run out of time and still do
the "Before you finish" steps. Quality beats quantity — one clean file is worth more than ten that fail.

## Hard rules — breaking any of these means the whole night's work is discarded
- **Change layout only.** Do not rename, reorder, add, remove or "improve" anything: no code, no
  string, no number, no annotation, no import, no comment text. Comments stay where they are,
  word for word (you may re-wrap a long comment across lines and add the usual leading `*`).
- **Do not touch any file listed in `docs/codex/HANDS_OFF.txt`.**
- **Do not touch anything outside the file you are reformatting** — no build files, workflows,
  scripts, resources, docs or tests other than the queued file itself.
- Do not fix bugs, warnings or typos you notice. Write them in the report instead (below).
- If a file is generated, binary, or will not tokenise in the checker, skip it and say so in the report.

## Before you finish
1. Run the checker on the whole branch — it must report every file as `ok`, with no `FAIL` lines:
   ```
   python3 tools/check_format_only.py --base claude/supernova-github-access-yfhe89
   ```
2. Add **one** final commit containing `docs/codex/reports/YYYY-MM-DD.md` with:
   - the files reformatted (with their longest line before → after, as printed by the checker);
   - files skipped and why;
   - anything odd you noticed (possible bugs, typos, dead code) — **listed, not fixed**;
   - the exact final checker output.
3. Push the branch.

That is the whole job. If anything in these instructions seems to require a judgement call, skip
the file and write the question in the report rather than guessing.
