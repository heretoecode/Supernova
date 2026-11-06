# Upgrade and rollback matrix — 0.157

The new database is `media-v64.db`, schema 64. `media.db` remains the old logical rollback snapshot. Old released
helpers inherit destructive downgrade behaviour, so changing only onDowngrade in the new APK would not protect
rollback. Separate filenames keep the old helper away from the new schema. Later 0.157 changes are not visible
in an old APK. Retaining both files costs additional disk space; no automatic old-store deletion is introduced.

Migration is worker-only, cross-process leased and staged: checkpoint/quick-check old DB, copy and fsync, apply the
actual upgrade transaction to a private next file, quick-check/checkpoint/disable WAL, fsync, atomic rename. Failure
cleans the staged copy and retains the original logical data. Checkpointing can change SQLite journal headers;
the promise is retained logical data, not byte-identical legacy DB pages. The current helper refuses a future
unsupported schema without inheriting the old deletion helper.

| Scenario | Automated evidence | Device/integration requirement |
| --- | --- | --- |
| Frozen 0.153 → 64 | HistoricalUpgrade157Test: verified historical source/fixture hashes, real schema 63 SQL, metadata, collection, hidden, watched, resume, settings/opaque credentials retained | Signed in-place upgrade, real account login and shortcut inventory |
| Frozen 0.154 → 64 | Same independent release receipt; identical schema-63 bytes are expected | Same signed in-place matrix |
| Schema 62/current legacy backup | LegacySchema62MigrationTest, CanonicalMatchProviderTest, backup preparation/stage/cycle families | Actual owner archive import on Shield, all category combinations |
| 63 copy failure / staged migration | MediaStoreUpgradeTest and SQLite transaction checks | Low-space/power-loss/restart during copy and rename |
| Current 65 opened by 64 | MediaStoreUpgradeTest refuses, retains rows | Never point an old released helper at the new filename |
| Local ↔ network physical relink | PhysicalOwnershipRelinkTest: public IDs/state retained, backing ownership moved, conflicts refused, MediaStore ID reuse and recursive triggers | Mount loss/remount and real cross-protocol destination identities |
| Raw backup / current live replacement | Archive name stays media.db; helper/backup/export tests resolve actual current filename; FileCore DatabaseLocator157Test covers legacy export | Complete APK backup/import and restart through actual settings screen |
| Personal roles | HIDDEN schema 2 role refs/pending/Undo; schema-1 compatibility; stale manual match blocked | Screen adoption, duplicate filenames and credentials-bearing locators |
| Rollback to old APK | Old schema-63 logical rows unchanged after new relink; separate preferences/auth files retained | Existing upgrade certificate, real AndroidKeyStore/provider/filesystem behaviour |

Synthetic fixture provenance is in `src/test/resources/upgrade157/`. No account password/token is embedded. The
host matrix cannot prove AndroidKeyStore continuity, scoped-storage permissions, package-manager behaviour or
power-loss durability on the Shield. These are explicit acceptance gates, not passing simulated hardware tests.
