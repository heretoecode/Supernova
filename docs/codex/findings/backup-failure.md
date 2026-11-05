# Backup preparation failure — analysis only

2026-11-04. App baseline: pulled `codex/0155-code-2026-11-04` at `391326f1d`.
Brief and hands-off rules: fetched Claude `51c20fab2`. Backup export/model/archive
sources cited here are identical between those heads. Paths below are relative to
`src/main/java/com/archos/mediacenter/video/` unless explicitly qualified.
The installed Shield APK's exact source commit is unknown: first confirm it in
Diagnostic Report `build-device.json` (`source_commit`, `version_name`, `version_code`).
This is a source analysis, not a reproduction on the owner's database. No app code changed.

## Most likely explanation

**Media matches can exceed the 4 MiB uncompressed category limit with far fewer
than 61,000 identified files.** `LogicalBackupArchive` allows 100,000 match rows
but refuses any category over 4,194,304 bytes. Excluding filenames/locations does
not reduce the match inventory to changed/manual matches: every indexed match
is exported with an opaque physical reference. Compression cannot help: this
check runs before ZIP creation. Actual matched-file count and byte count are
needed to confirm the owner's failure; total media rows alone do not prove it.

A second serious candidate is a single imperfect/legacy scraper relationship:
the match reader demands durable provider IDs and exact physical ownership for
*every* identified non-`putio:` row. Ordinary WebDAV URLs, including Put.io served
through WebDAV, are not excluded by the `putio:` scheme exception.

## Exact message boundary and all preparation routes

`leanback/settings/BackupOperationModel.java:117–154` catches `Exception`, not
`Throwable`. At `:135–144` the decisive branch is
`phase == Phase.PREPARING_EXPORT ? "Backup could not be prepared. Existing backups and application data were retained."`.
The full create path is `:161–177`: `LogicalBackupExporter.preparePrivate(...)`,
`VerifiedBackupPublication.prepare(...)`, publication assignment, then
`prunePrivateExports(file)`. Thus even retention cleanup *after* successful
verification can show this message. A `VerifiedBackupPublication.Failure` uses
the different destination-retry text; preparation's `prepare` throws ordinary
IOExceptions, not that wrapper.

| ID | File:line | What goes wrong | Evidence (quoted code) | Suggested fix / diagnostic distinction | Severity | Confidence |
|---|---|---|---|---|---|---|
| BF-01 | `utils/backup/LogicalBackupArchive.java:22,168–172,239–250` | Valid large category exceeds 4 MiB, or sum exceeds 20 MiB | `if (content.length > MAX_ENTRY)`; `if (total > MAX_TOTAL || output.size() + n > MAX_ENTRY)` | Capacity-safe writer/reader agreement, described below; record category and byte counts | user-visible | high in code; medium for owner |
| BF-02 | `utils/backup/PortableMediaMatches.java:78–114`; `MediaMatchInventory.java:98–136` | One missing, foreign, malformed or stale identification aborts entire backup | `throw new IOException("Identification pointer is outside its physical owner")` | Preserve safety; classify unsupported/stale records with a reviewed lossless representation, never silently omit matches | user-visible | high |
| BF-03 | `utils/backup/LogicalBackupExporter.java:61–67,148–156` | Concurrent settings, health or identification change rejects snapshot | `!before.equals(preferences.getAll()) || !healthBefore.equals(health.getAll())`; `!matchInventory.signature.equals(MediaMatchInventory.capture(context).signature)` | Stable snapshot/epoch contract; bounded retry and reason code | user-visible | high |
| BF-04 | `utils/backup/PortableSettingsRegistry.java:156–191,204–205`; category appendix | A stored value/label/pending record is valid for an old screen but outside positive backup schema | `throw new JSONException("Unsupported saved setting value: " + entry.getKey())` | Test all real writer values and legacy migrations against exporter/verifier | user-visible | high mechanism; owner unknown |
| BF-05 | `utils/backup/LogicalBackupCatalog.java:43–75` | Read-only DB open/query/map/savepoint/release fails, version differs, duplicates or cap reached | `physical.size()>=100000`; `database.getVersion()!=VideoOpenHelper.getDatabaseVersion()` | Distinguish SQLite/storage/schema and count errors | user-visible | high |
| BF-06 | `leanback/ViewingHistory.java:55–63,276–308,569–622` | Pending history writer exceeds 15 seconds, damaged history DB, strict schema/ranges or >100k records | `WRITER.submit(...).get(15, TimeUnit.SECONDS)`; `throw new IOException("Unsupported or damaged viewing history")` | Record bounded stage/reason; test busy and legacy history inputs | user-visible | high |
| BF-07 | `utils/backup/LogicalBackupExporter.java:168–180`; `LogicalBackupArchive.java:190–212` | Cache folder/temp creation, stream write/close, fsync, rename or filesystem/security failure | `if (!folder.isDirectory() && !folder.mkdirs())`; `file.getFD().sync()`; `!temporary.renameTo(destination)` | Test full/read-only/cache-unavailable filesystem and cleanup | user-visible | high |
| BF-08 | `utils/backup/LogicalBackupArchive.java:205,227–302`; `VerifiedBackupPublication.java:38–41` | Closed archive fails ZIP/CRC/hash/UTF-8/manifest/schema/category validation or disappears/changes while read | `readCategories(temporary, cancel)`; `LogicalBackupArchive.readCategories(privateArchive,cancel)` | Test writer output through both reads; preserve verification | user-visible | high |
| BF-09 | `leanback/settings/BackupOperationModel.java:328–338` | Retention folder cannot be listed or an old private archive cannot be deleted after new archive is verified | `if (files == null)`; `retained++ >= 3 && !file.delete()` | Separate successful preparation from private retention status, Claude owns screen behavior | user-visible | high |
| BF-10 | `leanback/settings/BackupOperationModel.java:117–154`; exporter `:159–161` | Any uncaught runtime exception from JSON, preferences, mapper, collection, file or SQLite API reaches generic failure | `catch (JSONException | RuntimeException invalid)`; `catch (Exception failure)` | Stage plus stable reason; never raw preference values, paths or exception messages | user-visible | high |

Additional boundary cases, not hidden failure classes:

- Selection: exporter `:34–38,143–144` rejects null/empty/unregistered/no-source
  categories. `create` copies the set *before* `work`; null elements/copy failures
  there do not take the worker's generic-message path. All current categories are
  registered (`LogicalBackupArchive.java:75–84`), so unimplemented selection is
  unlikely for defaults.
