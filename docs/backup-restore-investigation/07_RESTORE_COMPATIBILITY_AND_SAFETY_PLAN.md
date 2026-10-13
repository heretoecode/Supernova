# Restore compatibility, format evolution and safety plan

Existing verified maintenance report 08 supplies staging, bounds, journal, locks, schema conflict and downgrade evidence. This report adds precise omission semantics and owner no-artwork compatibility consequences. Everything in the test section is **proposed, not run**.

## Current reader compatibility and safety

| Check / operation | Source-verified contract | Limit / implication |
|---|---|---|
| Archive stage | UUID `cache/restore-*`, canonical child containment, reject backslash/outside-path/unsupported entry/canonical duplicate; stream ZIP | Max 100,000 entries, aggregate expansion 4 GiB; settings/named metadata read capped 4 MiB, db version 32 bytes during stage. No disk-free reservation; stage is not proof of restoration |
| Manifest | format Supernova, version 1.0, credentialsIncluded false, applicationVersion `0.[1-9][0-9]*`, positive createdUtc, **exact** category set library/viewing history/settings/sources/custom rows/personal artwork, no duplicates | Fixed categories describe capabilities, not actual inclusion. `viewingHistorySchema` is written but not checked by manifest validator; no per-category schema/checksum/mode flags |
| Required files | manifest, instructions file, media DB, db_version, settings | named/history/shortcuts/provider/art dirs optional to reader, even though current writer always emits named+history |
| Media schema | manifest DB version equals running `VideoOpenHelper` (62); SQLite user_version agrees; quick_check returns ok; privacy sweep | No full expected table/column/constraint/view/trigger fingerprint. Different upstream DB62 remains potentially structurally incompatible |
| History schema | version 1, quick_check, permitted non-internal objects, exact field names, restricted identity/ranges and Android locale | Stronger than media validation, but no claim of full affinity/PK/constraint fingerprint validation; import accepts absent history DB |
| Provider schema | put.io version 2 + quick_check + generic privacy | No full put.io table/column/relationship schema check; absent provider DB accepted |
| Shortcuts | Generic privacy validation opens DB and reads schema/text | No dedicated version/full-schema or explicit quick_check in validateStage for shortcuts; future compatibility fixtures needed |
| Preferences | Portable accepted keys/types, string/set sanitizer, named groups/keys validation; JSON metadata bounds | Current version's allowlist rejects unknown keys; settings decode starts clear; no key migration/default intent registry |
| Pre-restore backup | Calls current export before live changes | Fails restore if recovery export fails; dated portable ZIP omits credentials/unallowlisted settings and selected viewing data. It is not a complete rollback of destination state |
| Lock/journal | Await history writer; file+process restore lock, ProviderDiscoveryGate, history LOCK, exclusive media holder; retain original files/settings/named auth; AtomicFile journal before apply | Raw recovery state is private; do not export it. Locks/journal are substantial existing infrastructure, not proof every power-loss boundary works |
| Apply | File swaps and prefs commits, journal marked committed, cleanup/recovery; startup recover before provider opens | Fresh file copy uses ordinary streams, without per-file data/directory fsync here; full power-loss durability needs proof. Successful apply + cleanup failure can appear as restore error although commit occurred |

