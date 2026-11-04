# Codex brief — native archive on a branch instead of a Release (owner approved 2026-11-04)

Rules: `BRIEF_0155_MORE.md` rules 1–5, `BRIEF_0155_ENGINE_BUILD.md` widened rules, `docs/codex/HANDS_OFF.txt`.
Branch `codex/0155-code-2026-11-04` only. No PR, merge, rebase, force-push, workflow dispatch.

## Why
Claude ran `supernova-0156-native-archive.yml` from `claude/supernova-github-access-yfhe89` four times (runs
41847532946, 41847738060, 41847968898, 41849511956). Claude fixed two things on that branch (take them from there,
commits `11c60bbb6`, `f4c22771d`): artifact ZIPs must be requested with `Accept: application/json` (GitHub answers 415
to octet-stream), and `gh()` now prints the failing command and GitHub's reason. With those, all four ABIs download
and verify every time, but `gh release create` is refused: **HTTP 403 "Resource not accessible by integration"**, even
with the job token showing `Contents: write` and the repository default set to read and write. Claude's own session may
not create Releases either. The owner chose: keep the archive **in the repository on a dedicated branch**.

## The list
1. **Archive to a branch.** Change the archive job (new files/edits only to the `*supernova156*` /
   `supernova-0156-*` files you created; keep Claude's two fixes) so that, after the same verification, it commits the
   four ABI ZIPs (each < 100 MB — check and fail closed if not), the native lock, the verification receipt and a
   manifest with SHA-256 of every file to an **orphan branch `native-archive-0146`** (one commit, nothing else on it),
   pushed with the job token; if the branch already exists, verify its files byte-for-byte instead of rewriting it.
   Afterwards fetch the branch fresh and re-verify every hash. Manual dispatch, plus the same push trigger limited to
   the workflow's own file on `claude/supernova-github-access-yfhe89` that Claude added (so Claude can start it).
   Offline tests for: oversize file, hash mismatch, existing branch identical, existing branch different (fail).
2. **Read side.** Make `build-supernova156-native-stack.py` accept `--third-party-source branch` (fetch the files from
   `native-archive-0146`, verify against the lock/manifest exactly as the Release path did); keep `actions` as the
   explicit fallback. Update the native-stack workflow choice list and its offline tests accordingly.
3. **Plan B, documented only:** in `findings/native-archive-plan.md`, if the token also cannot push the branch, the
   exact steps for the owner to run once from a computer (`gh run download 40843685431`, verify with the script,
   commit to the orphan branch, push), and how the build then uses it.
4. Update `HANDOVER.md` (the "Engine release — Claude's steps" section: archive via branch) and the engine lock digests
   for any recipe file you change.
5. **Then** continue `BRIEF_0155_NEXT.md` from item 3 (DP-10) as your last report says.

## Before you finish
Report `docs/codex/reports/YYYY-MM-DD-0155-archive-branch.md`; confirm with
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no existing `.github/`, Gradle or hands-off
file changed (other than your own `*supernova156*` / `supernova-0156-*` files). Commit and push.