- Cancellation: `cancel()` sets the flag (`BackupOperationModel.java:93–95`);
  clearing the model does this too (`:343–349`). Catalogue `:28–29`, archive
  `:37–39` and publication `:31` throw `InterruptedIOException`. The worker uses
  **the same preparation text** but sets phase CANCELLED (`:121–126,145–146`).
  Ordinary IOExceptions wrapping an interrupt (notably history `awaitWrites`)
  need not be classified CANCELLED. No automatic whole-export timeout exists;
  the explicit timeout on this path is history's 15-second queue fence.
  Lock acquisition, DB queries and serialization can delay cancellation.
- `work` rejects busy/cleared state (`:109–111`) before submission. Executor
  rejection (`:154–157`) reports **"Backup worker is unavailable."**, not the
  owner's message. OutOfMemoryError, stack overflow, class-initializer Error,
  process death and native crash are **not caught** by `catch (Exception)`;
  memory pressure is plausible but an OOM alone does not explain this clean
  FAILED message. Failed temp deletion is best effort (`deleteOnExit`), not an
  additional preparation failure; suppressed cleanup failure in `create :168–171`
  retains the original verification exception.
- Android 11 SAF grant, USB permission, WebDAV credentials/server timeout,
  missing destination and document-provider readback are not needed here.
  The exporter reads private DB/preferences, not remote media bytes. Source
  permission loss can leave imperfect DB state but is not a live network-open
  error during export. Private cache/storage/SQLite permission errors *can*
  fail preparation. No broad-storage grant is required for private cache.

## Writer and verifier contract (including apparent self-rejection)

`LogicalBackupArchive.java:147–189` rejects existing private destination,
missing parent, empty payload set, unknown codec, null payload, malformed JSON,
category validator failure, missing schema and >4 MiB UTF-8. It clones each
payload through JSON text before validation. At `:190–208` all ZIP output,
close, sync and rename IO failures propagate. The first closed read at `:205`
can reject even output this writer created:

| File:line | Quoted rejection / predicate | Plausible self-output condition |
|---|---|---|
| `LogicalBackupArchive.java:236–240` | `"Unsupported or duplicate logical backup entry"`; `"Backup entry exceeds limits"` | Normal writer has unique registered nonempty entries; corruption/race/I/O defect needed |
| `:249–256` | `"Backup exceeds size limits"`; `"Backup entry verification failed"` | 20 MiB total cap is enforced only on read; all categories individually <=4 MiB can still sum above it. CRC/size mismatch suggests I/O/race |
| `:260–273` | `"Backup manifest or instructions missing"`; `"Unsupported logical backup manifest"` | Writer uses `BuildConfig.VERSION_NAME`; verifier requires `0\.[1-9][0-9]*` exactly. A suffix/debug/upstream version self-rejects; official `0.154` passes |
| `:275–288` | `"Backup contains no categories"`; `"Category identity must be text"`; `"Unsupported or duplicate category schema"` | No normal current writer mismatch found: schema 1 accepted, SOURCES/TITLE_PLAYBACK schema 2 expressly allowed |
| `:289–302` | `"Missing or changed backup category"`; `"Category schema does not match its manifest"`; `"Unlisted backup payload rejected"`; `"Invalid logical backup schema"` | Manifest byte count/SHA-256/schema must agree; malformed/changed archive or future codec disagreement |
| `:46–65` | `"Invalid backup text"`; `"Cannot verify backup digest"` | Invalid UTF-8/corruption or digest provider failure; writer normally emits UTF-8 |
| `VerifiedBackupPublication.java:39–41,32–36` | `"Private archive exceeds supported limits"`; `"Backup copy has unexpected trailing data"`; `"Backup copy is incomplete"`; `"Backup digest unavailable"` | Missing/non-file/empty/>24 MiB compressed archive, read race, hash or stream failure. Compressed ceiling is not the first large-library limit |

The reader runs **twice** (temporary file, then renamed private archive), each
validating all categories; publication additionally hashes the closed file.
Default `MATCHES` schema 1 writes and validates consistently; the main issue is
capacity or source strictness, not a demonstrated universal schema mismatch.
`BackupCategory.java:28` actually defaults TITLE_PLAYBACK **off** as well as the
three location-sensitive categories. `BackupWorkspace.java:95` uses persisted
per-category choices before `defaultIncluded`, so the owner's stated selection
may include it; confirm selection rather than assuming it is absent.

## Destination and import failures: checked, but not this message path

`VerifiedBackupPublication.publish :44–66` checks private file length/hash
(`"Private archive changed after preparation"`), source length during copy
(`"Private archive changed during publication"`), null provider write stream
(`"Document provider did not open the destination"`), stream/write/flush/close
errors, null/mismatching readback (`"Document provider readback verification failed"`),
hash trailing/incomplete bytes (`:35–36`) and cancellation (`:31`). SAF
`resolver.openOutputStream(document,"wt")` / `openInputStream(document)`
(`:73–75`) can fail on missing document, revoked grant, unavailable USB/provider,
permission/security or provider runtime errors. `publish` wraps IO/runtime errors
as `Failure`; cleanup errors yield LEFT_UNVERIFIED, never deletion of a document
not known to belong to this export (`:60–77`). `BackupOperationModel.publish`
uses phase PUBLISHING, so these produce the destination-retry or generic-operation
text, **not PREPARING_EXPORT's failure text**. The picker has not opened when
`create()` fails. No destination timeout is implemented in this helper.

`LogicalBackupImport` is the restore reader, not the export writer; `create()`
never calls it. Its `:47–64` rejects null source, folder/temp/stream/close/sync
errors, empty input, >24 MiB compressed bytes, cancellation, closed-archive
validation/digest failure and verified-file rename collision/failure. It wraps
errors with cleanupPending/cancelled flags (`:14–24,57,63`), displayed in the
IMPORTING branch instead. Later `Prepared.payloads :35–39` rejects closed/changed
archive or malformed retained JSON; `close :42–43` can report failed deletion.
These similarly named exceptions must not be mistaken for evidence about the
owner's create operation.

## Category failures and row caps

