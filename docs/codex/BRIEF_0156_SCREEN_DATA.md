# Codex brief — 0.156 data for the remaining screens (5 Nov 2026)

**Do not start until the owner hands this over.** Rules as `BRIEF_0156_CODE.md`: your branch only, merge
`claude/supernova-github-access-yfhe89` first, no PR/merge/dispatch, one statement per line, lines ≤ 120, a test for
every change, no screens, wording or resources (Claude builds those). Report each item in `docs/codex/HANDOVER.md` and
add its API to `docs/codex/findings/0156-ui-hooks.md`.

Approved designs: `docs/0.154/OWNER_NOTES.md` #14–17, `docs/design/proposals/network-files-v2/`,
`network-shares/`, `add-connection/`, and `docs/0.155/OWNER_NOTES.md` #25.

1. **Scan now — stop and plain progress.** `PreviewLibraryScan` (or a new class): `stop()` that ends a running scan
   cleanly (no half-written rows, the next request starts normally) and a `ScanProgress progress()` snapshot for the
   tile: source label ("Put.io › Films", "USB drive Backup #1"), checked so far, total when known, running or not,
   and on failure a short plain reason. Main-thread safe, no I/O.
2. **Recent scans.** Keep the last 10 finished scans (when, which source, how long, new/removed/updated counts, or
   why it did not finish) in a bounded store and expose them as a list. Include them in backups only if they already
   fall under an existing category.
3. **Scan when Supernova opens.** Honour `auto_rescan_on_app_restart` only when the last completed scan is over an
   hour old (owner rule). Default automatic schedule every hour.
4. **Put.io service row.** One status object: signed in as, storage used/total, reachable now (with the time of the
   last check), library sync state (up to date / syncing / failed with reason), and Retry. No credentials exposed.
5. **Test connection.** For a draft connection (type, host, port, folder, user, password) that has **not** been
   saved: connect, list, and return either "N folders, M videos found" counts or which field is wrong (address,
   port, sign-in, folder, certificate) plus a plain reason. Nothing is stored on failure; a timeout is bounded.
6. **Library Health — missing files.** For each missing file: library title (not the address), where it was
   (source name and folder), since when, old file name; actions Find it again (search the same source for a file
   with the same size/name and re-link it, keeping watched state and progress) and Remove (from the library only).
   Counts must match `LibraryAttentionCount` (see `findings/library-health-counts.md`).
7. **Export .nfo files.** A job with a count first ("1,809 videos; read-only shares such as Put.io are skipped"),
   progress callbacks ("240 of 1,809") and a result (written / skipped read-only / failed), cancellable.
