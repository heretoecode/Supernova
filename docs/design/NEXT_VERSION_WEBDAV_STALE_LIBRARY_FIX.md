# Next version — WebDAV stale library entries cannot be deleted

**Priority:** High. **Target:** next Supernova version after 0.143. **Status:** User-confirmed reproduction; source investigation and fix pending. **Do not treat as implemented.**

## Reproduction
- Multiple files were removed independently from the WebDAV service several days before 2026-10-27.
- They no longer exist on WebDAV but still appear in Supernova's library.
- Selecting **Delete** in Supernova displays a deletion-failed error; stale entries remain.
- User attempted deletion at about 21:47 Ireland local time on 2026-10-27; supplied diagnostic archive `Supernova Diagnostics - 2026-10-27 21-48-16-706.zip`.
- The diagnostic coverage is partial; no deletion-specific exception was established. Earlier repeated scan failures were **not proven** to belong to WebDAV.

## Codex work — investigate, implement and test
1. Trace Delete from UI through library database and WebDAV operation; determine the actual failure and remote HTTP/status class before implementing.
2. Handle an **authoritatively confirmed absent** remote file safely: allow its stale local record to be removed without requiring remote deletion to succeed. Preserve unrelated records and alternate files.
3. Keep **Delete remote file** distinct from **Remove from Library** (local record only); use clear, non-surprising UI wording and confirmation.
4. On a **complete successful** WebDAV scan, reconcile entries for files confirmed absent. Never prune on network outage, auth failure, timeout, partial listing, scan failure or transient errors.
5. Add privacy-safe diagnostics: operation type, source category, status/error class, scan completeness and local reconciliation outcome. Do not log credentials, tokens, full paths or private URLs.
6. Regression test multiple missing files, repeat operations, existing files, 404/401/403/405/423/5xx, offline/timeout, partial scans, source reappearance, and titles with multiple files/versions.
7. Verify on physical Nvidia Shield with real WebDAV source if available. Report device QA separately from automated tests.

## Release/handover
Include this in the **next-version Codex work queue** and changelog when actually fixed. Do not alter 0.143, merge to main or create an APK merely to record the task.