Default categories include all ten settings groups, Home/custom page, queue and
Continue choices, preferred/review, pins/hidden, matches/playlists, history and
positions. Export merges pending restored intent; a single invalid pending JSON
or wrong preference type can fail an otherwise empty category. JSON `get*`,
`new JSONObject/JSONArray`, numeric parsing, preference `getString/getBoolean`,
SQLite cursor access and mapper binding can also throw without an explicit
`throw` statement: exporter `:159–161` wraps JSON/runtime failures; direct
IOExceptions pass to the model. Closed-reader category dispatch is exactly
`LogicalBackupArchive.java:87–119` (no optional category is silently skipped).

Caps are independent of total media rows: physical/shows/pin **inventory**,
hidden **inventory**, identification owners/matches, playback inventory, viewing
recovery and most exported intent categories are bounded at 100,000; playlists
at 10,000 definitions and 100,000 total memberships; Home at 200 rows and
100,000 memberships per row; custom page at 500 genres; sources, locations and
bookmarks at 10,000. Pins are read for all movie/show rows, not just pinned rows;
hidden inventory counts all video rows, not just hidden ones. These are read
whenever the catalogue is requested, even if PINNED/HIDDEN aren't selected.
61,000 media rows do not alone cross 100,000, but orphaned scraper rows, shows,
pending intent and viewing identities can increase separate totals.

The appendix inventories explicit rejection sites in the collection/validation
methods (including optional categories). Quoted throws include their precise
reason. Validator field/type/identity/range/duplicate rejections apply both to
exported payload and saved pending intent; source-read rejections apply before
writing. Restore-only transaction/application failures are excluded because
`create()` never calls them. Library constructors may internally recover bad
preferences; only an exception that escapes can reach the model.

## What the Diagnostic Report actually contains

**There is currently nowhere in that ZIP guaranteed to contain the backup cause.**
`BackupOperationModel.work :121–148` discards `failure`: no Diagnostics call,
Android Log call, stack trace, saved Throwable, error code, failed category or
byte count. Neither exporter/archive/publication logs these failures. `State`
retains the generic message/phase only. BackupImport's `Failure` cause and cleanup
flags are also just transient exceptions. Restore-specific
`PreferenceRestoreTransaction.java:168` (`restore_refresh_failed`) and
`MediaMatchReconciler.java:21,26` are not export failure instrumentation.

For owner/Claude examining an exported report:

1. ZIP root `build-device.json`: exact app source/version/device/API
   (`Diagnostics.java:1365–1382`). Compare to this baseline before applying line
   conclusions to the installed APK.
2. ZIP root `events.jsonl` and rotations; `important-YYYYMMDD.jsonl`,
   `incident-manual-YYYYMMDD.jsonl` / `incident-auto-YYYYMMDD.jsonl` and rotations,
   `flight.jsonl`: surrounding UI/scan/error/incident events if recorded. Locate
   UTC of the attempt and correlate process/sequence/operation IDs. These may
   show a scan at the time, not the swallowed backup exception. Exports contain
   only allow-listed recorder files (`DiagnosticArchive.java:10–16`;
   `Diagnostics.java:1414–1426`), **not logcat, private backup ZIPs, DBs or preferences**.
3. ZIP root `manifest.json` (`DiagnosticArchive.java:102–118`), `summary.txt`,
   `coverage.txt`: retained event counts, incident markers, dropped/write-error
   counts and partial-coverage warning, not a hidden backup-error section.
   `README.txt` expressly excludes exception messages/preferences/raw paths
   (`Diagnostics.java:1396–1399`). Absence of a backup event does not prove success.

Claude could add privacy-safe stage/category/count/size/reason diagnostics plus
exception *class* and cause classes, never raw JSON, paths, credentials, preference
values or unchecked exception text. That is a proposed fix, not implemented here.
Reproduce with a diagnostic build to obtain the actual swallowed cause; category
isolation can narrow it, but any change of selection must be recorded and does
not make a smaller backup equivalent to the requested complete backup.

## Ranked causes and proof for the leading fix

| Rank / ID | Evidence and fit for this Shield | Proposed action / proving test |
|---|---|---|
| 1 / BF-01 | Default MATCHES scales with every identified file. 4 MiB applies before compression; 61k matched rows is well above illustrative size below. HISTORY or a large Home membership can also hit it | Claude should make large supported categories fit a bounded, versioned writer/reader format together (stream/chunk categories or deliberately budget larger bounded limits with measured heap); align 100k row promises with byte/total/ZIP limits. Never truncate or bypass verification |
| 2 / BF-02 | Large old library has more opportunity for one missing provider ID, bad episode number or foreign/deleted scraper pointer. WebDAV is not excluded | Read-only fixture census and reason capture; prove valid legacy relationships round-trip and truly foreign pointers remain rejected |
| 3 / BF-03 | Long capture plus ongoing 15-minute scans or scraper/health updates can change any compared store, even unselected preferences | Run under concurrent scan/scrape/preferences fixture; verify consistent snapshot or explicit bounded retry, with no dropped intent |
| 4 / BF-04, BF-06 | Real legacy preference values, custom labels, pending intent or history schema/ranges fail strict positive codecs; history writer fence may time out | Round-trip each real writer/default/legacy value and capture stage/reason; busy-writer test at 15s |
| 5 / BF-07, BF-09 | Low internal cache space/fsync/rename/retention failures possible on Shield; no destination was open | Inject ENOSPC/read/rename/list/delete failures and verify old backups/data remain |
| 6 / BF-05, BF-08 | Inventory cap, incompatible schema, manifest version, corruption/digest errors possible but 61k and official version alone don't imply them | Boundary and corruption fixtures, exact APK version/source check |

Leading regression test Claude should add: create 61,050 unique valid identified
non-Put.io physical files (TMDB movies and episodes, USB + WebDAV, with correct
metadata owners), export precisely the owner's selected categories, and run
`preparePrivate` then `VerifiedBackupPublication.prepare`. Assert all selected
rows and pending intents survive both closed reads, including first/last rows,
and restore preview can read them; source databases/preferences and previous
backup bytes unchanged. Test at the revised entry/total/chunk boundaries and
one byte/row above, cancellation/cleanup, corrupted/doubled/unlisted entries,
wrong hashes and a >100k attack input. Measure peak memory on Android 11 Shield;
a passing desktop size calculation is not heap proof. Existing category tests
are small (`src/test/.../utils/LogicalBackup*`, `PortableMediaMatchesTest` etc.);
no 61k end-to-end acceptance run is claimed here.

### Illustrative capacity calculation (not owner's data)

Compact schema-1 JSON for 61,050 unique valid movie identities is **8,413,815 bytes** (8.02 MiB), while zlib-compressed bytes are 331,700. The writer refuses the former before compression. This Python UTF-8 calculation follows the codec fields, not Android execution; real movie/episode/opaque-origin sizes differ.

