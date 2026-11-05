# Codex brief — backup failure analysis, then continue NEXT (owner approved 2026-11-04)

Rules: `BRIEF_0155_MORE.md` rules 1–5 and `docs/codex/HANDS_OFF.txt` (read it first). Branch
`codex/0155-code-2026-11-04` only. No PR, merge, rebase, force-push or workflow dispatch. No UI or design work.

## 1. Backup cannot be prepared — analysis only (do first)
**Symptom (owner's Shield, 0.154, 4 Nov 2026):** Settings › System › Backup & Restore › create backup shows
"Preparing and verifying backup…" with Cancel, then "Backup could not be prepared. Existing backups and application
data were retained." with "Back to Backup & Restore". Default categories were selected (everything except File
bookmarks, Library sources, Filenames and locations). The owner's library is large (about 61,000 media rows; Put.io
WebDAV and USB sources).

Starting points: `leanback/settings/BackupOperationModel.java` (shows the message) and
`utils/backup/` (`LogicalBackupImport`, the export/writer side, category registries, verification of the closed
archive, destination copy).

Write `docs/codex/findings/backup-failure.md`:
- every path that leads to "could not be prepared" (each exception, verification failure, size/limit check, timeout,
  cancellation, missing permission or destination, category serialiser failure), with file:line and quoted code;
- which of them are plausible on a large library on Android 11 / Shield (e.g. size limits, per-category row caps,
  memory, a category whose data does not round-trip through its own verifier, a strict check that rejects real data);
- what diagnostics the app already records for a failed backup, and exactly where in an exported Diagnostic Report the
  owner or Claude would find the cause;
- a ranked list of causes, and for the top one a fix described in words plus the test that would prove it.
Do not change app code for this item (Backup is a screen the owner sees; Claude fixes it). One commit.

## 2. Then continue `BRIEF_0155_NEXT.md` from item 3 (DP-10)
As your last report says, and on through its list.

## Before you finish
Update `docs/codex/HANDOVER.md`; write `docs/codex/reports/YYYY-MM-DD-0155-backup.md` with the exact place to
continue; confirm with `git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no existing `.github/`,
Gradle or hands-off file changed. Commit and push.