Exact implementation: [SafeBackup.java:18](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java#L18), [SafeBackup.java:60](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java#L60), [SafeBackup.java:117](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java#L117), [RestoreJournal.java:19](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/RestoreJournal.java#L19), [RestoreJournal.java:32](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/RestoreJournal.java#L32), [RestoreJournal.java:41](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/RestoreJournal.java#L41), [CustomApplication.java:472](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/CustomApplication.java#L472). `VerifiedBackup.validate` checks ZIP-required entries/CRC/size, not all of `SafeBackup.validateStage` schema/privacy conditions: successful export-byte verification is not complete restore certification.

## Omission semantics today (critical for granular controls)

| Absent or empty component | Existing result on successful restore |
|---|---|
| Absent media/settings/manifest/version/instructions | Restore rejected before live replacement |
| `settings.json={}` | Default prefs cleared; only package's forced UI choice and import artwork-pending flag subsequently added |
| Key omitted in settings | Destination key deleted; receives consumer fallback/default, not preserved local user setting |
| History/shortcuts/provider DB absent | Corresponding live DB plus WAL/SHM moved out then deleted on committed cleanup; no preservation of omitted category |
| Named file absent/empty object | Named groups not listed stay live; OpenSubtitles credentials are separately cleared regardless |
| Named group present/empty map | That group cleared/replaced; unrelated missing named groups still remain |
| Artwork directory absent | Existing poster/backdrop/picture directory moved away and removed; retained metadata triggers subsequent background artwork re-fetch |
| Other excluded named stores / blacklisteds_db / ftp_shortcuts_db / OS grants | Not in SafeBackup swap/clear list; remain on same installation. New installation cannot recover them from archive. Missing ownership/health groups can likewise retain old state beside a replaced media DB: future referential reconciliation required |
| Credentials (always absent/unsupported entry) | Existing credentials_db and WAL/SHM removed; put.io OAuth envelope and .bak removed; default auth absent, named OpenSubtitles cleared |

[SafeBackup.java:127](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java#L127) deliberately schedules swaps for **all** these DBs/art dirs regardless of stage presence. A future Off switch cannot safely mean “drop ZIP entry, existing destination untouched” under this code. Define and implement category omission before exposing independent controls.

## Old artwork archives and no-artwork future exports

**Verified:** writer already omits known reproducible art files; reader accepts archives with no art directories as long as fixed manifest categories remain. Retained scraper/provider references allow best-effort regeneration. File absence alone does not require art bytes to validate or restore the media DB. Existing reader also accepts `scraper_pictures/`, although current writer omits it.

**Proposed compatible policy:** retain a dedicated Supernova 1.0 reader with its old validation contract. Accept bounded/validated old artwork files to preserve legacy restores; compatibility handling may restore them or explicitly skip them after clear review, but do not reject an archive solely for artwork presence and never rewrite/delete old backups. New exports contain no artwork bytes or artwork toggle. Owner must approve whether legacy personal images are restored or skipped and whether regenerable image selection references are retained. Skipping requires intentional directory/reset semantics and offline placeholders, not orphan DB references or user-collection loss.

The current validator insists on the `personal artwork` category even when no image is included. Removing it from the manifest makes old readers reject the archive. Leaving it in a new no-artwork export is technically accepted but potentially misleading. **Recommend a new, explicitly versioned logical format** for real granular categories/default intent, with the old 1.0 reader kept. Do not casually label redesigned archives 1.0, or imply version-number-only schema compatibility.

## Future format and restore contract (proposal)

Manifest: format identifier/version; producer version; created UTC; actual category list, schema and inclusion state; entry digest/size map; relationship namespace/reference maps; settings key schema/default-intent origin; explicit replace/merge/empty semantics; privacy declaration and no-artwork invariant. Do not include device secrets or personally revealing paths outside selected categories. Unknown future categories must be rejected or safely declared unsupported under a documented rule, not silently installed.

Logical payloads should preserve stable entity IDs and unresolved references rather than raw SQLite internal IDs. Keep old whole-DB snapshot import isolated, verify schema fingerprints and migration feasibility before commit. Adding/changing schema requires an owner-approved successor version strategy; **do not choose a number in this audit**. Existing MediaLib destructive downgrade helper means APK downgrade is not safe rollback; restore version gates likewise reject old DB versions unless a designed migration exists. [DeleteOnDowngradeSQLiteOpenHelper.java:55](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediaprovider/DeleteOnDowngradeSQLiteOpenHelper.java#L55) and maintenance report 08.

For partial restore, keep omitted destination categories unchanged, distinguish present-empty from absent, enforce dependency checks and referential remapping, and explain explicit replacement. Preserve/reuse journal/locks, journal every affected store, classify committed-but-cleanup-pending outcomes, preflight enough free space, validate journal recoverability and staged data durability. Clarify auth policy: legacy restore currently disconnects accounts; new selected-category restore should not erase unrelated live credentials as a side effect, while never importing secrets.

## Required future synthetic acceptance matrix

No owner/private DBs or paths in public fixtures. Use developer-owned fake locators/credentials, and safe count/enum evidence only. These are implementation release gates, not tests claimed completed now.

| Fixture / failure injection | Required result |
|---|---|
| Current 1.0 full archive; current writer no-art output; old artwork/poster/backdrop/picture archive | Validate intended old policy; preserve matches/history/collections; regenerate missing art or use safe offline fallback; no new export image payload |
| Empty library/settings, absent named/history/optional DBs/art dirs; all category On/Off combinations with empty versus absent distinction | Legacy behaviour documented; new format omission preserves live categories, no dangling references or silent deletion; state outcome count invariants |
| Same DB62 wrong Supernova/upstream schema, missing tables/columns, incompatible triggers/views, unknown history/provider/shortcut schemas | Reject before mutation using future schema fingerprint; no successful-looking structurally unusable restore |
| Corrupt CRC, truncated/empty ZIP, duplicate canonical paths, traversal/backslash/unknown entry, oversized entry/count/expanded DB, unsupported manifest/categories | Reject safely; leave originals usable; remove owned stage; no platform filesystem escape |
| Synthetic secrets in DB/password/token/session columns, URI user-info/query/fragment, JSON nested keys, key names, labels, values, sets, historic free pages | Excluded/sanitized/rejected consistently, VACUUM removes deleted secret bytes; expose remaining plain-label/username gap without claiming private leak |
| Each registered setter including changed → default, never-set vs persisted-default, removed key, receiving default/type changes | Preserve explicit user intent; follow-default state follows policy; no resetting disabled diagnostic/provider-search intent |
| History/resume four-state combinations, negative sentinel -2, positive unfinished positions, duration/times/legacy watched flags, PrivateMode | Exact category privacy contract, no history resurrection, restore Continue Watching correctly after successful playback/state reconcile |
| Offline/unmounted/revoked source, renamed file/mount, duplicate names/same size, query-selected URI, conflicting matches/provider reconnect | Retain user history/collections/unresolved matches without destructive purge or false reassociation; show pending state |
| Process kill at prepared journal, each DB/WAL/SHM/art/token rename, preference commit, journal committed, cleanup; repeat recovery kill | All-original or coherently all-restored; no mixed referential state; success/cleanup failure truthfully distinguished; recovery state remains private |
| Disk full during stage, fresh copy, snapshot/ZIP, journal/preference write, cloud/local copy/readback; busy scan/history writer | No original data loss, honest retry/error, released locks, retained recoverable archive/cleanup notice; no fake success |
| SAF local, USB/network-backed/cloud, no rename/delete/readback, URI changes, delayed metadata, grant loss, Activity exit/recreation, process force-stop, concurrent export | No misleading final-name incomplete file where provider supports safe publish; accurate supported-limit message; no deletion of unrelated docs; attempt-journal cleanup and correct timestamps |
| Large synthetic library, default compression versus alternatives, lock-hold/heap/temporary-space and stage latency | Measured comparison against baseline, truthful progress and cancellation; no unmeasured performance promises |
| **Signed 0.138 → signed 0.139 upgrade** with app identity/signature/data preserved | Install over current data without wipe; all existing sources/settings/history/rows/collections accessible; old backups still handle according to approved policy; no destructive downgrade used as recovery |

Reuse existing tests in `VerifiedBackupTest`/`RestoreJournalTest` as implementation seeds, plus maintenance proposed migration/privacy tests; this audit did not compile/run them or trigger CI. Build/APK/signing work remains separately authorised. Import review itself can create a **validated staged file**, but no restore was performed here.