## Appendix: explicit source/category rejection index

Each row is a quoted source throw, with its enclosing export/validation helper
identified by the file/range inventory above. Empty/missing JSON fields and API
exceptions are covered by the implicit-exception boundary described above.
Optional SOURCES/BOOKMARKS/LOCATIONS rows are not executed for the reported
selection. `NumberFormatException` throws are caught and converted locally.
Context/provider overloads not used by `create` are omitted where separable.

| ID | File:line | What goes wrong | Evidence | Suggested fix | Severity | Confidence |
|---|---|---|---|---|---|---|
| BF-C | `utils/backup/LogicalBackupExporter.java:35` | Choose at least one backup category | `throw new IOException("Choose at least one backup category");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:38` | Selected backup category is not implemented yet; no backup was created | `throw new IOException("Selected backup category is not implemented yet; no backup was created");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:62` | Identification changed during backup; retry | `throw new IOException("Identification changed during backup; retry");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:67` | Identification changed during backup; retry | `throw new IOException("Identification changed during backup; retry");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:144` | Selected category has no source exporter | `throw new IOException("Selected category has no source exporter");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:149` | Configuration changed during backup; retry to capture a consistent selection | `throw new IOException("Configuration changed during backup; retry to capture a consistent selection");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:151` | Bookmarks changed during backup; retry to capture a consistent selection | `throw new IOException("Bookmarks changed during backup; retry to capture a consistent selection");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:153` | Indexed sources changed during backup; retry to capture a consistent selection | `throw new IOException("Indexed sources changed during backup; retry to capture a consistent selection");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:156` | Identification changed during backup; retry | `throw new IOException("Identification changed during backup; retry");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:160` | Selected configuration failed logical backup validation; no backup was created | `throw new IOException( "Selected configuration failed logical backup validation; no backup was created", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupExporter.java:170` | Cannot prepare private logical backup folder | `throw new IOException("Cannot prepare private logical backup folder");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupCatalog.java:29` | Logical backup cancelled | `if(cancel.getAsBoolean()\|\|Thread.currentThread().isInterrupted())throw new InterruptedIOException("Logical backup cancelled");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupCatalog.java:44` | Indexed media schema is not compatible with logical backup | `if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Indexed media schema is not compatible with logical backup");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupCatalog.java:58` | Indexed physical inventory is ambiguous or exceeds supported limits | `cancelled(cancel);Video video=(Video)mapper.bind(rows);if(video.getId()<=0\|\|!seen.add(video.getId())\|\|physical.size()>=100000)throw new IOException("Indexed physical inventory is ambiguous or exceeds supported limits");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupCatalog.java:67` | Indexed show identity is invalid | `cancelled(cancel);if(snapshot.shows.size()>=100000\|\|rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER\|\|rows.getLong(0)<=0)throw new IOException("Indexed show identity is invalid");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/LogicalBackupCatalog.java:75` | Complete indexed identity inventory is unavailable; backup was not created | `}catch(RuntimeException invalid){throw new IOException("Complete indexed identity inventory is unavailable; backup was not created",invalid);}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSettingsRegistry.java:205` | Unsupported saved setting value:  | `throw new JSONException("Unsupported saved setting value: " + entry.getKey());` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSettingsRegistry.java:219` | Unsupported settings schema or category | `throw new JSONException("Unsupported settings schema or category");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSettingsRegistry.java:228` | Unsupported category-owned setting | `throw new JSONException("Unsupported category-owned setting");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSettingsRegistry.java:232` | Setting set required | `throw new JSONException("Setting set required");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSettingsRegistry.java:238` | Duplicate or invalid setting set | `throw new JSONException("Duplicate or invalid setting set");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSettingsRegistry.java:243` | Invalid portable setting value | `throw new JSONException("Invalid portable setting value");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:18` | Home label needs privacy review before backup | `throw new JSONException("Home label needs privacy review before backup");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:89` | Unsupported pending Home schema | `throw new JSONException("Unsupported pending Home schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:92` | Too many pending Home rows | `throw new JSONException("Too many pending Home rows");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:99` | Invalid pending Home row | `throw new JSONException("Invalid pending Home row");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:105` | Too many pending memberships | `throw new JSONException("Too many pending memberships");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:116` | Unsupported Home intent schema | `throw new JSONException("Unsupported Home intent schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:119` | Too many Home rows | `throw new JSONException("Too many Home rows");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:126` | Invalid Home row identity | `throw new JSONException("Invalid Home row identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:132` | Invalid Home row Boolean | `throw new JSONException("Invalid Home row Boolean");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:134` | Empty Home media types | `throw new JSONException("Empty Home media types");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:136` | Unsupported Home sort | `throw new JSONException("Unsupported Home sort");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:140` | Invalid Home maximum type or range | `throw new JSONException("Invalid Home maximum type or range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:144` | Invalid Home maximum | `throw new JSONException("Invalid Home maximum");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:155` | Watch Next membership belongs to its own category | `throw new JSONException("Watch Next membership belongs to its own category");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/HomeIntentCodec.java:161` | Duplicate Home title reference | `throw new JSONException("Duplicate Home title reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:17` | Custom Library text needs privacy review before backup | `throw new JSONException("Custom Library text needs privacy review before backup");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:27` | Saved Library page needs review | `throw new JSONException("Saved Library page needs review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:40` | Unsupported Library page schema | `throw new JSONException("Unsupported Library page schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:50` | Unsupported Library definition fields | `throw new JSONException("Unsupported Library definition fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:59` | Unsupported Library page option | `throw new JSONException("Unsupported Library page option");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:64` | Invalid Library page Boolean | `throw new JSONException("Invalid Library page Boolean");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:70` | Invalid Library page year or rating | `throw new JSONException("Invalid Library page year or rating");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:72` | Unsupported Library language | `throw new JSONException("Unsupported Library language");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:75` | Too many Library genres | `throw new JSONException("Too many Library genres");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:79` | Library genre must be text | `throw new JSONException("Library genre must be text");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableCustomPage.java:83` | Duplicate Library genre | `throw new JSONException("Duplicate Library genre");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingChoices.java:40` | Unsupported viewing choice schema | `throw new JSONException("Unsupported viewing choice schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingChoices.java:43` | Too many viewing choices | `throw new JSONException("Too many viewing choices");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingChoices.java:50` | Queue reference must be text | `throw new JSONException("Queue reference must be text");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingChoices.java:58` | Invalid Continue Watching dismissal | `throw new JSONException("Invalid Continue Watching dismissal");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingChoices.java:64` | Duplicate viewing choice | `throw new JSONException("Duplicate viewing choice");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingChoices.java:85` | Unsupported saved Continue Watching choice | `throw new JSONException("Unsupported saved Continue Watching choice");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:19` | Unmatched version parent needs a valid indexed location | `throw new JSONException("Unmatched version parent needs a valid indexed location");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:26` | Version title exceeds supported range | `throw new JSONException("Version title exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:37` | Version episode exceeds supported range | `throw new JSONException("Version episode exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:41` | Unsupported version parent identity | `throw new JSONException("Unsupported version parent identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:59` | Unsupported preferred-version schema | `throw new JSONException("Unsupported preferred-version schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:62` | Too many preferred versions | `throw new JSONException("Too many preferred versions");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:67` | Invalid preferred-version fields | `throw new JSONException("Invalid preferred-version fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:71` | Expected version title must be text | `throw new JSONException("Expected version title must be text");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:78` | Invalid expected version title | `throw new JSONException("Invalid expected version title");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:82` | Conflicting preferred versions for one parent | `throw new JSONException("Conflicting preferred versions for one parent");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:97` | Saved version choice needs review | `throw new JSONException("Saved version choice needs review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:102` | Typed exception; see quoted guard | `throw new NumberFormatException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortablePreferredVersions.java:105` | Unsupported saved version selection | `throw new JSONException("Unsupported saved version selection");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:35` | Unsupported review schema | `throw new JSONException("Unsupported review schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:38` | Too many review notes | `throw new JSONException("Too many review notes");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:44` | Invalid review fields | `throw new JSONException("Invalid review fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:51` | Invalid expected match | `throw new JSONException("Invalid expected match");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:60` | Expected match exceeds supported identity range | `throw new JSONException("Expected match exceeds supported identity range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:64` | Duplicate review note | `throw new JSONException("Duplicate review note");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:74` | Saved review note needs inspection | `throw new JSONException("Saved review note needs inspection");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableReviewNotes.java:79` | Review identity exceeds supported range | `throw new JSONException("Review identity exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:16` | Integer viewing state required | `throw new JSONException("Integer viewing state required");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:19` | Viewing state outside supported range | `throw new JSONException("Viewing state outside supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:27` | Unsupported viewing identity | `throw new JSONException("Unsupported viewing identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:31` | Typed exception; see quoted guard | `throw new NumberFormatException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:33` | Typed exception; see quoted guard | `throw new NumberFormatException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:35` | Viewing identity outside supported range | `throw new JSONException("Viewing identity outside supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:54` | Saved viewing record needs review | `throw new JSONException("Saved viewing record needs review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:78` | Unsupported viewing state schema | `throw new JSONException("Unsupported viewing state schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:81` | Too many viewing records | `throw new JSONException("Too many viewing records");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:86` | Viewing identity must be text | `throw new JSONException("Viewing identity must be text");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:93` | Invalid history fields | `throw new JSONException("Invalid history fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:99` | Invalid position fields | `throw new JSONException("Invalid position fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:106` | Duplicate viewing identity | `throw new JSONException("Duplicate viewing identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:115` | No viewing category selected | `throw new JSONException("No viewing category selected");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:172` | Unsupported viewing recovery schema | `throw new JSONException("Unsupported viewing recovery schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:175` | Too many viewing recovery records | `throw new JSONException("Too many viewing recovery records");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:181` | Invalid viewing recovery fields | `throw new JSONException("Invalid viewing recovery fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:189` | Duplicate viewing recovery identity | `throw new JSONException("Duplicate viewing recovery identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/PortableViewingState.java:198` | No viewing recovery category selected | `throw new JSONException("No viewing recovery category selected");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/LegacyViewingSnapshot.java:26` | Invalid legacy viewing inventory | `throw new JSONException("Invalid legacy viewing inventory");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/LegacyViewingSnapshot.java:40` | Legacy viewing values need review | `throw new JSONException("Legacy viewing values need review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/LegacyViewingSnapshot.java:47` | Legacy viewing time exceeds supported range | `throw new JSONException("Legacy viewing time exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:62` | Viewing history is busy; retry the operation | `throw new IOException("Viewing history is busy; retry the operation", failure);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:290` | Invalid viewing recovery identity | `throw new IOException("Invalid viewing recovery identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:293` | Invalid viewing recovery integer | `throw new IOException("Invalid viewing recovery integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:297` | Invalid viewing recovery range | `throw new IOException("Invalid viewing recovery range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:299` | Viewing recovery exceeds supported limits | `throw new IOException("Viewing recovery exceeds supported limits");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:305` | Viewing recovery inventory unavailable | `throw new IOException("Viewing recovery inventory unavailable", failure);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:573` | Unsupported or damaged viewing history | `throw new IOException("Unsupported or damaged viewing history");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:579` | Unexpected history database objects | `throw new IOException("Unexpected history database objects");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:586` | Unexpected history metadata | `throw new IOException("Unexpected history metadata");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:591` | Invalid history locale | `throw new IOException("Invalid history locale");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:603` | Unexpected history column contract | `throw new IOException("Unexpected history column contract");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:609` | Unexpected history fields | `throw new IOException("Unexpected history fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:619` | Invalid viewing history | `throw new IOException("Invalid viewing history");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `leanback/ViewingHistory.java:622` | Invalid viewing history database | `throw new IOException("Invalid viewing history database", failure);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:31` | Invalid indexed title pin state | `throw new IllegalArgumentException("Invalid indexed title pin state");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:97` | Invalid indexed pin inventory | `throw new IOException("Invalid indexed pin inventory");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:104` | Indexed pin state could not be validated | `throw new IOException("Indexed pin state could not be validated", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:111` | Unsupported pin schema | `throw new JSONException("Unsupported pin schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:114` | Too many pinned titles | `throw new JSONException("Too many pinned titles");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:122` | Invalid pin fields | `throw new JSONException("Invalid pin fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:125` | Invalid pin kind | `throw new JSONException("Invalid pin kind");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:130` | Pin source identity exceeds supported range | `throw new JSONException("Pin source identity exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:136` | Invalid pin logical identity | `throw new JSONException("Invalid pin logical identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:140` | Pin logical identity exceeds supported range | `throw new JSONException("Pin logical identity exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:144` | Duplicate pin source | `throw new JSONException("Duplicate pin source");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePinnedTitles.java:155` | Duplicate indexed title row | `throw new JSONException("Duplicate indexed title row");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:28` | Invalid indexed hidden-item state | `throw new IOException("Invalid indexed hidden-item state");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:31` | Hidden-item inventory unavailable | `}catch(RuntimeException invalid){throw new IOException("Hidden-item inventory unavailable",invalid);}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:37` | Unsupported hidden-item schema | `throw new JSONException("Unsupported hidden-item schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:39` | Too many hidden items | `if(rows.length()>100000)throw new JSONException("Too many hidden items");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:42` | physical | `if(row.length()!=1\|\|!(row.get("physical") instanceof String))throw new JSONException("Invalid hidden-item fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:44` | Duplicate hidden-item reference | `if(!seen.add(ref))throw new JSONException("Duplicate hidden-item reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableHiddenItems.java:50` | Invalid indexed hidden-item state | `if(entry.getKey()==null\|\|entry.getKey()<=0\|\|entry.getValue()==null)throw new JSONException("Invalid indexed hidden-item state");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:31` | Invalid saved-playlist identity | `throw new JSONException("Invalid saved-playlist identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:35` | Playlist identity exceeds supported range | `throw new JSONException("Playlist identity exceeds supported range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:43` | Invalid playlist integer | `throw new JSONException("Invalid playlist integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:51` | Invalid playlist name | `throw new JSONException("Invalid playlist name");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:56` | Playlist name requires privacy review | `throw new JSONException("Playlist name requires privacy review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:60` | Invalid playlist name encoding | `throw new JSONException("Invalid playlist name encoding");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:66` | Invalid saved-playlist member | `throw new JSONException("Invalid saved-playlist member");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:69` | Typed exception; see quoted guard | `throw new NumberFormatException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:71` | Playlist provider identity exceeds receiving API | `throw new JSONException("Playlist provider identity exceeds receiving API");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:82` | Invalid native playlist identity | `throw new JSONException("Invalid native playlist identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:85` | Too many playlist members | `throw new JSONException("Too many playlist members");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:100` | Saved-playlist schema is incompatible | `throw new IOException("Saved-playlist schema is incompatible");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:115` | Invalid saved-playlist definition | `throw new IOException("Invalid saved-playlist definition");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:118` | Unknown saved-playlist sync status | `throw new IOException("Unknown saved-playlist sync status");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:122` | Ambiguous or excessive playlist definitions | `throw new IOException("Ambiguous or excessive playlist definitions");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:137` | Invalid playlist owner | `throw new IOException("Invalid playlist owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:142` | Invalid playlist membership status | `throw new IOException("Invalid playlist membership status");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:145` | Unknown playlist membership status | `throw new IOException("Unknown playlist membership status");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:150` | Invalid playlist provider identity | `throw new IOException("Invalid playlist provider identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:153` | Ambiguous or excessive playlist membership | `throw new IOException("Ambiguous or excessive playlist membership");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:163` | Saved-playlist inventory cannot be exported safely | `throw new IOException("Saved-playlist inventory cannot be exported safely", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:169` | Unsupported saved-playlist schema | `throw new JSONException("Unsupported saved-playlist schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:172` | Too many saved playlists | `throw new JSONException("Too many saved playlists");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:178` | Invalid saved-playlist fields | `throw new JSONException("Invalid saved-playlist fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:182` | Duplicate saved-playlist identity | `throw new JSONException("Duplicate saved-playlist identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:188` | Invalid or excessive playlist members | `throw new JSONException("Invalid or excessive playlist members");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:192` | Duplicate playlist member | `throw new JSONException("Duplicate playlist member");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:204` | Invalid indexed playlist ownership | `throw new JSONException("Invalid indexed playlist ownership");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableSavedPlaylists.java:208` | Ambiguous saved-playlist references | `throw new JSONException("Ambiguous saved-playlist references");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:21` | Invalid media-match integer | `throw new JSONException("Invalid media-match integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:32` | Unsupported media-match identity | `throw new JSONException("Unsupported media-match identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:37` | Typed exception; see quoted guard | `throw new NumberFormatException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:39` | Media-match identity exceeds receiving API | `throw new JSONException("Media-match identity exceeds receiving API");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:43` | Invalid episode provider identity | `throw new JSONException("Invalid episode provider identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:58` | Invalid native match integer | `throw new IOException("Invalid native match integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:65` | Media-match schema is incompatible | `throw new IOException("Media-match schema is incompatible");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:80` | Invalid indexed match owner | `throw new IOException("Invalid indexed match owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:87` | Identified movie has no durable provider identity | `throw new IOException("Identified movie has no durable provider identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:93` | Identified episode has no durable provider identity | `throw new IOException("Identified episode has no durable provider identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:97` | Unknown indexed media-match kind | `throw new IOException("Unknown indexed media-match kind");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:100` | Identified media has no owned metadata pointer | `throw new IOException("Identified media has no owned metadata pointer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:111` | Identification pointer is outside its physical owner | `throw new IOException("Identification pointer is outside its physical owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:114` | Ambiguous or excessive indexed matches | `throw new IOException("Ambiguous or excessive indexed matches");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:117` | Media identification cannot be exported safely | `throw new IOException("Media identification cannot be exported safely", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:124` | Invalid media-match fields | `throw new JSONException("Invalid media-match fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:131` | Unsupported media-match schema | `throw new JSONException("Unsupported media-match schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:134` | Too many media matches | `throw new JSONException("Too many media matches");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:139` | Invalid media-match fields | `throw new JSONException("Invalid media-match fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:142` | Duplicate media-match owner | `throw new JSONException("Duplicate media-match owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:150` | Invalid private pending match schema | `throw new JSONException("Invalid private pending match schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:158` | Invalid private pending match state | `throw new JSONException("Invalid private pending match state");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:175` | Invalid match source ownership | `throw new JSONException("Invalid match source ownership");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableMediaMatches.java:178` | Match source is outside the physical inventory | `throw new JSONException("Match source is outside the physical inventory");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:64` | Receiving identification guard unavailable | `throw new IOException("Receiving identification guard unavailable", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:72` | Invalid receiving identification integer | `throw new IOException("Invalid receiving identification integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:85` | Receiving identification schema is incompatible | `throw new IOException("Receiving identification schema is incompatible");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:101` | Invalid receiving identification owner | `throw new IOException("Invalid receiving identification owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:109` | Receiving identification has no owned metadata | `throw new IOException("Receiving identification has no owned metadata");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:126` | Receiving identification points outside its metadata owner | `throw new IOException( "Receiving identification points outside its metadata owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:131` | Duplicate receiving metadata owner | `throw new IOException("Duplicate receiving metadata owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:136` | Duplicate receiving identification owner | `throw new IOException("Duplicate receiving identification owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:147` | Receiving identification inventory unavailable | `throw new IOException("Receiving identification inventory unavailable", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchInventory.java:164` | Receiving media changed before identification review | `throw new IOException("Receiving media changed before identification review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:33` | Invalid private match choice | `throw new JSONException("Invalid private match choice");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:37` | Invalid private episode identity | `throw new JSONException("Invalid private episode identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:57` | Invalid private match integer | `throw new JSONException("Invalid private match integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:67` | Invalid private match intent | `throw new JSONException("Invalid private match intent");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:75` | Invalid private match originals | `throw new JSONException("Invalid private match originals");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:79` | Missing private match originals | `throw new JSONException("Missing private match originals");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:199` | Invalid saved match intent | `throw new JSONException("Invalid saved match intent");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/MediaMatchIntents.java:213` | Unresolved identification conflicts with a reused physical reference | `throw new JSONException( "Unresolved identification conflicts with a reused physical reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:35` | Unsupported per-title playback choice | `throw new IllegalArgumentException("Unsupported per-title playback choice");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:53` | Invalid indexed per-title playback integer | `throw new IOException("Invalid indexed per-title playback integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:79` | Invalid indexed per-title playback state | `throw new IOException("Invalid indexed per-title playback state");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:87` | Invalid indexed subtitle count | `throw new IOException("Invalid indexed subtitle count");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:96` | Per-title playback inventory unavailable | `throw new IOException("Per-title playback inventory unavailable", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:109` | Invalid per-title playback fields | `throw new JSONException("Invalid per-title playback fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:114` | Unsupported per-title playback range | `throw new JSONException("Unsupported per-title playback range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:117` | Invalid subtitle selection intent | `throw new JSONException("Invalid subtitle selection intent");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:125` | Unsupported per-title playback choice | `throw new JSONException("Unsupported per-title playback choice");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:132` | Unsupported per-title playback schema | `throw new JSONException("Unsupported per-title playback schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:135` | Too many per-title playback choices | `throw new JSONException("Too many per-title playback choices");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:140` | Invalid per-title playback fields | `throw new JSONException("Invalid per-title playback fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:145` | Conflicting per-title playback choices | `throw new JSONException("Conflicting per-title playback choices");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitlePlayback.java:154` | Invalid indexed playback identity | `throw new JSONException("Invalid indexed playback identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/TitlePlaybackOverrides.java:36` | Invalid receiving playback owner | `throw new JSONException("Invalid receiving playback owner");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/TitlePlaybackOverrides.java:47` | Playback identity guard unavailable | `throw new JSONException("Playback identity guard unavailable");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/TitlePlaybackOverrides.java:86` | Invalid explicit playback recovery value | `throw new JSONException("Invalid explicit playback recovery value");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/TitlePlaybackOverrides.java:91` | Invalid explicit playback choice fields | `throw new JSONException("Invalid explicit playback choice fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/TitlePlaybackOverrides.java:94` | Missing explicit playback reference | `throw new JSONException("Missing explicit playback reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/TitlePlaybackOverrides.java:200` | Invalid explicit playback store | `throw new JSONException("Invalid explicit playback store");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePhysicalReferences.java:19` | Indexed physical identity required | `for(Video video:records){if(video.getId()<=0)throw new IllegalArgumentException("Indexed physical identity required");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePhysicalReferences.java:20` | Duplicate indexed physical identity | `if(videos.putIfAbsent(video.getId(),video)!=null)throw new IllegalArgumentException("Duplicate indexed physical identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePhysicalReferences.java:36` | Indexed physical identity required | `if(sourceId<=0)throw new JSONException("Indexed physical identity required");String fingerprint=fingerprints.get(sourceId);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePhysicalReferences.java:40` | Indexed physical identity required | `public String pendingReference(long sourceId)throws JSONException{if(sourceId<=0)throw new JSONException("Indexed physical identity required");if(unresolved==null)unresolved=new PortableTitleReferences(context,null);return unresolved.reference("v"+sourceId);}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortablePhysicalReferences.java:43` | unresolved: | `PortableTitleReferences.validate(ref);if(!ref.startsWith("unresolved:")\|\|!ref.matches(".*:v[1-9][0-9]{0,18}"))throw new JSONException("Physical identity required");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitleReferences.java:22` | [a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12} | `if(!saved.matches("[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}")){saved=UUID.randomUUID().toString();if(!prefs.edit().putString(ORIGIN,saved).commit())throw new IllegalStateException("Cannot preserve backup origin");}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitleReferences.java:26` | [vs][1-9][0-9]* | `if(!sourceKey.matches("[vs][1-9][0-9]*"))throw new JSONException("Unsupported indexed membership identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableTitleReferences.java:33` | (?:tmdb:(?:movie|tv):[1-9][0-9]{0,18}|unresolved:[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}:[vs][1-9][0-9]{0,18}) | `if(!reference.matches("(?:tmdb:(?:movie\|tv):[1-9][0-9]{0,18}\|unresolved:[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}:[vs][1-9][0-9]{0,18})"))throw new JSONException("Unsafe or unsupported logical title reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:19` | Invalid bookmark label | `throw new JSONException("Invalid bookmark label");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:24` | Bookmark label needs privacy review | `throw new JSONException("Bookmark label needs privacy review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:28` | Invalid bookmark label encoding | `throw new JSONException("Invalid bookmark label encoding");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:34` | Unsupported bookmark schema | `throw new JSONException("Unsupported bookmark schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:37` | Too many file bookmarks | `throw new JSONException("Too many file bookmarks");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:43` | Invalid bookmark fields | `throw new JSONException("Invalid bookmark fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:46` | Invalid or duplicate bookmark identity | `throw new JSONException("Invalid or duplicate bookmark identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:49` | Typed exception; see quoted guard | `throw new NumberFormatException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:51` | Bookmark identity out of range | `throw new JSONException("Bookmark identity out of range");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableBookmarks.java:92` | Bookmark label cannot be classified safely | `throw new JSONException("Bookmark label cannot be classified safely");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:17` | Invalid bookmark recovery integer | `throw new JSONException("Invalid bookmark recovery integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:25` | Invalid private bookmark text | `throw new JSONException("Invalid private bookmark text");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:32` | Unsupported bookmark recovery schema | `throw new JSONException("Unsupported bookmark recovery schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:37` | Invalid absent bookmark image | `throw new JSONException("Invalid absent bookmark image");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:42` | Invalid bookmark recovery target | `throw new JSONException("Invalid bookmark recovery target");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:47` | Unexpected legacy bookmark field | `throw new JSONException("Unexpected legacy bookmark field");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:54` | Bookmark schema is incompatible; existing data retained | `throw new IOException("Bookmark schema is incompatible; existing data retained");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:65` | Bookmark column contract is incompatible | `throw new IOException("Bookmark column contract is incompatible");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:69` | Bookmark column contract is incomplete | `throw new IOException("Bookmark column contract is incomplete");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:80` | Invalid existing bookmark state | `throw new IOException("Invalid existing bookmark state");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:92` | Invalid bookmark sequence | `throw new IOException("Invalid bookmark sequence");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/BookmarkStoreRecovery.java:124` | Bookmark inventory unavailable; no data changed | `throw new IOException("Bookmark inventory unavailable; no data changed", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:22` | Invalid saved source configuration | `throw new JSONException("Invalid saved source configuration");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:26` | Invalid saved source configuration | `throw new JSONException("Invalid saved source configuration");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:67` | Invalid pending sources | `throw new JSONException("Invalid pending sources");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:94` | Unsupported source schema | `throw new JSONException("Unsupported source schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:100` | Too many saved sources | `throw new JSONException("Too many saved sources");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:104` | Invalid source reference | `throw new JSONException("Invalid source reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLibrarySources.java:108` | Duplicate source reference | `throw new JSONException("Duplicate source reference");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:17` | Invalid indexed-source recovery integer | `throw new JSONException("Invalid indexed-source recovery integer");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:25` | Invalid private indexed-source text | `throw new JSONException("Invalid private indexed-source text");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:31` | Unsupported indexed-source recovery image | `throw new JSONException("Unsupported indexed-source recovery image");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:36` | Invalid absent indexed-source image | `throw new JSONException("Invalid absent indexed-source image");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:41` | Invalid indexed-source recovery row | `throw new JSONException("Invalid indexed-source recovery row");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:47` | Invalid indexed-source rescan intent | `throw new JSONException("Invalid indexed-source rescan intent");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:98` | Indexed-source schema is incompatible; no migration performed | `throw new IOException("Indexed-source schema is incompatible; no migration performed");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:108` | Indexed-source column contract is incompatible | `throw new IOException("Indexed-source column contract is incompatible");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:112` | Indexed-source columns are incomplete | `throw new IOException("Indexed-source columns are incomplete");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:126` | Invalid indexed-source row | `throw new IOException("Invalid indexed-source row");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:141` | Invalid indexed-source sequence | `throw new IOException("Invalid indexed-source sequence");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/IndexedSourceStoreRecovery.java:162` | Indexed-source inventory unavailable | `throw new IOException("Indexed-source inventory unavailable", invalid);` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:35` | Invalid indexed-source label | `throw new JSONException("Invalid indexed-source label");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:40` | Indexed-source label needs privacy review | `throw new JSONException("Indexed-source label needs privacy review");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:44` | Invalid indexed-source label encoding | `throw new JSONException("Invalid indexed-source label encoding");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:50` | Unsupported indexed-source schema | `throw new JSONException("Unsupported indexed-source schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:53` | Too many indexed sources | `throw new JSONException("Too many indexed sources");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:59` | Invalid indexed-source fields | `throw new JSONException("Invalid indexed-source fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:63` | Ambiguous indexed-source location | `throw new JSONException("Ambiguous indexed-source location");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:104` | Ambiguous indexed-source location | `throw new JSONException("Ambiguous indexed-source location");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableIndexedSources.java:111` | Indexed-source label cannot be classified safely | `throw new JSONException("Indexed-source label cannot be classified safely");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:17` | Typed exception; see quoted guard | `controls(value))throw new IllegalArgumentException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:26` | (?i).*%2f.* | `uri.getRawPath().matches("(?i).*%2f.*")\|\|uri.getPort()>65535\|\|uri.getPort()==0)throw new IllegalArgumentException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:27` | file | `if(scheme.equals("file")) {if(authority!=null&&!authority.isEmpty())throw new IllegalArgumentException();}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:28` | Typed exception; see quoted guard | `else if(authority==null\|\|authority.isEmpty()\|\|uri.getHost()==null)throw new IllegalArgumentException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:31` | / | `if(Arrays.asList(canonical.getPath().split("/")).contains(".."))throw new IllegalArgumentException();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:33` | Unsupported or credential-bearing location; reconnect it separately | `}catch(Exception invalid){throw new JSONException("Unsupported or credential-bearing location; reconnect it separately");}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:38` | Location identity unavailable | `}catch(Exception unavailable){throw new JSONException("Location identity unavailable");}` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:41` | location:[a-f0-9]{64} | `if(value==null\|\|!value.matches("location:[a-f0-9]{64}"))throw new JSONException("Invalid location identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:49` | schema | `if(payload.length()!=2\|\|!Integer.valueOf(1).equals(payload.get("schema")))throw new JSONException("Unsupported location schema");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:50` | rows | `JSONArray rows=payload.getJSONArray("rows");if(rows.length()>10000)throw new JSONException("Too many saved locations");Map<String,String> result=new LinkedHashMap<>();` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:52` | ref | `JSONObject row=rows.getJSONObject(i);if(row.length()!=2\|\|!(row.get("ref") instanceof String)\|\|!(row.get("uri") instanceof String))throw new JSONException("Invalid location fields");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
| BF-C | `utils/backup/PortableLocations.java:54` | Conflicting or invalid location identity | `if(!uri.equals(locator(uri))\|\|!ref.equals(reference(uri))\|\|result.putIfAbsent(ref,uri)!=null)throw new JSONException("Conflicting or invalid location identity");` | Preserve validation; classify stage/reason and test real source value | user-visible | high |
