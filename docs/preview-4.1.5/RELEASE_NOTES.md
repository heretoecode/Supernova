# Supernova Preview 4.1.5

Stability corrections on Preview 4.1.4; existing presentation retained.

## Confirmed source fixes
- Details launch no longer requires a shared image view that Preview file cards do not provide.
- Automatic preferred-version launch carries genuine resume state; explicit start/manual version intent remains separate.
- Up Next compares logical episodes before choosing an encode.
- Modern library preferences, per-source scanning identity, browser horizontal focus and failed-artwork state corrected.

## Hardening
- Isolate failing library records, retain safe snapshots on load failure and emit redacted cursor diagnostics. The original Shield cursor-window trigger remains unproven.
- Durable restore journal and startup recovery reduce incomplete-restore exposure; full power-loss/concurrent-writer guarantees are not claimed.
- Provider skip metadata no longer synthesises playback completion. Historical long WebDAV premature termination remains unresolved.
- Metadata requests can retry after failures; Home no longer waits for remote artwork before revealing content.

## Security
- Internal media bridge restricted to loopback.
- Both SFTP backends pin first-use server identity and reject changes, with explicit verified-rotation reset. First-use interception remains possible.
- Backup warning explicitly identifies unencrypted recoverable credentials. Archives are not encrypted.

## Regression coverage and acceptance
Targeted tests cover loader failure, transitions, version/resume policy, logical episode progression, source identity, settings replacement, restore recovery and bridge binding. See BUILD_RESULTS.md for executed results.

PHYSICAL SHIELD QA REQUIRED. All 23 audit findings are accounted for in AUDIT_DISPOSITION.md; deferred items and partial mitigations remain visible.
